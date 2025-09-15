package com.proyecto.sa2.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyecto.sa2.models.Asiento;
import com.proyecto.sa2.models.Cuenta;
import com.proyecto.sa2.services.AsientoService;
import com.proyecto.sa2.services.CuentaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/asientos")
public class AsientoController {

    private final AsientoService asientoService;
    private final CuentaService cuentaService;

    public AsientoController(AsientoService asientoService, CuentaService cuentaService) {
        this.asientoService = asientoService;
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public String mostrarAsientos(Model model) throws JsonProcessingException {
        List<Cuenta> cuentas = cuentaService.listarCuentas();
        model.addAttribute("cuentas", cuentas);

        // Convertimos las cuentas a JSON para el JS
        ObjectMapper mapper = new ObjectMapper();
        String cuentasJson = mapper.writeValueAsString(
                cuentas.stream()
                        .map(c -> Map.of(
                                "codigo", c.getCodigo(),
                                "nombre", c.getNombre()
                        ))
                        .collect(Collectors.toList())
        );
        model.addAttribute("cuentasJson", cuentasJson);

        return "asientos";
    }

    @GetMapping("/lista")
    @ResponseBody
    public List<Asiento> listarAsientos() {
        return asientoService.listarAsientos();
    }

    @PostMapping("/agregar")
    @ResponseBody
    public Map<String, Object> agregarAsiento(@RequestBody Map<String, Object> payload) {
        try {
            String concepto = (String) payload.get("concepto");
            List<Map<String, Object>> detallesRaw = (List<Map<String, Object>>) payload.get("detalles");
            asientoService.guardarAsiento(concepto, detallesRaw);
            return Map.of("exito", true, "mensaje", "Asiento agregado correctamente");
        } catch (Exception e) {
            return Map.of("exito", false, "mensaje", "Error al agregar el asiento: " + e.getMessage());
        }
    }
}
