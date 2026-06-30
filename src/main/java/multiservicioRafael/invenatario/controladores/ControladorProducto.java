package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Categoria;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Producto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/productos")
public class ControladorProducto {

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(
                Sistema.getInstancia().obtenerListaProductos()
        );
    }

    @GetMapping("/categorias")
    public ResponseEntity<?> listarCategorias() {
        List<Categoria> categorias = Sistema.getInstancia().listarCategoria();
        if (categorias == null || categorias.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("LISTA_VACIA");
        }
        List<String> nombres = categorias.stream()
                .map(Categoria::getNombre)
                .collect(Collectors.toList());
        return ResponseEntity.ok(nombres);
    }

    @GetMapping("/categorias-marcas")
    public ResponseEntity<?> listarCategoriasConMarcas() {
        try {
            List<Map<String, Object>> lista = Sistema.getInstancia().listarCategoriasConMarcas();
            if (lista == null || lista.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("LISTA_VACIA");
            }
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            System.out.println("Error en controlador listarCategoriasConMarcas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }
}
