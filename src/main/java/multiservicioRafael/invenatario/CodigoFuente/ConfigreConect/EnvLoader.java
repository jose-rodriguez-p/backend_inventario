package multiservicioRafael.invenatario.CodigoFuente.ConfigreConect;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvLoader {
    public static Properties loadProperties() {
        Properties properties = new Properties();
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties");
        if (input == null) {
            input = EnvLoader.class.getClassLoader().getResourceAsStream("application.properties");
        }
        
        if (input != null) {
            try {
                properties.load(input);
            } catch (IOException e) {
                System.err.println("Error al cargar application.properties: " + e.getMessage());
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    // Ignorar
                }
            }
        } else {
            System.err.println("Advertencia: No se encontró el archivo application.properties");
        }

        // Sobrescribir con variables de entorno
        overrideWithEnv(properties);
        return properties;
    }

    private static void overrideWithEnv(Properties properties) {
        // Claves conocidas para asegurar que se carguen incluso si el properties está vacío
        String[] keys = {
            "url", "user", "password",
            "brevo.smtp.host", "brevo.smtp.port", "brevo.smtp.user", "brevo.smtp.password", "brevo.smtp.from",
            "apisperu.api.key", "apisperu.ruc.api.key"
        };
        for (String key : keys) {
            String envName = key.replace('.', '_').toUpperCase();
            String envValue = System.getenv(envName);
            if (envValue != null && !envValue.isEmpty()) {
                properties.setProperty(key, envValue);
            }
        }

        // Mapear también variables estándar de bases de datos
        String dbUrl = System.getenv("DB_URL");
        if (dbUrl != null && !dbUrl.isEmpty()) {
            properties.setProperty("url", dbUrl);
        }
        String dbUser = System.getenv("DB_USER");
        if (dbUser != null && !dbUser.isEmpty()) {
            properties.setProperty("user", dbUser);
        }
        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbPassword != null && !dbPassword.isEmpty()) {
            properties.setProperty("password", dbPassword);
        }
        
        // Procesar cualquier otra propiedad cargada dinámicamente desde el archivo
        for (String name : properties.stringPropertyNames()) {
            String envName = name.replace('.', '_').toUpperCase();
            String envValue = System.getenv(envName);
            if (envValue != null && !envValue.isEmpty()) {
                properties.setProperty(name, envValue);
            }
        }
    }
}
