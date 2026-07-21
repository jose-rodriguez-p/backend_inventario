package multiservicioRafael.invenatario.repository.Interfaces;

import java.util.List;
import java.util.Map;

public interface MantenimientoDaoInterface {

    Map<String, Object> registrarOrden(
            String dniCliente, String placa, String estado, String fecha,
            String nota, double precioManoObra, String usuarioLogueado,
            List<Map<String, Object>> items);

    List<Map<String, Object>> listarOrdenes(String busqueda);

    Map<String, Object> obtenerComprobanteMantenimiento(int idOrdenServicio);

    Map<String, Object> editarEstadoOrden(String usuarioNombre, int idOrdenServicio, String nuevoEstado);
}
