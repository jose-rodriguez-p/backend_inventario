package multiservicioRafael.invenatario.repository.Interfaces;

import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.modal.Servicio;

public interface ServicioDaoInterface {

    List<Servicio> listarServicios();

    List<Map<String, Object>> listarServiciosConRepuestos();

    List<Map<String, Object>> listarTodosRepuestos();

    String crearServicio(String usuario, String nombre, String estado, List<Map<String, Object>> repuestos);

    String editarServicio(String usuario, int idServicio, String nombre, String estado, List<Map<String, Object>> repuestos);
}
