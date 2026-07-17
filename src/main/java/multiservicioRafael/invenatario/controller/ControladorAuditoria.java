package multiservicioRafael.invenatario.controller;

import java.util.Map;
import multiservicioRafael.invenatario.facade.AuditoriaFachada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auditoria")
public class ControladorAuditoria {

    private final AuditoriaFachada auditoriaFachada = new AuditoriaFachada();

    @GetMapping("/actividad")
    public ResponseEntity<Map<String, Object>> obtenerActividad(
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String tipoAccion,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamanoPagina) {
        try {
            Map<String, Object> resultado = auditoriaFachada.obtenerActividad(
                    fechaDesde, fechaHasta, usuario, tabla, tipoAccion, pagina, tamanoPagina);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of());
        }
    }

    @GetMapping("/filtros")
    public ResponseEntity<Map<String, Object>> obtenerFiltros() {
        try {
            return ResponseEntity.ok(auditoriaFachada.obtenerFiltrosDisponibles());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of());
        }
    }
}