package multiservicioRafael.invenatario.facade;

import multiservicioRafael.invenatario.repository.MantenimientoDao;
import multiservicioRafael.invenatario.repository.Interfaces.MantenimientoDaoInterface;
import multiservicioRafael.invenatario.modal.Trabajador;
import multiservicioRafael.invenatario.service.ServicioExportacion.ExportadorService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MantenimientoFachada {

    private final MantenimientoDaoInterface mantenimientoDao;
    private final TrabajadorFachada trabajadorFachada;
    private final ExportadorService exportador;

    public MantenimientoFachada() {
        this.mantenimientoDao = new MantenimientoDao();
        this.trabajadorFachada = new TrabajadorFachada();
        this.exportador = ExportadorService.getInstancia();
    }

    public Map<String, Object> registrarOrden(
            String dniCliente, String placa, String estado, String fecha,
            String nota, double precioManoObra, String usuarioLogueado,
            List<Map<String, Object>> items) {

        return mantenimientoDao.registrarOrden(dniCliente, placa, estado, fecha,
                nota, precioManoObra, usuarioLogueado, items);
    }

    public List<Map<String, Object>> listarOrdenes(String busqueda, int pagina, int porPagina) {
        List<Map<String, Object>> todas = mantenimientoDao.listarOrdenes(busqueda);
        int desde = (pagina - 1) * porPagina;
        int hasta = Math.min(desde + porPagina, todas.size());
        if (desde >= todas.size()) {
            return new ArrayList<>();
        }
        return todas.subList(desde, hasta);
    }

    public int contarOrdenes(String busqueda) {
        return mantenimientoDao.listarOrdenes(busqueda).size();
    }

    public Map<String, Object> obtenerResumen() {
        Map<String, Object> resumen = new HashMap<>();
        try {
            List<Map<String, Object>> todas = mantenimientoDao.listarOrdenes("");
            int total = todas.size();
            double monto = 0;
            for (Map<String, Object> o : todas) {
                monto += ((Number) o.getOrDefault("precioTotal", 0)).doubleValue();
            }
            resumen.put("totalOrdenes", total);
            resumen.put("montoTotal", monto);
            resumen.put("ticketPromedio", total > 0 ? monto / total : 0);
        } catch (Exception e) {
            resumen.put("totalOrdenes", 0);
            resumen.put("montoTotal", 0.0);
            resumen.put("ticketPromedio", 0.0);
        }
        return resumen;
    }

    public List<Map<String, Object>> listarTecnicos() {
        List<Map<String, Object>> lista = new ArrayList<>();
        try {
            ArrayList<Trabajador> trabajadores = trabajadorFachada.obtenerListaTrabajadores();
            for (Trabajador t : trabajadores) {
                if ("Activo".equalsIgnoreCase(t.getEstado()) && t.getCargo() != null) {
                    String cargo = t.getCargo().toLowerCase();
                    if (cargo.contains("mantenimiento") || cargo.contains("mecánico") || cargo.contains("mecanico")) {
                        Map<String, Object> fila = new HashMap<>();
                        fila.put("id_trabajador", t.getNumeroDocumento());
                        String completo = t.getNombre() + " " + t.getApellido_paterno()
                                + (t.getApellido_materno() != null ? " " + t.getApellido_materno() : "");
                        fila.put("nombre_completo", completo.trim());
                        fila.put("cargo", t.getCargo());
                        lista.add(fila);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error listarTecnicos: " + e.getMessage());
        }
        return lista;
    }

    public Map<String, Object> editarEstadoOrden(String usuarioNombre, int idOrdenServicio, String nuevoEstado) {
        return mantenimientoDao.editarEstadoOrden(usuarioNombre, idOrdenServicio, nuevoEstado);
    }

    public Map<String, Object> obtenerComprobanteMantenimiento(int idOrdenServicio) {
        return mantenimientoDao.obtenerComprobanteMantenimiento(idOrdenServicio);
    }

    public byte[] generarComprobanteMantenimientoPDF(Map<String, Object> comprobante) throws Exception {
        return exportador.generarComprobanteMantenimientoPDF(comprobante);
    }

    public byte[] generarPDFBytesMantenimiento(List<Map<String, Object>> ordenes) throws Exception {
        String[] headers = {"N° Orden", "Fecha", "Hora", "Cliente", "DNI", "Vehículo", "Servicios", "Técnicos", "Total", "Estado"};
        String[] keys = {"idOrdenServicio", "fecha", "hora", "cliente", "dniCliente", "descripcionVehiculo", "servicios", "tecnicos", "precioTotal", "estado"};
        float[] pesos = {1.5f, 2f, 1.5f, 3f, 2f, 3f, 3f, 3f, 2f, 2f};
        return exportador.generarPDF("Reporte de Órdenes de Mantenimiento", headers, keys, pesos, ordenes);
    }

    public byte[] generarExcelBytesMantenimiento(List<Map<String, Object>> ordenes) throws Exception {
        String[] headers = {"N° ORDEN", "FECHA", "HORA", "CLIENTE", "DNI", "VEHÍCULO", "SERVICIOS", "TÉCNICOS", "TOTAL", "ESTADO"};
        String[] keys = {"idOrdenServicio", "fecha", "hora", "cliente", "dniCliente", "descripcionVehiculo", "servicios", "tecnicos", "precioTotal", "estado"};
        return exportador.generarExcel("Reporte de Órdenes de Mantenimiento", headers, keys, ordenes);
    }
}
