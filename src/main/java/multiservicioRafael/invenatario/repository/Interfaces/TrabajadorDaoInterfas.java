package multiservicioRafael.invenatario.repository.Interfaces;

import java.util.ArrayList;
import java.util.Map;
import multiservicioRafael.invenatario.modal.Trabajador;

public interface TrabajadorDaoInterfas {
    ArrayList<Trabajador> listTrabajador();
    String agregarTranbajador(Trabajador t, String usuario, String contrasena, String usuarioLogueado);
    boolean existeDni(String dni);
    ArrayList<Map<String, Object>> listarCargos();
    ArrayList<Map<String, Object>> listarDocumentos();
    String editarTrabajador(Trabajador t, String usuarioLogueado, String username, String password);
}
