package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


// Capa REST (Spring Boot): solo puente entre Angular y la clase Sistema (Facade).
 
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class ControladorLogin {

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginRequest credenciales) {
        if (credenciales == null
                || credenciales.getUsername() == null
                || credenciales.getUsername().isBlank()
                || credenciales.getPassword() == null
                || credenciales.getPassword().isBlank()) {
            return null;
        }

        return Sistema.getInstancia().procesarLogin(
                credenciales.getUsername().trim(),
                credenciales.getPassword());
    }

    @GetMapping("/validar-usuario/{username}")
    public ResponseEntity<String> validarUsuario(@PathVariable String username) {
        String resultado = Sistema.getInstancia().validarUsuarioExistente(username.trim());
        if ("ERROR".equalsIgnoreCase(resultado)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR");
        }
        return ResponseEntity.ok("USUARIO_EXISTE");
    }

    @PostMapping("/enviar-codigo")
    public String enviarCodigo(@RequestBody LoginRequest solicitud) {
        if (solicitud == null || solicitud.getUsername() == null || solicitud.getUsername().isBlank()) {
            return "ERROR";
        }

        String usuario = solicitud.getUsername().trim();
        String correo = Sistema.getInstancia().validarUsuarioExistente(usuario);

        if ("ERROR".equalsIgnoreCase(correo)) {
            return "ERROR";
        }

        return Sistema.getInstancia().enviarCodigoVerificacion(usuario, correo);
    }

    @PostMapping("/validar-codigo")
    public String validarCodigo(@RequestBody CodigoVerificacionRequest solicitud) {
        if (solicitud == null
                || solicitud.getUsername() == null
                || solicitud.getUsername().isBlank()
                || solicitud.getCodigo() == null) {
            return "CODIGO_INVALIDO";
        }

        return Sistema.getInstancia().validarCodigoIngresado(
                solicitud.getUsername().trim(),
                solicitud.getCodigo());
    }

    @PostMapping("/actualizar-password")
    public String actualizarPassword(@RequestBody ActualizarPasswordRequest solicitud) {
        if (solicitud == null
                || solicitud.getUsername() == null
                || solicitud.getUsername().isBlank()
                || solicitud.getNewPassword() == null
                || solicitud.getNewPassword().isBlank()) {
            return "ERROR";
        }

        boolean actualizado = Sistema.getInstancia().actualizarContrasena(
                solicitud.getUsername().trim(),
                solicitud.getNewPassword());

        return actualizado ? "PASSWORD_ACTUALIZADA" : "ERROR";
    }
}
