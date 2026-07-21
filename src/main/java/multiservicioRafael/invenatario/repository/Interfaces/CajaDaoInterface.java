package multiservicioRafael.invenatario.repository.Interfaces;

import java.util.Map;

public interface CajaDaoInterface {

    Map<String, Object> obtenerCajaAbierta(String usuarioNombre);

    String abrirCaja(String usuarioNombre, double saldoInicial);

    Map<String, Object> obtenerResumenCierre(int idCierreCaja);

    String cerrarCaja(int idCierreCaja, double totVentasCajero);
}