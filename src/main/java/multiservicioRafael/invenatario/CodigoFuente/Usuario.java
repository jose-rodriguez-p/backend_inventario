/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente;

/**
 *
 * @author jose
 */
class Usuario {
    private Trabajador tarbajador;
    private String usuario="jose";
    private String password="74622233";
    private String estado;

    public Usuario() {
    }
    
    public Usuario(Trabajador tarbajador, String usuario, String password, String estado) {
        this.tarbajador = tarbajador;
        this.usuario = usuario;
        this.password = password;
        this.estado = estado;
    }

    public Trabajador getTarbajador() {
        return tarbajador;
    }

    public void setTarbajador(Trabajador tarbajador) {
        this.tarbajador = tarbajador;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}
