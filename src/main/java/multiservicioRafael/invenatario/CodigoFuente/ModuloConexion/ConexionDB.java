package multiservicioRafael.invenatario.CodigoFuente.ModuloConexion;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {
    private static ConexionDB conexion;
    
    private Connection connection;

    private ConexionDB() {
        Properties properties = new Properties();
        
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo application.properties en la raíz de resources.");
            }

            properties.load(input);

            Class.forName("org.postgresql.Driver");
            
            this.connection = DriverManager.getConnection(
                properties.getProperty("url"),       
                properties.getProperty("user"),      
                properties.getProperty("password")   
            );


        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error crítico al conectar a la base de datos: " + e.getMessage(), e);
        }
    }
    public static ConexionDB getInstance() {
        if (conexion == null) {
            conexion=new ConexionDB();
        }
        return conexion;
    }
    Connection getConnection() {
        return this.connection;
    }
}