package multiservicioRafael.invenatario.repository;

import java.sql.CallableStatement;
import multiservicioRafael.invenatario.config.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.repository.Interfaces.MantenimientoDaoInterface;

// --- ÚNICO IMPORT DE JSON REQUERIDO (DE TU LIBRERÍA INSTALADA) ---
import org.json.JSONArray;
import tools.jackson.databind.ObjectMapper;

public class MantenimientoDao implements MantenimientoDaoInterface {

    @Override
    public Map<String, Object> registrarOrden(
            String dniCliente, String nombreCliente, String descripcionVehiculo,
            double precioManoObra, double precioTotal, int idEstado, String nota,
            String usuarioLogueado, List<Map<String, Object>> items) {

        Map<String, Object> resultado = new HashMap<>();
        Connection cn = null;

        try {
            cn = ConexionDB.getInstance().getConnection();

            ObjectMapper mapper = new ObjectMapper();
            String jsonDetalle = mapper.writeValueAsString(items);
            String sql = "{ ? = call public.fn_registrar_orden_mantenimiento(?, ?, ?, ?, ?, ?, ?::jsonb) }";

            try (CallableStatement cs = cn.prepareCall(sql)) {
                cs.registerOutParameter(1, Types.VARCHAR);
                cs.setString(2, usuarioLogueado);                      
                cs.setString(3, dniCliente);                           
                cs.setString(4, descripcionVehiculo);                  
                cs.setString(5, "Pendiente");                        
                cs.setDate(6, new java.sql.Date(System.currentTimeMillis()));
                cs.setString(7, nota);                                
                cs.setString(8, jsonDetalle);                          
                cs.execute();
                String respuestaBD = cs.getString(1);

                if (respuestaBD != null && respuestaBD.startsWith("OK")) {
                    resultado.put("status", "OK");
                    resultado.put("mensaje", "Orden registrada correctamente.");
                } else {
                    resultado.put("status", "ERROR");
                    resultado.put("mensaje", respuestaBD); // Captura el mensaje descriptivo de tu función
                }
            }

        } catch (Exception e) {
            System.out.println("Error registrarOrden: " + e.getMessage());
            resultado.put("status", "ERROR");
            resultado.put("mensaje", "Error en servidor: " + e.getMessage());
        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (Exception ex) {
                    // Silencioso
                }
            }
        }
        return resultado;
    }

    @Override
    public List<Map<String, Object>> listarOrdenes(String busqueda) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_ordenes_mantenimiento(?)";

        try (Connection cn = ConexionDB.getInstance().getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, busqueda);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();

                    fila.put("idOrdenServicio", rs.getInt("id_orden_servicio"));

                    // Formateo de fecha y hora
                    String horaRaw = rs.getString("hora");
                    String fechaRaw = rs.getString("fecha");
                    String hora = (horaRaw != null && horaRaw.length() >= 5) ? horaRaw.substring(0, 5) : "";
                    String fecha = (fechaRaw != null && fechaRaw.length() >= 10) ? fechaRaw.substring(0, 10) : "";

                    fila.put("hora", fecha + " " + hora);
                    fila.put("fecha", fecha);
                    fila.put("cliente", rs.getString("cliente"));
                    fila.put("dniCliente", rs.getString("dni"));
                    fila.put("descripcionVehiculo", rs.getString("vehiculo"));
                    fila.put("precioTotal", rs.getDouble("total"));
                    fila.put("estado", rs.getString("estado"));
                    fila.put("nota", rs.getString("nota"));
                    fila.put("usuarioRegistro", rs.getString("usuario_registro"));

                    // 1. Leemos la columna JSON de la base de datos
                    String detalleJsonRaw = rs.getString("detalle");

                    // 2. Convertimos usando tu dependencia "org.json"
                    Object detalleParsed = null;
                    List<String> serviciosList = new ArrayList<>();
                    List<String> tecnicosList = new ArrayList<>();

                    if (detalleJsonRaw != null && !detalleJsonRaw.isEmpty()) {
                        try {
                            JSONArray arr = new JSONArray(detalleJsonRaw);
                            detalleParsed = arr.toList();
                            for (int i = 0; i < arr.length(); i++) {
                                Object sv = arr.getJSONObject(i).opt("nombre_servicio");
                                if (sv != null) {
                                    serviciosList.add(sv.toString());
                                }
                                Object tw = arr.getJSONObject(i).opt("nombre_trabajador");
                                if (tw != null && !tecnicosList.contains(tw.toString())) {
                                    tecnicosList.add(tw.toString());
                                }
                            }
                        } catch (Exception jsonEx) {
                            System.out.println("Error parseando detalle JSON: " + jsonEx.getMessage());
                            detalleParsed = new ArrayList<>();
                        }
                    } else {
                        detalleParsed = new ArrayList<>();
                    }

                    fila.put("servicios", String.join(", ", serviciosList));
                    fila.put("tecnicos", String.join(", ", tecnicosList));
                    fila.put("detalle", detalleParsed);
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            System.out.println("Error listarOrdenes: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Map<String, Object> editarEstadoOrden(String usuarioNombre, int idOrdenServicio, String nuevoEstado) {
        Map<String, Object> resultado = new HashMap<>();
        try (Connection cn = ConexionDB.getInstance().getConnection()) {
            String sql = "SELECT public.fn_editar_estado_orden_mantenimiento(?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, usuarioNombre);
                ps.setInt(2, idOrdenServicio);
                ps.setString(3, nuevoEstado);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String res = rs.getString(1);
                        if (res != null && res.startsWith("OK")) {
                            resultado.put("status", "OK");
                            resultado.put("mensaje", res);
                        } else {
                            resultado.put("status", "ERROR");
                            resultado.put("mensaje", res);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error editarEstadoOrden: " + e.getMessage());
            resultado.put("status", "ERROR");
            resultado.put("mensaje", e.getMessage());
        }
        return resultado;
    }
}
