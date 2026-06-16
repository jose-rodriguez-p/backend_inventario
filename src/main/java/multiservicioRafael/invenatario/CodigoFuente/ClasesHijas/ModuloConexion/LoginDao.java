package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.Interfaces.LoginDaoInterface;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Usuario;

public class LoginDao implements LoginDaoInterface {

    @Override
    public Usuario validando(String usuario_1, String password) {
        String sql = "SELECT * FROM public.fn_login(?, ?)";
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement cs = conexion.prepareStatement(sql)) {
            cs.setString(1, usuario_1);
            cs.setString(2, password);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String user = rs.getString("usuario_nombre");
                    if ("ERROR".equalsIgnoreCase(user)) {
                        return null;
                    }
                    String trabajadorCompleto = rs.getString("trabajador_completo");
                    String[] partes = trabajadorCompleto.split(", ");
                    String rolNombre = rs.getString("rol_nombre");
                    java.sql.Array arraySql = rs.getArray("lista_menues");
                    String[] menus = (arraySql != null) ? (String[]) arraySql.getArray() : new String[0];
                    String nombre = "";
                    String apellidoPat = "";
                    String apellidoMat = "";
                    if (partes.length >= 3) {
                        nombre = partes[0];
                        apellidoPat = partes[1];
                        apellidoMat = partes[2];
                        System.out.println("Nombre por separado: " + nombre);
                    }
                    return new Usuario(user, rolNombre, menus, nombre, apellidoMat, apellidoPat);
                }
            }
        } catch (Exception e) {
            System.out.println("Error en validando: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String recuperar_contrasena(String usuario) {
        String sql = "SELECT * FROM public.fn_obtener_correo_usuario(?)";
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement cs = conexion.prepareStatement(sql)) {           
            cs.setString(1, usuario);            
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    String estado = rs.getString("estado");
                    String correo = rs.getString("correo");
                    if ("OK".equalsIgnoreCase(estado)) {
                        return correo;
                    }
                    return "ERROR";
                }
            }
        } catch (Exception e) {
            System.out.println("Error en recuperar_contrasena: " + e.getMessage());
        }
        return "ERROR";
    }

    @Override
    public String actualizarcontraseña(String usuario, String contrasena) {
        String sql = "SELECT fn_actualizar_contrasena(?, ?)";       
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement cs = conexion.prepareStatement(sql)) {           
            cs.setString(1, usuario);
            cs.setString(2, contrasena);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1); 
                }
            }
        } catch (Exception e) {
            System.out.println("Error en actualizarcontraseña: " + e.getMessage());
        }
        return "ERROR";
    }

}
