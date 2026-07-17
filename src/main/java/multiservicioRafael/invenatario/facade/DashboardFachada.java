package multiservicioRafael.invenatario.facade;

import multiservicioRafael.invenatario.repository.DashboardDao;
import multiservicioRafael.invenatario.repository.Interfaces.DashboardDaoInterface;

import java.time.LocalDate;
import java.util.Map;

public class DashboardFachada {

    private final DashboardDaoInterface dashboardDao;

    public DashboardFachada() {
        this.dashboardDao = new DashboardDao();
    }

    public Map<String, Object> obtenerEstadisticas(String fechaDesde, String fechaHasta) {
        String desde = (fechaDesde != null && !fechaDesde.isBlank())
                ? fechaDesde : LocalDate.now().withDayOfMonth(1).toString();
        String hasta = (fechaHasta != null && !fechaHasta.isBlank())
                ? fechaHasta : LocalDate.now().toString();
        return dashboardDao.obtenerEstadisticas(desde, hasta);
    }
}