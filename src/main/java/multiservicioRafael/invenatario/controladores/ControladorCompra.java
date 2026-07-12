package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.ClasesFachda.CompraFachada;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compras")
public class ControladorCompra {

    private final CompraFachada compraFachada = new CompraFachada();

    @GetMapping("/listar")
    public ResponseEntity<?> listarCompras() {
        try {
            List<Map<String, Object>> compras = compraFachada.listarCompras();
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<?> obtenerDetalle(@PathVariable int id) {
        try {
            List<Map<String, Object>> detalle = compraFachada.obtenerDetalle(id);
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Map<String, Object> datos) {
        try {
            String rucProveedor = (String) datos.get("ruc_proveedor");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) datos.get("items");

            if (rucProveedor == null || items == null || items.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Datos incompletos: proveedor e items son obligatorios");
            }

            String resultado = compraFachada.registrarCompra(rucProveedor, items);

            if ("COMPRA_REGISTRADA".equals(resultado)) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno: " + e.getMessage());
        }
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> compras) {
        try {
            byte[] pdfBytes = compraFachada.generarPDFBytesCompras(compras);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=compras.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody List<Map<String, Object>> compras) {
        try {
            byte[] excelBytes = compraFachada.generarExcelBytesCompras(compras);
            return ResponseEntity.ok()
                    .header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header("Content-Disposition", "attachment; filename=compras.xlsx")
                    .body(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
