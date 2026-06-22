package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Proveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proveedores")
public class ControladorProveedor {

    @GetMapping("/listar")
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(
                Sistema.getInstancia().obtenerListaProveedores()
        );
    }

    @GetMapping("/buscar-ruc/{ruc}")
    public ResponseEntity<?> buscarRuc(@PathVariable String ruc) {

        var datosEmpresa = Sistema.getInstancia().consutaRuc(ruc);

        if (datosEmpresa != null && !datosEmpresa.isEmpty()) {
            return ResponseEntity.ok(datosEmpresa);
        }

        return ResponseEntity
                .status(404)
                .body(Map.of("error", "Empresa no encontrada"));
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

    @PutMapping("/actualizar/{ruc}")
    public ResponseEntity<String> actualizarProveedor(
            @PathVariable String ruc,
            @RequestBody Proveedor proveedorActualizado
    ) {

        proveedorActualizado.setRuc(ruc);

        boolean exito = Sistema.getInstancia()
                .actualizarDatosProveedor(ruc, proveedorActualizado);

        if (exito) {
            return ResponseEntity.ok("Proveedor actualizado con éxito");
        }

        return ResponseEntity
                .badRequest()
                .body("Error al actualizar: Proveedor no encontrado o datos inválidos");
    }

    @PostMapping("/agregar")
    public ResponseEntity<String> agregarProveedor(@RequestBody Proveedor nuevoProveedor) {

        boolean creado = Sistema.getInstancia()
                .registrarProveedor(nuevoProveedor);

        if (creado) {
            return ResponseEntity.ok("Proveedor registrado exitosamente");
        }

        return ResponseEntity
                .badRequest()
                .body("Error al registrar el proveedor");
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> proveedores) {

        try {
            byte[] pdfBytes =
                    Sistema.getInstancia().generarPDFBytesProveedores(proveedores);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=proveedores.pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody List<Map<String, Object>> proveedores) {

        try {
            byte[] excelBytes =
                    Sistema.getInstancia().generarExcelBytesProveedores(proveedores);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=proveedores.xlsx")
                    .body(excelBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}