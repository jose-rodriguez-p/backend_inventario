/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.controladores;

import multiservicioRafael.invenatario.CodigoFuente.Sistema;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@CrossOrigin("*")
public class ControladorLogin {
    @PostMapping
    public String login(@RequestBody String[] datos) {
        if (datos == null || datos.length < 2) {
            return "ERROR: Datos incompletos";
        }
        Sistema sistema = new Sistema();
        String usuario = datos[0];
        String password = datos[1];
        sistema.iniciar(usuario, password);
        return "OK_PROCESADO"; 
    }
    
}
