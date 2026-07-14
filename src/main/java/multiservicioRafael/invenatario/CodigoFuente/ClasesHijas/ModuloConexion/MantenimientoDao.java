package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MantenimientoDao {

    public Map<String, Object> registrarOrden(
            String dniCliente, String nombreCliente, String descripcionVehiculo,
            double precioManoObra, double precioTotal, int idEstado, String nota,
            String usuarioLogueado, List<Map<String, Object>> items) {

        Map<String, Object> resultado = new HashMap<>();
        Connection cn = null;
        try {
            cn = ConexionDB.getInstance().getConnection();
            cn.setAutoCommit(false);

            String sqlOrden = "INSERT INTO orden_servicio "
                + "(dni_cliente, nombre_cliente, descripcion_vehiculo, precio_mano_obra, precio_total, id_estado, nota, usuario_logueado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id_orden_servicio";

            int idOrden;
            try (PreparedStatement ps = cn.prepareStatement(sqlOrden)) {
                ps.setString(1, dniCliente);
                ps.setString(2, nombreCliente);
                ps.setString(3, descripcionVehiculo);
                ps.setDouble(4, precioManoObra);
                ps.setDouble(5, precioTotal);
                ps.setInt(6, idEstado);
                ps.setString(7, nota != null ? nota : "");
                ps.setString(8, usuarioLogueado);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idOrden = rs.getInt(1);
                }
            }

            String sqlDetalle = "INSERT INTO orden_servicio_detalle "
                + "(id_orden_servicio, id_servicio, id_trabajador, cantidad, precio_subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                for (Map<String, Object> item : items) {
                    ps.setInt(1, idOrden);
                    ps.setInt(2, (int) item.get("id_servicio"));
                    ps.setInt(3, (int) item.get("id_trabajador"));
                    ps.setInt(4, (int) item.get("cantidad"));
                    ps.setDouble(5, (double) item.get("precio_subtotal"));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            cn.commit();
            resultado.put("id_orden_servicio", idOrden);
            resultado.put("status", "OK");
        } catch (Exception e) {
            if (cn != null) {
                try { cn.rollback(); } catch (Exception ex) {}
            }
            System.out.println("Error registrarOrden: " + e.getMessage());
            resultado.put("status", "ERROR");
            resultado.put("mensaje", e.getMessage());
        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (Exception ex) {}
            }
        }
        return resultado;
    }

    public List<Map<String, Object>> listarOrdenes(String busqueda, int pagina, int porPagina) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_orden_servicio, hora::TEXT, fecha::TEXT, cliente, dni, vehiculo, "
                   + "mano_obra, total, estado, tecnicos "
                   + "FROM public.fn_listar_ordenes_mantenimiento(?) LIMIT ? OFFSET ?";

        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, busqueda);
            ps.setInt(2, porPagina);
            ps.setInt(3, (pagina - 1) * porPagina);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("idOrdenServicio", rs.getInt("id_orden_servicio"));
                    String horaRaw = rs.getString("hora");
                    String fechaRaw = rs.getString("fecha");
                    String hora = (horaRaw != null && horaRaw.length() >= 5) ? horaRaw.substring(0, 5) : "";
                    String fecha = (fechaRaw != null && fechaRaw.length() >= 10) ? fechaRaw.substring(0, 10) : "";
                    fila.put("hora", fecha + " " + hora);
                    fila.put("fecha", fecha);
                    fila.put("cliente", rs.getString("cliente"));
                    fila.put("dniCliente", rs.getString("dni"));
                    fila.put("descripcionVehiculo", rs.getString("vehiculo"));
                    fila.put("precioManoObra", rs.getDouble("mano_obra"));
                    fila.put("precioTotal", rs.getDouble("total"));
                    fila.put("estado", rs.getString("estado"));
                    fila.put("tecnicos", rs.getString("tecnicos"));
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            System.out.println("Error listarOrdenes: " + e.getMessage());
        }
        return lista;
    }

    public int contarOrdenes(String busqueda) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM orden_servicio os ");
        sql.append("JOIN cliente c ON c.id_cliente = os.id_cliente ");
        sql.append("JOIN carro ca ON ca.id_carro = os.id_carro WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append("AND (c.nombre ILIKE ? OR c.apellido_paterno ILIKE ? OR c.dni ILIKE ? ");
            sql.append("OR ca.placa ILIKE ? OR os.id_orden_servicio::TEXT ILIKE ?) ");
            String like = "%" + busqueda.trim() + "%";
            for (int i = 0; i < 5; i++) params.add(like);
        }

        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error contarOrdenes: " + e.getMessage());
        }
        return 0;
    }

    public Map<String, Object> obtenerResumen() {
        Map<String, Object> resumen = new HashMap<>();
        String sql = "SELECT COUNT(*) AS total, COALESCE(SUM(precio_total), 0) AS monto, "
                   + "CASE WHEN COUNT(*) > 0 THEN COALESCE(SUM(precio_total), 0) / COUNT(*) ELSE 0 END AS promedio "
                   + "FROM orden_servicio";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                resumen.put("totalOrdenes", rs.getInt("total"));
                resumen.put("montoTotal", rs.getDouble("monto"));
                resumen.put("ticketPromedio", rs.getDouble("promedio"));
            }
        } catch (Exception e) {
            System.out.println("Error obtenerResumen: " + e.getMessage());
        }
        return resumen;
    }

    public List<Map<String, Object>> listarTecnicos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT t.id_trabajador, f.nombre, f.apellido_paterno, f.apellido_materno, f.cargo "
                   + "FROM public.fn_listar_trabajadores_completo() f "
                   + "JOIN trabajador t ON t.nro_documento = f.nro_documento "
                   + "WHERE t.estado = 'Activo' ORDER BY f.nombre";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_trabajador", rs.getInt("id_trabajador"));
                String completo = rs.getString("nombre") + " "
                    + rs.getString("apellido_paterno") + " "
                    + (rs.getString("apellido_materno") != null ? rs.getString("apellido_materno") : "");
                fila.put("nombre_completo", completo.trim());
                fila.put("cargo", rs.getString("cargo") != null ? rs.getString("cargo") : "");
                lista.add(fila);
            }
        } catch (Exception e) {
            System.out.println("Error listarTecnicos: " + e.getMessage());
        }
        return lista;
    }

    public Map<String, Object> editarEstadoOrden(String usuarioNombre, int idOrdenServicio, String nuevoEstado) {
        Map<String, Object> resultado = new HashMap<>();
        try (Connection cn = ConexionDB.getInstance().getConnection()) {
            String sql = "SELECT public.fn_editar_estado_orden_mantenimiento(?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, usuarioNombre);
                ps.setInt(2, idOrdenServicio);
                ps.setString(3, nuevoEstado);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String res = rs.getString(1);
                        if (res != null && res.startsWith("OK")) {
                            resultado.put("status", "OK");
                            resultado.put("mensaje", res);
                        } else {
                            resultado.put("status", "ERROR");
                            resultado.put("mensaje", res);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error editarEstadoOrden: " + e.getMessage());
            resultado.put("status", "ERROR");
            resultado.put("mensaje", e.getMessage());
        }
        return resultado;
    }
}
