package multiservicioRafael.invenatario.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import multiservicioRafael.invenatario.facade.TrabajadorFachada;
import multiservicioRafael.invenatario.repository.UsuarioLogeado;
import multiservicioRafael.invenatario.modal.Trabajador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trabajadores")
public class ControladorTrabajador {

    private final TrabajadorFachada trabajadorFachada = new TrabajadorFachada();

    @GetMapping("/listar")
    public ResponseEntity<List<Trabajador>> listarTrabajadores() {
        return ResponseEntity.ok(
                trabajadorFachada.obtenerListaTrabajadores()
        );
    }

    @GetMapping("/documentos")
    public ResponseEntity<List<Map<String, Object>>> listarDocumentos() {
        return ResponseEntity.ok(
                trabajadorFachada.obtenerListaDocumentos()
        );
    }

    @GetMapping("/cargos")
    public ResponseEntity<List<Map<String, Object>>> listarCargos() {
        return ResponseEntity.ok(
                trabajadorFachada.obtenerListaCargos()
        );
    }

    @GetMapping("/validar-dni/{dni}")
    public ResponseEntity<Map<String, Object>> validarDniExistente(@PathVariable String dni) {

        Map<String, Object> resp = new HashMap<>();
        resp.put("existe", trabajadorFachada.existeDniTrabajador(dni));

        return ResponseEntity.ok(resp);
    }

    @PostMapping("/crear")
    public ResponseEntity<String> crearTrabajador(@RequestBody Map<String, Object> datos) {

        try {
            String resultado =
                    trabajadorFachada.nuevoTrabajador(datos, UsuarioLogeado.getUsuario());

            if ("insertado".equalsIgnoreCase(resultado)) {
                return ResponseEntity.ok("Trabajador registrado exitosamente");
            }

            return ResponseEntity
                    .badRequest()
                    .body(resultado);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error crítico en el servidor: " + e.getMessage());
        }
    }

    @PutMapping("/actualizar/{dni}")
    public ResponseEntity<String> actualizarTrabajador(
            @PathVariable String dni,
            @RequestBody Map<String, Object> datosActualizados) {

        try {
            String resultado =
                    trabajadorFachada.actualizarTrabajador(dni, datosActualizados, UsuarioLogeado.getUsuario());

            if ("actualizado".equalsIgnoreCase(resultado)) {
                return ResponseEntity.ok("Trabajador actualizado exitosamente");
            }

            return ResponseEntity
                    .badRequest()
                    .body(resultado);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> trabajadores) {

        try {
            byte[] pdfBytes = trabajadorFachada.generarPDFBytes(trabajadores);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=reporte_trabajadores.pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody List<Map<String, Object>> trabajadores) {

        try {
            byte[] excelBytes = trabajadorFachada.generarExcelBytes(trabajadores);

            return ResponseEntity.ok()
                    .header("Content-Type",
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=reporte_trabajadores.xlsx")
                    .body(excelBytes);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/buscar-dni/{dni}")
    public ResponseEntity<Map<String, Object>> buscarDni(@PathVariable String dni) {

        // TODO: Implementar consultarDNIParseado en TrabajadorFachada
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", false);
        respuesta.put("message", "TODO: Implementar en TrabajadorFachada");

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

        // TODO: Implementar envío de código en TrabajadorFachada
        String resultado = "TODO: Implementar en TrabajadorFachada";

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/correo/validar")
    public ResponseEntity<String> validarCodigo(@RequestBody Map<String, String> request) {

        String dni = request.get("dni");
        String codigo = request.get("codigo");

        // TODO: Implementar validación de código en TrabajadorFachada
        String resultado = "TODO: Implementar en TrabajadorFachada";

        return ResponseEntity.ok(resultado);
    }
}