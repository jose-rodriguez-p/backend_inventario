package multiservicioRafael.invenatario.controller;

import java.util.Map;
import multiservicioRafael.invenatario.facade.DashboardFachada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class ControladorDashboard {

    private final DashboardFachada dashboardFachada = new DashboardFachada();

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas(
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta) {
        try {
            Map<String, Object> resultado = dashboardFachada.obtenerEstadisticas(fechaDesde, fechaHasta);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of());
        }
    }
}