package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.Interfaces.TrabajadorDaoInterfas;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Trabajador;
public class TrabajadorDao implements TrabajadorDaoInterfas {
    @Override
    public ArrayList<Trabajador> listTrabajador() {
        ArrayList<Trabajador> trabajadores = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_trabajadores_completo()";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement sc = conexion.prepareStatement(sql); ResultSet resultado = sc.executeQuery()) {

            while (resultado.next()) {
                String nroDoc = resultado.getString("nro_documento");
                String nom = resultado.getString("nombre");
                String apePat = resultado.getString("apellido_paterno");
                String apeMat = resultado.getString("apellido_materno");
                String cel = resultado.getString("celular");
                String correo = resultado.getString("correo");
                String direccion = resultado.getString("direccion");
                if (direccion == null) {
                    direccion = "";
                }
                String cargo = resultado.getString("cargo");
                String estado = resultado.getString("estado");
                String fechacompleta = resultado.getString("fecha_registro");
                String soloFecha = "";
                String soloHora = "";

                if (fechacompleta != null && fechacompleta.length() >= 19) {
                    soloFecha = fechacompleta.substring(0, 10); 
                    soloHora = fechacompleta.substring(11, 19);  
                }
                Trabajador t = new Trabajador(
                        nroDoc, 
                        nroDoc, 
                        nom, 
                        apeMat, 
                        apePat,
                        cel, 
                        correo, 
                        direccion, 
                        cargo, 
                        estado, 
                        soloFecha, 
                        soloHora 
                );

                trabajadores.add(t);
            }
        } catch (Exception e) {
            System.err.println("Error en listTrabajador: " + e.getMessage());
            e.printStackTrace();
        }
        return trabajadores;
    }

    @Override
    public boolean existeDni(String dni) {
        String sql = "SELECT 1 FROM trabajador WHERE nro_documento = ? LIMIT 1";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("Error validando DNI: " + e.getMessage());
        }
        return false;
    }

    @Override
    public ArrayList<Map<String, Object>> listarCargos() {
        ArrayList<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_cargo, nombre FROM cargo WHERE estado = 'Activo' ORDER BY nombre";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", rs.getInt("id_cargo"));
                item.put("nombre", rs.getString("nombre"));
                lista.add(item);
            }
        } catch (Exception e) {
            System.err.println("Error listar cargos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public ArrayList<Map<String, Object>> listarDocumentos() {
        ArrayList<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT id_documento, nombre FROM documento WHERE estado = 'Activo' ORDER BY nombre";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", rs.getInt("id_documento"));
                item.put("nombre", rs.getString("nombre"));
                lista.add(item);
            }
        } catch (Exception e) {
            System.err.println("Error listar documentos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public String agregarTranbajador(Trabajador t, String usuario, String contrasena, String usuarioLogueado) {
        String sql = "SELECT public.fn_insertar_trabajador_completo(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getApellido_paterno());
            ps.setString(3, t.getApellido_materno());
            ps.setString(4, t.getCelular());
            ps.setString(5, t.getCorreo());
            ps.setString(6, t.getDireccion());
            ps.setString(7, t.getNumeroDocumento());
            ps.setString(8, t.getDocumento());
            ps.setString(9, t.getCargo());
            ps.setString(10, t.getEstado());
            ps.setString(11, usuarioLogueado);
            if (usuario != null && !usuario.trim().isEmpty() && contrasena != null && !contrasena.trim().isEmpty()) {
                ps.setString(12, usuario.trim());
                ps.setString(13, contrasena);
            } else {
                ps.setNull(12, java.sql.Types.VARCHAR);
                ps.setNull(13, java.sql.Types.VARCHAR);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en agregarTrabajador: " + e.getMessage());
            e.printStackTrace();
            return "ERROR_DB: " + e.getMessage();
        }
        return "ERROR_DESCONOCIDO";
    }

    @Override
    public String editarTrabajador(Trabajador t, String usuarioLogueado, String username, String password) {
        String sql = "SELECT public.fn_editar_trabajador_completo(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, t.getNumeroDocumento());
            ps.setString(2, t.getNombre());
            ps.setString(3, t.getApellido_paterno());
            ps.setString(4, t.getApellido_materno());
            ps.setString(5, t.getCelular());
            ps.setString(6, t.getCorreo());
            ps.setString(7, t.getDireccion());
            ps.setString(8, t.getCargo());
            ps.setString(9, t.getEstado());
            ps.setString(10, usuarioLogueado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String resultado = rs.getString(1);
                    System.out.println("Resultado real desde la BD: [" + resultado + "]");
                    return resultado;
                }
            }
            return "NO_RETORNO";

        } catch (Exception e) {
            System.err.println("Error en editarTrabajador: " + e.getMessage());
            e.printStackTrace();
            return "ERROR_DB: " + e.getMessage();
        }
    }

}
