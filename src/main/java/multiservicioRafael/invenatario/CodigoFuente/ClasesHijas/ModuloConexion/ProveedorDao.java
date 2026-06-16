/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.Interfaces.ProveedorDaoInterfas;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Proveedor;

/**
 *
 * @author jose
 */
public class ProveedorDao implements ProveedorDaoInterfas {

    @Override
    public String ValidarExistencia(String ruc) {
        String sql = "SELECT verificar_existe_ruc(?) AS resultado";

        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement sc = conexion.prepareStatement(sql)) {
            sc.setString(1, ruc);
            try (ResultSet resultado = sc.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getString("resultado");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al validar RUC: " + e.getMessage());
        }
        return "error";
    }

    @Override
    public String agregarProveedor(String usuario, Proveedor proveedor) {
        String sql = "SELECT public.insertar_proveedor(?, ?, ?, ?, ?, ?, ?) AS resultado";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement sc = conexion.prepareStatement(sql)) {
            sc.setString(1, proveedor.getRuc());
            sc.setString(2, proveedor.getNombre_empresa());
            sc.setString(3, proveedor.getCelular());
            sc.setString(4, proveedor.getCorreo());
            sc.setString(5, proveedor.getDireccion());
            sc.setString(6, proveedor.getEstado());
            sc.setString(7, usuario);
            try (ResultSet resultado = sc.executeQuery()) {
                if (resultado.next()) {
                    String res = resultado.getString("resultado");
                    System.out.println("Respuesta de Supabase: " + res);
                    return res;
                }
            }
        } catch (Exception e) {
            System.out.println("Error crítico en DAO al agregar proveedor: " + e.getMessage());
            e.printStackTrace();
        }
        return "error_conexion";
    }

    @Override
    public ArrayList<Proveedor> listarProveedores() {
        ArrayList<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_proveedores()";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement sc = conexion.prepareStatement(sql); ResultSet resultado = sc.executeQuery()) {
            while (resultado.next()) {
                Proveedor p = new Proveedor();
                p.setRuc(resultado.getString("ruc").trim());
                p.setNombre_empresa(resultado.getString("nombre_empresa"));
                p.setCelular(resultado.getString("celular"));
                p.setCorreo(resultado.getString("correo"));
                p.setDireccion(resultado.getString("direccion"));
                p.setEstado(resultado.getString("estado"));
                lista.add(p);
            }
            return lista;
        } catch (Exception e) {
            System.out.println("Error al listar proveedores desde Supabase: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String ActualizarProveedor(String usuario, Proveedor proveedor) {
        String sql = "SELECT * FROM public.actualizar_proveedor(?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionDB.getInstance().getConnection(); PreparedStatement sc = conexion.prepareStatement(sql)) {
            sc.setString(1, proveedor.getRuc());
            sc.setString(2, proveedor.getNombre_empresa());
            sc.setString(3, proveedor.getCelular());
            sc.setString(4, proveedor.getCorreo());
            sc.setString(5, proveedor.getDireccion());
            sc.setString(6, proveedor.getEstado());
            sc.setString(7, usuario);
            try (ResultSet resultado = sc.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getString(1);
                }
            }

        } catch (Exception e) {
            System.out.println("Error al actualizar proveedor desde Supabase: " + e.getMessage());
            return "ERROR_CONEXION: " + e.getMessage();
        }

        return "error_desconocido";
    }

}
