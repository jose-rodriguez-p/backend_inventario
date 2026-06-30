package multiservicioRafael.invenatario.CodigoFuente;

import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Cliente;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Producto;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Usuario;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Proveedor;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Trabajador;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Categoria;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Marca;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ClienteDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ConfiguracionDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.LoginDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.MenuDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ProveedorDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.RepuestoDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.RolDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.TrabajadorDao;
import multiservicioRafael.invenatario.CodigoFuente.ModuloCorreo.ServicioCorreo;
import multiservicioRafael.invenatario.CodigoFuente.Patrones.GeneradorCodigo;
import multiservicioRafael.invenatario.CodigoFuente.Patrones.RegistroCodigosVerificacion;
import multiservicioRafael.invenatario.CodigoFuente.ServicioExportacion.ExportadorService;
import multiservicioRafael.invenatario.CodigoFuente.Patrones.ConsultaDNI;
import multiservicioRafael.invenatario.CodigoFuente.Patrones.ConsultaRuc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class Sistema {

    private String usuario;

    ProveedorDao proveedor = new ProveedorDao();

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
    private final List<String> listaCategorias;
    private final List<Map<String, Object>> listaDocumentos;
    private final List<Map<String, Object>> listaCargos;
    private final LoginDao login = new LoginDao();

    private Sistema() {
        this.listaClientes = new ArrayList<>();
        this.listaProductos = new ArrayList<>();
        this.listaCategorias = new ArrayList<>();
        this.listaDocumentos = new ArrayList<>();
        this.listaCargos = new ArrayList<>();

    }
    ExportadorService exportador = ExportadorService.getInstancia();

    public Usuario procesarLogin(String usuario, String contrasena) {
        Usuario user = login.validando(usuario, contrasena);
        if (user == null) {
            return null;
        }
        this.usuario = user.getUsername();
        return user;
    }

    public void setUsuarioLogueado(String usuario) {
        this.usuario = usuario;
    }

    public String validarUsuarioExistente(String usuario) {
        String resultado = login.recuperar_contrasena(usuario);
        System.out.println("usuario es: " + resultado);
        if (resultado == null || "ERROR".equalsIgnoreCase(resultado)) {
            return "ERROR";
        }
        return resultado;
    }

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

    public String validarCodigoIngresado(String usuario, String codigo) {
        if (codigo == null || codigo.trim().isBlank()) {
            return "CODIGO_INVALIDO";
        }
        boolean valido = RegistroCodigosVerificacion.getInstancia().validar(usuario, codigo.trim());
        return valido ? "CODIGO_VALIDO" : "CODIGO_INVALIDO";
    }

    public boolean actualizarContrasena(String usuario, String nuevaContrasena) {
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

    public String nuevoTrabajador(Map<String, Object> datos) {
        TrabajadorDao trabajadorDao = new TrabajadorDao();
        Trabajador t = new Trabajador();
        t.setNumeroDocumento(obtenerStringSeguro(datos.get("numeroDocumento"), ""));
        t.setNombre(obtenerStringSeguro(datos.get("nombre"), ""));
        t.setApellido_paterno(obtenerStringSeguro(datos.get("apellido_paterno"), ""));
        t.setApellido_materno(obtenerStringSeguro(datos.get("apellido_materno"), ""));
        t.setCelular(obtenerStringSeguro(datos.get("celular"), ""));
        t.setCorreo(obtenerStringSeguro(datos.get("correo"), ""));
        t.setDireccion(obtenerStringSeguro(datos.get("direccion"), ""));
        t.setDocumento(obtenerStringSeguro(datos.get("nombre_documento"), "DNI"));
        t.setCargo(obtenerStringSeguro(datos.get("nombre_cargo"), ""));
        t.setEstado(obtenerStringSeguro(datos.get("estado"), "Activo"));
        String usuarioAcceso = obtenerStringSeguro(datos.get("usuario"), null);
        String contrasena = datos.get("contrasena") != null ? String.valueOf(datos.get("contrasena")) : null;
        if (contrasena != null && contrasena.trim().isEmpty()) {
            contrasena = null;
        }
        String usuarioLogueado = obtenerStringSeguro(datos.get("usuarioLogueado"), this.usuario);
        if (usuarioLogueado == null || usuarioLogueado.isBlank()) {
            return "error_usuario_logueado_no_existe";
        }
        return trabajadorDao.agregarTranbajador(t, usuarioAcceso, contrasena, usuarioLogueado);
    }

    private String obtenerStringSeguro(Object valor, String valorDefecto) {
        if (valor == null) {
            return valorDefecto;
        }
        String resultado = String.valueOf(valor).trim();
        return resultado.isEmpty() ? valorDefecto : resultado;
    }

    public boolean existeDniTrabajador(String dni) {
        TrabajadorDao dao = new TrabajadorDao();
        return dao.existeDni(dni);
    }

    public byte[] generarPDFBytes(List<Map<String, Object>> trabajadores) throws Exception {
        String[] headers = {"Documento", "Nombre", "Apellido", "Cargo", "Estado", "Fecha Registro"};
        String[] keys = {"numeroDocumento", "nombre", "apellido_paterno", "cargo", "estado", "fecha_registro"};
        float[] pesos = {3f, 3.5f, 4.5f, 4f, 2.5f, 3.5f};

        return exportador.generarPDF("Reporte de Personal", headers, keys, pesos, trabajadores);
    }

    //generar el excel llamado  a la clase exportar servicio
    public byte[] generarExcelBytes(List<Map<String, Object>> trabajadores) throws Exception {
        String[] headers = {"DOCUMENTO", "NOMBRE", "APELLIDO", "CARGO", "ESTADO", "FECHA REGISTRO"};
        String[] keys = {"numeroDocumento", "nombre", "apellido_paterno", "cargo", "estado", "fecha_registro"};

        return exportador.generarExcel("Reporte de Personal", headers, keys, trabajadores);
    }

    public byte[] generarPDFBytesProveedores(List<Map<String, Object>> proveedores) throws Exception {
        String[] headers = {"RUC", "Nombre Empresa", "Celular", "Correo", "Dirección", "Estado"};
        String[] keys = {"ruc", "nombre_empresa", "celular", "correo", "direccion", "estado"};
        float[] pesos = {3f, 4.5f, 3f, 3.5f, 4f, 2.5f};

        return exportador.generarPDF("Reporte de Proveedores", headers, keys, pesos, proveedores);
    }

    public byte[] generarExcelBytesProveedores(List<Map<String, Object>> proveedores) throws Exception {
        String[] headers = {"RUC", "NOMBRE EMPRESA", "CELULAR", "CORREO", "DIRECCIÓN", "ESTADO"};
        String[] keys = {"ruc", "nombre_empresa", "celular", "correo", "direccion", "estado"};

        return exportador.generarExcel("Reporte de Proveedores", headers, keys, proveedores);
    }

    public byte[] generarPDFBytesClientes(List<Map<String, Object>> clientes) throws Exception {
        String[] headers = {"DNI", "Nombre", "Apellido Paterno", "Apellido Materno", "Celular", "Correo", "Estado", "Vehículos"};
        String[] keys = {"dni", "nombre", "apellido_paterno", "apellido_materno", "celular", "correo", "estado", "vehiculos"};
        float[] pesos = {2.5f, 3.5f, 4f, 4f, 2.5f, 3.5f, 2f, 4f};

        return exportador.generarPDF("Reporte de Clientes", headers, keys, pesos, clientes);
    }

    public byte[] generarExcelBytesClientes(List<Map<String, Object>> clientes) throws Exception {
        String[] headers = {"DNI", "NOMBRE", "APELLIDO PATERNO", "APELLIDO MATERNO", "CELULAR", "CORREO", "ESTADO", "VEHÍCULOS"};
        String[] keys = {"dni", "nombre", "apellido_paterno", "apellido_materno", "celular", "correo", "estado", "vehiculos"};

        return exportador.generarExcel("Reporte de Clientes", headers, keys, clientes);
    }

    public byte[] generarPDFBytesRepuestos(List<Map<String, Object>> repuestos) throws Exception {
        String[] headers = {"Producto", "Marca", "Categoría", "Proveedor", "Stock", "Stock Mínimo", "Precio Venta", "Estado"};
        String[] keys = {"nombre", "marca", "categoria", "nombre_proveedor", "stock", "stock_minimo", "precio_venta", "estado"};
        float[] pesos = {3.5f, 3f, 3f, 3.5f, 2f, 2f, 2.5f, 2f};

        return exportador.generarPDF("Reporte de Productos", headers, keys, pesos, repuestos);
    }

    public byte[] generarExcelBytesRepuestos(List<Map<String, Object>> repuestos) throws Exception {
        String[] headers = {"PRODUCTO", "MARCA", "CATEGORÍA", "PROVEEDOR", "STOCK", "STOCK MÍNIMO", "PRECIO VENTA", "ESTADO"};
        String[] keys = {"nombre", "marca", "categoria", "nombre_proveedor", "stock", "stock_minimo", "precio_venta", "estado"};

        return exportador.generarExcel("Reporte de Productos", headers, keys, repuestos);
    }

    // --- MÉTODOS DE ACCESO A DATOS (GETTERS) ---
    public List<Cliente> obtenerListaClientes() {
        return listaClientes;
    }

    public List<Producto> obtenerListaProductos() {
        return listaProductos;
    }

    public String actualizarTrabajador(String dni, Map<String, Object> datosActualizados) {
        TrabajadorDao trabajadorDao = new TrabajadorDao();
        Trabajador t = new Trabajador();
        t.setNumeroDocumento(dni != null ? dni.trim() : "");
        t.setNombre(datosActualizados.get("nombre") != null ? String.valueOf(datosActualizados.get("nombre")).trim() : "");
        t.setApellido_paterno(datosActualizados.get("apellido_paterno") != null ? String.valueOf(datosActualizados.get("apellido_paterno")).trim() : "");
        t.setApellido_materno(datosActualizados.get("apellido_materno") != null ? String.valueOf(datosActualizados.get("apellido_materno")).trim() : "");
        t.setCelular(datosActualizados.get("celular") != null ? String.valueOf(datosActualizados.get("celular")).trim() : "");
        t.setCorreo(datosActualizados.get("correo") != null ? String.valueOf(datosActualizados.get("correo")).trim() : "");
        t.setDireccion(datosActualizados.get("direccion") != null ? String.valueOf(datosActualizados.get("direccion")).trim() : "");
        t.setCargo(datosActualizados.get("cargo") != null ? String.valueOf(datosActualizados.get("cargo")).trim() : "");

        String estadoForm = datosActualizados.get("estado") != null ? String.valueOf(datosActualizados.get("estado")).trim() : "";
        t.setEstado(estadoForm.isEmpty() ? "Activo" : estadoForm);
        String username = datosActualizados.get("usuario") != null ? String.valueOf(datosActualizados.get("usuario")).trim() : null;
        if (username != null && username.isEmpty()) {
            username = null;
        }

        String password = datosActualizados.get("contrasena") != null ? String.valueOf(datosActualizados.get("contrasena")) : null;
        return trabajadorDao.editarTrabajador(
                t,
                this.usuario,
                username,
                password
        );
    }

    public ArrayList<Trabajador> obtenerListaTrabajadores() {
        TrabajadorDao trabajador = new TrabajadorDao();
        ArrayList<Trabajador> trabajadores = trabajador.listTrabajador();
        for (Trabajador t : trabajadores) {
            System.out.println(t);
        }
        if (trabajadores != null) {
            return trabajadores;
        }
        return null;
    }

    public List<String> obtenerListaCategorias() {
        return listaCategorias;
    }

    public List<Map<String, Object>> obtenerListaDocumentos() {
        TrabajadorDao dao = new TrabajadorDao();
        ArrayList<Map<String, Object>> docs = dao.listarDocumentos();
        return docs.isEmpty() ? listaDocumentos : docs;
    }

    public List<Map<String, Object>> obtenerListaCargos() {
        TrabajadorDao dao = new TrabajadorDao();
        ArrayList<Map<String, Object>> cargos = dao.listarCargos();
        return cargos.isEmpty() ? listaCargos : cargos;
    }

    public ArrayList<Map<String, Object>> listarRoles() {
        try {
            RolDao rol = new RolDao();
            return rol.listarRolesComoArreglos();
        } catch (Exception e) {
            System.err.println("Error en Sistema.listarRoles: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean actualizarRolYAccesos(String nombre, String estado, ArrayList<String> menus) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        RolDao rolDao = new RolDao();
        return rolDao.actualizarRolCompleto(nombre, estado, this.usuario, menus);
    }

    public ArrayList<String> listamenu() {
        MenuDao lista = new MenuDao();
        if (lista.listarmenus() != null) {
            return lista.listarmenus();
        } else {
            System.out.println("Los datos estan viajando vacios");
        }
        return null;
    }

    public boolean agregarRol(Map<String, Object> nuevoRol) {
        if (nuevoRol == null || !nuevoRol.containsKey("nombre")) {
            System.out.println("Error: El objeto nuevoRol es inválido.");
            return false;
        }
        RolDao rolDao = new RolDao();
        System.out.println("Procesando creación de rol por usuario: " + this.usuario);
        return rolDao.agregarRol(this.usuario, nuevoRol);
    }

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

    public Map<String, Object> consultarDNIParseado(String dni) {
        String json = ConsultaDNI.getInstance().getConsulta(dni);
        Map<String, Object> respuesta = new HashMap<>();
        if (json == null || json.isBlank()) {
            respuesta.put("success", false);
            return respuesta;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode dataNode = null;
            boolean success = false;
            if (root.has("success") && root.has("data")) {
                success = root.path("success").asBoolean(false);
                dataNode = root.path("data");
            } else if (root.has("nombres") || root.has("apellidoPaterno")) {
                success = true;
                dataNode = root;
            } else if (root.has("data")) {
                success = true;
                dataNode = root.path("data");
            }
            if (dataNode != null && !dataNode.isMissingNode()) {
                respuesta.put("success", success);
                respuesta.put("nombres", dataNode.path("nombres").asText(""));
                respuesta.put("apellidoPaterno", dataNode.path("apellidoPaterno").asText(""));
                respuesta.put("apellidoMaterno", dataNode.path("apellidoMaterno").asText(""));
                if (respuesta.get("nombres").toString().isBlank()) {
                    respuesta.put("nombres", dataNode.path("nombres_completos").asText(""));
                    if (respuesta.get("nombres").toString().isBlank()) {
                        respuesta.put("nombres", dataNode.path("nombre_completo").asText(""));
                    }
                }
                if (respuesta.get("nombres") == null || respuesta.get("nombres").toString().isBlank()) {
                    respuesta.put("success", false);
                }
            } else {
                respuesta.put("success", false);
            }
        } catch (Exception e) {
            System.err.println("Error al parsear JSON de DNI: " + e.getMessage());
            e.printStackTrace();
            respuesta.put("success", false);
        }
        return respuesta;
    }

    public String consultarDNI(String dni) {
        String resultado = ConsultaDNI.getInstance().getConsulta(dni);
        if (resultado == null) {
            return "No se pudo obtener información del DNI";
        }
        return resultado;
    }
//En estos metodo son de proveedor

    public Map<String, String> consutaRuc(String ruc) {
        String existencia = ValidarExistenciaProveedor(ruc);
        if ("existe".equalsIgnoreCase(existencia)) {
            Map<String, String> respuestaExiste = new HashMap<>();
            respuestaExiste.put("error_tipo", "YA_EXISTE");
            return respuestaExiste;
        }
        String json = ConsultaRuc.getInstance().getConsulta(ruc);
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            Map<String, String> datos = new HashMap<>();
            datos.put("razonSocial", root.path("razonSocial").asText());
            datos.put("direccion", root.path("direccion").asText());

            return datos;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean actualizarDatosProveedor(String ruc, Proveedor proveedorActualizado) {
        System.out.println(proveedorActualizado);
        String resultado = proveedor.ActualizarProveedor(usuario, proveedorActualizado);
        System.out.println(resultado);
        if (resultado.equalsIgnoreCase("actualizado")) {
            return true;
        }
        return false;
    }

    public String ValidarExistenciaProveedor(String ruc) {
        return proveedor.ValidarExistencia(ruc);
    }

    public boolean registrarProveedor(Proveedor nuevoProveedor) {
        String resultado = proveedor.agregarProveedor(this.usuario, nuevoProveedor);
        if (resultado.equalsIgnoreCase("registrado")) {
            return true;
        }
        return false;
    }

    public ArrayList<Proveedor> obtenerListaProveedores() {
        return proveedor.listarProveedores();
    }

    //metodos de clientes
    ClienteDao clienteDao = new ClienteDao();

    public boolean consultarDBclienteExiste(String dni) {
        return clienteDao.validarExisteCliente(dni);
    }

    public List<Map<String, Object>> listarClientesConCarros() {
        try {
            ClienteDao dao = new ClienteDao();
            return dao.listarClientesConCarros();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String agregarProducto(Map<String, Object> datos) {
        try {
            System.out.println("Agregando producto: " + datos.get("codigo"));
            System.out.println("Proveedor RUC: " + datos.get("ruc_proveedor"));
            return "PRODUCTO_REGISTRADO";
        } catch (Exception e) {
            System.out.println("Error en agregarProducto: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    public List<Map<String, Object>> obtenerCategoriasProductos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String[] cats = {"Lubricantes", "Repuestos", "Filtros", "Neumáticos", "Frenos"};

        for (int i = 0; i < cats.length; i++) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("id", i + 1);
            fila.put("nombre", cats[i]);
            lista.add(fila);
        }

        return lista;
    }

    public String agregarCliente(Map<String, Object> datos) {
        try {
            Map<String, Object> clienteData = (Map<String, Object>) datos.get("cliente");
            List<Map<String, String>> carrosData = (List<Map<String, String>>) datos.get("carros");

            return clienteDao.registrarClienteConCarros(clienteData, carrosData, this.usuario);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String editarCliente(Map<String, Object> payload) {
        try {
            Map<String, Object> cliente = (Map<String, Object>) payload.get("cliente");
            List<Map<String, String>> carros = (List<Map<String, String>>) payload.get("carros");

            if (cliente == null || cliente.get("dni") == null) {
                return "error_validacion: Falta el DNI del cliente";
            }

            return this.clienteDao.editarClienteConCarros(cliente, carros, this.usuario);
        } catch (Exception e) {
            System.out.println("Error en editarCliente: " + e.getMessage());
            e.printStackTrace();
            return "error_backend: " + e.getMessage();
        }
    }

    public boolean verificarContrasena(String username, String contrasenaActual) {
        LoginDao login = new LoginDao();
        Usuario usuario = login.validando(username, contrasenaActual);
        if (usuario == null) {
            return false;
        }
        return true;
    }

    ;
    public boolean restablecerContrasenaUsuario(String usuario) {
        LoginDao login = new LoginDao();
        try {
            if (usuario == null || usuario.trim().isEmpty()) {
                return false;
            }
            String resultado = login.resetearContrasena(usuario.trim());

            if ("PASSWORD_RESETEADA".equals(resultado)) {
                return true;
            } else {
                System.out.println("No se pudo resetear la contraseña. Motivo: " + resultado);
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error en la capa de Sistema al restablecer contraseña: " + e.getMessage());
            return false;
        }
    }

    public List<Categoria> listarCategoria() {
        ConfiguracionDao configuracion = new ConfiguracionDao();
        List<Categoria> lista = configuracion.listarCategorias();

        if (lista == null || lista.isEmpty()) {
            System.out.println("No se encontraron categorías o la lista está vacía.");
        } else {
            System.out.println("Categorías cargadas correctamente. Total: " + lista.size());
        }

        return lista;
    }

    public String agregarCategoriaSistema(Categoria categoria) {

        if (categoria == null) {
            return "ERROR: La categoría es nula.";
        }

        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return "ERROR: El nombre de la categoría no puede estar vacío.";
        }

        if (categoria.getEstado() == null || categoria.getEstado().trim().isEmpty()) {
            return "ERROR: El estado de la categoría no puede estar vacío.";
        }
        ConfiguracionDao dao = new ConfiguracionDao();
        System.out.println(this.usuario);
        return dao.agregarCategoriaConFuncion(this.usuario, categoria);
    }

    public String modificarEstadoCategoriaSistema(String nombreCategoria, String nuevoEstado) {
        System.out.println(this.usuario);

        if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) {
            return "ERROR: El nombre de la categoría no puede estar vacío.";
        }

        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return "ERROR: El nuevo estado no puede estar vacío.";
        }
        ConfiguracionDao dao = new ConfiguracionDao();
        return dao.modificarEstadoCategoriaConFuncion(
                this.usuario,
                nombreCategoria.trim(),
                nuevoEstado.trim()
        );
    }

    public List<Marca> obtenerMarcasConCategorias() {
        ConfiguracionDao configuracionDao = new ConfiguracionDao();
        return configuracionDao.listarMarcasConCategorias();
    }

    public String agregarMarcaConCategoriasSistema(String marcaNombre, String marcaEstado, List<String> categoriasNombres) {
        ConfiguracionDao configuracionDao = new ConfiguracionDao();
        return configuracionDao.agregarMarcaConCategorias(this.usuario, marcaNombre, marcaEstado, categoriasNombres);
    }

    public String editarMarcaConCategoriasSistema(String marcaNombre, String marcaEstadoNuevo, List<String> categoriasNombres) {
        ConfiguracionDao configuracionDao = new ConfiguracionDao();
        return configuracionDao.editarMarcaConCategorias(this.usuario, marcaNombre, marcaEstadoNuevo, categoriasNombres);
    }

    public List<Map<String, Object>> listarCategoriasConMarcas() {
        ConfiguracionDao configuracionDao = new ConfiguracionDao();
        return configuracionDao.listarCategoriasConMarcas();
    }

    public List<Map<String, Object>> listarRepuestos() {
        RepuestoDao repuesto = new RepuestoDao();
        return repuesto.listarRepuestos();
    }

    public String agregarRepuestoSistema(String nombre, String categoriaNombre, String marcaNombre,
            String proveedorNombre, int cantidad, java.math.BigDecimal precioCompra,
            java.math.BigDecimal precioVenta, int stockMinimo, String estado) {
        RepuestoDao repuestoDao = new RepuestoDao();
        return repuestoDao.agregarRepuesto(this.usuario, nombre, categoriaNombre, marcaNombre,
                proveedorNombre, cantidad, precioCompra, precioVenta,
                stockMinimo, estado);
    }

    public String editarRepuestoSistema(String nombre, String categoriaNombre, String marcaNombre,
            String proveedorNombre, int cantidad, java.math.BigDecimal precioCompra,
            java.math.BigDecimal precioVenta, int stockMinimo, String estado) {
        RepuestoDao repuestoDao = new RepuestoDao();
        return repuestoDao.editarRepuesto(this.usuario, nombre, categoriaNombre, marcaNombre,
                proveedorNombre, cantidad, precioCompra, precioVenta,
                stockMinimo, estado);
    }

}
