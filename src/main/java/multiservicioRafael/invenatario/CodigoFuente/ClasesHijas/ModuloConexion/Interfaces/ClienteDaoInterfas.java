/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.Interfaces;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jose
 */
public interface ClienteDaoInterfas {
    
    boolean validarExisteCliente(String dni);
    public List<Map<String, Object>> listarClientesConCarros();
}
