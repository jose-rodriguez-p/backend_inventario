package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Proveedor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ControladorProveedor {

    @GetMapping("/listar")
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(Sistema.getInstancia().obtenerListaProveedores());
    }

    @GetMapping("/buscar-ruc/{ruc}")
    public ResponseEntity<?> buscarRuc(@PathVariable String ruc) {
        var datosEmpresa = Sistema.getInstancia().consutaRuc(ruc);

        if (datosEmpresa != null && !datosEmpresa.isEmpty()) {
            return ResponseEntity.ok(datosEmpresa);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Empresa no encontrada"));
        }
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

    @PutMapping("/actualizar/{ruc}")
    public ResponseEntity<String> actualizarProveedor(@PathVariable String ruc, @RequestBody Proveedor proveedorActualizado) {
        proveedorActualizado.setRuc(ruc);
        boolean exito = Sistema.getInstancia().actualizarDatosProveedor(ruc, proveedorActualizado);
        if (exito) {
            return ResponseEntity.ok("Proveedor actualizado con éxito");
        } else {
            return ResponseEntity.status(400).body("Error al actualizar: Proveedor no encontrado o datos inválidos");
        }
    }

    @PostMapping("/agregar")
    public ResponseEntity<String> agregarProveedor(@RequestBody Proveedor nuevoProveedor) {
        boolean creado = Sistema.getInstancia().registrarProveedor(nuevoProveedor);
        if (creado) {
            return ResponseEntity.ok("Proveedor registrado exitosamente");
        } else {
            return ResponseEntity.status(400).body("Error al registrar el proveedor");
        }
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> proveedores) {
        try {
            byte[] pdfBytes = Sistema.getInstancia().generarPDFBytesProveedores(proveedores);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "proveedores.pdf");
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody List<Map<String, Object>> proveedores) {
        try {
            byte[] excelBytes = Sistema.getInstancia().generarExcelBytesProveedores(proveedores);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "proveedores.xlsx");
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
