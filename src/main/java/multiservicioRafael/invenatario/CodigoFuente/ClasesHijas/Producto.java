package multiservicioRafael.invenatario.CodigoFuente.ClasesHijas;

public class Producto {
    private String codigo;
    private String nombre;
    private String marca;
    private String categoria;
    private int stock;
    private int stock_minimo;
    private double precio_venta;
    private String estado;

    public Producto() {}

    public Producto(String codigo, String nombre, String marca, String categoria, int stock, int stock_minimo, double precio_venta, String estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.stock = stock;
        this.stock_minimo = stock_minimo;
        this.precio_venta = precio_venta;
        this.estado = estado;
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getStock_minimo() { return stock_minimo; }
    public void setStock_minimo(int stock_minimo) { this.stock_minimo = stock_minimo; }
    public double getPrecio_venta() { return precio_venta; }
    public void setPrecio_venta(double precio_venta) { this.precio_venta = precio_venta; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
