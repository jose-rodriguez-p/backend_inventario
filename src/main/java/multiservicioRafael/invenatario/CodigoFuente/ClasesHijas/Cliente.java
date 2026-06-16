package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas;

public class Cliente {
    private String dni;
    private String nombre;
    private String apellido_paterno;
    private String apellido_materno;
    private String celular;
    private String correo;

    public Cliente() {}

    public Cliente(String dni, String nombre, String apellido_paterno, String apellido_materno, String celular, String correo) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido_paterno = apellido_paterno;
        this.apellido_materno = apellido_materno;
        this.celular = celular;
        this.correo = correo;
    }

    // Getters y Setters
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido_paterno() { return apellido_paterno; }
    public void setApellido_paterno(String apellido_paterno) { this.apellido_paterno = apellido_paterno; }
    public String getApellido_materno() { return apellido_materno; }
    public void setApellido_materno(String apellido_materno) { this.apellido_materno = apellido_materno; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}
