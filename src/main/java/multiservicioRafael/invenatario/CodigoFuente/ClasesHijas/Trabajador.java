package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas;



public class Trabajador {

    private String documento;
    private String numeroDocumento;
    private String nombre;
    private String apellido_materno;
    private String apellido_paterno;
    private String celular;
    private String correo;
    private String direccion;
    private String cargo;
    private String estado;
    private String fecha;
    private String hora;

    public Trabajador() {
    }

    public Trabajador(String numeroDocumento, String nombre, String apellido_materno, String apellido_paterno, String celular, String correo, String cargo, String estado,String fecha,String hora ) {
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.apellido_materno = apellido_materno;
        this.apellido_paterno = apellido_paterno;
        this.celular = celular;
        this.correo = correo;
        this.cargo = cargo;
        this.estado = estado;
        this.fecha=fecha;
        this.hora=hora;
    }

    public Trabajador(String nombre, String apellido_materno, String apellido_paterno) {
        this.nombre = nombre;
        this.apellido_materno = apellido_materno;
        this.apellido_paterno = apellido_paterno;
    }

    public Trabajador(String documento, String numeroDocumento, String nombre, String apellido_materno, String apellido_paterno, String celular, String correo, String direccion, String cargo, String estado, String fecha, String hora) {
        this.documento = documento;
        this.numeroDocumento = numeroDocumento;
        this.nombre = nombre;
        this.apellido_materno = apellido_materno;
        this.apellido_paterno = apellido_paterno;
        this.celular = celular;
        this.correo = correo;
        this.direccion = direccion;
        this.cargo = cargo;
        this.estado = estado;
        this.fecha = fecha;
        this.hora = hora;
    }

    // Getters y Setters
    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido_materno() {
        return apellido_materno;
    }

    public void setApellido_materno(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }

    public String getApellido_paterno() {
        return apellido_paterno;
    }

    public void setApellido_paterno(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    

    @Override
    public String toString() {
        return "Trabajador{" + "documento=" + documento + ", numeroDocumento=" + numeroDocumento + ", nombre=" + nombre + ", apellido_materno=" + apellido_materno + ", apellido_paterno=" + apellido_paterno + ", celular=" + celular + ", correo=" + correo + ", direccion=" + direccion + ", cargo=" + cargo + ", estado=" + estado + ", fecha=" + fecha + ", hora=" + hora + '}';
    }
    
    

}
