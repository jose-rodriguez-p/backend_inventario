package multiservicioRafael.invenatario.controller;

import multiservicioRafael.invenatario.facade.CajaFachada;
import multiservicioRafael.invenatario.repository.UsuarioLogeado;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/caja")
public class ControladorCaja {

    private final CajaFachada cajaFachada = new CajaFachada();

    @GetMapping("/estado")
    public ResponseEntity<Map<String, Object>> estado() {
        try {
            String usuario = UsuarioLogeado.getUsuario();
            Map<String, Object> caja = cajaFachada.obtenerCajaAbierta(usuario);
            Map<String, Object> body = new HashMap<>();
            body.put("abierta", caja != null);
            body.put("caja", caja);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of());
        }
    }

    @PostMapping("/abrir")
    public ResponseEntity<Map<String, Object>> abrir(@RequestBody Map<String, Object> datos) {
        try {
            String usuario = UsuarioLogeado.getUsuario();
            if (usuario == null || usuario.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("status", "ERROR", "message", "No se identificó al usuario"));
            }
            double saldoInicial = ((Number) datos.getOrDefault("saldo_inicial", 0)).doubleValue();
            String resultado = cajaFachada.abrirCaja(usuario, saldoInicial);
            if ("OK".equals(resultado)) {
                return ResponseEntity.ok(Map.of("status", "OK", "message", "Caja abierta correctamente"));
            }
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", resultado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "ERROR", "message", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/resumen")
    public ResponseEntity<Map<String, Object>> resumen(@PathVariable("id") int idCierreCaja) {
        try {
            return ResponseEntity.ok(cajaFachada.obtenerResumenCierre(idCierreCaja));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of());
        }
    }

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<Map<String, Object>> cerrar(@PathVariable("id") int idCierreCaja,
                                                      @RequestBody Map<String, Object> datos) {
        try {
            double totCajero = ((Number) datos.getOrDefault("tot_ventas_cajero", 0)).doubleValue();
            String resultado = cajaFachada.cerrarCaja(idCierreCaja, totCajero);
            if ("OK".equals(resultado)) {
                return ResponseEntity.ok(Map.of("status", "OK", "message", "Caja cerrada correctamente"));
            }
            return ResponseEntity.badRequest().body(Map.of("status", "ERROR", "message", resultado));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "ERROR", "message", "Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> comprobante(@PathVariable("id") int idCierreCaja) {
        try {
            Map<String, Object> resumen = cajaFachada.obtenerResumenCierre(idCierreCaja);
            byte[] pdf = cajaFachada.generarComprobanteCierrePDF(resumen);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=cierre_caja_" + idCierreCaja + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}