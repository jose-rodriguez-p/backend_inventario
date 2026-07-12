package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;

import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.CompraDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ConexionDB;
import multiservicioRafael.invenatario.CodigoFuente.ServicioExportacion.ExportadorService;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class CompraFachada {

    private final CompraDao compraDao;
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

    public byte[] generarPDFBytesCompras(List<Map<String, Object>> compras) throws Exception {
        String[] headers = {"Fecha", "Proveedor", "RUC", "Productos", "Total"};
        String[] keys = {"fec_compra", "nombre_proveedor", "ruc_proveedor", "cantidad_items", "tot_pago"};
        float[] pesos = {3f, 4f, 3f, 2f, 2.5f};
        return exportador.generarPDF("Reporte de Compras", headers, keys, pesos, compras);
    }

    public byte[] generarExcelBytesCompras(List<Map<String, Object>> compras) throws Exception {
        String[] headers = {"FECHA", "PROVEEDOR", "RUC", "PRODUCTOS", "TOTAL"};
        String[] keys = {"fec_compra", "nombre_proveedor", "ruc_proveedor", "cantidad_items", "tot_pago"};
        return exportador.generarExcel("Reporte de Compras", headers, keys, compras);
    }
}
