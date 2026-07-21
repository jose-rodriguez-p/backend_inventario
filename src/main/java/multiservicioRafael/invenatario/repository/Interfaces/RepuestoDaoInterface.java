package multiservicioRafael.invenatario.repository.Interfaces;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface RepuestoDaoInterface {

    List<Map<String, Object>> listarRepuestos();

    String agregarRepuesto(String usuario, String nombre, String categoriaNombre, String marcaNombre,
                           String proveedorNombre, int cantidad, BigDecimal precioCompra,
                           BigDecimal precioVenta, int stockMinimo, String estado);

    String editarRepuesto(String usuario, String nombre, String categoriaNombre, String marcaNombre,
                          String proveedorNombre, int cantidad, BigDecimal precioCompra,
                          BigDecimal precioVenta, int stockMinimo, String estado);
}
