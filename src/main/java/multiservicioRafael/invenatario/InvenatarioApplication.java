package multiservicioRafael.invenatario;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InvenatarioApplication { 

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(InvenatarioApplication.class, args);
        abrirNavegador("http://localhost:4200");
    }

    public static void abrirNavegador(String url) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                Thread.sleep(2000);
                desktop.browse(new URI(url));
                System.out.println(">>> Sistema listo. Abriendo frontend en: " + url);
            } catch (IOException | URISyntaxException e) {
                System.err.println("Error al abrir la URL: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("La espera fue interrumpida: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        } else {
            System.out.println("El sistema operativo no soporta la apertura automática de ventanas.");
        }
    }
}
