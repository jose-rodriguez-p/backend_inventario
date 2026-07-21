package multiservicioRafael.invenatario;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import multiservicioRafael.invenatario.config.ConexionDB;

@SpringBootTest
class InvenatarioApplicationTests {

	@Test
	void contextLoads() {
		try {
			multiservicioRafael.invenatario.repository.VentasDao dao = new multiservicioRafael.invenatario.repository.VentasDao();
			Map<String, Object> map = dao.obtenerComprobanteVenta(45);
			System.out.println("=== MAP FOR ORDER 45 ===");
			System.out.println("subtotal: " + map.get("subtotal"));
			System.out.println("igv: " + map.get("igv"));
			System.out.println("precio_total: " + map.get("precio_total"));
			System.out.println("items: " + map.get("items"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
