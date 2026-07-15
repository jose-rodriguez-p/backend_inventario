package multiservicioRafael.invenatario.repository.Interfaces;

import java.util.List;
import java.util.Map;

public interface MantenimientoDaoInterface {

    Map<String, Object> registrarOrden(
            String dniCliente, String nombreCliente, String descripcionVehiculo,
            double precioManoObra, double precioTotal, int idEstado, String nota,
            String usuarioLogueado, List<Map<String, Object>> items);

    List<Map<String, Object>> listarOrdenes(String busqueda);

    Map<String, Object> editarEstadoOrden(String usuarioNombre, int idOrdenServicio, String nuevoEstado);
}
