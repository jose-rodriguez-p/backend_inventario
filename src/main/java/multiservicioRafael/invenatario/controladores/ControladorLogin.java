package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class ControladorLogin {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest credenciales) {

        if (credenciales == null
                || credenciales.getUsername() == null
                || credenciales.getUsername().isBlank()
                || credenciales.getPassword() == null
                || credenciales.getPassword().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "CREDENCIALES_INVALIDAS"));
        }

        Usuario usuario = Sistema.getInstancia().procesarLogin(
                credenciales.getUsername().trim(),
                credenciales.getPassword()
        );

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "LOGIN_INCORRECTO"));
        }

        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/validar-usuario/{username}")
    public ResponseEntity<Map<String, String>> validarUsuario(@PathVariable String username) {

        String resultado = Sistema.getInstancia()
                .validarUsuarioExistente(username.trim());

        if ("ERROR".equalsIgnoreCase(resultado)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "ERROR"));
        }

        return ResponseEntity.ok(Map.of("status", "USUARIO_EXISTE"));
    }

    @PostMapping("/enviar-codigo")
    public ResponseEntity<Map<String, String>> enviarCodigo(@RequestBody LoginRequest solicitud) {

        if (solicitud == null || solicitud.getUsername() == null || solicitud.getUsername().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("status", "ERROR"));
        }

        String usuario = solicitud.getUsername().trim();

        String correo = Sistema.getInstancia()
                .validarUsuarioExistente(usuario);

        if ("ERROR".equalsIgnoreCase(correo)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "ERROR"));
        }

        String resultado = Sistema.getInstancia()
                .enviarCodigoVerificacion(usuario, correo);

        return ResponseEntity.ok(Map.of("status", resultado));
    }

    @PostMapping("/validar-codigo")
    public ResponseEntity<Map<String, String>> validarCodigo(@RequestBody CodigoVerificacionRequest solicitud) {

        if (solicitud == null
                || solicitud.getUsername() == null
                || solicitud.getUsername().isBlank()
                || solicitud.getCodigo() == null) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("status", "CODIGO_INVALIDO"));
        }

        String resultado = Sistema.getInstancia().validarCodigoIngresado(
                solicitud.getUsername().trim(),
                solicitud.getCodigo()
        );

        return ResponseEntity.ok(Map.of("status", resultado));
    }

    @PostMapping("/actualizar-password")
    public ResponseEntity<Map<String, String>> actualizarPassword(@RequestBody ActualizarPasswordRequest solicitud) {

        if (solicitud == null
                || solicitud.getUsername() == null
                || solicitud.getUsername().isBlank()
                || solicitud.getNewPassword() == null
                || solicitud.getNewPassword().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of("status", "ERROR"));
        }

        boolean actualizado = Sistema.getInstancia().actualizarContrasena(
                solicitud.getUsername().trim(),
                solicitud.getNewPassword()
        );

        if (actualizado) {
            return ResponseEntity.ok(Map.of("status", "PASSWORD_ACTUALIZADA"));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "ERROR"));
    }

    @PostMapping("/resetear-password")
    public ResponseEntity<String> resetearPassword(@RequestBody Map<String, String> datos) {
        try {
            String username = datos.get("username");
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("DATOS_INCOMPLETOS");
            }
            boolean reseteado = Sistema.getInstancia().restablecerContrasenaUsuario(username);

            if (reseteado) {
                return ResponseEntity.ok("PASSWORD_RESETEADA");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR_AL_RESETEAR");
            }
        } catch (Exception e) {
            System.out.println("Error en controlador resetearPassword: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }

}