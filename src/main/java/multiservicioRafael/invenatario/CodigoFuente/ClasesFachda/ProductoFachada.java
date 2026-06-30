package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;



import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ConexionDB;


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

    public List<Map<String, Object>> listarRepuestos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_repuestos()";

        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("nombre_repuesto", rs.getString("nombre_repuesto"));
                fila.put("nombre_categoria", rs.getString("nombre_categoria"));
                fila.put("nombre_marca", rs.getString("nombre_marca"));
                fila.put("nombre_proveedor", rs.getString("nombre_proveedor"));
                fila.put("cantidad", rs.getInt("cantidad"));
                fila.put("precio_compra", rs.getBigDecimal("precio_compra"));
                fila.put("precio_venta", rs.getBigDecimal("precio_venta"));
                fila.put("stock_minimo", rs.getInt("stock_minimo"));
                fila.put("estado", rs.getString("estado"));
                lista.add(fila);
            }
        } catch (Exception e) {
            System.out.println("Error en ProductoFachada.listarRepuestos: " + e.getMessage());
        }
        return lista;
    }
}
