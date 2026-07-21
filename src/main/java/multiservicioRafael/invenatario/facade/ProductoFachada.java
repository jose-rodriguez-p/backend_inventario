package multiservicioRafael.invenatario.facade;



import multiservicioRafael.invenatario.modal.Producto;
import multiservicioRafael.invenatario.repository.RepuestoDao;
import multiservicioRafael.invenatario.repository.ConfiguracionDao;
import multiservicioRafael.invenatario.repository.Interfaces.RepuestoDaoInterface;
import multiservicioRafael.invenatario.repository.Interfaces.ConfiguracionDaoInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.config.ConexionDB;


public class ProductoFachada {

    private final List<Producto> listaProductos;
    private final RepuestoDaoInterface repuestoDao;
    private final ConfiguracionDaoInterface configuracionDao;

    public ProductoFachada() {
        this.listaProductos = new ArrayList<>();
        this.repuestoDao = new RepuestoDao();
        this.configuracionDao = new ConfiguracionDao();
    }

    public String agregarProducto(Map<String, Object> datos) {
        try {
            System.out.println("Agregando producto: " + datos.get("codigo"));
            System.out.println("Proveedor RUC: " + datos.get("ruc_proveedor"));
            return "PRODUCTO_REGISTRADO";
        } catch (Exception e) {
            System.out.println("Error en ProductoFachada.agregarProducto: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    public List<Producto> obtenerListaProductos() {
        return listaProductos;
    }

    public List<Map<String, Object>> listarRepuestos() {
        return repuestoDao.listarRepuestos();
    }

    public String agregarRepuesto(String nombre, String categoriaNombre, String marcaNombre,
                                   String proveedorNombre, int cantidad, BigDecimal precioCompra,
                                   BigDecimal precioVenta, int stockMinimo, String estado, String usuarioLogueado) {
        return repuestoDao.agregarRepuesto(usuarioLogueado, nombre, categoriaNombre, marcaNombre,
                                           proveedorNombre, cantidad, precioCompra, precioVenta,
                                           stockMinimo, estado);
    }

    public String editarRepuesto(String nombre, String categoriaNombre, String marcaNombre,
                                  String proveedorNombre, int cantidad, BigDecimal precioCompra,
                                  BigDecimal precioVenta, int stockMinimo, String estado, String usuarioLogueado) {
        return repuestoDao.editarRepuesto(usuarioLogueado, nombre, categoriaNombre, marcaNombre,
                                          proveedorNombre, cantidad, precioCompra, precioVenta,
                                          stockMinimo, estado);
    }

    public List<Map<String, Object>> listarCategoriasConMarcas() {
        return configuracionDao.listarCategoriasConMarcas();
    }
}
