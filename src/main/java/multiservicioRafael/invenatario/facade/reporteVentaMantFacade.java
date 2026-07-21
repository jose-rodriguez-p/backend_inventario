/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.facade;

import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.repository.Interfaces.reporteVentaMantDaoInterfas;
import multiservicioRafael.invenatario.repository.reporteVentaMantDao;
import multiservicioRafael.invenatario.service.ServicioExportacion.ExportadorService;

/**
 *
 * @author jose
 */
public class reporteVentaMantFacade {
    
    private final reporteVentaMantDaoInterfas reporteVentaMantDao;
    private final ExportadorService exportador;

    public reporteVentaMantFacade() {
        this.reporteVentaMantDao = new reporteVentaMantDao();
        this.exportador = ExportadorService.getInstancia();
    }
    
    public List<Map<String, Object>> listarMovimientos() {
        return reporteVentaMantDao.listarMovimientos();
    }

    public byte[] generarPDFBytes(List<Map<String, Object>> datos) throws Exception {
        String[] headers = {"N°", "Fecha", "Hora", "Tipo", "Cliente", "DNI", "Carro", "Repuesto", "Mecánico", "Vendedor", "Total"};
        String[] keys = {"nOrden", "fecha", "hora", "tipo", "cliente", "dni", "carro", "repuesto", "mecanico", "vendedor", "total"};
        float[] pesos = {1.5f, 2f, 1.5f, 2f, 3f, 2f, 3f, 3f, 3f, 3f, 2f};
        return exportador.generarPDF("Reporte General de Movimientos", headers, keys, pesos, datos);
    }

    public byte[] generarExcelBytes(List<Map<String, Object>> datos) throws Exception {
        String[] headers = {"N°", "FECHA", "HORA", "TIPO", "CLIENTE", "DNI", "CARRO", "REPUESTO", "MECÁNICO", "VENDEDOR", "TOTAL"};
        String[] keys = {"nOrden", "fecha", "hora", "tipo", "cliente", "dni", "carro", "repuesto", "mecanico", "vendedor", "total"};
        return exportador.generarExcel("Reporte General de Movimientos", headers, keys, datos);
    }
}
