package multiservicioRafael.invenatario.controladores;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Trabajador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trabajadores")
@CrossOrigin(origins = "*")
public class ControladorTrabajador {

    @GetMapping("/listar")
    public ResponseEntity<List<Trabajador>> listarTrabajadores() {
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

    @GetMapping("/validar-dni/{dni}")
    public ResponseEntity<Map<String, Object>> validarDniExistente(@PathVariable String dni) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("existe", Sistema.getInstancia().existeDniTrabajador(dni));
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearTrabajador(@RequestBody Map<String, Object> datos) {
        try {
            
            String resultado = Sistema.getInstancia().nuevoTrabajador(datos);
            if ("insertado".equalsIgnoreCase(resultado)) {
                return ResponseEntity.ok("Trabajador registrado exitosamente");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error crítico en el servidor: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{dni}")
    public ResponseEntity<String> actualizarTrabajador(
            @PathVariable String dni,
            @RequestBody Map<String, Object> datosActualizados) {
        try {
            String resultado = Sistema.getInstancia().actualizarTrabajador(dni, datosActualizados);

            if ("actualizado".equalsIgnoreCase(resultado)) {
                return ResponseEntity.ok("Trabajador actualizado exitosamente");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);

        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage()); 
        }
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> trabajadores) {
        try {
            byte[] pdfBytes = Sistema.getInstancia().generarPDFBytes(trabajadores);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_trabajadores.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody List<Map<String, Object>> trabajadores) {
        try {
            byte[] excelBytes = Sistema.getInstancia().generarExcelBytes(trabajadores);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "reporte_trabajadores.xlsx");
            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/buscar-dni/{dni}")
    public ResponseEntity<Map<String, Object>> buscarDni(@PathVariable String dni) {
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
}
