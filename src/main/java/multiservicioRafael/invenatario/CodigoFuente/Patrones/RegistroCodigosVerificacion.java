package multiservicioRafael.invenatario.CodigoFuente.Patrones;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Patrón Singleton: almacena códigos temporales por usuario.
 */
public class RegistroCodigosVerificacion {

    private static final int MINUTOS_VALIDEZ = 15;
    private static RegistroCodigosVerificacion instancia;

    private final Map<String, EntradaCodigo> codigos = new ConcurrentHashMap<>();

    private RegistroCodigosVerificacion() {
    }

    public static RegistroCodigosVerificacion getInstancia() {
        if (instancia == null) {
            instancia = new RegistroCodigosVerificacion();
        }
        return instancia;
    }

    public void guardar(String usuario, String codigo) {
        long expiraEn = System.currentTimeMillis() + (MINUTOS_VALIDEZ * 60_000L);
        codigos.put(usuario.toLowerCase(), new EntradaCodigo(codigo, expiraEn, false));
    }

    public boolean validar(String usuario, String codigoIngresado) {
        EntradaCodigo entrada = codigos.get(usuario.toLowerCase());
        if (entrada == null) {
            return false;
        }
        if (System.currentTimeMillis() > entrada.expiraEn) {
            codigos.remove(usuario.toLowerCase());
            return false;
        }
        if (!entrada.codigo.equals(codigoIngresado.trim())) {
            return false;
        }
        entrada.validado = true;
        return true;
    }

    public boolean estaValidado(String usuario) {
        EntradaCodigo entrada = codigos.get(usuario.toLowerCase());
        return entrada != null && entrada.validado && System.currentTimeMillis() <= entrada.expiraEn;
    }

    public void eliminar(String usuario) {
        codigos.remove(usuario.toLowerCase());
    }

    public int getMinutosValidez() {
        return MINUTOS_VALIDEZ;
    }

    private static class EntradaCodigo {
        private final String codigo;
        private final long expiraEn;
        private boolean validado;

        private EntradaCodigo(String codigo, long expiraEn, boolean validado) {
            this.codigo = codigo;
            this.expiraEn = expiraEn;
            this.validado = validado;
        }
    }
}
