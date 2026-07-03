
package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/ventas")
public class ControladorVentas {

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
            // Convert fecha to correct format for timestamp (add time if missing)
            if (fechaEmision != null && fechaEmision.length() == 10) {
                fechaEmision += " 00:00:00";
            }
            double descuentoGlobal = ((Number) datos.getOrDefault("descuento_global", 0)).doubleValue();
            String tipoDescuento = (String) datos.getOrDefault("descuento_tipo", "%");
            // Convert tipoDescuento to match backend expectation
            if (tipoDescuento != null) {
                if (tipoDescuento.equals("%")) {
                    tipoDescuento = "porcentaje";
                } else if (tipoDescuento.equals("S/")) {
                    tipoDescuento = "monto";
                }
            }
            String nota = (String) datos.get("nota");
            List<Map<String, Object>> detalle = (List<Map<String, Object>>) datos.get("items");
            
            String resultado = Sistema.getInstancia().registrarVenta(
                    usuarioNombre, clienteDni, tipoComprobante, serie,
                    estado, metodoPago, fechaEmision, descuentoGlobal,
                    tipoDescuento, nota, detalle
            );
            
            if (resultado.equals("OK")) {
                // Get the last inserted order ID (we can adjust later if needed)
                return ResponseEntity.ok(Map.of(
                    "status", "OK",
                    "message", "Venta registrada exitosamente",
                    "id_orden_venta", 1 // Temporary
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", resultado));
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
            Map<String, Object> resultado = Sistema.getInstancia().listarVentas(busqueda, pagina, porPagina);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of());
        }
    }
}
