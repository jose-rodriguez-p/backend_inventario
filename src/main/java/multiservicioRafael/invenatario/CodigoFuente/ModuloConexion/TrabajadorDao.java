/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ModuloConexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import multiservicioRafael.invenatario.CodigoFuente.Trabajador;

/**
 *
 * @author jose
 */
public class TrabajadorDao implements TrabajadorDaoInterfas {

    @Override
    public ArrayList<Trabajador> listTrabajador() {
        ArrayList<Trabajador> trabajadores = new ArrayList<>();
        String sql = "SELECT * FROM public.fn_listar_trabajadores_completo()";

        try {
            Connection conexion = ConexionDB.getInstance().getConnection();
            try (PreparedStatement sc = conexion.prepareStatement(sql)) {
                try (ResultSet resultado = sc.executeQuery()) {
                    while (resultado.next()) {
                        String doc = resultado.getString("documento");
                        String nom = resultado.getString("Nombre");
                        String apeMat = resultado.getString("Apellido Materno");
                        String apePat = resultado.getString("Apellido Paterno");
                        String cel = resultado.getString("Celular");
                        String correo = resultado.getString("Correo");
                        String cargo = resultado.getString("Cargo");
                        String estado = resultado.getString("Estado");
                        String fechacompleta = resultado.getString("Fecha de Registro");
                        String soloFecha="";
                        String soloHora="";
                        if (fechacompleta!=null) {
                            soloFecha = fechacompleta.substring(0, 10);
                            soloHora = fechacompleta.substring(11, 19);
                        }
                        Trabajador t = new Trabajador(doc, nom, apeMat, apePat, cel, correo, cargo, estado, soloFecha,soloHora);
                        trabajadores.add(t);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trabajadores;
    }

}
