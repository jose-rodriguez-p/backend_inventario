package multiservicioRafael.invenatario.repository;

import multiservicioRafael.invenatario.config.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.modal.Servicio;
import multiservicioRafael.invenatario.repository.Interfaces.ServicioDaoInterface;

public class ServicioDao implements ServicioDaoInterface {

    @Override
    public List<Servicio> listarServicios() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT id_servicio, nombre, estado FROM servicio WHERE estado = 'Activo' ORDER BY nombre";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getString("nombre"),
                    0,
                    rs.getString("estado")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error listarServicios: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarServiciosConRepuestos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_servicio, nombre, estado FROM servicio ORDER BY nombre";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                int idServicio = rs.getInt("id_servicio");
                fila.put("id_servicio", idServicio);
                fila.put("nombre", rs.getString("nombre"));
                fila.put("estado", rs.getString("estado"));
                fila.put("repuestos", listarRepuestosPorServicio(idServicio));
                lista.add(fila);
            }
        } catch (Exception e) {
            System.out.println("Error listarServiciosConRepuestos: " + e.getMessage());
        }
        return lista;
    }

    private List<Map<String, Object>> listarRepuestosPorServicio(int idServicio) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT sr.id_repuesto, r.nombre AS nombre_repuesto, sr.cantidad "
                   + "FROM servicio_repuesto sr "
                   + "JOIN repuesto r ON sr.id_repuesto = r.id_repuesto "
                   + "WHERE sr.id_servicio = ? ORDER BY r.nombre";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("id_repuesto", rs.getInt("id_repuesto"));
                    fila.put("nombre_repuesto", rs.getString("nombre_repuesto"));
                    fila.put("cantidad", rs.getInt("cantidad"));
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            System.out.println("Error listarRepuestosPorServicio: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Map<String, Object>> listarTodosRepuestos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_repuestos()";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("nombre_repuesto", rs.getString("nombre_repuesto"));
                fila.put("nombre_categoria", rs.getString("nombre_categoria"));
                fila.put("nombre_marca", rs.getString("nombre_marca"));
                fila.put("precio_venta", rs.getDouble("precio_venta"));
                fila.put("estado", rs.getString("estado"));
                lista.add(fila);
            }
        } catch (Exception e) {
            System.out.println("Error listarTodosRepuestos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public String crearServicio(String usuario, String nombre, String estado, List<Map<String, Object>> repuestos) {
        String sql = "SELECT public.fn_agregar_servicio(?, ?, ?, ?::JSON)";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < repuestos.size(); i++) {
                Map<String, Object> r = repuestos.get(i);
                if (i > 0) json.append(",");
                String nom = (String) r.get("nombre_repuesto");
                json.append("{\"nombre\":\"").append(escapeJson(nom))
                    .append("\",\"cantidad\":").append(r.get("cantidad")).append("}");
            }
            json.append("]");

            ps.setString(1, usuario);
            ps.setString(2, nombre);
            ps.setString(3, estado);
            ps.setString(4, json.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String result = rs.getString(1);
                    if ("OK".equals(result)) return "OK";
                    return "ERROR: " + result;
                }
            }
        } catch (Exception e) {
            System.out.println("Error crearServicio: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
        return "ERROR";
    }

    @Override
    public String editarServicio(String usuario, int idServicio, String nombre, String estado, List<Map<String, Object>> repuestos) {
        Connection cn = null;
        try {
            cn = ConexionDB.getInstance().getConnection();
            cn.setAutoCommit(false);

            String sqlServicio = "UPDATE servicio SET nombre = ?, estado = ? WHERE id_servicio = ?";
            try (PreparedStatement ps = cn.prepareStatement(sqlServicio)) {
                ps.setString(1, nombre);
                ps.setString(2, estado);
                ps.setInt(3, idServicio);
                int filas = ps.executeUpdate();
                if (filas == 0) {
                    cn.rollback();
                    return "ERROR: Servicio no encontrado";
                }
            }

            String sqlDelete = "DELETE FROM servicio_repuesto WHERE id_servicio = ?";
            try (PreparedStatement ps = cn.prepareStatement(sqlDelete)) {
                ps.setInt(1, idServicio);
                ps.executeUpdate();
            }

            String sqlFindRepuesto = "SELECT id_repuesto FROM repuesto WHERE nombre = ? AND estado = 'Activo'";
            String sqlInsert = "INSERT INTO servicio_repuesto (id_servicio, id_repuesto, cantidad) VALUES (?, ?, ?)";

            try (PreparedStatement psFind = cn.prepareStatement(sqlFindRepuesto);
                 PreparedStatement psIns = cn.prepareStatement(sqlInsert)) {
                for (Map<String, Object> r : repuestos) {
                    String nomRep = (String) r.get("nombre_repuesto");
                    psFind.setString(1, nomRep);
                    try (ResultSet rs = psFind.executeQuery()) {
                        if (rs.next()) {
                            int idRepuesto = rs.getInt("id_repuesto");
                            psIns.setInt(1, idServicio);
                            psIns.setInt(2, idRepuesto);
                            psIns.setInt(3, ((Number) r.get("cantidad")).intValue());
                            psIns.addBatch();
                        }
                    }
                }
                psIns.executeBatch();
            }

            cn.commit();
            return "OK";
        } catch (Exception e) {
            if (cn != null) {
                try { cn.rollback(); } catch (Exception ex) {}
            }
            System.out.println("Error editarServicio: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); cn.close(); } catch (Exception ex) {}
            }
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
