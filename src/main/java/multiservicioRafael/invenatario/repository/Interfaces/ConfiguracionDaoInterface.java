package multiservicioRafael.invenatario.repository.Interfaces;

import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.modal.Categoria;
import multiservicioRafael.invenatario.modal.Marca;

public interface ConfiguracionDaoInterface {

    List<Categoria> listarCategorias();

    String modificarEstadoCategoriaConFuncion(String usuario, String nombreCategoria, String nuevoEstado);

    String agregarCategoriaConFuncion(String usuario, Categoria categoria);

    List<Marca> listarMarcasConCategorias();

    String agregarMarcaConCategorias(String usuarioNombre, String marcaNombre, String marcaEstado, List<String> categoriasNombres);

    String editarMarcaConCategorias(String usuarioNombre, String marcaNombre, String marcaEstadoNuevo, List<String> categoriasNombres);

    List<Map<String, Object>> listarCategoriasConMarcas();
}
