/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.Patrones;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

/**
 *
 * @author jose
 */


public class ConsultaDNI {

    private static ConsultaDNI instancia;
    private String token;

    private ConsultaDNI() {
        Properties prop = multiservicioRafael.invenatario.CodigoFuente.EnvLoader.loadProperties();
        this.token = prop.getProperty("apisperu.api.key");
    }

    private String ejecutarConsulta(String dni) {
        String url = "https://dniruc.apisperu.com/api/v1/dni/" + dni + "?token=" + this.token;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ConsultaDNI getInstance() {
        if (instancia == null) {
            instancia = new ConsultaDNI();
        }
        return instancia;
    }

    public String getConsulta(String dni) {
        return ejecutarConsulta(dni);
    }
}