
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Trabajador;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.TrabajadorDao;
import multiservicioRafael.invenatario.CodigoFuente.ServicioExportacion.ExportadorService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrabajadorFachada {

    private final TrabajadorDao trabajadorDao;
    private final ExportadorService exportador;

    public TrabajadorFachada() {
        this.trabajadorDao = new TrabajadorDao();
        this.exportador = ExportadorService.getInstancia();
    }
    public String nuevoTrabajador(Map<String, Object> datos, String usuarioLogueado) {
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
        String quienRegistra = obtenerStringSeguro(datos.get("usuarioLogueado"), usuarioLogueado);
        if (quienRegistra == null || quienRegistra.isBlank()) {
            return "error_usuario_logueado_no_existe";
        }

        return trabajadorDao.agregarTranbajador(t, usuarioAcceso, contrasena, quienRegistra);
    }
    public String actualizarTrabajador(String dni, Map<String, Object> datosActualizados, String usuarioLogueado) {
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

        return trabajadorDao.editarTrabajador(t, usuarioLogueado, username, password);
    }
    public boolean existeDniTrabajador(String dni) {
        return trabajadorDao.existeDni(dni);
    }
    public ArrayList<Trabajador> obtenerListaTrabajadores() {
        ArrayList<Trabajador> trabajadores = trabajadorDao.listTrabajador();
        for (Trabajador t : trabajadores) {
            System.out.println(t);
        }
        return trabajadores;
    }
    public List<Map<String, Object>> obtenerListaDocumentos() {
        ArrayList<Map<String, Object>> docs = trabajadorDao.listarDocumentos();
        return docs;
    }
    public List<Map<String, Object>> obtenerListaCargos() {
        ArrayList<Map<String, Object>> cargos = trabajadorDao.listarCargos();
        return cargos;
    }
    public byte[] generarPDFBytes(List<Map<String, Object>> trabajadores) throws Exception {
        String[] headers = {"Documento", "Nombre", "Apellido", "Cargo", "Estado", "Fecha Registro"};
        String[] keys = {"numeroDocumento", "nombre", "apellido_paterno", "cargo", "estado", "fecha_registro"};
        float[] pesos = {3f, 3.5f, 4.5f, 4f, 2.5f, 3.5f};

        return exportador.generarPDF("Reporte de Personal", headers, keys, pesos, trabajadores);
    }
    public byte[] generarExcelBytes(List<Map<String, Object>> trabajadores) throws Exception {
        String[] headers = {"DOCUMENTO", "NOMBRE", "APELLIDO", "CARGO", "ESTADO", "FECHA REGISTRO"};
        String[] keys = {"numeroDocumento", "nombre", "apellido_paterno", "cargo", "estado", "fecha_registro"};

        return exportador.generarExcel("Reporte de Personal", headers, keys, trabajadores);
    }

    private String obtenerStringSeguro(Object valor, String valorDefecto) {
        if (valor == null) {
            return valorDefecto;
        }
        String resultado = String.valueOf(valor).trim();
        return resultado.isEmpty() ? valorDefecto : resultado;
    }
}
