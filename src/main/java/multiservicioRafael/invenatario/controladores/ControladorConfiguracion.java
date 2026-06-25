package multiservicioRafael.invenatario.controladores;

import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
public class ControladorConfiguracion {

    @GetMapping("/roles/listar")
    public ResponseEntity<List<Map<String, Object>>> listarRoles() {

        try {
            List<Map<String, Object>> roles
                    = Sistema.getInstancia().listarRoles();

            if (roles == null || roles.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(roles);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/roles/crear")
    public ResponseEntity<Boolean> crearRol(@RequestBody Map<String, Object> nuevoRol) {

        try {
            boolean guardado
                    = Sistema.getInstancia().agregarRol(nuevoRol);

            if (guardado) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.badRequest().body(false);
            }

        } catch (Exception e) {
            System.out.println("Error en controlador al guardar: " + e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(false);
        }
    }

    // 1. MÉTODO EXCLUSIVO PARA VALIDAR EN TIEMPO REAL
    @PostMapping("/validar-password-actual")
    public ResponseEntity<Boolean> validarPasswordActual(@RequestBody Map<String, String> datos) {
        try {
            String username = datos.get("username");
            String contrasenaActual = datos.get("contrasenaActual");

            if (username == null || contrasenaActual == null) {
                return ResponseEntity.badRequest().body(false);
            }
            boolean esValida = Sistema.getInstancia().verificarContrasena(username, contrasenaActual);

            return ResponseEntity.ok(esValida);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/actualizar-password")
    public ResponseEntity<String> actualizarPassword(@RequestBody Map<String, String> datos) {
        try {
            String username = datos.get("username");
            String newPassword = datos.get("newPassword");
            if (username == null || newPassword == null) {
                return ResponseEntity.badRequest().body("DATOS_INCOMPLETOS");
            }
            boolean actualizado = Sistema.getInstancia().actualizarContrasena(username, newPassword);

            if (actualizado) {
                return ResponseEntity.ok("PASSWORD_ACTUALIZADA");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR_AL_ACTUALIZAR");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }

   
}
