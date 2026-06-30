package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;


import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.ClienteDao;
import multiservicioRafael.invenatario.CodigoFuente.Patrones.ConsultaDNI;
import multiservicioRafael.invenatario.CodigoFuente.ServicioExportacion.ExportadorService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ClienteFachada {

    private final ClienteDao clienteDao;
    private final ExportadorService exportador;

    public ClienteFachada() {
        this.clienteDao = new ClienteDao();
        this.exportador = ExportadorService.getInstancia();
    }
    public boolean consultarDBclienteExiste(String dni) {
        return clienteDao.validarExisteCliente(dni);
    }

    public List<Map<String, Object>> listarClientesConCarros() {
        try {
            return clienteDao.listarClientesConCarros();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    
    public String agregarCliente(Map<String, Object> datos, String usuarioLogueado) {
        try {
            Map<String, Object> clienteData = (Map<String, Object>) datos.get("cliente");
            List<Map<String, String>> carrosData = (List<Map<String, String>>) datos.get("carros");

            return clienteDao.registrarClienteConCarros(clienteData, carrosData, usuarioLogueado);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
    public String editarCliente(Map<String, Object> payload, String usuarioLogueado) {
        try {
            Map<String, Object> cliente = (Map<String, Object>) payload.get("cliente");
            List<Map<String, String>> carros = (List<Map<String, String>>) payload.get("carros");

            if (cliente == null || cliente.get("dni") == null) {
                return "error_validacion: Falta el DNI del cliente";
            }

            return clienteDao.editarClienteConCarros(cliente, carros, usuarioLogueado);
        } catch (Exception e) {
            System.out.println("Error en ClienteFachada.editarCliente: " + e.getMessage());
            e.printStackTrace();
            return "error_backend: " + e.getMessage();
        }
    }
    public String consultarDNI(String dni) {
        String resultado = ConsultaDNI.getInstance().getConsulta(dni);
        if (resultado == null) {
            return "No se pudo obtener información del DNI";
        }
        return resultado;
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
}
