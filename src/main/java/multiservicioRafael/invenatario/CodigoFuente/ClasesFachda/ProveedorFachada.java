package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;



import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Proveedor;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ProveedorDao;
import multiservicioRafael.invenatario.CodigoFuente.Patrones.ConsultaRuc;
import multiservicioRafael.invenatario.CodigoFuente.ServicioExportacion.ExportadorService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProveedorFachada {

    private final ProveedorDao proveedorDao;
    private final ExportadorService exportador;

    public ProveedorFachada() {
        this.proveedorDao = new ProveedorDao();
        this.exportador = ExportadorService.getInstancia();
    }

    
    public Map<String, String> consultaRuc(String ruc) {
        String existencia = validarExistenciaProveedor(ruc);
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

    public String validarExistenciaProveedor(String ruc) {
        return proveedorDao.ValidarExistencia(ruc);
    }

    public boolean registrarProveedor(Proveedor nuevoProveedor, String usuarioLogueado) {
        String resultado = proveedorDao.agregarProveedor(usuarioLogueado, nuevoProveedor);
        return "registrado".equalsIgnoreCase(resultado);
    }

    
    public boolean actualizarDatosProveedor(Proveedor proveedorActualizado, String usuarioLogueado) {
        System.out.println(proveedorActualizado);
        String resultado = proveedorDao.ActualizarProveedor(usuarioLogueado, proveedorActualizado);
        System.out.println(resultado);
        return "actualizado".equalsIgnoreCase(resultado);
    }

    public ArrayList<Proveedor> obtenerListaProveedores() {
        return proveedorDao.listarProveedores();
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
}
