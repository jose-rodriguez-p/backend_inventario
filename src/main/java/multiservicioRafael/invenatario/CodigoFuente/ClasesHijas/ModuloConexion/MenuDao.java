/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;

import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.Interfaces.MenuDaoInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author jose
 */
public class MenuDao implements MenuDaoInterface{

    @Override
    public ArrayList<String> listarmenus() {
        ArrayList<String> menus = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_menu()";
        try (Connection conexion = ConexionDB.getInstance().getConnection();
             PreparedStatement sc = conexion.prepareStatement(sql);
             ResultSet resultado = sc.executeQuery()) {           
            while (resultado.next()) {
                String nombreMenu = resultado.getString("nombre");
                menus.add(nombreMenu);
            }
            return menus;          
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
     
}
