package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;

import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.MantenimientoDao;
import java.util.List;
import java.util.Map;

public class MantenimientoFachada {

    private final MantenimientoDao mantenimientoDao;

    public MantenimientoFachada() {
        this.mantenimientoDao = new MantenimientoDao();
    }

    public Map<String, Object> registrarOrden(
            String dniCliente, String nombreCliente, String descripcionVehiculo,
            double precioManoObra, double precioTotal, int idEstado, String nota,
            String usuarioLogueado, List<Map<String, Object>> items) {
        return mantenimientoDao.registrarOrden(dniCliente, nombreCliente, descripcionVehiculo,
            precioManoObra, precioTotal, idEstado, nota, usuarioLogueado, items);
    }

    public List<Map<String, Object>> listarOrdenes(String busqueda, int pagina, int porPagina) {
        return mantenimientoDao.listarOrdenes(busqueda, pagina, porPagina);
    }

    public int contarOrdenes(String busqueda) {
        return mantenimientoDao.contarOrdenes(busqueda);
    }

    public Map<String, Object> obtenerResumen() {
        return mantenimientoDao.obtenerResumen();
    }

    public List<Map<String, Object>> listarTecnicos() {
        return mantenimientoDao.listarTecnicos();
    }

    public Map<String, Object> editarEstadoOrden(String usuarioNombre, int idOrdenServicio, String nuevoEstado) {
        return mantenimientoDao.editarEstadoOrden(usuarioNombre, idOrdenServicio, nuevoEstado);
    }
}
