package multiservicioRafael.invenatario.repository.Interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface CompraDaoInterface {

    List<Map<String, Object>> listarCompras();

    List<Map<String, Object>> obtenerDetalle(int idOperCompra);

    String registrarCompra(String rucProveedor, List<Map<String, Object>> items, Connection conn) throws SQLException;
}
