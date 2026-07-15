/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package multiservicioRafael.invenatario.repository.Interfaces;
import java.util.ArrayList;
import multiservicioRafael.invenatario.modal.Proveedor;
/**
 *
 * @author jose
 */
public interface ProveedorDaoInterfas {
    
    String ValidarExistencia(String ruc);
    String agregarProveedor(String usuario,Proveedor proveedor);
    ArrayList<Proveedor> listarProveedores();
    String ActualizarProveedor(String usuario,Proveedor proveedor);
    
}
