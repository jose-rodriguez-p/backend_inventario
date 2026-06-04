package multiservicioRafael.invenatario.controladores;

import java.util.ArrayList;
import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.Trabajador;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trabajadores")
@CrossOrigin(origins = "*")
public class ControladorTrabajador {

    @GetMapping("/listar")
    public ResponseEntity<List<Trabajador>> listarTrabajadores() {
        ArrayList<Trabajador> trabajador=Sistema.getInstancia().obtenerListaTrabajadores();
        for(Trabajador trabajado:trabajador){
            System.out.println(trabajado);
        }
        return ResponseEntity.ok(Sistema.getInstancia().obtenerListaTrabajadores());
    }

    @GetMapping("/documentos")
    public ResponseEntity<List<Map<String, Object>>> listarDocumentos() {
        return ResponseEntity.ok(Sistema.getInstancia().obtenerListaDocumentos());
    }

    @GetMapping("/cargos")
    public ResponseEntity<List<Map<String, Object>>> listarCargos() {
        return ResponseEntity.ok(Sistema.getInstancia().obtenerListaCargos());
    }
}
