package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.ClasesFachda.ClienteFachada;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.MantenimientoDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ServicioDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.UsuarioLogeado;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Servicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/mantenimiento")
public class ControladorMantenimiento {

    private final ClienteFachada clienteFachada = new ClienteFachada();
    private final ServicioDao servicioDao = new ServicioDao();
    private final MantenimientoDao mantenimientoDao = new MantenimientoDao();

    @GetMapping("/servicios")
    public ResponseEntity<?> listarServicios() {
        try {
            List<Servicio> lista = servicioDao.listarServicios();
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }

    @GetMapping("/tecnicos")
    public ResponseEntity<?> listarTecnicos() {
        try {
            List<Map<String, Object>> lista = mantenimientoDao.listarTecnicos();
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
            String descripcionVehiculo = (String) datos.get("descripcion_vehiculo");
            double precioManoObra = ((Number) datos.getOrDefault("precio_mano_obra", 0)).doubleValue();
            double precioTotal = ((Number) datos.getOrDefault("precio_total", 0)).doubleValue();
            int idEstado = ((Number) datos.getOrDefault("id_estado", 1)).intValue();
            String nota = (String) datos.getOrDefault("nota", "");
            List<Map<String, Object>> items = (List<Map<String, Object>>) datos.get("items");

            if (cliente == null || items == null || items.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Datos incompletos: cliente e items son obligatorios"));
            }

            String dniCliente = (String) cliente.get("dni");
            String nombreCliente = (String) cliente.get("nombre");
            if (cliente.get("apellido_paterno") != null) {
                nombreCliente += " " + cliente.get("apellido_paterno");
            }
            if (cliente.get("apellido_materno") != null) {
                nombreCliente += " " + cliente.get("apellido_materno");
            }

            Map<String, Object> resultado = mantenimientoDao.registrarOrden(
                dniCliente, nombreCliente.trim(), descripcionVehiculo,
                precioManoObra, precioTotal, idEstado, nota,
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
            List<Map<String, Object>> datos = mantenimientoDao.listarOrdenes(busqueda, pagina, porPagina);
            int total = mantenimientoDao.contarOrdenes(busqueda);
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
            Map<String, Object> resumen = mantenimientoDao.obtenerResumen();
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno: " + e.getMessage()));
        }
    }
}
