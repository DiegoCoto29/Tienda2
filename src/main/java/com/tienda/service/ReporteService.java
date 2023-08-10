package com.tienda.service;

import java.io.IOException;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface ReporteService {

    public ResponseEntity<Resource>
            generateReporte(
                    String reporte,  //el nombre del archivo llamado .jasper
                    Map<String, Object> parametros, //los parametros del reporte... si tiene parametros...
                    String tipo  //el tipo de reporte... PDF, Xls, Csv, vPdf...
            ) throws IOException;

}
