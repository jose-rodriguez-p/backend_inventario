package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

/**
 * Clase estática para gestionar el usuario logueado.
 * Evita múltiples instancias y mantiene el estado del usuario actual.
 */
public class UsuarioLogeado {

    private static String usuario;

    private UsuarioLogeado() {
        // Constructor privado para evitar instancias
    }

    public static String getUsuario() {
        return usuario;
    }

    public static void setUsuario(String usuario) {
        UsuarioLogeado.usuario = usuario;
    }

    public static void limpiar() {
        usuario = null;
    }
}
