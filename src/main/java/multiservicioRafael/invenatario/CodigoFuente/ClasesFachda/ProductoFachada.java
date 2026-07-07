package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;



import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Producto;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.RepuestoDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ConfiguracionDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ConexionDB;


public class ProductoFachada {

    private final List<Producto> listaProductos;
    private final RepuestoDao repuestoDao;
    private final ConfiguracionDao configuracionDao;

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
