package multiservicioRafael.invenatario.CodigoFuente;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ModuloConexion.LoginDao;
import multiservicioRafael.invenatario.CodigoFuente.ModuloConexion.TrabajadorDao;
import multiservicioRafael.invenatario.CodigoFuente.ModuloCorreo.ServicioCorreo;
import multiservicioRafael.invenatario.CodigoFuente.Patrones.GeneradorCodigo;
import multiservicioRafael.invenatario.CodigoFuente.Patrones.RegistroCodigosVerificacion;

public class Sistema {

    private static Sistema instancia;

    public static Sistema getInstancia() {
        if (instancia == null) {
            instancia = new Sistema();
        }
        return instancia;
    }

    // Arreglos de objetos (Temporales, se reemplazarán por Base de Datos más adelante)
    private final List<Cliente> listaClientes;
    private final List<Producto> listaProductos;
    private final List<Proveedor> listaProveedores;
    private final List<Trabajador> listaTrabajadores;
    private final List<String> listaCategorias;
    private final List<Map<String, Object>> listaDocumentos;
    private final List<Map<String, Object>> listaCargos;
    private final LoginDao login = new LoginDao();

    private Sistema() {
        this.listaClientes = new ArrayList<>();
        this.listaProductos = new ArrayList<>();
        this.listaProveedores = new ArrayList<>();
        this.listaTrabajadores = new ArrayList<>();
        this.listaCategorias = new ArrayList<>();
        this.listaDocumentos = new ArrayList<>();
        this.listaCargos = new ArrayList<>();

        generarDatosMasivos();
    }

    //en este simulamos a las personas 
    private void generarDatosMasivos() {
        // Requiere: SELECT * FROM clientes en la base de datos.
        String[] nombres = {"Jose", "Ana", "Carlos", "Lucia", "Ricardo", "Elena", "Pedro", "Maria", "Jorge", "Sofia"};
        String[] apellidosP = {"Rodriguez", "Lopez", "Sanchez", "Mendoza", "Vargas", "Castro", "Gomez", "Paredes", "Torres", "Vega"};
        String[] apellidosM = {"Peña", "Garcia", "Torres", "Ruiz", "Ortiz", "Soto", "Lara", "Diaz", "Rojas", "Salas"};

        for (int i = 1; i <= 30; i++) {
            listaClientes.add(new Cliente(
                    "7" + (10000000 + i),
                    nombres[i % 10] + " " + i,
                    apellidosP[i % 10],
                    apellidosM[i % 10],
                    "9" + (10000000 + i),
                    nombres[i % 10].toLowerCase() + i + "@gmail.com"
            ));
        }

        // Requiere: SELECT * FROM trabajadores JOIN cargos ON ...
        String[] cargosNombres = {"Mecánico", "Administrador", "Vendedor", "Asistente"};
        for (int i = 1; i <= 25; i++) {
            listaTrabajadores.add(new Trabajador(
                    "DNI",
                    "T" + (20000000 + i),
                    nombres[i % 10],
                    apellidosM[i % 10],
                    apellidosP[i % 10],
                    "9" + (20000000 + i),
                    "trabajador" + i + "@multiservicio.com",
                    "Calle Falsa " + i,
                    cargosNombres[i % 4],
                    "Activo"
            ));
        }

        // Requiere: SELECT * FROM productos JOIN categorias ON ...
        String[] cats = {"Lubricantes", "Repuestos", "Filtros", "Neumáticos", "Frenos"};
        String[] prodNombres = {"Aceite Motor", "Filtro Aire", "Pastillas Freno", "Neumático", "Bujía", "Amortiguador", "Disco Freno", "Batería"};
        for (int i = 1; i <= 30; i++) {
            listaProductos.add(new Producto(
                    "PROD-" + String.format("%03d", i),
                    prodNombres[i % 8] + " " + (i * 10),
                    "Marca " + (i % 5),
                    cats[i % 5],
                    10 + i,
                    5 + (i % 5),
                    25.0 + (i * 2),
                    "Activo"
            ));
        }

        // Requiere: SELECT * FROM proveedores
        for (int i = 1; i <= 10; i++) {
            listaProveedores.add(new Proveedor(
                    "20" + (1000000000L + i),
                    "Empresa Automotriz " + i + " S.A.",
                    "9" + (30000000 + i),
                    "contacto" + i + "@empresa.com",
                    "Zona Industrial " + i,
                    "Activo"
            ));
        }

        // Catálogos auxiliares
        // Requiere: SELECT * FROM categorias, SELECT * FROM documentos, etc.
        listaCategorias.addAll(List.of("Lubricantes", "Repuestos", "Filtros", "Neumáticos", "Frenos"));
        listaDocumentos.addAll(List.of(Map.of("id", 1, "nombre", "DNI"), Map.of("id", 2, "nombre", "Pasaporte")));
        listaCargos.addAll(List.of(Map.of("id", 1, "nombre", "Mecánico"), Map.of("id", 2, "nombre", "Administrador"), Map.of("id", 3, "nombre", "Vendedor")));
    }

    public Usuario procesarLogin(String usuario, String contrasena) {
        System.out.println("usuario " + usuario);
        System.out.println("password " + contrasena);

        Usuario user = login.validando(usuario, contrasena);
        if (user == null) {
            System.out.println("datos incorrectos");
            return null;
        }
        return user;
    }

    //@return correo electrónico del usuario o "ERROR" si no existe.
    public String validarUsuarioExistente(String usuario) {
        String resultado = login.recuperar_contrasena(usuario);
        System.out.println("usuario es: " + resultado);
        if (resultado == null || "ERROR".equalsIgnoreCase(resultado)) {
            return "ERROR";
        }
        return resultado;
    }

    //Genera código, lo guarda (15 min) y lo envía al correo vía Brevo.
    public String enviarCodigoVerificacion(String usuario, String correo) {
        String codigo = GeneradorCodigo.generarSeisDigitos();
        RegistroCodigosVerificacion.getInstancia().guardar(usuario, codigo);

        boolean enviado = ServicioCorreo.getInstancia().enviarCodigoVerificacion(correo, codigo);
        if (!enviado) {
            RegistroCodigosVerificacion.getInstancia().eliminar(usuario);
            return "ERROR";
        }
        return "CODIGO_ENVIADO";
    }

    // Valida el código de 6 dígitos ingresado por el usuario.
    public String validarCodigoIngresado(String usuario, String codigo) {
        if (codigo == null || codigo.trim().isBlank()) {
            return "CODIGO_INVALIDO";
        }
        boolean valido = RegistroCodigosVerificacion.getInstancia().validar(usuario, codigo.trim());
        return valido ? "CODIGO_VALIDO" : "CODIGO_INVALIDO";
    }

    // Actualiza la contraseña (pendiente: persistir en BD con DAO).
    public boolean actualizarContrasena(String usuario, String nuevaContrasena) {
        if (!RegistroCodigosVerificacion.getInstancia().estaValidado(usuario)) {
            return false;
        }
        if (nuevaContrasena == null || nuevaContrasena.isBlank()) {
            return false;
        }
        RegistroCodigosVerificacion.getInstancia().eliminar(usuario);
        String resultado = login.actualizarcontraseña(usuario, nuevaContrasena);
        if (resultado.equalsIgnoreCase("ERROR")) {
            return false;
        }
        return true;
    }

    // --- MÉTODOS DE ACCESO A DATOS (GETTERS) ---
    public List<Cliente> obtenerListaClientes() {

        return listaClientes;
    }

    public List<Producto> obtenerListaProductos() {
        return listaProductos;
    }

    public List<Proveedor> obtenerListaProveedores() {
        return listaProveedores;
    }

    public ArrayList<Trabajador> obtenerListaTrabajadores() {
        TrabajadorDao trabajador= new TrabajadorDao();
        ArrayList<Trabajador>trabajadores=trabajador.listTrabajador();
        if (!trabajadores.equals(null)) {
            return trabajadores;
        }
        return null;
    }

    public List<String> obtenerListaCategorias() {
        return listaCategorias;
    }

    public List<Map<String, Object>> obtenerListaDocumentos() {
        return listaDocumentos;
    }

    public List<Map<String, Object>> obtenerListaCargos() {
        return listaCargos;
    }

    //Obtiene estadísticas para el dashboard.
    // Requiere: Consultas agregadas (COUNT, SUM) sobre ventas, productos y clientes.
    public Map<String, Object> obtenerEstadisticasDashboard() {
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalVentas", 25800.75);
        estadisticas.put("totalClientes", listaClientes.size());
        estadisticas.put("totalProductos", listaProductos.size());
        estadisticas.put("productosBajoStock", 4);
        estadisticas.put("ventasMensuales", Map.of(
                "labels", List.of("Ene", "Feb", "Mar", "Abr", "May", "Jun"),
                "data", List.of(4500, 5200, 3800, 6100, 5900, 7200)
        ));
        return estadisticas;
    }
}
