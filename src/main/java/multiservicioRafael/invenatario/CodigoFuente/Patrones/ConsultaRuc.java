package multiservicioRafael.invenatario.CodigoFuente.Patrones;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class ConsultaRuc {
    private static ConsultaRuc instancia;
    private String token;
    private ConsultaRuc() {
        Properties prop = multiservicioRafael.invenatario.CodigoFuente.ConfigreConect.EnvLoader.loadProperties();
        this.token = prop.getProperty("apisperu.api.key");
    }
    public static synchronized ConsultaRuc getInstance() {
        if (instancia == null) {
            instancia = new ConsultaRuc();
        }
        return instancia;
    }
    public String getConsulta(String ruc) {
        if (token == null || token.isEmpty()) {
            System.err.println("Token no configurado.");
            return null;
        }
        String url = "https://dniruc.apisperu.com/api/v1/ruc/" + ruc + "?token=" + this.token;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("Error en API: Código " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
