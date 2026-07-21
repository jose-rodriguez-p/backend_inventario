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
            String dniCliente, String placa, String estado, String fecha,
            String nota, double precioManoObra, String usuarioLogueado,
            List<Map<String, Object>> items) {

        Map<String, Object> resultado = new HashMap<>();
        Connection cn = null;
        try {
            cn = ConexionDB.getInstance().getConnection();
            ObjectMapper mapper = new ObjectMapper();
            String jsonDetalle = mapper.writeValueAsString(items);

            String sql = "{ ? = call public.fn_registrar_orden_mantenimiento(?, ?, ?, ?, ?::date, ?::text, ?::numeric, ?::jsonb) }";

            try (CallableStatement cs = cn.prepareCall(sql)) {
                cs.registerOutParameter(1, Types.VARCHAR);
                cs.setString(2, usuarioLogueado);
                cs.setString(3, dniCliente);
                cs.setString(4, placa);
                cs.setString(5, estado);
                cs.setString(6, fecha);
                cs.setString(7, nota);
                cs.setDouble(8, precioManoObra);
                cs.setString(9, jsonDetalle);

                cs.execute();

                String respuestaBD = cs.getString(1);
                if (respuestaBD != null && respuestaBD.startsWith("OK")) {
                    resultado.put("status", "OK");
                    resultado.put("mensaje", "Orden registrada correctamente.");
                } else {
                    resultado.put("status", "ERROR");
                    resultado.put("mensaje", respuestaBD);
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
            if (busqueda == null || busqueda.trim().isEmpty()) {
                ps.setNull(1, java.sql.Types.VARCHAR);
            } else {
                ps.setString(1, busqueda.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("idOrdenServicio", getValorSeguro(rs, "id_orden_servicio"));
                    fila.put("hora", String.valueOf(getValorSeguro(rs, "hora")));

                    Object fRegistro = getValorSeguro(rs, "fecha_registro");
                    Object fServicio = getValorSeguro(rs, "fecha_servicio");
                    Object fCulminacion = getValorSeguro(rs, "fecha_culminacion");
                    Object fFecha = getValorSeguro(rs, "fecha");

                    fila.put("fechaRegistro", fRegistro != null ? fRegistro : fFecha);
                    fila.put("fechaServicio", fServicio != null ? fServicio : fFecha);
                    fila.put("fechaCulminacion", fCulminacion);
                    fila.put("fecha", fFecha != null ? fFecha : (fServicio != null ? fServicio : fRegistro));

                    fila.put("cliente", getValorSeguro(rs, "cliente"));
                    fila.put("dniCliente", getValorSeguro(rs, "dni"));
                    fila.put("descripcionVehiculo", getValorSeguro(rs, "vehiculo"));

                    Object manoObraVal = getValorSeguro(rs, "mano_obra");
                    fila.put("precioManoObra", manoObraVal instanceof Number ? ((Number) manoObraVal).doubleValue() : 0.0);

                    Object totalVal = getValorSeguro(rs, "total");
                    fila.put("precioTotal", totalVal instanceof Number ? ((Number) totalVal).doubleValue() : 0.0);

                    fila.put("estado", getValorSeguro(rs, "estado"));
                    fila.put("nota", getValorSeguro(rs, "nota"));
                    fila.put("usuarioRegistro", getValorSeguro(rs, "usuario_registro"));

                    Object detalleVal = getValorSeguro(rs, "detalle");
                    String detalleJsonRaw = detalleVal != null ? detalleVal.toString() : null;
                    Object detalleParsed = null;
                    List<String> serviciosList = new ArrayList<>();
                    List<String> tecnicosList = new ArrayList<>();

                    Object tecnicosRaw = getValorSeguro(rs, "tecnicos");
                    if (tecnicosRaw != null && !tecnicosRaw.toString().isEmpty()) {
                        tecnicosList.add(tecnicosRaw.toString());
                    }

                    if (detalleJsonRaw != null && !detalleJsonRaw.isEmpty()) {
                        try {
                            JSONArray arr = new JSONArray(detalleJsonRaw);
                            detalleParsed = arr.toList();
                            System.out.println("Detalle parsed: " + detalleParsed);
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
                            System.out.println("Servicios list: " + serviciosList);
                            System.out.println("Tecnicos list: " + tecnicosList);
                        } catch (Exception jsonEx) {
                            System.out.println("Error parseando detalle JSON: " + jsonEx.getMessage());
                            jsonEx.printStackTrace();
                            detalleParsed = new ArrayList<>();
                        }
                    } else {
                        System.out.println("Detalle JSON is null or empty");
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

            // Si la orden quedó Completada, la enlazamos a la caja abierta de quien
            // realiza la acción (no de quien la registró originalmente), igual que
            // se hace con las ventas en VentasDao.vincularVentaConCajaAbierta.
            if ("OK".equals(resultado.get("status")) && "Completado".equalsIgnoreCase(nuevoEstado)) {
                vincularServicioConCajaAbierta(cn, idOrdenServicio, usuarioNombre);
            }
        } catch (Exception e) {
            System.out.println("Error editarEstadoOrden: " + e.getMessage());
            resultado.put("status", "ERROR");
            resultado.put("mensaje", e.getMessage());
        }
        return resultado;
    }

    /**
     * Enlaza una orden de mantenimiento ya Completada con la caja abierta del
     * usuario que la completó, insertando en caja_servicio (id_cierre_caja,
     * id_orden_serv). Evita duplicados por si el estado se vuelve a guardar
     * como "Completado" más de una vez para la misma orden.
     */
    private void vincularServicioConCajaAbierta(Connection conexion, int idOrdenServicio, String usuarioNombre) {
        String sqlYaEnlazado = "SELECT 1 FROM public.caja_servicio WHERE id_orden_serv = ?";
        try (PreparedStatement psCheck = conexion.prepareStatement(sqlYaEnlazado)) {
            psCheck.setInt(1, idOrdenServicio);
            try (ResultSet rsCheck = psCheck.executeQuery()) {
                if (rsCheck.next()) {
                    return; // ya estaba enlazada a una caja, no duplicar
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String sqlCaja = "SELECT cc.id_cierre_caja FROM public.cierre_caja cc " +
                "JOIN public.usuario u ON u.id_usuario = cc.id_usuario " +
                "WHERE u.usuario = ? AND cc.estado_caja = 'A' " +
                "ORDER BY cc.id_cierre_caja DESC LIMIT 1";
        try (PreparedStatement ps = conexion.prepareStatement(sqlCaja)) {
            ps.setString(1, usuarioNombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idCierreCaja = rs.getInt("id_cierre_caja");
                    String sqlInsert = "INSERT INTO public.caja_servicio (id_cierre_caja, id_orden_serv) VALUES (?, ?)";
                    try (PreparedStatement psInsert = conexion.prepareStatement(sqlInsert)) {
                        psInsert.setInt(1, idCierreCaja);
                        psInsert.setInt(2, idOrdenServicio);
                        psInsert.executeUpdate();
                    }
                } else {
                    System.out.println("Aviso: la orden " + idOrdenServicio +
                            " se completó pero " + usuarioNombre + " no tiene caja abierta.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> obtenerComprobanteMantenimiento(int idOrdenServicio) {
        List<Map<String, Object>> lista = listarOrdenes(String.valueOf(idOrdenServicio));
        for (Map<String, Object> orden : lista) {
            Object idVal = orden.get("idOrdenServicio");
            if (idVal != null && Integer.parseInt(idVal.toString()) == idOrdenServicio) {
                return orden;
            }
        }
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return new HashMap<>();
    }

    private Object getValorSeguro(ResultSet rs, String colName) {
        try {
            return rs.getObject(colName);
        } catch (Exception e) {
            return null;
        }
    }
}