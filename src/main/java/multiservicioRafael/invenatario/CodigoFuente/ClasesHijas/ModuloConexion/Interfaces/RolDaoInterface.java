package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.Interfaces;

import java.util.ArrayList;
import java.util.Map;

public interface RolDaoInterface {

    ArrayList<Map<String, Object>> listarRolesComoArreglos();

    boolean actualizarRolCompleto(String nombreRol, String estado, String usuarioOperador, ArrayList<String> menus);

    boolean agregarRol(String usuario, Map<String, Object> nuevoRol);
}
