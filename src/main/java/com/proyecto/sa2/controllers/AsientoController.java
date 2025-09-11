package com.proyecto.sa2.controllers;

import com.proyecto.sa2.models.Asiento;
import com.proyecto.sa2.models.Cuenta;
import com.proyecto.sa2.models.DetalleAsiento;
import com.proyecto.sa2.services.AsientoService;
import com.proyecto.sa2.services.CuentaService;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AsientoController {

    private final AsientoService asientoService;
    private final CuentaService cuentaService;

    public AsientoController(AsientoService asientoService, CuentaService cuentaService) {
        this.asientoService = asientoService;
        this.cuentaService = cuentaService;
    }

    // Página principal de Asientos
    @GetMapping("/asientos")
    public String mostrarAsientos(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("cuentas", cuentaService.listarCuentas());
        model.addAttribute("asientos", asientoService.listarAsientos());
        return "asientos";
    }

    // Agregar un nuevo asiento
    @PostMapping("/asientos/agregar")
    @ResponseBody
    public Map<String, Object> agregarAsiento(@RequestParam String concepto,
                                              @RequestParam List<String> cuentas,
                                              @RequestParam List<Double> debe,
                                              @RequestParam List<Double> haber) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Crear asiento nuevo
            Asiento asiento = new Asiento(0, LocalDate.now(), concepto);

            // Agregar detalles al asiento
            for (int i = 0; i < cuentas.size(); i++) {
                Cuenta c = cuentaService.buscarCuenta(cuentas.get(i));
                asiento.agregarDetalle(new DetalleAsiento(c, debe.get(i), haber.get(i)));
            }

            // Intentar agregar asiento al service
            Asiento agregado = asientoService.agregarAsiento(asiento); // ahora devuelve null si está desequilibrado

            if (agregado != null) {
                response.put("exito", true);
                response.put("mensaje", "Asiento agregado correctamente");
            } else {
                response.put("exito", false);
                response.put("mensaje", "Partida doble desequilibrada (Debe ≠ Haber)");
            }

        } catch (Exception e) {
            response.put("exito", false);
            response.put("mensaje", "Error al agregar el asiento");
        }

        return response;
    }

    // Endpoint para devolver todos los asientos en JSON
    @GetMapping("/asientos/lista")
    @ResponseBody
    public List<Asiento> obtenerAsientos() {
        return asientoService.listarAsientos();
    }

}
