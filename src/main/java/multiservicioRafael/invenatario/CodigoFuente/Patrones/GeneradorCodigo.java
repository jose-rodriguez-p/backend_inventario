package multiservicioRafael.invenatario.CodigoFuente.Patrones;

import java.security.SecureRandom;

/**
 * Patrón Factory: genera códigos numéricos de verificación.
 */
public final class GeneradorCodigo {

    private static final SecureRandom RANDOM = new SecureRandom();

    private GeneradorCodigo() {
    }

    public static String generarSeisDigitos() {
        int numero = RANDOM.nextInt(1_000_000);
        return String.format("%06d", numero);
    }
}
