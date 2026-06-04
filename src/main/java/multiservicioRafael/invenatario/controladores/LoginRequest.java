package multiservicioRafael.invenatario.controladores;

/**
 * DTO simple para recibir credenciales desde Angular (sin Spring en la lógica).
 */
public class LoginRequest {

    private String username;
    private String password;

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
}
