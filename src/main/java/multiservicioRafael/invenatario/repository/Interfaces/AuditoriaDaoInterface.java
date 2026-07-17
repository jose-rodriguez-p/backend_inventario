package multiservicioRafael.invenatario.repository.Interfaces;

import java.time.LocalDate;
import java.util.Map;

public interface AuditoriaDaoInterface {

    Map<String, Object> obtenerActividad(LocalDate desde, LocalDate hasta, String usuario,
                                         String tabla, String tipoAccion, int pagina, int tamanoPagina);

    Map<String, Object> obtenerFiltrosDisponibles();
}