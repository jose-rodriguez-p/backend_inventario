package multiservicioRafael.invenatario.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import multiservicioRafael.invenatario.repository.UsuarioLogeado;
import org.springframework.stereotype.Component;


@Component
public class UsuarioContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String usuario = httpRequest.getHeader("X-User-Logged");
            if (usuario != null && !usuario.trim().isEmpty()) {
                UsuarioLogeado.setUsuario(usuario.trim());
            }
        }
        
        try {
            chain.doFilter(request, response);
        } finally {
            UsuarioLogeado.limpiar();
        }
    }
}
