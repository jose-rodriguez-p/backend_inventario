package multiservicioRafael.invenatario.controladores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*")
public class Controladormenus {

    @PostMapping("/lista-menus")
    public ResponseEntity<ArrayList<String>> listamenus() {
        ArrayList<String> menus = Sistema.getInstancia().listamenu();
        return ResponseEntity.ok(menus != null ? menus : new ArrayList<>());
    }

    @PutMapping("/actualizar/{nombre}")
    public ResponseEntity<Map<String, String>> actualizarRol(
            @PathVariable("nombre") String nombre,
            @RequestBody Map<String, Object> payload) {
        Map<String, String> respuesta = new HashMap<>();
        try {
            String estado = (String) payload.get("estado");
            @SuppressWarnings("unchecked")
            ArrayList<String> menus = (ArrayList<String>) payload.get("menus");
            String usuarioLogueado = (String) payload.get("usuarioLogueado");
            if (usuarioLogueado != null && !usuarioLogueado.isBlank()) {
                Sistema.getInstancia().setUsuarioLogueado(usuarioLogueado);
            }
            boolean exito = Sistema.getInstancia().actualizarRolYAccesos(nombre, estado, menus);
            if (exito) {
                respuesta.put("mensaje", "El rol y sus accesos han sido actualizados con éxito.");
                return new ResponseEntity<>(respuesta, HttpStatus.OK);
            }
            respuesta.put("error", "No se pudo encontrar o actualizar el rol.");
            return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            respuesta.put("error", "Ocurrió un error interno: " + e.getMessage());
            return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
