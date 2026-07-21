package multiservicioRafael.invenatario.repository.Interfaces;

import java.util.List;
import java.util.Map;

public interface VentasDaoInterface {

    Map<String, Object> registrarVenta(String usuarioNombre, String clienteDni, String tipoComprobante, String serie,
                                       String estado, String metodoPago, String fechaEmision, double descuentoGlobal,
                                       String tipoDescuento, String nota, List<Map<String, Object>> detalle);

    Map<String, Object> listarVentas(String busqueda, int pagina, int porPagina);

    Map<String, Object> obtenerComprobanteVenta(int idOrdenVenta);
}