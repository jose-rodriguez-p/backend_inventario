package multiservicioRafael.invenatario.controladores;

import java.util.HashMap;
import multiservicioRafael.invenatario.CodigoFuente.ClasesFachda.ClienteFachada;
import multiservicioRafael.invenatario.CodigoFuente.ClasesFachda.AutenticacionFachada;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.UsuarioLogeado;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/clientes")
public class ControladorCliente {

    private final ClienteFachada clienteFachada = new ClienteFachada();
    private final AutenticacionFachada autenticacionFachada = new AutenticacionFachada();

    @GetMapping("/buscar-dni/{dni}")
    public ResponseEntity<Map<String, Object>> buscarDni(@PathVariable String dni) {

        boolean existeEnDB = clienteFachada.consultarDBclienteExiste(dni);

        if (existeEnDB) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "success", false,
                            "message", "El DNI ya existe en nuestra base de datos."
                    ));
        }

        Map<String, Object> respuesta = clienteFachada.consultarDNIParseado(dni);

        if (Boolean.TRUE.equals(respuesta.get("success"))) {
            return ResponseEntity.ok(respuesta);
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(respuesta);
    }

    @PostMapping("/correo/enviar")
    public ResponseEntity<String> enviarCorreo(@RequestBody Map<String, String> request) {

        String dni = request.get("dni");
        String correo = request.get("correo");

        String resultado = autenticacionFachada.enviarCodigoVerificacion(dni, correo);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/correo/validar")
    public ResponseEntity<String> validarCodigo(@RequestBody Map<String, String> request) {

        String dni = request.get("dni");
        String codigo = request.get("codigo");

        String resultado = autenticacionFachada.validarCodigoIngresado(dni, codigo);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarClientes() {

        try {
            List<Map<String, Object>> resultado = clienteFachada.listarClientesConCarros();

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, String>> registrar(@RequestBody Map<String, Object> payload) {

        String respuestaBd = clienteFachada.agregarCliente(payload, UsuarioLogeado.getUsuario());

        Map<String, String> respuestaJson = new HashMap<>();
        respuestaJson.put("status", respuestaBd);

        if ("registrado".equals(respuestaBd)) {
            return ResponseEntity.ok(respuestaJson);
        } else {
            return ResponseEntity.badRequest().body(respuestaJson);
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<Map<String, String>> editar(@RequestBody Map<String, Object> payload) {

        String respuestaBd = clienteFachada.editarCliente(payload, UsuarioLogeado.getUsuario());

        Map<String, String> respuestaJson = new HashMap<>();
        respuestaJson.put("status", respuestaBd);

        if ("editado".equals(respuestaBd)) {
            return ResponseEntity.ok(respuestaJson);
        } else if (respuestaBd != null && respuestaBd.startsWith("error_validacion")) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(respuestaJson); 
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaJson); 
        }
    }

    @GetMapping("/verificar-placa/{placa}")
    public ResponseEntity<?> verificarPlaca(@PathVariable String placa) {
        try {
            List<Map<String, Object>> clientes = clienteFachada.listarClientesConCarros();
            for (Map<String, Object> c : clientes) {
                List<Map<String, String>> carros = (List<Map<String, String>>) c.get("carros");
                if (carros != null) {
                    for (Map<String, String> carro : carros) {
                        if (placa.equalsIgnoreCase(carro.get("placa"))) {
                            return ResponseEntity.ok(Map.of(
                                "existe", true,
                                "cliente", c.get("nombre") + " " + c.get("apellido_paterno"),
                                "dni", c.get("dni")
                            ));
                        }
                    }
                }
            }
            return ResponseEntity.ok(Map.of("existe", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> clientes) {
        try {
            byte[] pdfBytes = clienteFachada.generarPDFBytesClientes(clientes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_clientes.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody List<Map<String, Object>> clientes) {
        try {
            byte[] excelBytes = clienteFachada.generarExcelBytesClientes(clientes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "reporte_clientes.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
