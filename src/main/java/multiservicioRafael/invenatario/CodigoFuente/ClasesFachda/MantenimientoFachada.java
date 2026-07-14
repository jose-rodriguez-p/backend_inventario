package multiservicioRafael.invenatario.CodigoFuente.ClasesFachda;

import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.ModuloConexion.MantenimientoDao;
import multiservicioRafael.invenatario.CodigoFuente.ClasesHijas.Trabajador;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MantenimientoFachada {

    private final MantenimientoDao mantenimientoDao;
    private final TrabajadorFachada trabajadorFachada;

    public MantenimientoFachada() {
        this.mantenimientoDao = new MantenimientoDao();
        this.trabajadorFachada = new TrabajadorFachada();
    }

    public Map<String, Object> registrarOrden(
            String dniCliente, String nombreCliente, String descripcionVehiculo,
            double precioManoObra, double precioTotal, int idEstado, String nota,
            String usuarioLogueado, List<Map<String, Object>> items) {
        return mantenimientoDao.registrarOrden(dniCliente, nombreCliente, descripcionVehiculo,
            precioManoObra, precioTotal, idEstado, nota, usuarioLogueado, items);
    }

    public List<Map<String, Object>> listarOrdenes(String busqueda, int pagina, int porPagina) {
        List<Map<String, Object>> todas = mantenimientoDao.listarOrdenes(busqueda);
        int desde = (pagina - 1) * porPagina;
        int hasta = Math.min(desde + porPagina, todas.size());
        if (desde >= todas.size()) return new ArrayList<>();
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
}
