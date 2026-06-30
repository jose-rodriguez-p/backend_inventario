package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;



import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Categoria;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ConfiguracionDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriaFachada {

    private final ConfiguracionDao configuracionDao;

    public CategoriaFachada() {
        this.configuracionDao = new ConfiguracionDao();
    }

    public List<Categoria> listarCategoria() {
        List<Categoria> lista = configuracionDao.listarCategorias();

        if (lista == null || lista.isEmpty()) {
            System.out.println("No se encontraron categorías o la lista está vacía.");
        } else {
            System.out.println("Categorías cargadas correctamente. Total: " + lista.size());
        }

        return lista;
    }

    public String agregarCategoriaSistema(Categoria categoria, String usuarioLogueado) {
        if (categoria == null) {
            return "ERROR: La categoría es nula.";
        }
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return "ERROR: El nombre de la categoría no puede estar vacío.";
        }
        if (categoria.getEstado() == null || categoria.getEstado().trim().isEmpty()) {
            return "ERROR: El estado de la categoría no puede estar vacío.";
        }

        System.out.println(usuarioLogueado);
        return configuracionDao.agregarCategoriaConFuncion(usuarioLogueado, categoria);
    }

    public String modificarEstadoCategoriaSistema(String nombreCategoria, String nuevoEstado, String usuarioLogueado) {
        System.out.println(usuarioLogueado);

        if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) {
            return "ERROR: El nombre de la categoría no puede estar vacío.";
        }
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return "ERROR: El nuevo estado no puede estar vacío.";
        }

        return configuracionDao.modificarEstadoCategoriaConFuncion(
                usuarioLogueado,
                nombreCategoria.trim(),
                nuevoEstado.trim()
        );
    }
    public List<Map<String, Object>> obtenerCategoriasProductos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String[] cats = {"Lubricantes", "Repuestos", "Filtros", "Neumáticos", "Frenos"};

        for (int i = 0; i < cats.length; i++) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("id", i + 1);
            fila.put("nombre", cats[i]);
            lista.add(fila);
        }

        return lista;
    }
}
