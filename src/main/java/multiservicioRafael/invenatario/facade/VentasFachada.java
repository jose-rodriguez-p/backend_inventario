package multiservicioRafael.invenatario.facade;

import multiservicioRafael.invenatario.repository.VentasDao;
import multiservicioRafael.invenatario.repository.Interfaces.VentasDaoInterface;

import java.util.List;
import java.util.Map;

public class VentasFachada {

    private final VentasDaoInterface ventasDao;

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
