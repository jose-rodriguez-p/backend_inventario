package multiservicioRafael.invenatario.repository;

import multiservicioRafael.invenatario.config.ConexionDB;
import multiservicioRafael.invenatario.repository.Interfaces.CajaDaoInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO de apertura/cierre de caja. No crea ni modifica funciones ni tablas:
 * usa exclusivamente sentencias SQL directas sobre cierre_caja y caja_venta,
 * que ya existen en el esquema pero no tenían ninguna capa de negocio.
 */
public class CajaDao implements CajaDaoInterface {

    @Override
    public Map<String, Object> obtenerCajaAbierta(String usuarioNombre) {
        String sql = "SELECT cc.id_cierre_caja, cc.fec_apertura, cc.saldo_inicial " +
                "FROM public.cierre_caja cc " +
                "JOIN public.usuario u ON u.id_usuario = cc.id_usuario " +
                "WHERE u.usuario = ? AND cc.estado_caja = 'A' " +
                "ORDER BY cc.id_cierre_caja DESC LIMIT 1";
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuarioNombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> caja = new HashMap<>();
                    caja.put("id_cierre_caja", rs.getInt("id_cierre_caja"));
                    caja.put("fec_apertura", rs.getTimestamp("fec_apertura").toString());
                    caja.put("saldo_inicial", rs.getDouble("saldo_inicial"));
                    return caja;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String abrirCaja(String usuarioNombre, double saldoInicial) {
        try (Connection conexion = ConexionDB.getInstance().getConnection()) {

            if (obtenerCajaAbierta(usuarioNombre) != null) {
                return "error: ya tienes una caja abierta. Ciérrala antes de abrir otra.";
            }

            Integer idUsuario = null;
            try (PreparedStatement psUsr = conexion.prepareStatement(
                    "SELECT id_usuario FROM public.usuario WHERE usuario = ?")) {
                psUsr.setString(1, usuarioNombre);
                try (ResultSet rs = psUsr.executeQuery()) {
                    if (rs.next()) idUsuario = rs.getInt("id_usuario");
                }
            }
            if (idUsuario == null) return "error: usuario no encontrado";

            String sql = "INSERT INTO public.cierre_caja " +
                    "(id_usuario, fec_apertura, saldo_inicial, tot_ventas_sistema, tot_ventas_cajero, estado_caja) " +
                    "VALUES (?, ?, ?, 0, 0, 'A')";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setBigDecimal(3, java.math.BigDecimal.valueOf(saldoInicial));
                ps.executeUpdate();
            }
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "error_backend: " + e.getMessage();
        }
    }

    @Override
    public Map<String, Object> obtenerResumenCierre(int idCierreCaja) {
        Map<String, Object> resumen = new HashMap<>();
        List<Map<String, Object>> ventas = new ArrayList<>();
        double totSistema = 0;

        // Se agrega cc.id_usuario para poder filtrar correctamente los mantenimientos
        // del cajero dueño de esta caja.
        String sqlCabecera = "SELECT cc.id_cierre_caja, cc.id_usuario, cc.fec_apertura, cc.fec_cierre, cc.saldo_inicial, " +
                "cc.tot_ventas_cajero, cc.estado_caja, t.nombre AS nombre_usuario, t.apellido_paterno AS ap_usuario " +
                "FROM public.cierre_caja cc " +
                "JOIN public.usuario u ON u.id_usuario = cc.id_usuario " +
                "JOIN public.trabajador t ON t.id_trabajador = u.id_trabajador " +
                "WHERE cc.id_cierre_caja = ?";

        String sqlVentas = "SELECT ov.id_orden_venta, ov.fecha_emision, ov.metodo_pago, ov.tipo_comprobante, " +
                "ov.serie, ov.correlativo, ov.precio_total, " +
                "COALESCE(c.nombre || ' ' || c.apellido_paterno, 'Cliente Varios') AS cliente " +
                "FROM public.caja_venta cv " +
                "JOIN public.orden_venta ov ON ov.id_orden_venta = cv.id_orden_venta " +
                "JOIN public.cliente c ON c.id_cliente = ov.id_cliente " +
                "WHERE cv.id_cierre_caja = ? AND ov.estado = 'Pagado' " +
                "ORDER BY ov.id_orden_venta";

        try (Connection conexion = ConexionDB.getInstance().getConnection()) {
            Integer idUsuarioCaja = null;
            try (PreparedStatement ps = conexion.prepareStatement(sqlCabecera)) {
                ps.setInt(1, idCierreCaja);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        resumen.put("id_cierre_caja", rs.getInt("id_cierre_caja"));
                        resumen.put("fec_apertura", rs.getTimestamp("fec_apertura").toString());
                        resumen.put("fec_cierre", rs.getTimestamp("fec_cierre") != null ? rs.getTimestamp("fec_cierre").toString() : null);
                        resumen.put("saldo_inicial", rs.getDouble("saldo_inicial"));
                        resumen.put("tot_ventas_cajero", rs.getDouble("tot_ventas_cajero"));
                        resumen.put("estado_caja", rs.getString("estado_caja"));
                        resumen.put("usuario", (rs.getString("nombre_usuario") + " " + rs.getString("ap_usuario")).trim());
                        idUsuarioCaja = rs.getInt("id_usuario");
                    }
                }
            }
            try (PreparedStatement ps = conexion.prepareStatement(sqlVentas)) {
                ps.setInt(1, idCierreCaja);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> v = new HashMap<>();
                        v.put("n_orden", rs.getInt("id_orden_venta"));
                        v.put("fecha_emision", rs.getTimestamp("fecha_emision").toString());
                        v.put("metodo_pago", rs.getString("metodo_pago"));
                        v.put("comprobante", rs.getString("tipo_comprobante") + " " + rs.getString("serie") + "-" + rs.getString("correlativo"));
                        v.put("cliente", rs.getString("cliente"));
                        double total = rs.getDouble("precio_total");
                        v.put("total", total);
                        totSistema += total;
                        ventas.add(v);
                    }
                }
            }

            // --- Mantenimientos COMPLETADOS del mismo cajero, culminados durante la caja abierta ---
            List<Map<String, Object>> mantenimientos = new ArrayList<>();
            double totMantenimientos = 0;

            if (idUsuarioCaja != null) {
                String sqlMant = "SELECT os.id_orden_servicio, os.fecha_culminacion, " +
                        "os.precio_mano_obra, " +
                        "COALESCE(c.nombre || ' ' || c.apellido_paterno, 'Cliente') AS cliente, " +
                        "COALESCE(SUM(dr.precio_total), 0) AS total_repuestos " +
                        "FROM public.orden_servicio os " +
                        "LEFT JOIN public.cliente c ON c.id_cliente = os.id_cliente " +
                        "LEFT JOIN public.detalle_orden_servicio dos ON dos.id_orden_servicio = os.id_orden_servicio " +
                        "LEFT JOIN public.detalle_orden_repuesto dr ON dr.id_det_servicio = dos.id_det_servicio " +
                        "WHERE os.estado = 'Completado' " +
                        "AND os.fecha_culminacion IS NOT NULL " +
                        "AND os.id_usuario = ? " +
                        "AND os.fecha_culminacion >= ? " +
                        "GROUP BY os.id_orden_servicio, os.fecha_culminacion, os.precio_mano_obra, c.nombre, c.apellido_paterno " +
                        "ORDER BY os.id_orden_servicio";

                try (PreparedStatement psMant = conexion.prepareStatement(sqlMant)) {
                    psMant.setInt(1, idUsuarioCaja);
                    String fecAperStr = (String) resumen.get("fec_apertura");
                    psMant.setTimestamp(2, fecAperStr != null
                            ? Timestamp.valueOf(fecAperStr.replace(" ", "T").substring(0, 19))
                            : new Timestamp(System.currentTimeMillis()));
                    try (ResultSet rs = psMant.executeQuery()) {
                        while (rs.next()) {
                            Map<String, Object> m = new HashMap<>();
                            m.put("n_orden", rs.getInt("id_orden_servicio"));
                            m.put("fecha_emision", rs.getTimestamp("fecha_culminacion").toString());
                            m.put("metodo_pago", "Servicio");
                            m.put("comprobante", "Mantenimiento");
                            m.put("cliente", rs.getString("cliente"));
                            double manoObra = rs.getDouble("precio_mano_obra");
                            double totalRepuestos = rs.getDouble("total_repuestos");
                            double total = manoObra + totalRepuestos;
                            m.put("total", total);
                            totMantenimientos += total;
                            mantenimientos.add(m);
                        }
                    }
                } catch (Exception eMant) {
                    eMant.printStackTrace();
                }
            }

            ventas.addAll(mantenimientos);

            resumen.put("ventas", ventas);
            resumen.put("cantidad_ventas", ventas.size() - mantenimientos.size());
            resumen.put("cantidad_mantenimientos", mantenimientos.size());
            resumen.put("tot_ventas_sistema", totSistema + totMantenimientos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resumen;
    }

    @Override
    public String cerrarCaja(int idCierreCaja, double totVentasCajero) {
        Map<String, Object> resumen = obtenerResumenCierre(idCierreCaja);
        if (!"A".equals(resumen.get("estado_caja"))) {
            return "error: esta caja ya está cerrada";
        }
        double totSistema = ((Number) resumen.getOrDefault("tot_ventas_sistema", 0.0)).doubleValue();

        String sql = "UPDATE public.cierre_caja SET fec_cierre = ?, tot_ventas_sistema = ?, " +
                "tot_ventas_cajero = ?, estado_caja = 'C' WHERE id_cierre_caja = ? AND estado_caja = 'A'";
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(totSistema));
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(totVentasCajero));
            ps.setInt(4, idCierreCaja);
            int filas = ps.executeUpdate();
            return filas > 0 ? "OK" : "error: la caja ya estaba cerrada o no existe";
        } catch (Exception e) {
            e.printStackTrace();
            return "error_backend: " + e.getMessage();
        }
    }
}