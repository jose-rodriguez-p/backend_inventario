package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas;

public class Proveedor {
    private String ruc;
    private String nombre_empresa;
    private String celular;
    private String correo;
    private String direccion;
    private String estado;

    public Proveedor() {}

    public Proveedor(String ruc, String nombre_empresa, String celular, String correo, String direccion, String estado) {
        this.ruc = ruc;
        this.nombre_empresa = nombre_empresa;
        this.celular = celular;
        this.correo = correo;
        this.direccion = direccion;
        this.estado = estado;
    }

    // Getters y Setters
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getNombre_empresa() { return nombre_empresa; }
    public void setNombre_empresa(String nombre_empresa) { this.nombre_empresa = nombre_empresa; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Proveedor{" + "ruc=" + ruc + ", nombre_empresa=" + nombre_empresa + ", celular=" + celular + ", correo=" + correo + ", direccion=" + direccion + ", estado=" + estado + '}';
    }
    
}
