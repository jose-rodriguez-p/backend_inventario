package multiservicioRafael.invenatario.CodigoFuente.Fachadas;

import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.MenuDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.RolDao;

import java.util.ArrayList;
import java.util.Map;

public class RolMenuFachada {

    private final RolDao rolDao;
    private final MenuDao menuDao;

    public RolMenuFachada() {
        this.rolDao = new RolDao();
        this.menuDao = new MenuDao();
    }
    public ArrayList<Map<String, Object>> listarRoles() {
        try {
            return rolDao.listarRolesComoArreglos();
        } catch (Exception e) {
            System.err.println("Error en RolMenuFachada.listarRoles: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public boolean actualizarRolYAccesos(String nombre, String estado, ArrayList<String> menus, String usuarioLogueado) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        return rolDao.actualizarRolCompleto(nombre, estado, usuarioLogueado, menus);
    }
    public ArrayList<String> listarMenu() {
        ArrayList<String> menus = menuDao.listarmenus();
        if (menus != null) {
            return menus;
        }
        System.out.println("Los datos están viajando vacíos");
        return null;
    }
    public boolean agregarRol(Map<String, Object> nuevoRol, String usuarioLogueado) {
        if (nuevoRol == null || !nuevoRol.containsKey("nombre")) {
            System.out.println("Error: El objeto nuevoRol es inválido.");
            return false;
        }
        System.out.println("Procesando creación de rol por usuario: " + usuarioLogueado);
        return rolDao.agregarRol(usuarioLogueado, nuevoRol);
    }
}
