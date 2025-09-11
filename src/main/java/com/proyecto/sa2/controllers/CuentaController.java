package com.proyecto.sa2.controllers;

import com.proyecto.sa2.models.Cuenta;
import com.proyecto.sa2.models.TipoCuenta;
import com.proyecto.sa2.services.CuentaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    // Mostrar Plan de Cuentas
    @GetMapping("/plan-cuentas")
    public String mostrarPlanCuentas(HttpSession session, Model model) {
        Object usuario = session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("cuentas", cuentaService.listarCuentas());
        return "plan_cuentas";
    }

    // Endpoint para obtener lista de cuentas en JSON
    @GetMapping("/plan-cuentas/lista")
    @ResponseBody
    public List<Cuenta> obtenerCuentas() {
        return cuentaService.listarCuentas();
    }

    // Agregar cuenta con validación
    @PostMapping("/plan-cuentas/agregar")
    @ResponseBody
    public Map<String, Object> agregarCuenta(@RequestParam String codigo,
                                             @RequestParam String nombre,
                                             @RequestParam String tipo) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (cuentaService.existeCuenta(codigo)) {
                response.put("exito", false);
                response.put("mensaje", "Ya existe una cuenta con ese código");
            } else {
                cuentaService.agregarCuenta(codigo, nombre, TipoCuenta.valueOf(tipo));
                response.put("exito", true);
                response.put("mensaje", "Cuenta agregada correctamente");
            }
        } catch (Exception e) {
            response.put("exito", false);
            response.put("mensaje", "Error al agregar la cuenta");
        }

        return response;
    }

    // Eliminar cuenta
    @PostMapping("/plan-cuentas/eliminar")
    @ResponseBody
    public Map<String, Object> eliminarCuenta(@RequestParam String codigo) {
        Map<String, Object> response = new HashMap<>();

        if (cuentaService.existeCuenta(codigo)) {
            cuentaService.eliminarCuenta(codigo);
            response.put("exito", true);
            response.put("mensaje", "Cuenta eliminada correctamente");
        } else {
            response.put("exito", false);
            response.put("mensaje", "No se encontró la cuenta a eliminar");
        }

        return response;
    }
}
