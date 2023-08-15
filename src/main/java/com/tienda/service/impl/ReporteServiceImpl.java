package com.tienda.service.impl;

import com.tienda.service.ReporteService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    DataSource dataSource;

    @Override
    public ResponseEntity<Resource>
            generateReporte(
                    String reporte,
                    Map<String, Object> parametros,
                    String tipo) throws IOException {
        try {

            //se define el "tipo" de salida del reporte...
            String estilo;
            if (tipo.equals("vPdf")) {
                estilo = "inline; ";
            } else {
                estilo = "attachment; ";
            }

            //se establece la ruta donde estan los archivos de reportes .jasper
            String reportePath = "reportes";

            //se define el archivo donde se generara en memoria el reporte ya generado
            ByteArrayOutputStream salida = new ByteArrayOutputStream();

            //se define efeectivamente el objeto para leer la definicion del reporte del jasper
            ClassPathResource fuente = new ClassPathResource(
                    reportePath
                    + File.separator
                    + reporte
                    + ".jasper");

            //se define un objetp para poder leer el archivo
            InputStream elReporte = fuente.getInputStream();

            //se genera el reporte segun la definicion...  .jasper
            var reporteJasper
                    = JasperFillManager.fillReport(
                            elReporte,
                            parametros,
                            dataSource.getConnection());

            //se inicia el proceso para responderle al usuario
            //se define el tipo de salida de la respuesta
            MediaType mediaType=null;
            //se establece el string para hacer el archivo de salida
            String archivoSalida="";

            //se usa un arreglo de byte para extraer la info generada
            byte[] data;

            //se considera el tipo de salida seleccionada
            switch (tipo) {
                case "Pdf", "vPdf" -> {
                    JasperExportManager
                            .exportReportToPdfStream(
                                    reporteJasper,
                                    salida);
                    mediaType = MediaType.APPLICATION_PDF;
                    archivoSalida = reporte+".pdf";
                }
                case "Xls" -> {
                    JRXlsxExporter exportador = new JRXlsxExporter();
                    exportador.setExporterInput(
                            new SimpleExporterInput(
                                    reporteJasper));
                    exportador.setExporterOutput(
                            new SimpleOutputStreamExporterOutput(
                                    salida));
                    SimpleXlsxReportConfiguration configuracion
                            = new SimpleXlsxReportConfiguration();
                    configuracion.setDetectCellType(true);
                    configuracion.setCollapseRowSpan(true);
                    exportador.setConfiguration(configuracion);
                    exportador.exportReport();
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    archivoSalida = reporte + ".xlsx";
                }
                case "Csv" -> {
                    JRCsvExporter exportador = new JRCsvExporter();
                    exportador.setExporterInput(
                            new SimpleExporterInput(
                                    reporteJasper));
                    exportador.setExporterOutput(
                            new SimpleWriterExporterOutput(
                                    salida));
                    exportador.exportReport();
                    mediaType = MediaType.TEXT_PLAIN;
                    archivoSalida = reporte + ".csv";
                }
            }
            
            
            
            

           //se recuperan los bytes del reporte generado
           data=salida.toByteArray();
           
           //se definen los encabezados de la pagina a responder o descargar
           HttpHeaders headers = new HttpHeaders();
           headers.set("Content-Disposition",
                  estilo+"filename=\""+archivoSalida+"\"");
            
            //se retorna la respuesta al usuario
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(
                            new InputStreamResource(
                                new ByteArrayInputStream
                                        (data)));
                    
        } catch (SQLException | JRException e) {
            e.printStackTrace();
        }
        return null;
    }

}
