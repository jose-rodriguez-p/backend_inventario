package multiservicioRafael.invenatario.CodigoFuente.ModuloCorreo;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import multiservicioRafael.invenatario.CodigoFuente.Patrones.RegistroCodigosVerificacion;

public class ServicioCorreo {

    private static ServicioCorreo instancia;
    private final Resend resend;
    private final Properties config;

    private ServicioCorreo() {
        config = cargarConfiguracion();
        String apiKey = config.getProperty("resend.api.key");
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("Falta configurar resend.api.key");
        }
        resend = new Resend(apiKey);
    }

    public static ServicioCorreo getInstancia() {
        if (instancia == null) {
            instancia= new ServicioCorreo();
        }

        return instancia;
    }

    public boolean enviarCodigoVerificacion( String correoDestino, String codigo) {
        try {
            int minutos= RegistroCodigosVerificacion.getInstancia().getMinutosValidez();
            String remitente= config.getProperty("resend.from","Multiservicio Rafael <onboarding@resend.dev>");
            String html = """
                <div style="
                max-width:600px;
                margin:auto;
                padding:35px;
                background:#ffffff;
                border:1px solid #dddddd;
                border-radius:12px;
                font-family:Arial;">

                    <h1 style="
                    text-align:center;
                    color:#1565C0;">

                    🔐 Multiservicio Rafael

                    </h1>

                    <p>Hola,</p>

                    <p>
                    Usa el siguiente código
                    para verificar tu cuenta:
                    </p>

                    <div style="
                    background:#F3F6FF;
                    padding:20px;
                    text-align:center;
                    border-radius:10px;">

                        <span style="
                        font-size:34px;
                        font-weight:bold;
                        color:#1565C0;
                        letter-spacing:6px;">

                        %s

                        </span>

                    </div>

                    <p>
                    Este código es válido por
                    <strong>%d minutos</strong>.
                    </p>

                    <p>
                    Si no realizaste esta solicitud,
                    ignora este mensaje.
                    </p>

                    <hr>

                    <p style="color:#777">

                    Gracias por confiar
                    en nosotros.

                    <br>

                    Equipo Multiservicio Rafael

                    </p>

                </div>
                """.formatted(
                    codigo,
                    minutos
            );
            CreateEmailOptions request= CreateEmailOptions.builder()
                            .from(remitente)
                            .to(correoDestino)
                            .subject(
                                    "🔐 Código de verificación"
                            )
                            .html(html)
                            .build();
            resend.emails().send(request);
            return true;

        } catch (Exception e) {

            System.err.println("Error enviando correo: "+ e.getMessage());
            return false;
        }
    }

    private Properties cargarConfiguracion() {

        Properties properties= new Properties();
        try (InputStream input= Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
                    if (input == null) {
                        throw new RuntimeException("No se encontró application.properties");
                    }
                    properties.load(input );
                    return properties;
                } catch (IOException e) {
                    throw new RuntimeException("Error cargando configuración",e
                    );
                }
    }
}
