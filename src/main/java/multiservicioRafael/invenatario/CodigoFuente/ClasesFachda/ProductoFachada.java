package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;



import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ProductoFachada {

    private final List<Producto> listaProductos;

    public ProductoFachada() {
        this.listaProductos = new ArrayList<>();
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
}
