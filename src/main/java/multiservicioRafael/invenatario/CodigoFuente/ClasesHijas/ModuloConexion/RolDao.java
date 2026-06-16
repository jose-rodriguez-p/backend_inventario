package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.Interfaces.RolDaoInterface;

public class RolDao implements RolDaoInterface {

    @Override
    public ArrayList<Map<String, Object>> listarRolesComoArreglos() {
        ArrayList<Map<String, Object>> listaRoles = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_rol()";
        try (Connection con = ConexionDB.getInstance().getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> rolMap = new LinkedHashMap<>();
                rolMap.put("id", rs.getInt("id_cargo_ref"));
                rolMap.put("nombre", rs.getString("nombre_cargo"));
                rolMap.put("estado", rs.getString("estado_cargo"));
                rolMap.put("tieneSistema", rs.getBoolean("tiene_sistema"));
                ArrayList<String> menus = new ArrayList<>();
                java.sql.Array pgArray = rs.getArray("menus");
                if (pgArray != null) {
                    String[] arregloMenus = (String[]) pgArray.getArray();
                    if (arregloMenus != null) {
                        menus.addAll(Arrays.asList(arregloMenus));
                    }
                }
                rolMap.put("menus", menus);

                listaRoles.add(rolMap);
            }
        } catch (Exception e) {
            System.err.println("Error al listar roles: " + e.getMessage());
            e.printStackTrace();
        }
        return listaRoles;
    }

    @Override
    public boolean actualizarRolCompleto(String nombreRol, String estado, String usuarioOperador, ArrayList<String> menus) {
        String sql = "{ ? = call public.fn_actualizar_rol_completo(?, ?, ?, ?) }";

        try (Connection con = ConexionDB.getInstance().getConnection(); CallableStatement cs = con.prepareCall(sql)) {

            cs.registerOutParameter(1, Types.VARCHAR);

            System.out.println("DEBUG - ROL: " + nombreRol);
            cs.setString(2, nombreRol);

            System.out.println("DEBUG - ESTADO: " + estado);
            cs.setString(3, estado);

            System.out.println("DEBUG - USUARIO: " + usuarioOperador);
            cs.setString(4, usuarioOperador);

            if (menus == null) {
                menus = new ArrayList<>();
            }
            System.out.println("DEBUG - MENUS: " + menus);

            java.sql.Array pgArray = con.createArrayOf("VARCHAR", menus.toArray(new String[0]));
            cs.setArray(5, pgArray);

            cs.execute();

            String resultado = cs.getString(1);
            System.out.println("DEBUG - RESULTADO FUNCION: " + resultado);

            return "actualizado".equalsIgnoreCase(resultado);

        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO EN ACTUALIZAR ROL:");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean agregarRol(String usuario, Map<String, Object> nuevoRol) {
        String sql = "{? = call public.fn_insertar_rol_completo(?, ?, ?, ?)}";
        try (Connection con = ConexionDB.getInstance().getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            cs.setString(2, (String) nuevoRol.get("nombre"));
            cs.setString(3, (boolean) nuevoRol.get("activo") ? "Activo" : "Inactivo");
            cs.setString(4, usuario);
            ArrayList<String> menus = (ArrayList<String>) nuevoRol.get("menus");
            java.sql.Array pgArray = con.createArrayOf("VARCHAR", menus.toArray(new String[0]));
            cs.setArray(5, pgArray);
            cs.execute();
            String resultado = cs.getString(1);
            return "insertado".equals(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
