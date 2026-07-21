package multiservicioRafael.invenatario.repository;

import multiservicioRafael.invenatario.config.ConexionDB;
import multiservicioRafael.invenatario.repository.Interfaces.DashboardDaoInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Todo con JDBC plano (mismo estilo que CompraDao), sin funciones SQL nuevas
 * en la BD: así no hace falta correr ninguna migración para que el
 * dashboard funcione. Una sola Connection se reutiliza para todas las
 * consultas del período.
 */
public class DashboardDao implements DashboardDaoInterface {

    @Override
    public Map<String, Object> obtenerEstadisticas(String fechaDesde, String fechaHasta) {
        Map<String, Object> resultado = new HashMap<>();

        LocalDate desde = LocalDate.parse(fechaDesde);
        LocalDate hasta = LocalDate.parse(fechaHasta);
        long dias = java.time.temporal.ChronoUnit.DAYS.between(desde, hasta) + 1;
        LocalDate prevDesde = desde.minusDays(dias);
        LocalDate prevHasta = desde.minusDays(1);

        // Cuando el rango filtrado es un único día (filtro "Hoy"), agrupar
        // por día deja 1 solo punto (o 2, si "desde" y "hasta" difieren en
        // 1 día) y la gráfica se ve como puntos sueltos. El profesor pidió
        // que en ese caso se desglose por hora en vez de por día.
        String granularidad = dias <= 1 ? "hour" : dias <= 31 ? "day" : dias <= 180 ? "week" : "month";
        resultado.put("granularidadTendencia", granularidad);

        try (Connection cn = ConexionDB.getInstance().getConnection()) {
            cargarSeccion(resultado, "resumen", () -> obtenerResumen(cn, desde, hasta, prevDesde, prevHasta), new HashMap<>());
            cargarSeccion(resultado, "ventasMensuales", () -> obtenerVentasMensuales(cn, desde, hasta, granularidad), new ArrayList<>());
            cargarSeccion(resultado, "stockPorCategoria", () -> obtenerStockPorCategoria(cn), new ArrayList<>());
            cargarSeccion(resultado, "ordenesPorEstado", () -> obtenerOrdenesPorEstado(cn, desde, hasta), new ArrayList<>());
            cargarSeccion(resultado, "ventasVServicios", () -> obtenerVentasVServicios(cn, desde, hasta, granularidad), new ArrayList<>());
            cargarSeccion(resultado, "comparativoProductos", () -> obtenerComparativoProductos(cn, desde, hasta, granularidad), new ArrayList<>());
            cargarSeccion(resultado, "topProductos", () -> obtenerTopProductos(cn, desde, hasta), new ArrayList<>());
            cargarSeccion(resultado, "topServicios", () -> obtenerTopServicios(cn, desde, hasta), new ArrayList<>());
            cargarSeccion(resultado, "topTrabajadores", () -> obtenerTopTrabajadores(cn, desde, hasta), new ArrayList<>());
            cargarSeccion(resultado, "productosStockCritico", () -> obtenerProductosStockCritico(cn), new ArrayList<>());
        } catch (Exception e) {
            System.out.println("Error en DashboardDao.obtenerEstadisticas (conexión): " + e.getMessage());
            e.printStackTrace();
        }
        return resultado;
    }

    // Ejecuta una sección del dashboard de forma aislada: si esta consulta
    // en particular falla (tabla/columna mal escrita, etc.), se deja un
    // valor por defecto y se loguea el error, pero NO se interrumpen las
    // demás secciones. Antes, un solo try/catch general hacía que un error
    // en cualquier consulta "apagara" todas las que venían después.
    private <T> void cargarSeccion(Map<String, Object> resultado, String clave, ConsultaDashboard<T> consulta, T valorPorDefecto) {
        try {
            resultado.put(clave, consulta.ejecutar());
        } catch (Exception e) {
            System.out.println("Error en DashboardDao [" + clave + "]: " + e.getMessage());
            e.printStackTrace();
            resultado.put(clave, valorPorDefecto);
        }
    }

    @FunctionalInterface
    private interface ConsultaDashboard<T> {
        T ejecutar() throws Exception;
    }

    private Map<String, Object> obtenerResumen(Connection cn, LocalDate desde, LocalDate hasta,
                                               LocalDate prevDesde, LocalDate prevHasta) throws Exception {
        Map<String, Object> r = new HashMap<>();

        r.put("ventasMesActual", sumarVentas(cn, desde, hasta));
        r.put("ventasMesAnterior", sumarVentas(cn, prevDesde, prevHasta));
        r.put("ordenesServicioMes", contarOrdenesServicio(cn, desde, hasta));
        r.put("ordenesServicioMesAnterior", contarOrdenesServicio(cn, prevDesde, prevHasta));

        String sqlStock = "SELECT COUNT(*) FROM repuesto WHERE cantidad <= stock_minimo AND estado = 'Activo'";
        try (PreparedStatement ps = cn.prepareStatement(sqlStock); ResultSet rs = ps.executeQuery()) {
            r.put("productosBajoStock", rs.next() ? rs.getInt(1) : 0);
        }

        String sqlProv = "SELECT COUNT(*) FROM proveedor WHERE estado = 'Activo'";
        try (PreparedStatement ps = cn.prepareStatement(sqlProv); ResultSet rs = ps.executeQuery()) {
            r.put("proveedoresActivos", rs.next() ? rs.getInt(1) : 0);
        }
        return r;
    }

    private double sumarVentas(Connection cn, LocalDate desde, LocalDate hasta) throws Exception {
        String sql = "SELECT COALESCE(SUM(precio_total),0) FROM orden_venta " +
                "WHERE fecha_emision::date BETWEEN ? AND ? AND estado <> 'Anulado'";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }

    private int contarOrdenesServicio(Connection cn, LocalDate desde, LocalDate hasta) throws Exception {
        String sql = "SELECT COUNT(*) FROM orden_servicio WHERE COALESCE(fecha_servicio, fecha_registro::date) BETWEEN ? AND ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // Arma la expresión SQL que trunca una columna de fecha/timestamp según
    // la granularidad decidida por el backend, siempre devuelta como texto
    // (to_char) en vez de castear a ::date: con granularidad "hour" un
    // ::date habría descartado la hora y colapsado todo el día en 1 fila.
    // granularidad viene siempre de un valor fijo calculado en el propio
    // backend ("hour"/"day"/"week"/"month"), nunca del usuario: es seguro
    // concatenarlo directo en el SQL.
    private String periodoSelect(String columnaExpr, String granularidad) {
        String formato = "hour".equals(granularidad) ? "YYYY-MM-DD HH24:00:00" : "YYYY-MM-DD";
        return "to_char(date_trunc('" + granularidad + "', " + columnaExpr + "), '" + formato + "')";
    }

    private List<Map<String, Object>> obtenerVentasMensuales(Connection cn, LocalDate desde, LocalDate hasta, String granularidad) throws Exception {
        Map<String, Double> ventasPorPeriodo = new LinkedHashMap<>();
        String sqlVentas = "SELECT " + periodoSelect("fecha_emision", granularidad) + " AS periodo, SUM(precio_total) AS total " +
                "FROM orden_venta WHERE fecha_emision::date BETWEEN ? AND ? AND estado <> 'Anulado' GROUP BY 1";
        try (PreparedStatement ps = cn.prepareStatement(sqlVentas)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ventasPorPeriodo.put(rs.getString("periodo"), rs.getDouble("total"));
            }
        }

        // Compras: se usa operacion_compra.tot_pago (el monto real pagado por
        // cada operación de compra a proveedor), agrupado con la misma
        // granularidad que las ventas para que ambas series calcen mes a mes,
        // semana a semana, día a día u hora a hora.
        Map<String, Double> comprasPorPeriodo = new LinkedHashMap<>();
        String sqlCompras = "SELECT " + periodoSelect("fec_compra", granularidad) + " AS periodo, SUM(tot_pago) AS total " +
                "FROM operacion_compra WHERE fec_compra::date BETWEEN ? AND ? GROUP BY 1";
        try (PreparedStatement ps = cn.prepareStatement(sqlCompras)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) comprasPorPeriodo.put(rs.getString("periodo"), rs.getDouble("total"));
            }
        }

        return combinarPorPeriodo(desde, hasta, ventasPorPeriodo, comprasPorPeriodo, "ventas", "compras", granularidad);
    }

    private List<Map<String, Object>> obtenerStockPorCategoria(Connection cn) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT c.nombre AS categoria, " +
                "COUNT(*) FILTER (WHERE r.cantidad = 0) AS sin_stock, " +
                "COUNT(*) FILTER (WHERE r.cantidad > 0 AND r.cantidad <= r.stock_minimo) AS bajo_stock, " +
                "COUNT(*) FILTER (WHERE r.cantidad > r.stock_minimo) AS en_stock " +
                "FROM repuesto r JOIN categoria c ON c.id_categoria = r.id_categoria " +
                "WHERE r.estado = 'Activo' GROUP BY c.nombre ORDER BY c.nombre";
        try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("categoria", rs.getString("categoria"));
                fila.put("sinStock", rs.getInt("sin_stock"));
                fila.put("bajoStock", rs.getInt("bajo_stock"));
                fila.put("enStock", rs.getInt("en_stock"));
                lista.add(fila);
            }
        }
        return lista;
    }

    private List<Map<String, Object>> obtenerOrdenesPorEstado(Connection cn, LocalDate desde, LocalDate hasta) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        // orden_servicio.estado es varchar directo (sin FK a estado_servicio,
        // esa tabla no está relacionada) — se agrupa tal cual.
        String sql = "SELECT os.estado AS estado, COUNT(*) AS total " +
                "FROM orden_servicio os " +
                "WHERE COALESCE(os.fecha_servicio, os.fecha_registro::date) BETWEEN ? AND ? GROUP BY os.estado ORDER BY os.estado";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("estado", rs.getString("estado"));
                    fila.put("total", rs.getInt("total"));
                    lista.add(fila);
                }
            }
        }
        return lista;
    }

    private List<Map<String, Object>> obtenerVentasVServicios(Connection cn, LocalDate desde, LocalDate hasta, String granularidad) throws Exception {
        Map<String, Double> ventasPorPeriodo = new LinkedHashMap<>();
        String sqlVentas = "SELECT " + periodoSelect("fecha_emision", granularidad) + " AS periodo, SUM(precio_total) AS total " +
                "FROM orden_venta WHERE fecha_emision::date BETWEEN ? AND ? AND estado <> 'Anulado' GROUP BY 1";
        try (PreparedStatement ps = cn.prepareStatement(sqlVentas)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ventasPorPeriodo.put(rs.getString("periodo"), rs.getDouble("total"));
            }
        }

        // orden_servicio no tiene columna de hora confiable para agrupar por
        // hora (solo "hora" de la cita), así que para granularidad "hour" se
        // usa fecha_registro (timestamp) cuando fecha_servicio es nulo, igual
        // que el resto del archivo ya hace vía COALESCE.
        Map<String, Double> serviciosPorPeriodo = new LinkedHashMap<>();
        String columnaServicio = "hour".equals(granularidad)
                ? "COALESCE(fecha_registro, fecha_servicio::timestamp)"
                : "COALESCE(fecha_servicio, fecha_registro::date)";
        String sqlServicios = "SELECT " + periodoSelect(columnaServicio, granularidad) + " AS periodo, SUM(precio_total) AS total " +
                "FROM orden_servicio WHERE COALESCE(fecha_servicio, fecha_registro::date) BETWEEN ? AND ? GROUP BY 1";
        try (PreparedStatement ps = cn.prepareStatement(sqlServicios)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) serviciosPorPeriodo.put(rs.getString("periodo"), rs.getDouble("total"));
            }
        }

        return combinarPorPeriodo(desde, hasta, ventasPorPeriodo, serviciosPorPeriodo, "ventas", "servicios", granularidad);
    }

    private List<Map<String, Object>> obtenerComparativoProductos(Connection cn, LocalDate desde, LocalDate hasta, String granularidad) throws Exception {
        Map<String, Double> ingresadosPorPeriodo = new LinkedHashMap<>();
        String sqlIngresados = "SELECT " + periodoSelect("oc.fec_compra", granularidad) + " AS periodo, SUM(dc.num_cantidad) AS total " +
                "FROM det_compra_rep dc JOIN operacion_compra oc ON oc.id_oper_compra = dc.id_oper_compra " +
                "WHERE oc.fec_compra::date BETWEEN ? AND ? GROUP BY 1";
        try (PreparedStatement ps = cn.prepareStatement(sqlIngresados)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ingresadosPorPeriodo.put(rs.getString("periodo"), rs.getDouble("total"));
            }
        }

        Map<String, Double> vendidosPorPeriodo = new LinkedHashMap<>();
        String sqlVendidos = "SELECT " + periodoSelect("ov.fecha_emision", granularidad) + " AS periodo, SUM(dv.cantidad) AS total " +
                "FROM detalle_venta dv JOIN orden_venta ov ON ov.id_orden_venta = dv.id_orden_venta " +
                "WHERE ov.fecha_emision::date BETWEEN ? AND ? AND ov.estado <> 'Anulado' GROUP BY 1";
        try (PreparedStatement ps = cn.prepareStatement(sqlVendidos)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) vendidosPorPeriodo.put(rs.getString("periodo"), rs.getDouble("total"));
            }
        }

        return combinarPorPeriodo(desde, hasta, ingresadosPorPeriodo, vendidosPorPeriodo, "ingresados", "vendidos", granularidad);
    }

    private List<Map<String, Object>> combinarPorPeriodo(LocalDate desde, LocalDate hasta,
                                                         Map<String, Double> a, Map<String, Double> b, String claveA, String claveB, String granularidad) {
        List<Map<String, Object>> lista = new ArrayList<>();
        DateTimeFormatter fmtIso = DateTimeFormatter.ISO_LOCAL_DATE;

        if ("hour".equals(granularidad)) {
            // El rango para "hour" siempre es 1 solo día (ver decisión de
            // granularidad más arriba), así que se generan las 24 horas de
            // "desde" para que la gráfica tenga sus 24 puntos aunque no haya
            // datos en algunas horas (en vez de aparecer solo 1-2 puntos).
            DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");
            java.time.LocalDateTime actual = desde.atStartOfDay();
            java.time.LocalDateTime limite = hasta.atTime(23, 0);
            while (!actual.isAfter(limite)) {
                agregarPeriodo(lista, actual.format(fmtHora), a, b, claveA, claveB);
                actual = actual.plusHours(1);
            }
        } else if ("day".equals(granularidad)) {
            LocalDate actual = desde;
            while (!actual.isAfter(hasta)) {
                agregarPeriodo(lista, actual.format(fmtIso), a, b, claveA, claveB);
                actual = actual.plusDays(1);
            }
        } else if ("week".equals(granularidad)) {
            // Postgres date_trunc('week', ...) trunca al lunes de esa semana
            // (ISO), así que alineamos la generación de claves al lunes para
            // que ambas coincidan exactamente.
            LocalDate actual = desde.with(java.time.DayOfWeek.MONDAY);
            while (!actual.isAfter(hasta)) {
                agregarPeriodo(lista, actual.format(fmtIso), a, b, claveA, claveB);
                actual = actual.plusWeeks(1);
            }
        } else {
            YearMonth actual = YearMonth.from(desde);
            YearMonth limite = YearMonth.from(hasta);
            DateTimeFormatter fmtMes = DateTimeFormatter.ofPattern("yyyy-MM-01");
            while (!actual.isAfter(limite)) {
                agregarPeriodo(lista, actual.atDay(1).format(fmtMes), a, b, claveA, claveB);
                actual = actual.plusMonths(1);
            }
        }
        return lista;
    }

    private void agregarPeriodo(List<Map<String, Object>> lista, String clave,
                                Map<String, Double> a, Map<String, Double> b, String claveA, String claveB) {
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("mes", clave);
        fila.put(claveA, a.getOrDefault(clave, 0.0));
        fila.put(claveB, b.getOrDefault(clave, 0.0));
        lista.add(fila);
    }

    private List<Map<String, Object>> obtenerTopProductos(Connection cn, LocalDate desde, LocalDate hasta) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT r.nombre, SUM(dv.cantidad) AS total_vendido " +
                "FROM detalle_venta dv " +
                "JOIN orden_venta ov ON ov.id_orden_venta = dv.id_orden_venta " +
                "JOIN repuesto r ON r.id_repuesto = dv.id_repuesto " +
                "WHERE ov.fecha_emision::date BETWEEN ? AND ? AND ov.estado <> 'Anulado' " +
                "GROUP BY r.nombre ORDER BY total_vendido DESC LIMIT 5";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("totalVendido", rs.getInt("total_vendido"));
                    lista.add(fila);
                }
            }
        }
        return lista;
    }

    private List<Map<String, Object>> obtenerTopServicios(Connection cn, LocalDate desde, LocalDate hasta) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT sv.nombre, COUNT(*) AS total " +
                "FROM detalle_orden_servicio dos " +
                "JOIN orden_servicio os ON os.id_orden_servicio = dos.id_orden_servicio " +
                "JOIN servicio sv ON sv.id_servicio = dos.id_servicio " +
                "WHERE COALESCE(os.fecha_servicio, os.fecha_registro::date) BETWEEN ? AND ? " +
                "GROUP BY sv.nombre ORDER BY total DESC LIMIT 5";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("total", rs.getInt("total"));
                    lista.add(fila);
                }
            }
        }
        return lista;
    }

    private List<Map<String, Object>> obtenerTopTrabajadores(Connection cn, LocalDate desde, LocalDate hasta) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT trim(tr.nombre || ' ' || tr.apellido_paterno) AS nombre, COUNT(*) AS total " +
                "FROM detalle_orden_servicio dos " +
                "JOIN orden_servicio os ON os.id_orden_servicio = dos.id_orden_servicio " +
                "JOIN trabajador tr ON tr.id_trabajador = dos.id_trabajador " +
                "WHERE COALESCE(os.fecha_servicio, os.fecha_registro::date) BETWEEN ? AND ? " +
                "GROUP BY tr.nombre, tr.apellido_paterno ORDER BY total DESC LIMIT 5";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, desde);
            ps.setObject(2, hasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("nombre", rs.getString("nombre"));
                    fila.put("total", rs.getInt("total"));
                    lista.add(fila);
                }
            }
        }
        return lista;
    }

    private List<Map<String, Object>> obtenerProductosStockCritico(Connection cn) throws Exception {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT nombre, cantidad, stock_minimo FROM repuesto " +
                "WHERE cantidad <= stock_minimo AND estado = 'Activo' ORDER BY cantidad ASC LIMIT 10";
        try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                fila.put("nombre", rs.getString("nombre"));
                fila.put("cantidad", rs.getInt("cantidad"));
                fila.put("stockMinimo", rs.getInt("stock_minimo"));
                lista.add(fila);
            }
        }
        return lista;
    }
}