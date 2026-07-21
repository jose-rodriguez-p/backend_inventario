package multiservicioRafael.invenatario.facade;

import multiservicioRafael.invenatario.modal.Usuario;
import multiservicioRafael.invenatario.repository.LoginDao;
import multiservicioRafael.invenatario.repository.Interfaces.LoginDaoInterface;
import multiservicioRafael.invenatario.repository.UsuarioLogeado;
import multiservicioRafael.invenatario.service.consultasApi.ServicioCorreo;
import multiservicioRafael.invenatario.service.Patrones.GeneradorCodigo;
import multiservicioRafael.invenatario.service.Patrones.RegistroCodigosVerificacion;

public class AutenticacionFachada {

    private final LoginDaoInterface loginDao;

    public AutenticacionFachada() {
        this.loginDao = new LoginDao();
    }
    public Usuario procesarLogin(String usuario, String contrasena) {
        Usuario usuarioLogueado = loginDao.validando(usuario, contrasena);
        if (usuarioLogueado != null) {
            UsuarioLogeado.setUsuario(usuario);
        }
        return usuarioLogueado;
    }
    public boolean verificarContrasena(String username, String contrasenaActual) {
        Usuario usuario = loginDao.validando(username, contrasenaActual);
        return usuario != null;
    }
    public String validarUsuarioExistente(String usuario) {
        String resultado = loginDao.recuperar_contrasena(usuario);
        System.out.println("usuario es: " + resultado);
        if (resultado == null || "ERROR".equalsIgnoreCase(resultado)) {
            return "ERROR";
        }
        return resultado;
    }
    public String enviarCodigoVerificacion(String usuario, String correo) {
        String codigo = GeneradorCodigo.generarSeisDigitos();
        RegistroCodigosVerificacion.getInstancia().guardar(usuario, codigo);

        boolean enviado = ServicioCorreo.getInstancia().enviarCodigoVerificacion(correo, codigo);
        if (!enviado) {
            RegistroCodigosVerificacion.getInstancia().eliminar(usuario);
            return "ERROR";
        }
        return "CODIGO_ENVIADO";
    }
    public String validarCodigoIngresado(String usuario, String codigo) {
        if (codigo == null || codigo.trim().isBlank()) {
            return "CODIGO_INVALIDO";
        }
        boolean valido = RegistroCodigosVerificacion.getInstancia().validar(usuario, codigo.trim());
        return valido ? "CODIGO_VALIDO" : "CODIGO_INVALIDO";
    }
    public boolean actualizarContrasena(String usuario, String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            return false;
        }
        RegistroCodigosVerificacion.getInstancia().eliminar(usuario);
        String resultado = loginDao.actualizarcontraseña(usuario, nuevaContrasena);
        return !"ERROR".equalsIgnoreCase(resultado);
    }
    public boolean restablecerContrasenaUsuario(String usuario) {
        try {
            if (usuario == null || usuario.trim().isEmpty()) {
                return false;
            }
            String resultado = loginDao.resetearContrasena(usuario.trim());

            if ("PASSWORD_RESETEADA".equals(resultado)) {
                return true;
            } else {
                System.out.println("No se pudo resetear la contraseña. Motivo: " + resultado);
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error en AutenticacionFachada al restablecer contraseña: " + e.getMessage());
            return false;
        }
    }
}
