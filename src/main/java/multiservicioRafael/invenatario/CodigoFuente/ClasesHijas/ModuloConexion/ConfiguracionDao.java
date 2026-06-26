/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Categoria;

/**
 *
 * @author jose
 */
public class ConfiguracionDao {

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.listar_categorias_sin_id()";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement cs = conexion.prepareStatement(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String estado = rs.getString("estado");
                Categoria cat = new Categoria(nombre, estado);
                lista.add(cat);
            }

        } catch (Exception e) {
            System.out.println("Error en listarCategoriasSinId: " + e.getMessage());
        }

        return lista;
    }

    public String modificarEstadoCategoriaConFuncion(String usuario, String nombreCategoria, String nuevoEstado) {
        String sql = "SELECT public.fn_modificar_estado_categoria(?, ?, ?)";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, nombreCategoria);
            ps.setString(3, nuevoEstado);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString(1); // "OK", "ERROR: ...", "SIN_CAMBIOS"
            }

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }

        return "ERROR: No se obtuvo respuesta del servidor";
    }

    public String agregarCategoriaConFuncion(String usuario, Categoria categoria) {
        String sql = "SELECT public.fn_agregar_categoria(?, ?, ?)";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, categoria.getNombre());
            ps.setString(3, categoria.getEstado());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString(1); // "OK" o "ERROR: ..."
            }

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }

        return "ERROR: No se obtuvo respuesta del servidor";
    }

}
