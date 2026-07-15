package multiservicioRafael.invenatario.facade;

import multiservicioRafael.invenatario.repository.ServicioDao;
import multiservicioRafael.invenatario.repository.Interfaces.ServicioDaoInterface;
import multiservicioRafael.invenatario.modal.Servicio;
import java.util.List;
import java.util.Map;

public class ServicioFachada {

    private final ServicioDaoInterface servicioDao;

    public ServicioFachada() {
        this.servicioDao = new ServicioDao();
    }

    public List<Servicio> listarServicios() {
        return servicioDao.listarServicios();
    }

    public List<Map<String, Object>> listarServiciosConRepuestos() {
        return servicioDao.listarServiciosConRepuestos();
    }

    public String crearServicio(String usuario, String nombre, String estado, List<Map<String, Object>> repuestos) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "ERROR: El nombre del servicio es obligatorio.";
        }
        if (repuestos == null || repuestos.isEmpty()) {
            return "ERROR: Debe agregar al menos un repuesto.";
        }
        return servicioDao.crearServicio(usuario, nombre.trim(), estado, repuestos);
    }

    public String editarServicio(String usuario, int idServicio, String nombre, String estado, List<Map<String, Object>> repuestos) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "ERROR: El nombre del servicio es obligatorio.";
        }
        return servicioDao.editarServicio(usuario, idServicio, nombre.trim(), estado, repuestos);
    }

    public List<Map<String, Object>> listarTodosRepuestos() {
        return servicioDao.listarTodosRepuestos();
    }
}
