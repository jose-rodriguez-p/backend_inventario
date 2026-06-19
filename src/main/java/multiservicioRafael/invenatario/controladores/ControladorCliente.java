package multiservicioRafael.invenatario.controladores;

import java.util.HashMap;
import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Cliente;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ControladorCliente {

    @GetMapping("/buscar-dni/{dni}")
    public ResponseEntity<Map<String, Object>> buscarDni(@PathVariable String dni) {
        boolean existeEnDB = Sistema.getInstancia().consultarDBclienteExiste(dni);
        if (existeEnDB) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("success", false, "message", "El DNI ya existe en nuestra base de datos."));
        }
        Map<String, Object> respuesta = Sistema.getInstancia().consultarDNIParseado(dni);
        if (Boolean.TRUE.equals(respuesta.get("success"))) {
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    @PostMapping("/correo/enviar")
    public ResponseEntity<String> enviarCorreo(@RequestBody Map<String, String> request) {
        String dni = request.get("dni");
        String correo = request.get("correo");
        String resultado = Sistema.getInstancia().enviarCodigoVerificacion(dni, correo);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/correo/validar")
    public ResponseEntity<String> validarCodigo(@RequestBody Map<String, String> request) {
        String dni = request.get("dni");
        String codigo = request.get("codigo");
        String resultado = Sistema.getInstancia().validarCodigoIngresado(dni, codigo);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarClientes() {
        try {
            List<Map<String, Object>> resultado
                    = Sistema.getInstancia().listarClientesConCarros();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, String>> registrar(@RequestBody Map<String, Object> payload) {
        String usuarioOperador = "operador_trujillo";
        payload.put("usuarioLogueado", usuarioOperador);

        String respuestaBd = Sistema.getInstancia().agregarCliente(payload);
        Map<String, String> respuestaJson = new HashMap<>();
        respuestaJson.put("status", respuestaBd);

        if (respuestaBd.equals("registrado")) {
            return ResponseEntity.ok(respuestaJson);
        } else {
            return ResponseEntity.badRequest().body(respuestaJson);
        }
    }

    @PutMapping("/editar")
    public ResponseEntity<Map<String, String>> editar(@RequestBody Map<String, Object> payload) {
        String usuarioOperador = "operador_trujillo";
        payload.put("usuarioLogueado", usuarioOperador);
        String respuestaBd = Sistema.getInstancia().editarCliente(payload);
        Map<String, String> respuestaJson = new HashMap<>();
        respuestaJson.put("status", respuestaBd);
        if (respuestaBd.equals("editado")) {
            return ResponseEntity.ok(respuestaJson);
        } else {
            return ResponseEntity.badRequest().body(respuestaJson);
        }
    }
}
