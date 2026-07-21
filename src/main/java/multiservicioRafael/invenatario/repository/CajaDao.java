package multiservicioRafael.invenatario.repository;

import multiservicioRafael.invenatario.config.ConexionDB;
import multiservicioRafael.invenatario.repository.Interfaces.CajaDaoInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO de apertura/cierre de caja. No crea ni modifica funciones ni tablas:
 * usa exclusivamente sentencias SQL directas sobre cierre_caja y caja_venta,
 * que ya existen en el esquema pero no tenían ninguna capa de negocio.
 */
public class CajaDao implements CajaDaoInterface {

    @Override
    public Map<String, Object> obtenerCajaAbierta(String usuarioNombre) {
        String sql = "SELECT cc.id_cierre_caja, cc.fec_apertura, cc.saldo_inicial " +
                "FROM public.cierre_caja cc " +
                "JOIN public.usuario u ON u.id_usuario = cc.id_usuario " +
                "WHERE u.usuario = ? AND cc.estado_caja = 'A' " +
                "ORDER BY cc.id_cierre_caja DESC LIMIT 1";
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuarioNombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> caja = new HashMap<>();
                    caja.put("id_cierre_caja", rs.getInt("id_cierre_caja"));
                    caja.put("fec_apertura", rs.getTimestamp("fec_apertura").toString());
                    caja.put("saldo_inicial", rs.getDouble("saldo_inicial"));
                    return caja;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String abrirCaja(String usuarioNombre, double saldoInicial) {
        try (Connection conexion = ConexionDB.getInstance().getConnection()) {

            if (obtenerCajaAbierta(usuarioNombre) != null) {
                return "error: ya tienes una caja abierta. Ciérrala antes de abrir otra.";
            }

            Integer idUsuario = null;
            try (PreparedStatement psUsr = conexion.prepareStatement(
                    "SELECT id_usuario FROM public.usuario WHERE usuario = ?")) {
                psUsr.setString(1, usuarioNombre);
                try (ResultSet rs = psUsr.executeQuery()) {
                    if (rs.next()) idUsuario = rs.getInt("id_usuario");
                }
            }
            if (idUsuario == null) return "error: usuario no encontrado";

            String sql = "INSERT INTO public.cierre_caja " +
                    "(id_usuario, fec_apertura, saldo_inicial, tot_ventas_sistema, tot_ventas_cajero, estado_caja) " +
                    "VALUES (?, ?, ?, 0, 0, 'A')";
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setBigDecimal(3, java.math.BigDecimal.valueOf(saldoInicial));
                ps.executeUpdate();
            }
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "error_backend: " + e.getMessage();
        }
    }

    @Override
    public Map<String, Object> obtenerResumenCierre(int idCierreCaja) {
        Map<String, Object> resumen = new HashMap<>();
        List<Map<String, Object>> ventas = new ArrayList<>();
        double totSistema = 0;

        String sqlCabecera = "SELECT cc.id_cierre_caja, cc.fec_apertura, cc.fec_cierre, cc.saldo_inicial, " +
                "cc.tot_ventas_cajero, cc.estado_caja, t.nombre AS nombre_usuario, t.apellido_paterno AS ap_usuario " +
                "FROM public.cierre_caja cc " +
                "JOIN public.usuario u ON u.id_usuario = cc.id_usuario " +
                "JOIN public.trabajador t ON t.id_trabajador = u.id_trabajador " +
                "WHERE cc.id_cierre_caja = ?";

        // Solo se cuentan ventas Pagadas (no Pendientes ni Anuladas): esto es lo que
        // "se contabiliza" en el cuadre de caja.
        String sqlVentas = "SELECT ov.id_orden_venta, ov.fecha_emision, ov.metodo_pago, ov.tipo_comprobante, " +
                "ov.serie, ov.correlativo, ov.precio_total, " +
                "COALESCE(c.nombre || ' ' || c.apellido_paterno, 'Cliente Varios') AS cliente " +
                "FROM public.caja_venta cv " +
                "JOIN public.orden_venta ov ON ov.id_orden_venta = cv.id_orden_venta " +
                "JOIN public.cliente c ON c.id_cliente = ov.id_cliente " +
                "WHERE cv.id_cierre_caja = ? AND ov.estado = 'Pagado' " +
                "ORDER BY ov.id_orden_venta";

        try (Connection conexion = ConexionDB.getInstance().getConnection()) {
            try (PreparedStatement ps = conexion.prepareStatement(sqlCabecera)) {
                ps.setInt(1, idCierreCaja);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        resumen.put("id_cierre_caja", rs.getInt("id_cierre_caja"));
                        resumen.put("fec_apertura", rs.getTimestamp("fec_apertura").toString());
                        resumen.put("fec_cierre", rs.getTimestamp("fec_cierre") != null ? rs.getTimestamp("fec_cierre").toString() : null);
                        resumen.put("saldo_inicial", rs.getDouble("saldo_inicial"));
                        resumen.put("tot_ventas_cajero", rs.getDouble("tot_ventas_cajero"));
                        resumen.put("estado_caja", rs.getString("estado_caja"));
                        resumen.put("usuario", (rs.getString("nombre_usuario") + " " + rs.getString("ap_usuario")).trim());
                    }
                }
            }
            try (PreparedStatement ps = conexion.prepareStatement(sqlVentas)) {
                ps.setInt(1, idCierreCaja);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> v = new HashMap<>();
                        v.put("n_orden", rs.getInt("id_orden_venta"));
                        v.put("fecha_emision", rs.getTimestamp("fecha_emision").toString());
                        v.put("metodo_pago", rs.getString("metodo_pago"));
                        v.put("comprobante", rs.getString("tipo_comprobante") + " " + rs.getString("serie") + "-" + rs.getString("correlativo"));
                        v.put("cliente", rs.getString("cliente"));
                        double total = rs.getDouble("precio_total");
                        v.put("total", total);
                        totSistema += total;
                        ventas.add(v);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resumen.put("ventas", ventas);
        resumen.put("cantidad_ventas", ventas.size());
        resumen.put("tot_ventas_sistema", totSistema);
        return resumen;
    }

    @Override
    public String cerrarCaja(int idCierreCaja, double totVentasCajero) {
        Map<String, Object> resumen = obtenerResumenCierre(idCierreCaja);
        if (!"A".equals(resumen.get("estado_caja"))) {
            return "error: esta caja ya está cerrada";
        }
        double totSistema = ((Number) resumen.getOrDefault("tot_ventas_sistema", 0.0)).doubleValue();

        String sql = "UPDATE public.cierre_caja SET fec_cierre = ?, tot_ventas_sistema = ?, " +
                "tot_ventas_cajero = ?, estado_caja = 'C' WHERE id_cierre_caja = ? AND estado_caja = 'A'";
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(totSistema));
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(totVentasCajero));
            ps.setInt(4, idCierreCaja);
            int filas = ps.executeUpdate();
            return filas > 0 ? "OK" : "error: la caja ya estaba cerrada o no existe";
        } catch (Exception e) {
            e.printStackTrace();
            return "error_backend: " + e.getMessage();
        }
    }
}