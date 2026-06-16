/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package multiservicioRafael.invenatario.CodigoFuente.ServicioExportacion;

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
}