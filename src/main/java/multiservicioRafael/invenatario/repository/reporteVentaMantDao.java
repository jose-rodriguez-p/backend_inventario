/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

/**
 *
 * @author jose
 */
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

                String fechaRaw = rs.getString("fecha");
                String hora = rs.getString("hora");
                fila.put("fecha", (fechaRaw != null && fechaRaw.length() >= 10) ? fechaRaw.substring(0, 10) : "");
                fila.put("hora", hora != null ? hora : "");

                fila.put("cliente", rs.getString("cliente"));
                fila.put("dni", rs.getString("dni"));
                fila.put("carro", rs.getString("carro"));
                fila.put("repuesto", rs.getString("repuesto"));
                fila.put("mecanico", rs.getString("mecanico"));
                fila.put("vendedor", rs.getString("vendedor"));
                fila.put("total", rs.getDouble("total"));

                lista.add(fila);
            }

        } catch (Exception e) {
            System.out.println("Error listarMovimientos: " + e.getMessage());
        }

        return lista;
    }

}
