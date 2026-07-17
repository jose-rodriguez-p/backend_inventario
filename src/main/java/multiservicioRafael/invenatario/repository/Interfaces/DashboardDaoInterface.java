package multiservicioRafael.invenatario.repository.Interfaces;

import java.util.Map;

public interface DashboardDaoInterface {
    Map<String, Object> obtenerEstadisticas(String fechaDesde, String fechaHasta);
}