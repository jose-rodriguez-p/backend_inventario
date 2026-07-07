package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.ClasesFachda.ProveedorFachada;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.UsuarioLogeado;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Proveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proveedores")
public class ControladorProveedor {

    private final ProveedorFachada proveedorFachada = new ProveedorFachada();

    @GetMapping("/listar")
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(
                proveedorFachada.obtenerListaProveedores()
        );
    }

    @GetMapping("/buscar-ruc/{ruc}")
    public ResponseEntity<?> buscarRuc(@PathVariable String ruc) {

        var datosEmpresa = proveedorFachada.consultaRuc(ruc);

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

        // TODO: Implementar envío de código en ProveedorFachada
        String resultado = "TODO: Implementar en ProveedorFachada";

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/correo/validar")
    public ResponseEntity<String> validarCodigo(@RequestBody Map<String, String> request) {

        String dni = request.get("dni");
        String codigo = request.get("codigo");

        // TODO: Implementar validación de código en ProveedorFachada
        String resultado = "TODO: Implementar en ProveedorFachada";

        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/actualizar/{ruc}")
    public ResponseEntity<String> actualizarProveedor(
            @PathVariable String ruc,
            @RequestBody Proveedor proveedorActualizado
    ) {

        proveedorActualizado.setRuc(ruc);

        boolean exito = proveedorFachada.actualizarDatosProveedor(proveedorActualizado, UsuarioLogeado.getUsuario());

        if (exito) {
            return ResponseEntity.ok("Proveedor actualizado con éxito");
        }

        return ResponseEntity
                .badRequest()
                .body("Error al actualizar: Proveedor no encontrado o datos inválidos");
    }

    @PostMapping("/agregar")
    public ResponseEntity<String> agregarProveedor(@RequestBody Proveedor nuevoProveedor) {

        boolean creado = proveedorFachada.registrarProveedor(nuevoProveedor, UsuarioLogeado.getUsuario());

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
            byte[] pdfBytes = proveedorFachada.generarPDFBytesProveedores(proveedores);

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
            byte[] excelBytes = proveedorFachada.generarExcelBytesProveedores(proveedores);

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