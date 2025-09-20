package com.example.SA2Gemini.controller;

import com.example.SA2Gemini.dto.LibroMayorReportData;
import com.example.SA2Gemini.entity.Cuenta;
import com.example.SA2Gemini.service.AsientoService;
import com.example.SA2Gemini.service.CuentaService;
import com.example.SA2Gemini.service.PdfGeneratorService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.thymeleaf.context.Context;

@Controller
public class ReporteController {

    @Autowired
    private AsientoService asientoService;

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @GetMapping("/reportes/libro-diario")
    public String showLibroDiarioForm() {
        return "libro-diario-form";
    }

    @PostMapping("/reportes/libro-diario")
    public String generateLibroDiarioReport(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                            Model model) {
        model.addAttribute("asientos", asientoService.getAsientosBetweenDates(startDate, endDate));
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "libro-diario-report";
    }

    

    @GetMapping("/reportes/libro-diario/pdf")
    public ResponseEntity<byte[]> generateLibroDiarioPdf(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                         @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                         HttpServletRequest request, HttpServletResponse response) throws DocumentException {
        // Reuse logic to get data
        Map<String, Object> data = new HashMap<>();
        data.put("asientos", asientoService.getAsientosBetweenDates(startDate, endDate));
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        data.put("isPdf", true); // Indicate that this is for PDF generation

        String htmlContent = pdfGeneratorService.generateHtml("libro-diario-report", data, request, response);
        byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml(htmlContent);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"libro_diario_" + startDate + "_to_" + endDate + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/reportes/libro-mayor")
    public String libroMayor(@RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                             @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                             @RequestParam(value = "cuentaId", required = false) Long cuentaId,
                             Model model) {
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusDays(1); // Default start date to yesterday
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now(); // Default end date
        }

        Map<String, List<LibroMayorReportData>> reportePorCuenta = asientoService.generarLibroMayorReporte(fechaInicio, fechaFin, cuentaId);
        model.addAttribute("reportePorCuenta", reportePorCuenta);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("cuentas", cuentaService.getAllCuentas()); // Pass all accounts for the dropdown
        model.addAttribute("selectedCuentaId", cuentaId); // To pre-select in the dropdown
        return "libro-mayor-report";
    }

    @PostMapping("/reportes/libro-mayor")
    public String generateLibroMayorReport(@RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                           @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                           @RequestParam(value = "cuentaId", required = false) Long cuentaId,
                                           Model model) {
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusDays(1); // Default start date to yesterday
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now(); // Default end date
        }

        Map<String, List<LibroMayorReportData>> reportePorCuenta = asientoService.generarLibroMayorReporte(fechaInicio, fechaFin, cuentaId);
        model.addAttribute("reportePorCuenta", reportePorCuenta);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("cuentas", cuentaService.getAllCuentas()); // Pass all accounts for the dropdown
        model.addAttribute("selectedCuentaId", cuentaId); // To pre-select in the dropdown
        return "libro-mayor-report";
    }

    @GetMapping("/reportes/libro-mayor/pdf")
    public ResponseEntity<byte[]> generateLibroMayorPdf(@RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                                                        @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                                                        @RequestParam(value = "cuentaId", required = false) Long cuentaId,
                                                        HttpServletRequest request, HttpServletResponse response) throws DocumentException {
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusDays(1); // Default start date to yesterday
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now(); // Default end date
        }

        // Reuse logic to get data
        Map<String, List<LibroMayorReportData>> reportePorCuenta = asientoService.generarLibroMayorReporte(fechaInicio, fechaFin, cuentaId);
        Map<String, Object> data = new HashMap<>();
        data.put("reportePorCuenta", reportePorCuenta);
        data.put("fechaInicio", fechaInicio);
        data.put("fechaFin", fechaFin);
        data.put("isPdf", true); // Indicate that this is for PDF generation
        // No need to pass 'cuentas' or 'selectedCuentaId' to PDF template as it's for display only

        String htmlContent = pdfGeneratorService.generateHtml("libro-mayor-report", data, request, response);
        byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml(htmlContent);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"libro_mayor_" + fechaInicio + "_to_" + fechaFin + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
