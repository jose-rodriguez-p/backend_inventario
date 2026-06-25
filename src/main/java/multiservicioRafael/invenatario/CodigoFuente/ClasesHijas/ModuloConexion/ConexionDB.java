package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {
    private static ConexionDB conexion;
    private HikariDataSource dataSource; 

    private ConexionDB() {
        Properties properties = multiservicioRafael.invenatario.CodigoFuente.ConfigreConect.EnvLoader.loadProperties();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("url"));
        config.setUsername(properties.getProperty("user"));
        config.setPassword(properties.getProperty("password"));
        config.setDriverClassName("org.postgresql.Driver");

        config.setMaximumPoolSize(5);        
        config.setConnectionTimeout(15000);   
        config.setIdleTimeout(120000);      
        config.setConnectionTestQuery("SELECT 1"); 

        this.dataSource = new HikariDataSource(config);
    }

    public static synchronized ConexionDB getInstance() {
        if (conexion == null) {
            conexion = new ConexionDB();
        }
        return conexion;
    }

    public Connection getConnection() throws SQLException {
        if (this.dataSource == null) {
            throw new SQLException("El pool de conexiones no se inicializó correctamente.");
        }
        return this.dataSource.getConnection();
    }
}