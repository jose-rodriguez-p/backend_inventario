package multiservicioRafael.invenatario.facade;

import multiservicioRafael.invenatario.repository.CajaDao;
import multiservicioRafael.invenatario.repository.Interfaces.CajaDaoInterface;
import multiservicioRafael.invenatario.service.ServicioExportacion.ExportadorService;

import java.util.Map;

public class CajaFachada {

    private final CajaDaoInterface cajaDao;
    private final ExportadorService exportador;

    public CajaFachada() {
        this.cajaDao = new CajaDao();
        this.exportador = ExportadorService.getInstancia();
    }

    public Map<String, Object> obtenerCajaAbierta(String usuarioNombre) {
        return cajaDao.obtenerCajaAbierta(usuarioNombre);
    }

    public String abrirCaja(String usuarioNombre, double saldoInicial) {
        return cajaDao.abrirCaja(usuarioNombre, saldoInicial);
    }

    public Map<String, Object> obtenerResumenCierre(int idCierreCaja) {
        return cajaDao.obtenerResumenCierre(idCierreCaja);
    }

    public String cerrarCaja(int idCierreCaja, double totVentasCajero) {
        return cajaDao.cerrarCaja(idCierreCaja, totVentasCajero);
    }

    public byte[] generarComprobanteCierrePDF(Map<String, Object> resumen) throws Exception {
        return exportador.generarComprobanteCierrePDF(resumen);
    }
}