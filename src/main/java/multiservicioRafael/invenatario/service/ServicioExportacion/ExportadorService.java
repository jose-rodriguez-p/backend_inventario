/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.service.ServicioExportacion;

import java.util.List;
import java.util.Map;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;

public class ExportadorService {
    private static ExportadorService instancia;
    private ExportadorService() {}
    public static synchronized ExportadorService getInstancia() {
        if (instancia == null) {
            instancia = new ExportadorService();
        }
        return instancia;
    }
    public byte[] generarPDF(String tituloReporte, String[] headers, String[] keys, float[] pesos, List<Map<String, Object>> datos) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(com.lowagie.text.PageSize.A4.rotate(), 36, 36, 54, 36);
        PdfWriter.getInstance(doc, out);
        doc.open();
        java.awt.Color rojoGuinda = new java.awt.Color(166, 43, 50);
        java.awt.Color grisOscuro = new java.awt.Color(30, 34, 41);
        java.awt.Color grisFondoAlterno = new java.awt.Color(244, 245, 247);
        java.awt.Color grisBorde = new java.awt.Color(220, 225, 230);
        Font fuenteEmpresa = new Font(Font.HELVETICA, 24, Font.BOLD, rojoGuinda);
        Font fuenteSubtitulo = new Font(Font.HELVETICA, 12, Font.NORMAL, new java.awt.Color(110, 115, 125));
        Font fuenteHeaderTabla = new Font(Font.HELVETICA, 10, Font.BOLD, java.awt.Color.WHITE);
        Font fuenteCelda = new Font(Font.HELVETICA, 10, Font.NORMAL, new java.awt.Color(50, 50, 50));
        doc.add(new Paragraph("MULTISERVICIOS RAFAEL", fuenteEmpresa));
        doc.add(new Paragraph(tituloReporte + " - Gestión Administrativa", fuenteSubtitulo));

        Paragraph linea = new Paragraph("________________________________________________________________________________________________________\n\n");
        linea.getFont().setColor(grisBorde);
        doc.add(linea);
        PdfPTable tabla = new PdfPTable(headers.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(pesos);

        for (String header : headers) {
            PdfPCell cellHeader = new PdfPCell(new Paragraph(header.toUpperCase(), fuenteHeaderTabla));
            cellHeader.setBackgroundColor(grisOscuro);
            cellHeader.setPadding(12);
            cellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellHeader.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellHeader.setBorderColor(grisOscuro);
            tabla.addCell(cellHeader);
        }
        int contadorFilas = 0;
        for (Map<String, Object> registro : datos) {
            java.awt.Color colorFila = (contadorFilas % 2 == 0) ? java.awt.Color.WHITE : grisFondoAlterno;

            for (int i = 0; i < keys.length; i++) {
                String valorCelda = String.valueOf(registro.getOrDefault(keys[i], "")).trim();
                PdfPCell cell = new PdfPCell(new Paragraph(valorCelda, fuenteCelda));
                cell.setBackgroundColor(colorFila);
                cell.setPadding(10);
                cell.setBorderColor(grisBorde);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                String keyActual = keys[i].toLowerCase();
                if (keyActual.contains("documento") || keyActual.contains("estado") || keyActual.contains("fecha") || keyActual.contains("id")) {
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                } else {
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                }
                if (keyActual.contains("estado") && "Activo".equalsIgnoreCase(valorCelda)) {
                    Font fuenteEstado = new Font(Font.HELVETICA, 10, Font.BOLD, new java.awt.Color(21, 128, 61));
                    cell.setPhrase(new Paragraph(valorCelda, fuenteEstado));
                }

                tabla.addCell(cell);
            }
            contadorFilas++;
        }
        doc.add(tabla);
        doc.close();
        return out.toByteArray();
    }

    //Generador de excel
    public byte[] generarExcel(String tituloReporte, String[] headers, String[] keys, List<Map<String, Object>> datos) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Sheet hoja = wb.createSheet("Reporte");
        hoja.setDisplayGridlines(true);

        XSSFCellStyle estiloTitulo = (XSSFCellStyle) wb.createCellStyle();
        XSSFCellStyle estiloHeader = (XSSFCellStyle) wb.createCellStyle();
        XSSFCellStyle estiloPar = (XSSFCellStyle) wb.createCellStyle();
        XSSFCellStyle estiloImpar = (XSSFCellStyle) wb.createCellStyle();

        byte[] colorRojoHex = new byte[]{(byte) 166, (byte) 43, (byte) 50};
        byte[] colorGrisDarkHex = new byte[]{(byte) 30, (byte) 34, (byte) 41};
        byte[] colorGrisClaroHex = new byte[]{(byte) 244, (byte) 245, (byte) 247};
        byte[] colorBordeHex = new byte[]{(byte) 215, (byte) 220, (byte) 225};

        XSSFFont fontTitulo = (XSSFFont) wb.createFont();
        fontTitulo.setBold(true);
        fontTitulo.setFontHeightInPoints((short) 16);
        fontTitulo.setColor(new XSSFColor(colorRojoHex, null));
        estiloTitulo.setFont(fontTitulo);
        XSSFFont fontHeader = (XSSFFont) wb.createFont();
        fontHeader.setBold(true);
        fontHeader.setFontHeightInPoints((short) 11);
        fontHeader.setColor(IndexedColors.WHITE.getIndex());
        estiloHeader.setFillForegroundColor(new XSSFColor(colorGrisDarkHex, null));
        estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloHeader.setFont(fontHeader);
        estiloHeader.setAlignment(HorizontalAlignment.CENTER);
        estiloHeader.setVerticalAlignment(VerticalAlignment.CENTER);
        estiloHeader.setBorderBottom(BorderStyle.THIN);
        estiloHeader.setBorderTop(BorderStyle.THIN);
        estiloHeader.setBorderLeft(BorderStyle.THIN);
        estiloHeader.setBorderRight(BorderStyle.THIN);
        estiloHeader.setBottomBorderColor(new XSSFColor(colorBordeHex, null));
        estiloHeader.setTopBorderColor(new XSSFColor(colorBordeHex, null));
        estiloHeader.setLeftBorderColor(new XSSFColor(colorBordeHex, null));
        estiloHeader.setRightBorderColor(new XSSFColor(colorBordeHex, null));
        estiloImpar.setFillForegroundColor(new XSSFColor(colorGrisClaroHex, null));
        estiloImpar.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFCellStyle[] estilosDatos = {estiloPar, estiloImpar};
        for (XSSFCellStyle est : estilosDatos) {
            est.setBorderBottom(BorderStyle.THIN);
            est.setBorderTop(BorderStyle.THIN);
            est.setBorderLeft(BorderStyle.THIN);
            est.setBorderRight(BorderStyle.THIN);
            est.setBottomBorderColor(new XSSFColor(colorBordeHex, null));
            est.setTopBorderColor(new XSSFColor(colorBordeHex, null));
            est.setLeftBorderColor(new XSSFColor(colorBordeHex, null));
            est.setRightBorderColor(new XSSFColor(colorBordeHex, null));
            est.setVerticalAlignment(VerticalAlignment.CENTER);
            est.setAlignment(HorizontalAlignment.LEFT);
        }

        int margenIzquierdo = 3;
        Row filaTitulo = hoja.createRow(1);
        Cell celdaTitulo = filaTitulo.createCell(margenIzquierdo);
        celdaTitulo.setCellValue("MULTISERVICIOS RAFAEL - " + tituloReporte.toUpperCase());
        celdaTitulo.setCellStyle(estiloTitulo);
        int ultimaColumnaIndex = margenIzquierdo + headers.length - 1;
        hoja.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, margenIzquierdo, ultimaColumnaIndex));
        Row filaHeaders = hoja.createRow(3);
        filaHeaders.setHeightInPoints(28);

        for (int i = 0; i < headers.length; i++) {
            Cell celdaH = filaHeaders.createCell(margenIzquierdo + i);
            celdaH.setCellValue(headers[i].toUpperCase());
            celdaH.setCellStyle(estiloHeader);
        }
        int indexFila = 4;
        for (Map<String, Object> registro : datos) {
            Row r = hoja.createRow(indexFila);
            r.setHeightInPoints(22);
            XSSFCellStyle estiloFilaActual = (indexFila % 2 == 0) ? estiloPar : estiloImpar;

            for (int i = 0; i < keys.length; i++) {
                Cell cDatos = r.createCell(margenIzquierdo + i);
                String valor = String.valueOf(registro.getOrDefault(keys[i], "")).trim();
                cDatos.setCellValue(valor);
                cDatos.setCellStyle(estiloFilaActual);
            }
            indexFila++;
        }
        for (int i = 0; i < headers.length; i++) {
            int colIndex = margenIzquierdo + i;
            if (i == 0) {
                hoja.setColumnWidth(colIndex, 14 * 256);
            } else {
                hoja.autoSizeColumn(colIndex);
                int anchoActual = hoja.getColumnWidth(colIndex);
                hoja.setColumnWidth(colIndex, anchoActual + (3 * 256));
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        return out.toByteArray();
    }

    // ----------------------------------------------------------------------
    // Comprobante de pago individual (boleta/factura) de una venta
    // ----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public byte[] generarComprobanteVentaPDF(Map<String, Object> c) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(com.lowagie.text.PageSize.A5, 30, 30, 30, 30);
        PdfWriter.getInstance(doc, out);
        doc.open();

        java.awt.Color rojoGuinda = new java.awt.Color(166, 43, 50);
        java.awt.Color grisOscuro = new java.awt.Color(30, 34, 41);
        java.awt.Color grisTexto = new java.awt.Color(90, 95, 105);
        java.awt.Color grisBorde = new java.awt.Color(220, 225, 230);
        Font fEmpresa = new Font(Font.HELVETICA, 18, Font.BOLD, rojoGuinda);
        Font fEtiqueta = new Font(Font.HELVETICA, 9, Font.NORMAL, grisTexto);
        Font fValor = new Font(Font.HELVETICA, 10, Font.BOLD, grisOscuro);
        Font fComprobante = new Font(Font.HELVETICA, 12, Font.BOLD, java.awt.Color.WHITE);
        Font fHeaderTabla = new Font(Font.HELVETICA, 9, Font.BOLD, java.awt.Color.WHITE);
        Font fCelda = new Font(Font.HELVETICA, 9, Font.NORMAL, new java.awt.Color(50, 50, 50));
        Font fTotalLabel = new Font(Font.HELVETICA, 10, Font.NORMAL, grisTexto);
        Font fTotalValor = new Font(Font.HELVETICA, 14, Font.BOLD, rojoGuinda);

        doc.add(new Paragraph("MULTISERVICIOS RAFAEL", fEmpresa));
        doc.add(new Paragraph("Repuestos y servicios automotrices", fEtiqueta));
        doc.add(new Paragraph(" "));

        String tipo = String.valueOf(c.getOrDefault("tipo_comprobante", "Boleta"));
        String numero = c.getOrDefault("serie", "") + "-" + c.getOrDefault("correlativo", "");
        PdfPTable cabecera = new PdfPTable(1);
        cabecera.setWidthPercentage(100);
        PdfPCell cellCab = new PdfPCell(new Paragraph((tipo.equalsIgnoreCase("Factura") ? "FACTURA ELECTRÓNICA" : "BOLETA DE VENTA") + "\n" + numero, fComprobante));
        cellCab.setBackgroundColor(grisOscuro);
        cellCab.setPadding(10);
        cellCab.setHorizontalAlignment(Element.ALIGN_CENTER);
        cabecera.addCell(cellCab);
        doc.add(cabecera);
        doc.add(new Paragraph(" "));

        agregarLineaDato(doc, "Fecha de emisión", String.valueOf(c.getOrDefault("fecha_emision", "-")), fEtiqueta, fValor);
        agregarLineaDato(doc, "Cliente", String.valueOf(c.getOrDefault("cliente", "-")), fEtiqueta, fValor);
        agregarLineaDato(doc, "DNI / Documento", String.valueOf(c.getOrDefault("dni", "-")), fEtiqueta, fValor);
        agregarLineaDato(doc, "Vendedor", String.valueOf(c.getOrDefault("vendedor", "-")), fEtiqueta, fValor);
        agregarLineaDato(doc, "Método de pago", String.valueOf(c.getOrDefault("metodo_pago", "-")), fEtiqueta, fValor);
        agregarLineaDato(doc, "Estado", String.valueOf(c.getOrDefault("estado", "-")), fEtiqueta, fValor);
        doc.add(new Paragraph(" "));

        String[] headers = {"Descripción", "Cant.", "P. Unit.", "Total"};
        float[] pesos = {5f, 1.3f, 1.7f, 1.8f};
        PdfPTable tabla = new PdfPTable(headers.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(pesos);
        for (String h : headers) {
            PdfPCell ch = new PdfPCell(new Paragraph(h, fHeaderTabla));
            ch.setBackgroundColor(rojoGuinda);
            ch.setPadding(6);
            ch.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(ch);
        }
        Object itemsObj = c.get("items");
        if (itemsObj instanceof List) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) itemsObj;
            for (Map<String, Object> item : items) {
                tabla.addCell(celdaSimple(String.valueOf(item.getOrDefault("nombre_repuesto", "")), fCelda, Element.ALIGN_LEFT, grisBorde));
                tabla.addCell(celdaSimple(String.valueOf(item.getOrDefault("cantidad", "")), fCelda, Element.ALIGN_CENTER, grisBorde));
                tabla.addCell(celdaSimple(formatoMoneda(item.get("precio_unitario")), fCelda, Element.ALIGN_RIGHT, grisBorde));
                tabla.addCell(celdaSimple(formatoMoneda(item.get("precio_subtotal")), fCelda, Element.ALIGN_RIGHT, grisBorde));
            }
        }
        doc.add(tabla);
        doc.add(new Paragraph(" "));

        agregarLineaTotal(doc, "Subtotal (valor de venta)", formatoMoneda(c.get("subtotal")), fTotalLabel, fValor);
        agregarLineaTotal(doc, "Descuento", formatoMoneda(c.get("descuento_global")), fTotalLabel, fValor);
        agregarLineaTotal(doc, "IGV (18%)", formatoMoneda(c.get("igv")), fTotalLabel, fValor);
        Paragraph total = new Paragraph("TOTAL A PAGAR:  S/ " + formatoMoneda(c.get("precio_total")), fTotalValor);
        total.setAlignment(Element.ALIGN_RIGHT);
        doc.add(total);

        String nota = c.get("nota") != null ? String.valueOf(c.get("nota")) : "";
        if (!nota.isBlank()) {
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph("Nota: " + nota, fEtiqueta));
        }

        doc.add(new Paragraph(" "));
        Paragraph pie = new Paragraph("Gracias por su compra — Multiservicios Rafael", fEtiqueta);
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(pie);

        doc.close();
        return out.toByteArray();
    }

    // ----------------------------------------------------------------------
    // Comprobante / resumen de cierre de caja (cuadre de caja)
    // ----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public byte[] generarComprobanteCierrePDF(Map<String, Object> r) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(com.lowagie.text.PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(doc, out);
        doc.open();

        java.awt.Color rojoGuinda = new java.awt.Color(166, 43, 50);
        java.awt.Color grisOscuro = new java.awt.Color(30, 34, 41);
        java.awt.Color grisTexto = new java.awt.Color(90, 95, 105);
        java.awt.Color grisBorde = new java.awt.Color(220, 225, 230);
        java.awt.Color verde = new java.awt.Color(21, 128, 61);
        java.awt.Color rojo = new java.awt.Color(185, 28, 28);
        Font fEmpresa = new Font(Font.HELVETICA, 22, Font.BOLD, rojoGuinda);
        Font fSubtitulo = new Font(Font.HELVETICA, 12, Font.NORMAL, grisTexto);
        Font fEtiqueta = new Font(Font.HELVETICA, 10, Font.NORMAL, grisTexto);
        Font fValor = new Font(Font.HELVETICA, 11, Font.BOLD, grisOscuro);
        Font fHeaderTabla = new Font(Font.HELVETICA, 10, Font.BOLD, java.awt.Color.WHITE);
        Font fCelda = new Font(Font.HELVETICA, 9, Font.NORMAL, new java.awt.Color(50, 50, 50));
        Font fTotalLabel = new Font(Font.HELVETICA, 11, Font.NORMAL, grisTexto);
        Font fTotalValor = new Font(Font.HELVETICA, 13, Font.BOLD, grisOscuro);

        doc.add(new Paragraph("MULTISERVICIOS RAFAEL", fEmpresa));
        doc.add(new Paragraph("Comprobante de Cierre de Caja", fSubtitulo));
        doc.add(new Paragraph(" "));

        agregarLineaDato(doc, "N° de caja", "#" + String.valueOf(r.getOrDefault("id_cierre_caja", "-")), fEtiqueta, fValor);
        agregarLineaDato(doc, "Cajero", String.valueOf(r.getOrDefault("usuario", "-")), fEtiqueta, fValor);
        agregarLineaDato(doc, "Apertura", String.valueOf(r.getOrDefault("fec_apertura", "-")), fEtiqueta, fValor);
        agregarLineaDato(doc, "Cierre", String.valueOf(r.getOrDefault("fec_cierre", "-")), fEtiqueta, fValor);
        agregarLineaDato(doc, "Saldo inicial", "S/ " + formatoMoneda(r.get("saldo_inicial")), fEtiqueta, fValor);
        doc.add(new Paragraph(" "));

        String[] headers = {"N° Orden", "Fecha", "Comprobante", "Cliente", "Método pago", "Total"};
        float[] pesos = {1.3f, 2f, 2.2f, 3f, 2f, 1.8f};
        PdfPTable tabla = new PdfPTable(headers.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(pesos);
        for (String h : headers) {
            PdfPCell ch = new PdfPCell(new Paragraph(h.toUpperCase(), fHeaderTabla));
            ch.setBackgroundColor(grisOscuro);
            ch.setPadding(8);
            ch.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(ch);
        }
        Object ventasObj = r.get("ventas");
        if (ventasObj instanceof List) {
            List<Map<String, Object>> ventas = (List<Map<String, Object>>) ventasObj;
            for (Map<String, Object> v : ventas) {
                tabla.addCell(celdaSimple("#" + v.getOrDefault("n_orden", ""), fCelda, Element.ALIGN_CENTER, grisBorde));
                tabla.addCell(celdaSimple(String.valueOf(v.getOrDefault("fecha_emision", "")), fCelda, Element.ALIGN_CENTER, grisBorde));
                tabla.addCell(celdaSimple(String.valueOf(v.getOrDefault("comprobante", "")), fCelda, Element.ALIGN_LEFT, grisBorde));
                tabla.addCell(celdaSimple(String.valueOf(v.getOrDefault("cliente", "")), fCelda, Element.ALIGN_LEFT, grisBorde));
                tabla.addCell(celdaSimple(String.valueOf(v.getOrDefault("metodo_pago", "")), fCelda, Element.ALIGN_CENTER, grisBorde));
                tabla.addCell(celdaSimple(formatoMoneda(v.get("total")), fCelda, Element.ALIGN_RIGHT, grisBorde));
            }
        }
        doc.add(tabla);
        doc.add(new Paragraph(" "));

        double totSistema = numero(r.get("tot_ventas_sistema"));
        double totCajero = numero(r.get("tot_ventas_cajero"));
        double diferencia = totCajero - totSistema;

        agregarLineaTotal(doc, "N° de ventas", String.valueOf(r.getOrDefault("cantidad_ventas", 0)), fTotalLabel, fValor);
        agregarLineaTotal(doc, "N° de mantenimientos", String.valueOf(r.getOrDefault("cantidad_mantenimientos", 0)), fTotalLabel, fValor);
        agregarLineaTotal(doc, "Total según sistema", "S/ " + formatoMoneda(totSistema), fTotalLabel, fTotalValor);
        agregarLineaTotal(doc, "Total contado (declarado por cajero)", "S/ " + formatoMoneda(totCajero), fTotalLabel, fTotalValor);

        Font fDiferencia = new Font(Font.HELVETICA, 13, Font.BOLD, Math.abs(diferencia) < 0.01 ? verde : rojo);
        String etiquetaDif = Math.abs(diferencia) < 0.01 ? "CAJA CUADRADA"
                : (diferencia > 0 ? "SOBRANTE" : "FALTANTE");
        Paragraph dif = new Paragraph(etiquetaDif + ":  S/ " + formatoMoneda(Math.abs(diferencia)), fDiferencia);
        dif.setAlignment(Element.ALIGN_RIGHT);
        doc.add(dif);

        doc.add(new Paragraph(" "));
        Paragraph pie = new Paragraph("Documento generado automáticamente al cierre de caja.", fEtiqueta);
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(pie);

        doc.close();
        return out.toByteArray();
    }

    // ----------------------------------------------------------------------
    // Helpers de formato compartidos por los comprobantes
    // ----------------------------------------------------------------------
    private void agregarLineaDato(Document doc, String etiqueta, String valor, Font fEtiqueta, Font fValor) throws Exception {
        PdfPTable fila = new PdfPTable(2);
        fila.setWidthPercentage(100);
        fila.setWidths(new float[]{2.2f, 4f});
        PdfPCell c1 = new PdfPCell(new Paragraph(etiqueta, fEtiqueta));
        c1.setBorder(0);
        c1.setPaddingBottom(3);
        PdfPCell c2 = new PdfPCell(new Paragraph(valor, fValor));
        c2.setBorder(0);
        c2.setPaddingBottom(3);
        fila.addCell(c1);
        fila.addCell(c2);
        doc.add(fila);
    }

    private void agregarLineaTotal(Document doc, String etiqueta, String valor, Font fEtiqueta, Font fValor) throws Exception {
        PdfPTable fila = new PdfPTable(2);
        fila.setWidthPercentage(100);
        fila.setWidths(new float[]{3f, 2f});
        PdfPCell c1 = new PdfPCell(new Paragraph(etiqueta, fEtiqueta));
        c1.setBorder(0);
        c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell c2 = new PdfPCell(new Paragraph(valor, fValor));
        c2.setBorder(0);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        fila.addCell(c1);
        fila.addCell(c2);
        doc.add(fila);
    }

    private PdfPCell celdaSimple(String texto, Font fuente, int alineacion, java.awt.Color borde) {
        PdfPCell cell = new PdfPCell(new Paragraph(texto, fuente));
        cell.setPadding(6);
        cell.setBorderColor(borde);
        cell.setHorizontalAlignment(alineacion);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private double numero(Object valor) {
        return (valor instanceof Number) ? ((Number) valor).doubleValue() : 0.0;
    }

    private String formatoMoneda(Object valor) {
        return String.format(java.util.Locale.US, "%.2f", numero(valor));
    }
}