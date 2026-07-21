package multiservicioRafael.invenatario.facade;

import multiservicioRafael.invenatario.repository.AuditoriaDao;
import multiservicioRafael.invenatario.repository.Interfaces.AuditoriaDaoInterface;

import java.time.LocalDate;
import java.util.Map;

public class AuditoriaFachada {

    private final AuditoriaDaoInterface auditoriaDao;

    public AuditoriaFachada() {
        this.auditoriaDao = new AuditoriaDao();
    }

    public Map<String, Object> obtenerActividad(String fechaDesde, String fechaHasta, String usuario,
                                                String tabla, String tipoAccion, Integer pagina, Integer tamanoPagina) {
        LocalDate desde = (fechaDesde != null && !fechaDesde.isBlank())
                ? LocalDate.parse(fechaDesde) : LocalDate.now().minusDays(29);
        LocalDate hasta = (fechaHasta != null && !fechaHasta.isBlank())
                ? LocalDate.parse(fechaHasta) : LocalDate.now();
        int paginaFinal = (pagina != null && pagina > 0) ? pagina : 1;
        int tamanoFinal = (tamanoPagina != null && tamanoPagina > 0) ? Math.min(tamanoPagina, 200) : 50;
        return auditoriaDao.obtenerActividad(desde, hasta, usuario, tabla, tipoAccion, paginaFinal, tamanoFinal);
    }

    public Map<String, Object> obtenerFiltrosDisponibles() {
        return auditoriaDao.obtenerFiltrosDisponibles();
    }
}