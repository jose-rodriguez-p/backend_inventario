package multiservicioRafael.invenatario.controladores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ClasesFachda.RolMenuFachada;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.UsuarioLogeado;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
public class Controladormenus {

    private final RolMenuFachada rolMenuFachada = new RolMenuFachada();

    @PostMapping("/lista-menus")
    public ResponseEntity<ArrayList<String>> listamenus() {
        ArrayList<String> menus = rolMenuFachada.listarMenu();
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
                UsuarioLogeado.setUsuario(usuarioLogueado);
            }

            boolean exito = rolMenuFachada.actualizarRolYAccesos(nombre, estado, menus, UsuarioLogeado.getUsuario());

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