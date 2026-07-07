package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Servicio;

public class ServicioDao {

    public List<Servicio> listarServicios() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT id_servicio, nombre, precio, estado FROM servicio WHERE estado = 'Activo' ORDER BY nombre";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getString("estado")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error listarServicios: " + e.getMessage());
        }
        return lista;
    }

    public List<Servicio> listarServiciosCompleto() {
        List<Servicio> lista = new ArrayList<>();
        String sql = "SELECT id_servicio, nombre, precio, estado FROM servicio ORDER BY nombre";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Servicio(
                    rs.getInt("id_servicio"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getString("estado")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error listarServiciosCompleto: " + e.getMessage());
        }
        return lista;
    }

    public String crearServicio(String usuario, String nombre, double precio, String estado) {
        String sql = "INSERT INTO servicio (nombre, precio, estado, usuario_logueado) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setDouble(2, precio);
            ps.setString(3, estado);
            ps.setString(4, usuario);
            ps.executeUpdate();
            return "OK";
        } catch (Exception e) {
            System.out.println("Error crearServicio: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }

    public String editarServicio(String usuario, String nombreOriginal, String nombreNuevo, double precio, String estado) {
        String sql = "UPDATE servicio SET nombre = ?, precio = ?, estado = ?, usuario_logueado = ? WHERE nombre = ?";
        try (Connection cn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreNuevo);
            ps.setDouble(2, precio);
            ps.setString(3, estado);
            ps.setString(4, usuario);
            ps.setString(5, nombreOriginal);
            int filas = ps.executeUpdate();
            return filas > 0 ? "OK" : "ERROR: Servicio no encontrado";
        } catch (Exception e) {
            System.out.println("Error editarServicio: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }
}
