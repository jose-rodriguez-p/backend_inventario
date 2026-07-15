package multiservicioRafael.invenatario.facade;



import multiservicioRafael.invenatario.modal.Categoria;
import multiservicioRafael.invenatario.repository.ConfiguracionDao;
import multiservicioRafael.invenatario.repository.Interfaces.ConfiguracionDaoInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriaFachada {

    private final ConfiguracionDaoInterface configuracionDao;

    public CategoriaFachada() {
        this.configuracionDao = new ConfiguracionDao();
    }

    public List<Categoria> listarCategoria() {
        return configuracionDao.listarCategorias();
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
