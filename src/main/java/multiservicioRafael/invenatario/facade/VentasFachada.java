package multiservicioRafael.invenatario.facade;

import multiservicioRafael.invenatario.repository.VentasDao;
import multiservicioRafael.invenatario.repository.Interfaces.VentasDaoInterface;
import multiservicioRafael.invenatario.service.ServicioExportacion.ExportadorService;

import java.util.List;
import java.util.Map;

public class VentasFachada {

    private final VentasDaoInterface ventasDao;
    private final ExportadorService exportador;

    public VentasFachada() {
        this.ventasDao = new VentasDao();
        this.exportador = ExportadorService.getInstancia();
    }

    public Map<String, Object> registrarVenta(String usuarioNombre, String clienteDni, String tipoComprobante, String serie,
                                              String estado, String metodoPago, String fechaEmision, double descuentoGlobal,
                                              String tipoDescuento, String nota, List<Map<String, Object>> detalle) {
        return ventasDao.registrarVenta(usuarioNombre, clienteDni, tipoComprobante, serie,
                estado, metodoPago, fechaEmision, descuentoGlobal,
                tipoDescuento, nota, detalle);
    }

    public Map<String, Object> listarVentas(String busqueda, int pagina, int porPagina) {
        return ventasDao.listarVentas(busqueda, pagina, porPagina);
    }

    public Map<String, Object> obtenerComprobanteVenta(int idOrdenVenta) {
        return ventasDao.obtenerComprobanteVenta(idOrdenVenta);
    }

    public byte[] generarComprobanteVentaPDF(Map<String, Object> comprobante) throws Exception {
        return exportador.generarComprobanteVentaPDF(comprobante);
    }

    public byte[] generarPDFBytesVentas(List<Map<String, Object>> ventas) throws Exception {
        String[] headers = {"N° Orden", "Productos", "Fecha", "Hora", "Cliente", "DNI", "Vendedor", "Método Pago", "Total"};
        String[] keys = {"n_orden", "productos", "fecha", "hora", "cliente", "dni", "vendedor", "metodo_pago", "total"};
        float[] pesos = {1.5f, 4f, 2f, 1.5f, 3f, 2f, 2.5f, 2.5f, 2f};
        return exportador.generarPDF("Reporte de Ventas", headers, keys, pesos, ventas);
    }

    public byte[] generarExcelBytesVentas(List<Map<String, Object>> ventas) throws Exception {
        String[] headers = {"N° ORDEN", "PRODUCTOS", "FECHA", "HORA", "CLIENTE", "DNI", "VENDEDOR", "MÉTODO PAGO", "TOTAL"};
        String[] keys = {"n_orden", "productos", "fecha", "hora", "cliente", "dni", "vendedor", "metodo_pago", "total"};
        return exportador.generarExcel("Reporte de Ventas", headers, keys, ventas);
    }
}