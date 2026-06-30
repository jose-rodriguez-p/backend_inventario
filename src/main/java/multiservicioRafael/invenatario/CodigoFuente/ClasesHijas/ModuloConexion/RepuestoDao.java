/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jose
 */
public class RepuestoDao {

    public List<Map<String, Object>> listarRepuestos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_repuestos()";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement cs = conexion.prepareStatement(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();

                fila.put("nombre_repuesto", rs.getString("nombre_repuesto"));
                fila.put("nombre_categoria", rs.getString("nombre_categoria"));
                fila.put("nombre_marca", rs.getString("nombre_marca"));
                fila.put("nombre_proveedor", rs.getString("nombre_proveedor"));
                fila.put("cantidad", rs.getInt("cantidad"));
                fila.put("precio_compra", rs.getDouble("precio_compra"));
                fila.put("precio_venta", rs.getDouble("precio_venta"));
                fila.put("stock_minimo", rs.getInt("stock_minimo"));
                fila.put("estado", rs.getString("estado"));

                lista.add(fila);
            }

        } catch (Exception e) {
            System.out.println("Error en listarRepuestos: " + e.getMessage());
        }

        return lista;
    }

    public String agregarRepuesto(String usuario, String nombre, String categoriaNombre, String marcaNombre,
                                   String proveedorNombre, int cantidad, BigDecimal precioCompra,
                                   BigDecimal precioVenta, int stockMinimo, String estado) {
        String sql = "SELECT public.fn_agregar_repuesto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, nombre);
            ps.setString(3, categoriaNombre);
            ps.setString(4, marcaNombre);
            ps.setString(5, proveedorNombre);
            ps.setInt(6, cantidad);
            ps.setBigDecimal(7, precioCompra);
            ps.setBigDecimal(8, precioVenta);
            ps.setInt(9, stockMinimo);
            ps.setString(10, estado);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }

        } catch (Exception e) {
            System.out.println("Error en agregarRepuesto DAO: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }

        return "ERROR: No se pudo agregar el repuesto";
    }

    public String editarRepuesto(String usuario, String nombre, String categoriaNombre, String marcaNombre,
                                  String proveedorNombre, int cantidad, BigDecimal precioCompra,
                                  BigDecimal precioVenta, int stockMinimo, String estado) {
        String sql = "SELECT public.fn_editar_repuesto(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, nombre);
            ps.setString(3, categoriaNombre);
            ps.setString(4, marcaNombre);
            ps.setString(5, proveedorNombre);
            ps.setInt(6, cantidad);
            ps.setBigDecimal(7, precioCompra);
            ps.setBigDecimal(8, precioVenta);
            ps.setInt(9, stockMinimo);
            ps.setString(10, estado);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }

        } catch (Exception e) {
            System.out.println("Error en editarRepuesto DAO: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }

        return "ERROR: No se pudo editar el repuesto";
    }
}
