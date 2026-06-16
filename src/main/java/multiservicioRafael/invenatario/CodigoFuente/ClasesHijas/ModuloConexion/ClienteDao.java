/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.Interfaces.ClienteDaoInterfas;
import tools.jackson.databind.ObjectMapper;

/**
 *
 * @author jose
 */
public class ClienteDao implements ClienteDaoInterfas {

    @Override
    public boolean validarExisteCliente(String dni) {
        String sql = "SELECT public.fc_verificar_existencia(?)";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error en validarExisteCliente: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> listarClientesConCarros() {

        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT * FROM public.fn_listar_clientes_con_carros()";

        try (
                Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Map<String, Object> fila = new HashMap<>();

                fila.put("dni", rs.getString("dni"));
                fila.put("nombre", rs.getString("nombre"));
                fila.put("apellido_paterno", rs.getString("apellido_paterno"));
                fila.put("apellido_materno", rs.getString("apellido_materno"));
                fila.put("celular", rs.getString("celular"));
                fila.put("estado", rs.getString("estado"));

                fila.put("placa", rs.getString("placa"));
                fila.put("marca", rs.getString("marca"));
                fila.put("modelo", rs.getString("modelo"));

                lista.add(fila);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }


}
