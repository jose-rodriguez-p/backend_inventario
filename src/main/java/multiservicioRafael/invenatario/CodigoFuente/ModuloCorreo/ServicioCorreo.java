package multiservicioRafael.invenatario.CodigoFuente.ModuloCorreo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import multiservicioRafael.invenatario.CodigoFuente.Patrones.RegistroCodigosVerificacion;

public class ServicioCorreo {

    private static ServicioCorreo instancia;
    private final Properties config;
    private final Session session;

    private ServicioCorreo() {
        config = cargarConfiguracion();
        
        if (config.getProperty("brevo.smtp.user") == null || config.getProperty("brevo.smtp.password") == null) {
            throw new RuntimeException("Falta configurar las credenciales de Brevo en application.properties");
        }

        Properties smtpProps = new Properties();
        smtpProps.put("mail.smtp.auth", "true");
        smtpProps.put("mail.smtp.starttls.enable", "true");
        smtpProps.put("mail.smtp.host", config.getProperty("brevo.smtp.host", "smtp-relay.brevo.com"));
        smtpProps.put("mail.smtp.port", config.getProperty("brevo.smtp.port", "587"));

        this.session = Session.getInstance(smtpProps, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    config.getProperty("brevo.smtp.user"),
                    config.getProperty("brevo.smtp.password")
                );
            }
        });
    }

    public static ServicioCorreo getInstancia() {
        if (instancia == null) {
            instancia = new ServicioCorreo();
        }
        return instancia;
    }

    public boolean enviarCodigoVerificacion(String correoDestino, String codigo) {
        try {
            int minutos = RegistroCodigosVerificacion.getInstancia().getMinutosValidez();
            String remitente = config.getProperty("brevo.smtp.from", "rodriguezpenajosemanuel62@gmail.com");
            
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
                """.formatted(codigo, minutos);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente, "Multiservicios Rafael"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
            message.setSubject("🔐 Código de verificación");
            message.setContent(html, "text/html; charset=utf-8");

            Transport.send(message);
            return true;

        } catch (Exception e) {
            System.err.println("Error enviando correo con Brevo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Properties cargarConfiguracion() {
        return multiservicioRafael.invenatario.CodigoFuente.EnvLoader.loadProperties();
    }
}