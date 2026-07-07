package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;

import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.VentasDao;

import java.util.List;
import java.util.Map;

public class VentasFachada {

    private final VentasDao ventasDao;

    public VentasFachada() {
        this.ventasDao = new VentasDao();
    }

    public String registrarVenta(String usuarioNombre, String clienteDni, String tipoComprobante, String serie,
                                 String estado, String metodoPago, String fechaEmision, double descuentoGlobal,
                                 String tipoDescuento, String nota, List<Map<String, Object>> detalle) {
        return ventasDao.registrarVenta(usuarioNombre, clienteDni, tipoComprobante, serie,
                                        estado, metodoPago, fechaEmision, descuentoGlobal,
                                        tipoDescuento, nota, detalle);
    }

    public Map<String, Object> listarVentas(String busqueda, int pagina, int porPagina) {
        return ventasDao.listarVentas(busqueda, pagina, porPagina);
    }
}
