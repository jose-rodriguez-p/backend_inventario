package multiservicioRafael.invenatario.controller;

import multiservicioRafael.invenatario.facade.ClienteFachada;
import multiservicioRafael.invenatario.facade.MantenimientoFachada;
import multiservicioRafael.invenatario.facade.ServicioFachada;
import multiservicioRafael.invenatario.repository.UsuarioLogeado;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/mantenimiento")
public class ControladorMantenimiento {

    private final ClienteFachada clienteFachada = new ClienteFachada();
    private final ServicioFachada servicioFachada = new ServicioFachada();
    private final MantenimientoFachada mantenimientoFachada = new MantenimientoFachada();

    @GetMapping("/servicios")
    public ResponseEntity<?> listarServicios() {
        try {
            List<Map<String, Object>> lista = servicioFachada.listarServiciosConRepuestos();
            return ResponseEntity.ok(lista != null ? lista : List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/tecnicos")
    public ResponseEntity<?> listarTecnicos() {
        try {
            List<Map<String, Object>> lista = mantenimientoFachada.listarTecnicos();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/buscar-cliente/{dni}")
    public ResponseEntity<?> buscarClientePorDni(@PathVariable String dni) {
        try {
            Map<String, Object> encontrado = clienteFachada.buscarClienteConCarrosPorDni(dni);
            if (encontrado != null) {
                return ResponseEntity.ok(encontrado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Cliente no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarOrden(@RequestBody Map<String, Object> datos) {
        try {
            Map<String, Object> cliente = (Map<String, Object>) datos.get("cliente");
            String placa = (String) datos.get("placa");
            String estado = (String) datos.getOrDefault("estado", "Pendiente");
            String fecha = (String) datos.get("fecha");
            String nota = (String) datos.getOrDefault("nota", "");
            double precioManoObra = ((Number) datos.getOrDefault("precio_mano_obra", 0)).doubleValue();
            List<Map<String, Object>> items = (List<Map<String, Object>>) datos.get("items");

            if (cliente == null || items == null || items.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Datos incompletos: cliente e items son obligatorios"));
            }

            String dniCliente = (String) cliente.get("dni");

            Map<String, Object> resultado = mantenimientoFachada.registrarOrden(
                    dniCliente, placa, estado, fecha, nota, precioManoObra,
                    UsuarioLogeado.getUsuario(), items);

            if ("OK".equals(resultado.get("status"))) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultado);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarOrdenes(
            @RequestParam(defaultValue = "") String busqueda,
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "10") int porPagina) {
        try {
            List<Map<String, Object>> datos = mantenimientoFachada.listarOrdenes(busqueda, pagina, porPagina);
            int total = mantenimientoFachada.contarOrdenes(busqueda);
            int totalPaginas = (int) Math.ceil((double) total / porPagina);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("datos", datos);
            respuesta.put("totalRegistros", total);
            respuesta.put("totalPaginas", totalPaginas);
            respuesta.put("paginaActual", pagina);
            respuesta.put("porPagina", porPagina);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen() {
        try {
            Map<String, Object> resumen = mantenimientoFachada.obtenerResumen();
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PostMapping("/agregar-vehiculo")
    public ResponseEntity<?> agregarVehiculo(@RequestBody Map<String, Object> datos) {
        try {
            String dni = (String) datos.get("dni");
            String placa = (String) datos.get("placa");
            String marca = (String) datos.get("marca");
            String modelo = (String) datos.get("modelo");
            String anio = (String) datos.get("anio");

            Map<String, Object> clienteActual = clienteFachada.buscarClienteConCarrosPorDni(dni);
            if (clienteActual == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Cliente no encontrado"));
            }

            List<Map<String, String>> carros = (List<Map<String, String>>) clienteActual.get("carros");
            if (carros == null) carros = new ArrayList<>();

            Map<String, String> nuevoCarro = new HashMap<>();
            nuevoCarro.put("placa", placa);
            nuevoCarro.put("marca", marca);
            nuevoCarro.put("modelo", modelo);
            nuevoCarro.put("anio", anio);
            carros.add(nuevoCarro);

            Map<String, Object> payload = new HashMap<>();
            payload.put("cliente", clienteActual);
            payload.put("carros", carros);

            String resultado = clienteFachada.editarCliente(payload, UsuarioLogeado.getUsuario());
            if (resultado != null && resultado.startsWith("editado")) {
                return ResponseEntity.ok(Map.of("status", "OK"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", resultado != null ? resultado : "Error desconocido"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @PutMapping("/editar-estado")
    public ResponseEntity<?> editarEstado(@RequestBody Map<String, Object> datos) {
        try {
            String usuarioNombre = (String) datos.get("usuario_logueado");
            int idOrdenServicio = ((Number) datos.get("id_orden_servicio")).intValue();
            String nuevoEstado = (String) datos.get("nuevo_estado");

            Map<String, Object> resultado = mantenimientoFachada.editarEstadoOrden(usuarioNombre, idOrdenServicio, nuevoEstado);

            if ("OK".equals(resultado.get("status"))) {
                return ResponseEntity.ok(resultado);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultado);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/comprobante")
    public ResponseEntity<byte[]> comprobante(
            @PathVariable("id") int idOrdenServicio,
            @RequestParam(value = "tipoComprobante", required = false) String tipoComprobante,
            @RequestParam(value = "metodoPago", required = false) String metodoPago) {
        try {
            Map<String, Object> comprobante = mantenimientoFachada.obtenerComprobanteMantenimiento(idOrdenServicio);
            if (comprobante.get("idOrdenServicio") == null && comprobante.get("id_orden_servicio") == null) {
                return ResponseEntity.notFound().build();
            }
            if (tipoComprobante != null && !tipoComprobante.isBlank()) {
                comprobante.put("tipoComprobante", tipoComprobante);
                if (tipoComprobante.equalsIgnoreCase("Factura")) {
                    comprobante.put("serie", "F001");
                } else {
                    comprobante.put("serie", "B001");
                }
            }
            if (metodoPago != null && !metodoPago.isBlank()) {
                comprobante.put("metodoPago", metodoPago);
            }
            byte[] pdf = mantenimientoFachada.generarComprobanteMantenimientoPDF(comprobante);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=comprobante_mantenimiento_" + idOrdenServicio + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> ordenes) {
        try {
            byte[] pdfBytes = mantenimientoFachada.generarPDFBytesMantenimiento(ordenes);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=reporte_mantenimiento.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody List<Map<String, Object>> ordenes) {
        try {
            byte[] excelBytes = mantenimientoFachada.generarExcelBytesMantenimiento(ordenes);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .header("Content-Disposition", "attachment; filename=reporte_mantenimiento.xlsx")
                    .body(excelBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
