package multiservicioRafael.invenatario.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.config.ConexionDB;
import multiservicioRafael.invenatario.repository.Interfaces.reporteVentaMantDaoInterfas;

public class reporteVentaMantDao implements reporteVentaMantDaoInterfas {

    @Override
    public List<Map<String, Object>> listarMovimientos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_movimientos()";

        try (Connection cn = ConexionDB.getInstance().getConnection(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                fila.put("nOrden", rs.getInt("n_orden"));
                fila.put("tipo", rs.getString("tipo"));

                Object fRegistro = getValorSeguro(rs, "fecha_registro");
                Object fCulminacion = getValorSeguro(rs, "fecha_culminacion");
                Object fServicio = getValorSeguro(rs, "fecha_servicio");
                Object fFecha = getValorSeguro(rs, "fecha");

                Object fechaBase = fCulminacion != null ? fCulminacion : (fRegistro != null ? fRegistro : (fFecha != null ? fFecha : fServicio));
                String fechaRaw = fechaBase != null ? fechaBase.toString() : "";
                String hora = String.valueOf(getValorSeguro(rs, "hora"));

                fila.put("fechaRegistro", fRegistro != null ? fRegistro.toString() : fechaRaw);
                fila.put("fechaCulminacion", fCulminacion != null ? fCulminacion.toString() : fechaRaw);
                fila.put("fechaServicio", fServicio != null ? fServicio.toString() : "");
                fila.put("fecha", (fechaRaw.length() >= 10) ? fechaRaw.substring(0, 10) : fechaRaw);
                fila.put("hora", (hora != null && !"null".equals(hora)) ? hora : "");

                fila.put("cliente", getValorSeguro(rs, "cliente"));
                fila.put("dni", getValorSeguro(rs, "dni"));
                fila.put("carro", getValorSeguro(rs, "carro"));
                fila.put("repuesto", getValorSeguro(rs, "repuesto"));
                fila.put("mecanico", getValorSeguro(rs, "mecanico"));
                fila.put("vendedor", getValorSeguro(rs, "vendedor"));

                Object totalVal = getValorSeguro(rs, "total");
                fila.put("total", totalVal instanceof Number ? ((Number) totalVal).doubleValue() : 0.0);

                lista.add(fila);
            }

        } catch (Exception e) {
            System.out.println("Error listarMovimientos: " + e.getMessage());
        }

        return lista;
    }

    private Object getValorSeguro(ResultSet rs, String colName) {
        try {
            return rs.getObject(colName);
        } catch (Exception e) {
            return null;
        }
    }
}
