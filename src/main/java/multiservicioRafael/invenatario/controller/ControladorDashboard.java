package multiservicioRafael.invenatario.controller;

import multiservicioRafael.invenatario.facade.DashboardFachada;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class ControladorDashboard {

    private final DashboardFachada dashboardFachada = new DashboardFachada();

    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {

        return ResponseEntity.ok(
                dashboardFachada.obtenerEstadisticasDashboard(0, 0)
        );
    }
}