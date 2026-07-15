


package multiservicioRafael.invenatario.service.consultasApi;

import java.util.Properties;
import multiservicioRafael.invenatario.config.ApiHttpClient;

public class ConsultaDocumento {

    private static ConsultaDocumento instancia;
    private final String token;
    private final ApiHttpClient httpClient;
    private static final String BASE_URL = "https://dniruc.apisperu.com/api/v1/";

    private ConsultaDocumento() {
        Properties prop = multiservicioRafael.invenatario.config.EnvLoader.loadProperties();
        this.token = prop.getProperty("apisperu.api.key");
        this.httpClient = new ApiHttpClient();
    }

    public static synchronized ConsultaDocumento getInstance() {
        if (instancia == null) {
            instancia = new ConsultaDocumento();
        }
        return instancia;
    }

    public String consultarRuc(String ruc) {
        return consultar("ruc", ruc);
    }

    public String consultarDni(String dni) {
        return consultar("dni", dni);
    }

    private String consultar(String tipo, String numeroDocumento) {
        if (token == null || token.isEmpty()) {
            System.err.println("Token no configurado.");
            return null;
        }
        String url = BASE_URL + tipo + "/" + numeroDocumento + "?token=" + token;
        return httpClient.get(url);
    }
}