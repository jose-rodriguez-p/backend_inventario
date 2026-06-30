package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;



import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashboardFachada {
    public Map<String, Object> obtenerEstadisticasDashboard(int totalClientes, int totalProductos) {
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalVentas", 25800.75);
        estadisticas.put("totalClientes", totalClientes);
        estadisticas.put("totalProductos", totalProductos);
        estadisticas.put("productosBajoStock", 4);
        estadisticas.put("ventasMensuales", Map.of(
                "labels", List.of("Ene", "Feb", "Mar", "Abr", "May", "Jun"),
                "data", List.of(4500, 5200, 3800, 6100, 5900, 7200)
        ));
        return estadisticas;
    }
}
