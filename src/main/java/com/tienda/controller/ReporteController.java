package com.tienda.controller;

import com.tienda.service.ReporteService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    ReporteService reporteService;

    @GetMapping("/principal")
    public String principal(Model model) {
        return "/reportes/principal";
    }

    @GetMapping("/usuarios")
    public ResponseEntity<Resource> usuarios(@RequestParam String tipo) throws IOException {
        return reporteService.generateReporte(
                "usuarios",
                null,
                tipo);
    }

    @GetMapping("/ventas")
    public ResponseEntity<Resource> ventas(@RequestParam String tipo) throws IOException {
        return reporteService.generateReporte(
                "ventas",
                null,
                tipo);
    }

    @GetMapping("/categorias")
    public ResponseEntity<Resource> categorias(@RequestParam String tipo) throws IOException {
        return reporteService.generateReporte(
                "categorias",
                null,
                tipo);
    }

    @GetMapping("/productos")
    public ResponseEntity<Resource> productos(@RequestParam String tipo) throws IOException {
        return reporteService.generateReporte(
                "productos",
                null,
                tipo);
    }
}
