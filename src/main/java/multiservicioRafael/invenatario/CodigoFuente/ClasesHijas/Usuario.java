package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas;

import java.util.List;

public class Usuario extends Trabajador {

    private String username;
    private String password;
    private String rol;
    private String[]accesoMenu;

    public Usuario() {
        super();
    }

    public Usuario(String username, String rol, String[] accesoMenu, String nombre, String apellido_materno, String apellido_paterno) {
        super(nombre, apellido_materno, apellido_paterno);
        this.username = username;
        this.rol = rol;
        this.accesoMenu = accesoMenu;
    }
    
    public Usuario(String username, String password, String rol, String[] accesoMenu, String documento, String numeroDocumento, String nombre, String apellido_materno, String apellido_paterno, String celular, String correo, String direccion, String cargo, String estado) {
        super(documento, numeroDocumento, nombre, apellido_materno, apellido_paterno, celular, correo, direccion, cargo, estado);
        this.username = username;
        this.password = password;
        this.rol = rol;
        this.accesoMenu = accesoMenu;
    }

    

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getAccesoMenu() {
        return accesoMenu;
    }

    public void setAccesoMenu(String[] accesoMenu) {
        this.accesoMenu = accesoMenu;
    }

    

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

}
