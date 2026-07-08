package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
        int offset = (pagina - 1) * porPagina;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.id_orden_servicio, o.fecha_creacion, o.nombre_cliente, o.dni_cliente, ");
        sql.append("o.descripcion_vehiculo, o.precio_mano_obra, o.precio_total, ");
        sql.append("CASE WHEN o.id_estado = 1 THEN 'Pendiente' WHEN o.id_estado = 2 THEN 'En Proceso' WHEN o.id_estado = 3 THEN 'Completado' ELSE 'Pendiente' END AS estado ");
        sql.append("FROM orden_servicio o ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append("AND (o.dni_cliente ILIKE ? OR o.nombre_cliente ILIKE ? OR o.descripcion_vehiculo ILIKE ?) ");
            String like = "%" + busqueda.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        sql.append("ORDER BY o.fecha_creacion DESC LIMIT ? OFFSET ?");
        params.add(porPagina);
        params.add(offset);

        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("idOrdenServicio", rs.getInt("id_orden_servicio"));
                    fila.put("hora", rs.getString("fecha_creacion") != null ? rs.getString("fecha_creacion") : "");
                    fila.put("cliente", rs.getString("nombre_cliente"));
                    fila.put("dniCliente", rs.getString("dni_cliente"));
                    fila.put("descripcionVehiculo", rs.getString("descripcion_vehiculo"));
                    fila.put("precioManoObra", rs.getDouble("precio_mano_obra"));
                    fila.put("precioTotal", rs.getDouble("precio_total"));
                    fila.put("estado", rs.getString("estado"));
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
        sql.append("SELECT COUNT(*) FROM orden_servicio o WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append("AND (o.dni_cliente ILIKE ? OR o.nombre_cliente ILIKE ? OR o.descripcion_vehiculo ILIKE ?) ");
            String like = "%" + busqueda.trim() + "%";
            params.add(like); params.add(like); params.add(like);
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
        String sql = "SELECT * FROM public.fn_listar_trabajadores_completo()";
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
}
