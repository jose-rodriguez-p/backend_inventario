/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente;

/**
 *
 * @author jose
 */
public class Sistema {
    public String iniciar(String usuario,String password){
        Usuario user= new Usuario();
        if(usuario.equalsIgnoreCase(user.getUsuario())&& password.equalsIgnoreCase(user.getPassword())){
            return "valido";
        }
        return "no valido";
    }
    
}
