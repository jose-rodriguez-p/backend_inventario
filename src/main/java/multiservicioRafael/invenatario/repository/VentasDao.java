package multiservicioRafael.invenatario.repository;

import multiservicioRafael.invenatario.config.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.repository.Interfaces.VentasDaoInterface;
import tools.jackson.databind.ObjectMapper;

public class VentasDao implements VentasDaoInterface {

    @Override
    public Map<String, Object> registrarVenta(String usuarioNombre, String clienteDni, String tipoComprobante, String serie,
                                              String estado, String metodoPago, String fechaEmision, double descuentoGlobal,
                                              String tipoDescuento, String nota, List<Map<String, Object>> detalle) {
        String sql = "{ ? = call public.fn_registrar_venta(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        String resultado = "error_desconocido";
        Integer idOrdenVenta = null;

        try (Connection conexion = ConexionDB.getInstance().getConnection()) {

            try (CallableStatement cs = conexion.prepareCall(sql)) {
                cs.registerOutParameter(1, java.sql.Types.VARCHAR);
                cs.setString(2, usuarioNombre);
                cs.setString(3, clienteDni);
                cs.setString(4, tipoComprobante);
                cs.setString(5, serie);
                cs.setString(6, estado);
                cs.setString(7, metodoPago);
                cs.setTimestamp(8, java.sql.Timestamp.valueOf(fechaEmision));
                cs.setBigDecimal(9, java.math.BigDecimal.valueOf(descuentoGlobal));
                cs.setString(10, tipoDescuento);
                cs.setString(11, nota);

                String detalleJsonStr = convertirDetalleAJson(detalle);
                org.postgresql.util.PGobject jsonObject = new org.postgresql.util.PGobject();
                jsonObject.setType("jsonb");
                jsonObject.setValue(detalleJsonStr);
                cs.setObject(12, jsonObject);

                cs.execute();
                resultado = cs.getString(1).trim();
            }

            // Si la venta se registró bien, recuperamos el id recién generado con currval()
            // (misma sesión/conexión que usó la función para el nextval del id_orden_venta),
            // y si el usuario tiene una caja abierta, la enlazamos ahí (tabla caja_venta ya existente).
            if ("OK".equals(resultado)) {
                idOrdenVenta = obtenerUltimoIdGenerado(conexion);
                if (idOrdenVenta != null) {
                    vincularVentaConCajaAbierta(conexion, idOrdenVenta, usuarioNombre);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            resultado = "error_backend: " + e.getMessage();
        }

        Map<String, Object> salida = new HashMap<>();
        salida.put("status", resultado);
        salida.put("id_orden_venta", idOrdenVenta);
        return salida;
    }

    private Integer obtenerUltimoIdGenerado(Connection conexion) {
        String sql = "SELECT currval('orden_venta_id_orden_venta_seq')";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void vincularVentaConCajaAbierta(Connection conexion, int idOrdenVenta, String usuarioNombre) {
        String sqlCaja = "SELECT cc.id_cierre_caja FROM public.cierre_caja cc " +
                "JOIN public.usuario u ON u.id_usuario = cc.id_usuario " +
                "WHERE u.usuario = ? AND cc.estado_caja = 'A' " +
                "ORDER BY cc.id_cierre_caja DESC LIMIT 1";
        try (PreparedStatement ps = conexion.prepareStatement(sqlCaja)) {
            ps.setString(1, usuarioNombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idCierreCaja = rs.getInt("id_cierre_caja");
                    String sqlInsert = "INSERT INTO public.caja_venta (id_orden_venta, id_cierre_caja) VALUES (?, ?)";
                    try (PreparedStatement psInsert = conexion.prepareStatement(sqlInsert)) {
                        psInsert.setInt(1, idOrdenVenta);
                        psInsert.setInt(2, idCierreCaja);
                        psInsert.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertirDetalleAJson(List<Map<String, Object>> detalle) {
        if (detalle == null || detalle.isEmpty()) {
            return "[]";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> transformedDetalle = new ArrayList<>();
            for (Map<String, Object> item : detalle) {
                Map<String, Object> transformed = new java.util.HashMap<>();
                Object repuesto = item.get("repuesto");
                String nombreRepuesto = "";
                if (repuesto instanceof Map) {
                    nombreRepuesto = (String) ((Map) repuesto).get("nombre");
                }
                Object cantidadObj = item.get("cantidad");
                int cantidad = (cantidadObj instanceof Number) ? ((Number) cantidadObj).intValue() : 0;

                Object precioUnitObj = item.get("precio_unit");
                double precioUnitario = (precioUnitObj instanceof Number) ? ((Number) precioUnitObj).doubleValue() : 0.0;

                Object descuentoObj = item.get("descuento");
                double descuentoPorcentaje = (descuentoObj instanceof Number) ? ((Number) descuentoObj).doubleValue() : 0.0;

                transformed.put("nombre_repuesto", nombreRepuesto);
                transformed.put("cantidad", cantidad);
                transformed.put("precio_unitario", precioUnitario);
                transformed.put("descuento_porcentaje", descuentoPorcentaje);
                transformedDetalle.add(transformed);
            }
            String json = mapper.writeValueAsString(transformedDetalle);
            System.out.println("Detalle JSON generado: " + json);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    @Override
    public Map<String, Object> listarVentas(String busqueda, int pagina, int porPagina) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_ventas(?, ?, ?)";
        int totalRegistros = 0;

        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            if (busqueda == null || busqueda.isEmpty()) {
                ps.setNull(1, java.sql.Types.VARCHAR);
            } else {
                ps.setString(1, busqueda);
            }
            ps.setInt(2, pagina);
            ps.setInt(3, porPagina);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new java.util.HashMap<>();
                    fila.put("n_orden", rs.getInt("n_orden"));
                    fila.put("productos", rs.getString("productos"));
                    fila.put("fecha", rs.getString("fecha"));
                    fila.put("hora", rs.getString("hora"));
                    fila.put("cliente", rs.getString("cliente"));
                    fila.put("dni", rs.getString("dni"));
                    fila.put("vendedor", rs.getString("vendedor"));
                    fila.put("metodo_pago", rs.getString("metodo_pago"));
                    fila.put("total", rs.getDouble("total"));
                    lista.add(fila);
                }
            }

            String sqlCount = "SELECT COUNT(*) FROM public.orden_venta";
            try (PreparedStatement psCount = conexion.prepareStatement(sqlCount);
                 ResultSet rsCount = psCount.executeQuery()) {
                if (rsCount.next()) {
                    totalRegistros = rsCount.getInt(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> resultado = new java.util.HashMap<>();
        resultado.put("ventas", lista);
        resultado.put("totalRegistros", totalRegistros);
        return resultado;
    }

    @Override
    public Map<String, Object> obtenerComprobanteVenta(int idOrdenVenta) {
        Map<String, Object> comprobante = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();

        String sqlCabecera = "SELECT ov.id_orden_venta, ov.fecha_emision, ov.tipo_comprobante, ov.serie, " +
                "ov.correlativo, ov.estado, ov.metodo_pago, ov.subtotal, ov.descuento_global, ov.igv, " +
                "ov.precio_total, ov.tipo_descuento, ov.nota, " +
                "c.dni, c.nombre AS cliente_nombre, c.apellido_paterno AS cliente_ap, c.apellido_materno AS cliente_am, " +
                "t.nombre AS vendedor_nombre, t.apellido_paterno AS vendedor_ap " +
                "FROM public.orden_venta ov " +
                "JOIN public.cliente c ON c.id_cliente = ov.id_cliente " +
                "LEFT JOIN public.usuario u ON u.id_usuario = ov.id_usuario " +
                "LEFT JOIN public.trabajador t ON t.id_trabajador = u.id_trabajador " +
                "WHERE ov.id_orden_venta = ?";

        String sqlItems = "SELECT dv.cantidad, dv.precio_unitario, dv.descuento_porcentaje, dv.precio_subtotal, " +
                "r.nombre AS nombre_repuesto " +
                "FROM public.detalle_venta dv " +
                "JOIN public.repuesto r ON r.id_repuesto = dv.id_repuesto " +
                "WHERE dv.id_orden_venta = ? " +
                "ORDER BY dv.id_detalle_venta";

        try (Connection conexion = ConexionDB.getInstance().getConnection()) {
            try (PreparedStatement ps = conexion.prepareStatement(sqlCabecera)) {
                ps.setInt(1, idOrdenVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        comprobante.put("id_orden_venta", rs.getInt("id_orden_venta"));
                        comprobante.put("fecha_emision", rs.getTimestamp("fecha_emision").toString());
                        comprobante.put("tipo_comprobante", rs.getString("tipo_comprobante"));
                        comprobante.put("serie", rs.getString("serie"));
                        comprobante.put("correlativo", rs.getString("correlativo"));
                        comprobante.put("estado", rs.getString("estado"));
                        comprobante.put("metodo_pago", rs.getString("metodo_pago"));
                        comprobante.put("subtotal", rs.getDouble("subtotal"));
                        comprobante.put("descuento_global", rs.getDouble("descuento_global"));
                        comprobante.put("igv", rs.getDouble("igv"));
                        comprobante.put("precio_total", rs.getDouble("precio_total"));
                        comprobante.put("tipo_descuento", rs.getString("tipo_descuento"));
                        comprobante.put("nota", rs.getString("nota"));
                        comprobante.put("dni", rs.getString("dni"));
                        String cliente = (rs.getString("cliente_nombre") + " " +
                                (rs.getString("cliente_ap") != null ? rs.getString("cliente_ap") : "") + " " +
                                (rs.getString("cliente_am") != null ? rs.getString("cliente_am") : "")).trim();
                        comprobante.put("cliente", cliente);
                        String vendedor = ((rs.getString("vendedor_nombre") != null ? rs.getString("vendedor_nombre") : "") + " " +
                                (rs.getString("vendedor_ap") != null ? rs.getString("vendedor_ap") : "")).trim();
                        comprobante.put("vendedor", vendedor.isEmpty() ? "-" : vendedor);
                    }
                }
            }
            try (PreparedStatement ps = conexion.prepareStatement(sqlItems)) {
                ps.setInt(1, idOrdenVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("nombre_repuesto", rs.getString("nombre_repuesto"));
                        item.put("cantidad", rs.getInt("cantidad"));
                        item.put("precio_unitario", rs.getDouble("precio_unitario"));
                        item.put("descuento_porcentaje", rs.getDouble("descuento_porcentaje"));
                        item.put("precio_subtotal", rs.getDouble("precio_subtotal"));
                        items.add(item);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        comprobante.put("items", items);
        return comprobante;
    }
}