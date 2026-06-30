package multiservicioRafael.invenatario.controladores;

import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Categoria;
import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
public class ControladorConfiguracion {

    @GetMapping("/roles/listar")
    public ResponseEntity<List<Map<String, Object>>> listarRoles() {

        try {
            List<Map<String, Object>> roles
                    = Sistema.getInstancia().listarRoles();

            if (roles == null || roles.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(roles);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/roles/crear")
    public ResponseEntity<Boolean> crearRol(@RequestBody Map<String, Object> nuevoRol) {

        try {
            boolean guardado
                    = Sistema.getInstancia().agregarRol(nuevoRol);

            if (guardado) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.badRequest().body(false);
            }

        } catch (Exception e) {
            System.out.println("Error en controlador al guardar: " + e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(false);
        }
    }

    // 1. MÉTODO EXCLUSIVO PARA VALIDAR EN TIEMPO REAL
    @PostMapping("/validar-password-actual")
    public ResponseEntity<Boolean> validarPasswordActual(@RequestBody Map<String, String> datos) {
        try {
            String username = datos.get("username");
            String contrasenaActual = datos.get("contrasenaActual");

            if (username == null || contrasenaActual == null) {
                return ResponseEntity.badRequest().body(false);
            }
            boolean esValida = Sistema.getInstancia().verificarContrasena(username, contrasenaActual);

            return ResponseEntity.ok(esValida);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping("/actualizar-password")
    public ResponseEntity<String> actualizarPassword(@RequestBody Map<String, String> datos) {
        try {
            String username = datos.get("username");
            String newPassword = datos.get("newPassword");
            if (username == null || newPassword == null) {
                return ResponseEntity.badRequest().body("DATOS_INCOMPLETOS");
            }
            boolean actualizado = Sistema.getInstancia().actualizarContrasena(username, newPassword);

            if (actualizado) {
                return ResponseEntity.ok("PASSWORD_ACTUALIZADA");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR_AL_ACTUALIZAR");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }

    @GetMapping("/listar-categorias")
    public ResponseEntity<?> obtenerCategorias() {
        try {
            List<Categoria> lista = Sistema.getInstancia().listarCategoria();
            if (lista == null || lista.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("LISTA_VACIA");
            }
            return ResponseEntity.ok(lista);

        } catch (Exception e) {
            System.out.println("Error en controlador obtenerCategorias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }

    @PutMapping("/categorias/estado")
    public ResponseEntity<String> modificarEstadoCategoria(
            @RequestParam String nombreCategoria,
            @RequestParam String nuevoEstado) {

        try {

            String respuesta = Sistema.getInstancia()
                    .modificarEstadoCategoriaSistema(
                            nombreCategoria,
                            nuevoEstado
                    );
            System.out.println(respuesta);

            if (respuesta.equals("OK")) {
                return ResponseEntity.ok(respuesta);
            }

            return ResponseEntity.badRequest().body(respuesta);

        } catch (Exception e) {

            System.out.println("Error en controlador al modificar estado: "
                    + e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/categorias/crear")
    public ResponseEntity<String> crearCategoria(
            @RequestBody Categoria nuevaCategoria) {

        try {

            String respuesta = Sistema.getInstancia()
                    .agregarCategoriaSistema(nuevaCategoria);

            if (respuesta.equals("OK")) {
                return ResponseEntity.ok(respuesta);
            }

            return ResponseEntity.badRequest().body(respuesta);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }

    @GetMapping("/marcas-categorias")
    public ResponseEntity<?> listarMarcasConCategorias() {
        try {
            List<multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Marca> lista = Sistema.getInstancia().obtenerMarcasConCategorias();
            if (lista == null || lista.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("LISTA_VACIA");
            }
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            System.out.println("Error en controlador listarMarcasConCategorias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }

    @PostMapping("/marcas/crear")
    public ResponseEntity<String> crearMarcaConCategorias(@RequestBody Map<String, Object> datos) {
        try {
            String marcaNombre = (String) datos.get("marcaNombre");
            String marcaEstado = (String) datos.get("marcaEstado");
            List<String> categoriasNombres = (List<String>) datos.get("categoriasNombres");

            if (marcaNombre == null || marcaEstado == null || categoriasNombres == null) {
                return ResponseEntity.badRequest().body("ERROR: Datos incompletos");
            }

            String respuesta = Sistema.getInstancia().agregarMarcaConCategoriasSistema(marcaNombre, marcaEstado, categoriasNombres);

            if (respuesta.equals("OK")) {
                return ResponseEntity.ok(respuesta);
            }

            return ResponseEntity.badRequest().body(respuesta);
        } catch (Exception e) {
            System.out.println("Error en controlador al crear marca: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR: " + e.getMessage());
        }
    }
    @PutMapping("/marcas/editar")
    public ResponseEntity<String> editarMarcaConCategorias(@RequestBody Map<String, Object> datos) {
        try {
            String marcaNombre = (String) datos.get("marcaNombre");
            String marcaEstadoNuevo = (String) datos.get("marcaEstadoNuevo");
            List<String> categoriasNombres = (List<String>) datos.get("categoriasNombres");

            if (marcaNombre == null || marcaEstadoNuevo == null || categoriasNombres == null) {
                return ResponseEntity.badRequest().body("ERROR: Datos incompletos");
            }

            String respuesta = Sistema.getInstancia().editarMarcaConCategoriasSistema(marcaNombre, marcaEstadoNuevo, categoriasNombres);

            if (respuesta.equals("OK")) {
                return ResponseEntity.ok(respuesta);
            }

            return ResponseEntity.badRequest().body(respuesta);
        } catch (Exception e) {
            System.out.println("Error en controlador al editar marca: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR: " + e.getMessage());
        }
    }

}
