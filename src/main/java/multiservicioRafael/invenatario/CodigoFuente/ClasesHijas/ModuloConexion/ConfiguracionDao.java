package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Categoria;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Marca;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author jose
 */
public class ConfiguracionDao {

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_categorias_con_marcas()";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement cs = conexion.prepareStatement(sql); ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombre_categoria");
                Categoria cat = new Categoria(nombre, "Activo");
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
                return rs.getString(1);
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
                return rs.getString(1);
            }

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }

        return "ERROR: No se obtuvo respuesta del servidor";
    }

    public List<Marca> listarMarcasConCategorias() {
        List<Marca> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_marcas_con_categorias()";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nombreMarca = rs.getString("nombre_marca");
                String estadoMarca = rs.getString("estado_marca");
                String categoriasJson = rs.getString("categorias");

                List<Categoria> categorias = new ArrayList<>();
                JSONArray arr = new JSONArray(categoriasJson);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String nombreCat = obj.getString("nombre");
                    String estadoCat = obj.getString("estado");
                    categorias.add(new Categoria(nombreCat, estadoCat));
                }

                Marca marca = new Marca(nombreMarca, estadoMarca, categorias);
                lista.add(marca);
            }
        } catch (Exception e) {
            System.out.println("Error en listarMarcasConCategorias: " + e.getMessage());
        }
        return lista;
    }

    public String agregarMarcaConCategorias(String usuarioNombre, String marcaNombre, String marcaEstado, List<String> categoriasNombres) {
        String sql = "SELECT public.fn_agregar_marca_con_categorias(?, ?, ?, ?)";
        String resultado = "ERROR: No se pudo ejecutar la operación";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            java.sql.Array categoriasArray = conexion.createArrayOf("VARCHAR", categoriasNombres.toArray());

            ps.setString(1, usuarioNombre);
            ps.setString(2, marcaNombre);
            ps.setString(3, marcaEstado);
            ps.setArray(4, categoriasArray);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado = rs.getString(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error en agregarMarcaConCategorias DAO: " + e.getMessage());
            resultado = "ERROR: " + e.getMessage();
        }
        return resultado;
    }

    public String editarMarcaConCategorias(String usuarioNombre, String marcaNombre, String marcaEstadoNuevo, List<String> categoriasNombres) {
        String sql = "SELECT public.fn_editar_marca_con_categorias(?, ?, ?, ?)";
        String resultado = "ERROR: No se pudo ejecutar la operación";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            java.sql.Array categoriasArray = conexion.createArrayOf("VARCHAR", categoriasNombres.toArray());

            ps.setString(1, usuarioNombre);
            ps.setString(2, marcaNombre);
            ps.setString(3, marcaEstadoNuevo);
            ps.setArray(4, categoriasArray);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resultado = rs.getString(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error en editarMarcaConCategorias DAO: " + e.getMessage());
            resultado = "ERROR: " + e.getMessage();
        }
        return resultado;
    }

    public List<Map<String, Object>> listarCategoriasConMarcas() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_categorias_con_marcas()";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("nombre_categoria", rs.getString("nombre_categoria"));

                List<String> marcas = new ArrayList<>();
                String marcasJson = rs.getString("marcas");

                if (marcasJson != null && !marcasJson.isEmpty()) {
                    org.json.JSONArray arr = new org.json.JSONArray(marcasJson);
                    for (int i = 0; i < arr.length(); i++) {
                        marcas.add(arr.getString(i));
                    }
                }

                fila.put("marcas", marcas);
                lista.add(fila);
            }
        } catch (Exception e) {
            System.out.println("Error en listarCategoriasConMarcas DAO: " + e.getMessage());
        }
        return lista;
    }
}
