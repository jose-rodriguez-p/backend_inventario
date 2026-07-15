package multiservicioRafael.invenatario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para verificar el estado de salud de la aplicación.
 * Utilizado por servicios de ping externos para mantener la aplicación activa.
 */
@RestController
public class ControladorSalud {

    @GetMapping("/api/health")
    public ResponseEntity<String> verificarSalud() {
        return ResponseEntity.ok("OK");
    }
}
