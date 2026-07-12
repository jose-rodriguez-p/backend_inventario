package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompraDao {

    public List<Map<String, Object>> listarCompras() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT o.id_oper_compra, o.fec_compra, o.tot_pago, " +
                "p.nombre_empresa AS nombre_proveedor, p.ruc AS ruc_proveedor, " +
                "COUNT(d.id_det_compra) AS cantidad_items " +
                "FROM operacion_compra o " +
                "LEFT JOIN det_compra_rep d ON d.id_oper_compra = o.id_oper_compra " +
                "LEFT JOIN proveedor p ON p.id_proveedor = d.id_proveedor " +
                "GROUP BY o.id_oper_compra, o.fec_compra, o.tot_pago, p.nombre_empresa, p.ruc " +
                "ORDER BY o.fec_compra DESC";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("id_oper_compra", rs.getInt("id_oper_compra"));
                fila.put("fec_compra", rs.getTimestamp("fec_compra") != null ? rs.getTimestamp("fec_compra").toString() : "");
                fila.put("nombre_proveedor", rs.getString("nombre_proveedor"));
                fila.put("ruc_proveedor", rs.getString("ruc_proveedor"));
                fila.put("cantidad_items", rs.getInt("cantidad_items"));
                fila.put("tot_pago", rs.getBigDecimal("tot_pago"));
                lista.add(fila);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Map<String, Object>> obtenerDetalle(int idOperCompra) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT r.nombre AS nombre_repuesto, d.num_cantidad, d.precio_compra, " +
                "(d.num_cantidad * d.precio_compra) AS subtotal " +
                "FROM det_compra_rep d " +
                "JOIN repuesto r ON r.id_repuesto = d.id_repuesto " +
                "WHERE d.id_oper_compra = ?";

        try (Connection conn = ConexionDB.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idOperCompra);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("nombre_repuesto", rs.getString("nombre_repuesto"));
                    fila.put("cantidad", rs.getInt("num_cantidad"));
                    fila.put("precio_compra", rs.getBigDecimal("precio_compra"));
                    fila.put("subtotal", rs.getBigDecimal("subtotal"));
                    lista.add(fila);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public String registrarCompra(String rucProveedor, List<Map<String, Object>> items, Connection conn) throws SQLException {

        int idProveedor = 0;
        String sqlFindProveedor = "SELECT id_proveedor FROM proveedor WHERE ruc = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sqlFindProveedor)) {
            ps.setString(1, rucProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idProveedor = rs.getInt("id_proveedor");
                } else {
                    throw new SQLException("Proveedor con RUC " + rucProveedor + " no encontrado");
                }
            }
        }

        BigDecimal totPago = BigDecimal.ZERO;
        for (Map<String, Object> item : items) {
            BigDecimal precio = new BigDecimal(item.get("precio_compra").toString());
            int cantidad = ((Number) item.get("cantidad")).intValue();
            totPago = totPago.add(precio.multiply(BigDecimal.valueOf(cantidad)));
        }

        String sqlCompra = "INSERT INTO operacion_compra (fec_compra, tot_pago) VALUES (NOW(), ?) RETURNING id_oper_compra";
        int idOperCompra;
        try (PreparedStatement ps = conn.prepareStatement(sqlCompra, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBigDecimal(1, totPago);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    idOperCompra = rs.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la operación de compra");
                }
            }
        }

        String sqlDetalle = "INSERT INTO det_compra_rep (id_oper_compra, id_repuesto, id_proveedor, num_cantidad, precio_compra) " +
                "VALUES (?, (SELECT id_repuesto FROM repuesto WHERE nombre = ? LIMIT 1), ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sqlDetalle)) {
            for (Map<String, Object> item : items) {
                ps.setInt(1, idOperCompra);
                ps.setString(2, (String) item.get("nombre_repuesto"));
                ps.setInt(3, idProveedor);
                ps.setInt(4, ((Number) item.get("cantidad")).intValue());
                ps.setBigDecimal(5, new BigDecimal(item.get("precio_compra").toString()));
                ps.addBatch();
            }
            ps.executeBatch();
        }

        String sqlActualizarStock = "UPDATE repuesto SET cantidad = cantidad + ? " +
                "WHERE id_repuesto = (SELECT id_repuesto FROM repuesto WHERE nombre = ? LIMIT 1)";

        try (PreparedStatement ps = conn.prepareStatement(sqlActualizarStock)) {
            for (Map<String, Object> item : items) {
                ps.setInt(1, ((Number) item.get("cantidad")).intValue());
                ps.setString(2, (String) item.get("nombre_repuesto"));
                ps.addBatch();
            }
            ps.executeBatch();
        }

        return "COMPRA_REGISTRADA";
    }
}
