package multiservicioRafael.invenatario.repository;

import multiservicioRafael.invenatario.config.ConexionDB;
import multiservicioRafael.invenatario.repository.Interfaces.AuditoriaDaoInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lee la actividad reciente del sistema (quién hizo qué, cuándo y sobre qué
 * tabla) desde las tablas auditoria + detalle_auditoria. No escribe nada:
 * los registros los generan los procedimientos/flujos que ya usan
 * UsuarioLogeado al crear/editar (ver ClienteDao, ProductoDao, etc.).
 * Mismo estilo JDBC plano que DashboardDao.
 */
public class AuditoriaDao implements AuditoriaDaoInterface {

    @Override
    public Map<String, Object> obtenerActividad(LocalDate desde, LocalDate hasta, String usuario,
                                                String tabla, String tipoAccion, int pagina, int tamanoPagina) {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> actividades = new ArrayList<>();
        int total = 0;

        StringBuilder where = new StringBuilder(" WHERE a.fecha_auditoria::date BETWEEN ? AND ? ");
        List<Object> params = new ArrayList<>();
        params.add(desde);
        params.add(hasta);

        if (usuario != null && !usuario.isBlank()) {
            where.append(" AND u.usuario = ? ");
            params.add(usuario);
        }
        if (tabla != null && !tabla.isBlank()) {
            where.append(" AND da.tabla_afectada = ? ");
            params.add(tabla);
        }
        if (tipoAccion != null && !tipoAccion.isBlank()) {
            where.append(" AND da.tipo_accion = ? ");
            params.add(tipoAccion);
        }

        String from = " FROM detalle_auditoria da " +
                "JOIN auditoria a ON a.id_auditoria = da.id_auditoria " +
                "JOIN usuario u ON u.id_usuario = da.id_usuario " +
                "JOIN trabajador t ON t.id_trabajador = u.id_trabajador ";

        try (Connection cn = ConexionDB.getInstance().getConnection()) {

            try (PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) " + from + where)) {
                bindParametros(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) total = rs.getInt(1);
                }
            }

            String sql = "SELECT a.fecha_auditoria, da.tipo_accion, da.tabla_afectada, " +
                    "da.id_registro, da.descripcion, u.usuario AS usuario_login, " +
                    "trim(t.nombre || ' ' || t.apellido_paterno) AS nombre_trabajador " +
                    from + where +
                    " ORDER BY a.fecha_auditoria DESC LIMIT ? OFFSET ?";
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                bindParametros(ps, params);
                int idx = params.size() + 1;
                ps.setInt(idx, tamanoPagina);
                ps.setInt(idx + 1, Math.max(0, (pagina - 1) * tamanoPagina));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> fila = new LinkedHashMap<>();
                        fila.put("fecha", rs.getTimestamp("fecha_auditoria").toString());
                        fila.put("tipoAccion", rs.getString("tipo_accion"));
                        fila.put("tabla", rs.getString("tabla_afectada"));
                        fila.put("idRegistro", rs.getObject("id_registro"));
                        fila.put("descripcion", rs.getString("descripcion"));
                        fila.put("usuario", rs.getString("usuario_login"));
                        fila.put("nombreTrabajador", rs.getString("nombre_trabajador"));
                        actividades.add(fila);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error en AuditoriaDao.obtenerActividad: " + e.getMessage());
            e.printStackTrace();
        }

        resultado.put("total", total);
        resultado.put("pagina", pagina);
        resultado.put("tamanoPagina", tamanoPagina);
        resultado.put("actividades", actividades);
        return resultado;
    }

    @Override
    public Map<String, Object> obtenerFiltrosDisponibles() {
        Map<String, Object> resultado = new HashMap<>();
        List<String> tablas = new ArrayList<>();
        List<String> tiposAccion = new ArrayList<>();
        List<Map<String, Object>> usuarios = new ArrayList<>();

        try (Connection cn = ConexionDB.getInstance().getConnection()) {
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT DISTINCT tabla_afectada FROM detalle_auditoria ORDER BY 1");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tablas.add(rs.getString(1));
            }

            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT DISTINCT tipo_accion FROM detalle_auditoria ORDER BY 1");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tiposAccion.add(rs.getString(1));
            }

            String sqlUsuarios = "SELECT DISTINCT u.usuario, trim(t.nombre || ' ' || t.apellido_paterno) AS nombre " +
                    "FROM detalle_auditoria da " +
                    "JOIN usuario u ON u.id_usuario = da.id_usuario " +
                    "JOIN trabajador t ON t.id_trabajador = u.id_trabajador " +
                    "ORDER BY nombre";
            try (PreparedStatement ps = cn.prepareStatement(sqlUsuarios);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> u = new LinkedHashMap<>();
                    u.put("usuario", rs.getString("usuario"));
                    u.put("nombre", rs.getString("nombre"));
                    usuarios.add(u);
                }
            }
        } catch (Exception e) {
            System.out.println("Error en AuditoriaDao.obtenerFiltrosDisponibles: " + e.getMessage());
            e.printStackTrace();
        }

        resultado.put("tablas", tablas);
        resultado.put("tiposAccion", tiposAccion);
        resultado.put("usuarios", usuarios);
        return resultado;
    }

    private void bindParametros(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}