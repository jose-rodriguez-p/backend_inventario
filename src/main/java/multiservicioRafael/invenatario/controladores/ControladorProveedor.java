package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.Proveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ControladorProveedor {

    @GetMapping("/listar")
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(Sistema.getInstancia().obtenerListaProveedores());
    }
}
