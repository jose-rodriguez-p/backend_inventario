/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.Interfaces;

import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Usuario;

/**
 *
 * @author jose
 */
public interface LoginDaoInterface {
    Usuario validando(String usuario,String password);
    String recuperar_contrasena(String usuario);
    String actualizarcontraseña(String usuario,String contrasena);
    
}
