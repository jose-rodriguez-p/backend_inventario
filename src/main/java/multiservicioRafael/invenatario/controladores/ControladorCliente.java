package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.Cliente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ControladorCliente {

    @GetMapping("/listar")
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(Sistema.getInstancia().obtenerListaClientes());
    }
}
