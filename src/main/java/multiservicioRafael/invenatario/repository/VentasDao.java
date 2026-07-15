
package multiservicioRafael.invenatario.repository;

import multiservicioRafael.invenatario.config.ConexionDB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.repository.Interfaces.VentasDaoInterface;
import tools.jackson.databind.ObjectMapper;

public class VentasDao implements VentasDaoInterface {

    @Override
    public String registrarVenta(String usuarioNombre, String clienteDni, String tipoComprobante, String serie,
                                 String estado, String metodoPago, String fechaEmision, double descuentoGlobal,
                                 String tipoDescuento, String nota, List<Map<String, Object>> detalle) {
        String sql = "{ ? = call public.fn_registrar_venta(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }"; // Added one more ?
        String resultado = "error_desconocido";
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             CallableStatement cs = conexion.prepareCall(sql)) {
            
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            cs.setString(2, usuarioNombre);
            cs.setString(3, clienteDni);
            cs.setString(4, tipoComprobante);
            cs.setString(5, serie);
            cs.setString(6, estado);
            cs.setString(7, metodoPago);
            cs.setTimestamp(8, java.sql.Timestamp.valueOf(fechaEmision));
            cs.setBigDecimal(9, java.math.BigDecimal.valueOf(descuentoGlobal));
            cs.setString(10, tipoDescuento);
            cs.setString(11, nota);
            
            String detalleJsonStr = convertirDetalleAJson(detalle);
            org.postgresql.util.PGobject jsonObject = new org.postgresql.util.PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(detalleJsonStr);
            cs.setObject(12, jsonObject);
            
            cs.execute();
            resultado = cs.getString(1).trim();
            
        } catch (Exception e) {
            e.printStackTrace();
            resultado = "error_backend: " + e.getMessage();
        }
        
        return resultado;
    }

    private String convertirDetalleAJson(List<Map<String, Object>> detalle) {
        if (detalle == null || detalle.isEmpty()) {
            return "[]";
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> transformedDetalle = new ArrayList<>();
            for (Map<String, Object> item : detalle) {
                Map<String, Object> transformed = new java.util.HashMap<>();
                // Get repuesto name from the selected repuesto
                Object repuesto = item.get("repuesto");
                String nombreRepuesto = "";
                if (repuesto instanceof Map) {
                    nombreRepuesto = (String) ((Map) repuesto).get("nombre");
                }
                // Get values with safe defaults
                Object cantidadObj = item.get("cantidad");
                int cantidad = (cantidadObj instanceof Number) ? ((Number) cantidadObj).intValue() : 0;
                
                Object precioUnitObj = item.get("precio_unit");
                double precioUnitario = (precioUnitObj instanceof Number) ? ((Number) precioUnitObj).doubleValue() : 0.0;
                
                Object descuentoObj = item.get("descuento");
                double descuentoPorcentaje = (descuentoObj instanceof Number) ? ((Number) descuentoObj).doubleValue() : 0.0;
                
                transformed.put("nombre_repuesto", nombreRepuesto);
                transformed.put("cantidad", cantidad);
                transformed.put("precio_unitario", precioUnitario);
                transformed.put("descuento_porcentaje", descuentoPorcentaje);
                transformedDetalle.add(transformed);
            }
            String json = mapper.writeValueAsString(transformedDetalle);
            System.out.println("Detalle JSON generado: " + json); // Log for debugging
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    @Override
    public Map<String, Object> listarVentas(String busqueda, int pagina, int porPagina) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_ventas(?, ?, ?)";
        int totalRegistros = 0;
        
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            
            if (busqueda == null || busqueda.isEmpty()) {
                ps.setNull(1, java.sql.Types.VARCHAR);
            } else {
                ps.setString(1, busqueda);
            }
            ps.setInt(2, pagina);
            ps.setInt(3, porPagina);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new java.util.HashMap<>();
                    fila.put("n_orden", rs.getInt("n_orden"));
                    fila.put("productos", rs.getString("productos"));
                    fila.put("fecha", rs.getString("fecha"));
                    fila.put("hora", rs.getString("hora"));
                    fila.put("cliente", rs.getString("cliente"));
                    fila.put("dni", rs.getString("dni"));
                    fila.put("vendedor", rs.getString("vendedor"));
                    fila.put("metodo_pago", rs.getString("metodo_pago"));
                    fila.put("total", rs.getDouble("total"));
                    lista.add(fila);
                }
            }
            
            // Obtener total de registros sin paginación
            String sqlCount = "SELECT COUNT(*) FROM public.orden_venta";
            try (PreparedStatement psCount = conexion.prepareStatement(sqlCount);
                 ResultSet rsCount = psCount.executeQuery()) {
                if (rsCount.next()) {
                    totalRegistros = rsCount.getInt(1);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Map<String, Object> resultado = new java.util.HashMap<>();
        resultado.put("ventas", lista);
        resultado.put("totalRegistros", totalRegistros);
        return resultado;
    }
}
