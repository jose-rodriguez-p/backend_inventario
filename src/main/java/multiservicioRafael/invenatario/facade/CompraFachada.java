package multiservicioRafael.invenatario.facade;

import multiservicioRafael.invenatario.repository.CompraDao;
import multiservicioRafael.invenatario.repository.Interfaces.CompraDaoInterface;
import multiservicioRafael.invenatario.config.ConexionDB;
import multiservicioRafael.invenatario.service.ServicioExportacion.ExportadorService;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class CompraFachada {

    private final CompraDaoInterface compraDao;
    private final ExportadorService exportador;

    public CompraFachada() {
        this.compraDao = new CompraDao();
        this.exportador = ExportadorService.getInstancia();
    }

    public List<Map<String, Object>> listarCompras() {
        return compraDao.listarCompras();
    }

    public List<Map<String, Object>> obtenerDetalle(int idOperCompra) {
        return compraDao.obtenerDetalle(idOperCompra);
    }

    public String registrarCompra(String rucProveedor, List<Map<String, Object>> items) {
        try (Connection conn = ConexionDB.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                String resultado = compraDao.registrarCompra(rucProveedor, items, conn);
                conn.commit();
                return resultado;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "error_backend: " + e.getMessage();
        }
    }

    private List<Integer> extraerIds(List<Map<String, Object>> compras) {
        List<Integer> ids = new java.util.ArrayList<>();
        for (Map<String, Object> c : compras) {
            Object idRaw = c.get("id_oper_compra");
            if (idRaw instanceof Number) {
                ids.add(((Number) idRaw).intValue());
            }
        }
        return ids;
    }

    public byte[] generarPDFBytesCompras(List<Map<String, Object>> compras) throws Exception {
        List<Integer> ids = extraerIds(compras);
        List<Map<String, Object>> detalle = compraDao.listarDetalleParaExport(ids);
        String[] headers = {"Fecha", "Proveedor", "RUC", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        String[] keys = {"fec_compra", "nombre_proveedor", "ruc_proveedor", "nombre_repuesto", "cantidad", "precio_compra", "subtotal"};
        float[] pesos = {2.5f, 3f, 2f, 3.5f, 1.5f, 2f, 2f};
        return exportador.generarPDF("Reporte de Compras", headers, keys, pesos, detalle);
    }

    public byte[] generarExcelBytesCompras(List<Map<String, Object>> compras) throws Exception {
        List<Integer> ids = extraerIds(compras);
        List<Map<String, Object>> detalle = compraDao.listarDetalleParaExport(ids);
        String[] headers = {"FECHA", "PROVEEDOR", "RUC", "PRODUCTO", "CANTIDAD", "PRECIO UNIT.", "SUBTOTAL"};
        String[] keys = {"fec_compra", "nombre_proveedor", "ruc_proveedor", "nombre_repuesto", "cantidad", "precio_compra", "subtotal"};
        return exportador.generarExcel("Reporte de Compras", headers, keys, detalle);
    }
}