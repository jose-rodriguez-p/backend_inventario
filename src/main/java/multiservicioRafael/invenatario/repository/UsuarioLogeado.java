package multiservicioRafael.invenatario.repository;

/**
 * Clase estática para gestionar el usuario logueado usando ThreadLocal.
 * Garantiza que cada petición web (que corre en su propio hilo) tenga su
 * propio estado de usuario aislado, evitando fallos de concurrencia.
 */
public class UsuarioLogeado {

    private static final ThreadLocal<String> usuarioThreadLocal = new ThreadLocal<>();

    private UsuarioLogeado() {
        // Constructor privado para evitar instancias
    }

    public static String getUsuario() {
        return usuarioThreadLocal.get();
    }

    public static void setUsuario(String usuario) {
        usuarioThreadLocal.set(usuario);
    }

    public static void limpiar() {
        usuarioThreadLocal.remove();
    }
}
