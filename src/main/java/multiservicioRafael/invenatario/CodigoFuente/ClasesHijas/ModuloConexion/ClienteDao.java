/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
                fila.put("correo", rs.getString("correo"));
                fila.put("estado", rs.getString("estado"));

                // Leer el campo carros como JSONB y convertirlo a lista
                String carrosJson = rs.getString("carros");
                if (carrosJson != null && !carrosJson.trim().isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    List<Map<String, String>> carrosList = mapper.readValue(carrosJson, List.class);
                    fila.put("carros", carrosList);
                } else {
                    fila.put("carros", new ArrayList<>());
                }

                lista.add(fila);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public String registrarClienteConCarros(Map<String, Object> cliente, List<Map<String, String>> carros, String usuarioLogueado) {
        String sql = "{ ? = call public.registrar_cliente_con_carros(?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        String resultado = "error_desconocido";
        try (
                Connection conexion = ConexionDB.getInstance().getConnection(); CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);

            cs.setString(2, (String) cliente.get("dni"));
            cs.setString(3, (String) cliente.get("nombre"));
            cs.setString(4, (String) cliente.get("apellido_paterno"));
            cs.setString(5, (String) cliente.get("apellido_materno"));
            cs.setString(6, (String) cliente.get("celular"));
            cs.setString(7, (String) cliente.get("correo"));
            cs.setString(8, (String) cliente.get("estado"));
            cs.setString(9, usuarioLogueado);

            String carrosJsonStr = convertirListaCarrosAJson(carros);

            org.postgresql.util.PGobject jsonObject = new org.postgresql.util.PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(carrosJsonStr);

            cs.setObject(10, jsonObject);
            cs.execute();
            resultado = cs.getString(1).trim();

        } catch (Exception e) {
            e.printStackTrace();
            resultado = "error_backend: " + e.getMessage();
        }

        return resultado;
    }

    private String convertirListaCarrosAJson(List<Map<String, String>> carros) {
        if (carros == null || carros.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < carros.size(); i++) {
            Map<String, String> carro = carros.get(i);
            sb.append("{");
            sb.append("\"placa\":\"").append(carro.get("placa")).append("\",");
            sb.append("\"marca\":\"").append(carro.get("marca")).append("\",");
            sb.append("\"modelo\":\"").append(carro.get("modelo")).append("\",");
            sb.append("\"anio\":\"").append(carro.get("anio")).append("\"");
            sb.append("}");

            if (i < carros.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("]");
        return sb.toString();
    }

    @Override
    public String editarClienteConCarros(Map<String, Object> cliente, List<Map<String, String>> carros, String usuarioLogueado) {
        String sql = "{ ? = call public.editar_cliente_con_carros(?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        String resultado = "error_desconocido";
        try (
                Connection conexion = ConexionDB.getInstance().getConnection(); CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            cs.setString(2, (String) cliente.get("dni"));
            cs.setString(3, (String) cliente.get("nombre"));
            cs.setString(4, (String) cliente.get("apellido_paterno"));
            cs.setString(5, (String) cliente.get("apellido_materno"));
            cs.setString(6, (String) cliente.get("celular"));
            cs.setString(7, (String) cliente.get("correo"));
            cs.setString(8, (String) cliente.get("estado"));
            cs.setString(9, usuarioLogueado);
            String carrosJsonStr = convertirListaCarrosAJson(carros);
            org.postgresql.util.PGobject jsonObject = new org.postgresql.util.PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(carrosJsonStr);
            cs.setObject(10, jsonObject);
            cs.execute();
            resultado = cs.getString(1).trim();
        } catch (Exception e) {
            e.printStackTrace();
            resultado = "error_backend: " + e.getMessage();
        }
        return resultado;
    }
}
