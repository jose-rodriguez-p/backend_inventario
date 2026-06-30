package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Categoria;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Producto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/productos")
public class ControladorProducto {

    @GetMapping("/listar")
    public ResponseEntity<List<Producto>> listarProductos() {
        return ResponseEntity.ok(
                Sistema.getInstancia().obtenerListaProductos()
        );
    }

    @GetMapping("/categorias")
    public ResponseEntity<?> listarCategorias() {
        List<Categoria> categorias = Sistema.getInstancia().listarCategoria();
        if (categorias == null || categorias.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("LISTA_VACIA");
        }
        List<String> nombres = categorias.stream()
                .map(Categoria::getNombre)
                .collect(Collectors.toList());
        return ResponseEntity.ok(nombres);
    }

    @GetMapping("/categorias-marcas")
    public ResponseEntity<?> listarCategoriasConMarcas() {
        try {
            List<Map<String, Object>> lista = Sistema.getInstancia().listarCategoriasConMarcas();
            if (lista == null || lista.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("LISTA_VACIA");
            }
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            System.out.println("Error en controlador listarCategoriasConMarcas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }

    @GetMapping("/listar-repuestos")
    public ResponseEntity<?> listarRepuestos() {
        try {
            List<Map<String, Object>> lista = Sistema.getInstancia().listarRepuestos();
            if (lista == null || lista.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("LISTA_VACIA");
            }
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            System.out.println("Error en controlador listarRepuestos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }

    @PutMapping("/editar-repuesto")
    public ResponseEntity<String> editarRepuesto(@RequestBody Map<String, Object> datos) {
        try {
            String nombre = (String) datos.get("nombre_repuesto");
            String categoriaNombre = (String) datos.get("nombre_categoria");
            String marcaNombre = (String) datos.get("nombre_marca");
            String proveedorNombre = (String) datos.get("nombre_proveedor");
            int cantidad = ((Number) datos.get("cantidad")).intValue();
            java.math.BigDecimal precioCompra = new java.math.BigDecimal(datos.get("precio_compra").toString());
            java.math.BigDecimal precioVenta = new java.math.BigDecimal(datos.get("precio_venta").toString());
            int stockMinimo = ((Number) datos.get("stock_minimo")).intValue();
            String estado = (String) datos.get("estado");

            String respuesta = Sistema.getInstancia().editarRepuestoSistema(
                nombre, categoriaNombre, marcaNombre, proveedorNombre,
                cantidad, precioCompra, precioVenta, stockMinimo, estado
            );

            if (respuesta.equals("OK")) {
                return ResponseEntity.ok("REPUESTO_ACTUALIZADO");
            } else {
                return ResponseEntity.badRequest().body(respuesta);
            }
        } catch (Exception e) {
            System.out.println("Error en controlador editarRepuesto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }

    @PostMapping("/agregar-repuesto")
    public ResponseEntity<String> agregarRepuesto(@RequestBody Map<String, Object> datos) {
        try {
            String nombre = (String) datos.get("nombre");
            String categoriaNombre = (String) datos.get("nombre_categoria");
            String marcaNombre = (String) datos.get("marca");
            String proveedorNombre = (String) datos.get("nombre_proveedor");
            int cantidad = ((Number) datos.get("cantidad")).intValue();
            java.math.BigDecimal precioCompra = new java.math.BigDecimal(datos.get("precio_compra").toString());
            java.math.BigDecimal precioVenta = new java.math.BigDecimal(datos.get("precio_venta").toString());
            int stockMinimo = ((Number) datos.get("stock_minimo")).intValue();
            String estado = (String) datos.get("estado");

            String respuesta = Sistema.getInstancia().agregarRepuestoSistema(
                nombre, categoriaNombre, marcaNombre, proveedorNombre,
                cantidad, precioCompra, precioVenta, stockMinimo, estado
            );

            if (respuesta.equals("OK")) {
                return ResponseEntity.ok("REPUESTO_REGISTRADO");
            } else {
                return ResponseEntity.badRequest().body(respuesta);
            }
        } catch (Exception e) {
            System.out.println("Error en controlador agregarRepuesto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR_INTERNO");
        }
    }

    @PostMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPDF(@RequestBody List<Map<String, Object>> repuestos) {
        try {
            byte[] pdfBytes = Sistema.getInstancia().generarPDFBytesRepuestos(repuestos);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_productos.pdf");
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            System.out.println("Error al exportar PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestBody List<Map<String, Object>> repuestos) {
        try {
            byte[] excelBytes = Sistema.getInstancia().generarExcelBytesRepuestos(repuestos);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "reporte_productos.xlsx");
            return ResponseEntity.ok().headers(headers).body(excelBytes);
        } catch (Exception e) {
            System.out.println("Error al exportar Excel: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
