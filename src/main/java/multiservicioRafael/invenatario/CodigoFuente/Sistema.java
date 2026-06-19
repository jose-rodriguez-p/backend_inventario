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
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ClienteDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.LoginDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.MenuDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ProveedorDao;
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
    ExportadorService exportador = ExportadorService.getInstancia();

    // Simulamos los datos iniciales
    private void generarDatosMasivos() {
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

        listaCategorias.addAll(List.of("Lubricantes", "Repuestos", "Filtros", "Neumáticos", "Frenos"));
        listaDocumentos.addAll(List.of(Map.of("id", 1, "nombre", "DNI"), Map.of("id", 2, "nombre", "Pasaporte")));
        listaCargos.addAll(List.of(Map.of("id", 1, "nombre", "Mecánico"), Map.of("id", 2, "nombre", "Administrador"), Map.of("id", 3, "nombre", "Vendedor")));
    }

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

    // --- MÉTODOS DE PRODUCTOS ---
    /**
     * Listar productos con el nombre del proveedor (datos genéricos)
     *
     * @return Lista de productos con información del proveedor
     */
    public List<Map<String, Object>> listarProductosConProveedor() {
        List<Map<String, Object>> lista = new ArrayList<>();

        // Datos genéricos por ahora
        String[] cats = {"Lubricantes", "Repuestos", "Filtros", "Neumáticos", "Frenos"};
        String[] prodNombres = {"Aceite Motor", "Filtro Aire", "Pastillas Freno", "Neumático", "Bujía", "Amortiguador", "Disco Freno", "Batería"};

        for (int i = 1; i <= 30; i++) {
            Map<String, Object> fila = new HashMap<>();
            fila.put("codigo", "PROD-" + String.format("%03d", i));
            fila.put("nombre", prodNombres[i % 8] + " " + (i * 10));
            fila.put("marca", "Marca " + (i % 5));
            fila.put("categoria", cats[i % 5]);
            fila.put("stock", 10 + i);
            fila.put("stock_minimo", 5 + (i % 5));
            fila.put("precio_compra", 25.0 + (i * 2));
            fila.put("precio_venta", 30.0 + (i * 2.5));
            fila.put("estado", i % 3 == 0 ? "Inactivo" : "Activo");
            fila.put("ruc_proveedor", i % 2 == 0 ? "20" + (1000000000L + (i / 2)) : "");
            fila.put("nombre_proveedor", i % 2 == 0 ? "Empresa Automotriz " + ((i / 2) + 1) + " S.A." : "Sin proveedor");
            lista.add(fila);
        }

        return lista;
    }

    /**
     * Agregar un nuevo producto (datos genéricos)
     *
     * @param datos Map con los datos del producto
     * @return "PRODUCTO_REGISTRADO" si exitoso, "error" si falla
     */
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

    public String editarProducto(Map<String, Object> datos) {
        try {
            String codigo = String.valueOf(datos.getOrDefault("codigo", "")).trim();
            Map<String, Object> productoOriginal = obtenerProductoPorCodigo(codigo);

            if (productoOriginal == null) {
                return "error";
            }
            boolean hayCambios = false;

            String nombreNuevo = String.valueOf(datos.getOrDefault("nombre", "")).trim();
            String nombreOriginal = String.valueOf(productoOriginal.getOrDefault("nombre", "")).trim();
            if (!nombreNuevo.equals(nombreOriginal)) {
                hayCambios = true;
            }

            String marcaNuevo = String.valueOf(datos.getOrDefault("marca", "")).trim();
            String marcaOriginal = String.valueOf(productoOriginal.getOrDefault("marca", "")).trim();
            if (!marcaNuevo.equals(marcaOriginal)) {
                hayCambios = true;
            }

            int cantidadNueva = Integer.parseInt(String.valueOf(datos.getOrDefault("cantidad", "0")));
            int cantidadOriginal = Integer.parseInt(String.valueOf(productoOriginal.getOrDefault("stock", "0")));
            if (cantidadNueva != cantidadOriginal) {
                hayCambios = true;
            }

            double precioVentaNuevo = Double.parseDouble(String.valueOf(datos.getOrDefault("precio_venta", "0")));
            double precioVentaOriginal = Double.parseDouble(String.valueOf(productoOriginal.getOrDefault("precio_venta", "0")));
            if (precioVentaNuevo != precioVentaOriginal) {
                hayCambios = true;
            }

            int stockMinimoNuevo = Integer.parseInt(String.valueOf(datos.getOrDefault("stock_minimo", "5")));
            int stockMinimoOriginal = Integer.parseInt(String.valueOf(productoOriginal.getOrDefault("stock_minimo", "5")));
            if (stockMinimoNuevo != stockMinimoOriginal) {
                hayCambios = true;
            }

            String estadoNuevo = String.valueOf(datos.getOrDefault("estado", "Activo")).trim();
            String estadoOriginal = String.valueOf(productoOriginal.getOrDefault("estado", "Activo")).trim();
            if (!estadoNuevo.equals(estadoOriginal)) {
                hayCambios = true;
            }

            String rucProveedorNuevo = String.valueOf(datos.getOrDefault("ruc_proveedor", "")).trim();
            String rucProveedorOriginal = String.valueOf(productoOriginal.getOrDefault("ruc_proveedor", "")).trim();
            if (!rucProveedorNuevo.equals(rucProveedorOriginal)) {
                hayCambios = true;
            }

            if (!hayCambios) {
                return "SIN_CAMBIOS";
            }
            System.out.println("Editando producto: " + codigo);
            System.out.println("Proveedor RUC: " + rucProveedorNuevo);
            return "PRODUCTO_ACTUALIZADO";
        } catch (Exception e) {
            System.out.println("Error en editarProducto: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * Obtener un producto por su código (para comparar cambios)
     *
     * @param codigo Código del producto
     * @return Map con los datos del producto o null si no existe
     */
    private Map<String, Object> obtenerProductoPorCodigo(String codigo) {
        List<Map<String, Object>> productos = listarProductosConProveedor();
        for (Map<String, Object> producto : productos) {
            if (codigo.equals(String.valueOf(producto.get("codigo")))) {
                return producto;
            }
        }
        return null;
    }

    /**
     * Obtener categorías de productos (datos genéricos)
     *
     * @return Lista de categorías
     */
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

    // --- MÉTODOS DE CLIENTES ---
    /**
     * Agregar un nuevo cliente
     *
     * @param datos Map con los datos del cliente
     * @return "CLIENTE_REGISTRADO" si exitoso, "error" si falla
     */
    public String agregarCliente(Map<String, Object> datos) {
        // TODO: Implementar método en ClienteDao para agregar cliente
        try {
            // Por ahora, retorno un mensaje temporal
            return "CLIENTE_REGISTRADO";
        } catch (Exception e) {
            System.out.println("Error en agregarCliente: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * Editar un cliente existente
     *
     * @param datos Map con los datos del cliente a editar
     * @return "CLIENTE_ACTUALIZADO" si exitoso, "error" si falla
     */
    public String editarCliente(Map<String, Object> datos) {
        // TODO: Implementar método en ClienteDao para editar cliente
        try {
            // Por ahora, retorno un mensaje temporal
            return "CLIENTE_ACTUALIZADO";
        } catch (Exception e) {
            System.out.println("Error en editarCliente: " + e.getMessage());
            e.printStackTrace();
            return "error";
        }
    }

}
