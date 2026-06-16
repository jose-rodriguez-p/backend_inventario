package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Producto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ControladorProducto {

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(Sistema.getInstancia().obtenerListaProductos());
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        return ResponseEntity.ok(Sistema.getInstancia().obtenerListaCategorias());
    }
}
