package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas;

public class Servicio {
    private int id_servicio;
    private String nombre;
    private double precio;
    private String estado;

    public Servicio() {}

    public Servicio(int id_servicio, String nombre, double precio, String estado) {
        this.id_servicio = id_servicio;
        this.nombre = nombre;
        this.precio = precio;
        this.estado = estado;
    }

    public int getId_servicio() { return id_servicio; }
    public void setId_servicio(int id_servicio) { this.id_servicio = id_servicio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Servicio{id_servicio=" + id_servicio + ", nombre=" + nombre + ", precio=" + precio + ", estado=" + estado + "}";
    }
}
