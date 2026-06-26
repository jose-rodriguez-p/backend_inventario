package multiservicioRafael.invenatario.controladores;

import java.util.HashMap;
import multiservicioRafael.invenatario.CodigoFuente.Sistema;
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

    @GetMapping("/buscar-dni/{dni}")
    public ResponseEntity<Map<String, Object>> buscarDni(@PathVariable String dni) {

        boolean existeEnDB = Sistema.getInstancia().consultarDBclienteExiste(dni);

        if (existeEnDB) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "success", false,
                            "message", "El DNI ya existe en nuestra base de datos."
                    ));
        }

        Map<String, Object> respuesta = Sistema.getInstancia().consultarDNIParseado(dni);

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

        String resultado = Sistema.getInstancia()
                .enviarCodigoVerificacion(dni, correo);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/correo/validar")
    public ResponseEntity<String> validarCodigo(@RequestBody Map<String, String> request) {

        String dni = request.get("dni");
        String codigo = request.get("codigo");

        String resultado = Sistema.getInstancia()
                .validarCodigoIngresado(dni, codigo);

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

        String respuestaBd = Sistema.getInstancia().agregarCliente(payload);

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

        String respuestaBd = Sistema.getInstancia().editarCliente(payload);

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

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> clientes) {
        try {
            byte[] pdfBytes = Sistema.getInstancia().generarPDFBytesClientes(clientes);

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
            byte[] excelBytes = Sistema.getInstancia().generarExcelBytesClientes(clientes);

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
