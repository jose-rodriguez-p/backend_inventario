package multiservicioRafael.invenatario.controller;

import multiservicioRafael.invenatario.facade.VentasFachada;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/ventas")
public class ControladorVentas {

    private final VentasFachada ventasFachada = new VentasFachada();

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrarVenta(@RequestBody Map<String, Object> datos) {
        try {
            String usuarioNombre = (String) datos.get("id_usuario");
            Map<String, Object> cliente = (Map<String, Object>) datos.get("cliente");
            String clienteDni = (String) cliente.get("dni");
            String tipoComprobante = (String) datos.get("tipo_comprobante");
            String serie = (String) datos.get("serie");
            String estado = (String) datos.get("estado");
            String metodoPago = (String) datos.get("metodo_pago");
            String fechaEmision = (String) datos.get("fecha_emision");
            if (fechaEmision != null && fechaEmision.length() == 10) {
                fechaEmision += " 00:00:00";
            }
            double descuentoGlobal = ((Number) datos.getOrDefault("descuento_global", 0)).doubleValue();
            String tipoDescuento = (String) datos.getOrDefault("descuento_tipo", "%");
            if (tipoDescuento != null) {
                if (tipoDescuento.equals("%")) {
                    tipoDescuento = "porcentaje";
                } else if (tipoDescuento.equals("S/")) {
                    tipoDescuento = "monto";
                }
            }
            String nota = (String) datos.get("nota");
            List<Map<String, Object>> detalle = (List<Map<String, Object>>) datos.get("items");

            Map<String, Object> resultado = ventasFachada.registrarVenta(
                    usuarioNombre, clienteDni, tipoComprobante, serie,
                    estado, metodoPago, fechaEmision, descuentoGlobal,
                    tipoDescuento, nota, detalle
            );

            String status = String.valueOf(resultado.get("status"));
            if ("OK".equals(status)) {
                return ResponseEntity.ok(Map.of(
                        "status", "OK",
                        "message", "Venta registrada exitosamente",
                        "id_orden_venta", resultado.get("id_orden_venta") != null ? resultado.get("id_orden_venta") : 0
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", status));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("status", "ERROR", "message", "Error interno del servidor: " + e.getMessage())
            );
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarVentas(
            @RequestParam(required = false, defaultValue = "") String busqueda,
            @RequestParam(required = false, defaultValue = "1") int pagina,
            @RequestParam(required = false, defaultValue = "10") int porPagina) {
        try {
            Map<String, Object> resultado = ventasFachada.listarVentas(busqueda, pagina, porPagina);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of());
        }
    }

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> comprobante(@PathVariable("id") int idOrdenVenta) {
        try {
            Map<String, Object> comprobante = ventasFachada.obtenerComprobanteVenta(idOrdenVenta);
            if (comprobante.get("id_orden_venta") == null) {
                return ResponseEntity.notFound().build();
            }
            byte[] pdf = ventasFachada.generarComprobanteVentaPDF(comprobante);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=comprobante_venta_" + idOrdenVenta + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> ventas) {
        try {
            byte[] pdfBytes = ventasFachada.generarPDFBytesVentas(ventas);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=reporte_ventas.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody List<Map<String, Object>> ventas) {
        try {
            byte[] excelBytes = ventasFachada.generarExcelBytesVentas(ventas);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=reporte_ventas.xlsx")
                    .body(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}