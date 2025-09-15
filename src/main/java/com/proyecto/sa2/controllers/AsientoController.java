package com.proyecto.sa2.controllers;

import com.proyecto.sa2.models.Asiento;
import com.proyecto.sa2.models.DetalleAsiento;
import com.proyecto.sa2.services.AsientoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/asientos")
public class AsientoController {

    private final AsientoService asientoService;

    public AsientoController(AsientoService asientoService) {
        this.asientoService = asientoService;
    }

    @GetMapping("/lista")
    public List<Asiento> listarAsientos() {
        return asientoService.listarAsientos();
    }

    @PostMapping("/agregar")
    public Map<String, Object> agregarAsiento(@RequestBody Map<String, Object> payload) {
        try {
            String concepto = (String) payload.get("concepto");
            List<Map<String, Object>> detallesRaw = (List<Map<String, Object>>) payload.get("detalles");

            List<DetalleAsiento> detalles = detallesRaw.stream()
                    .map(d -> new DetalleAsiento(
                            (String) d.get("cuenta"),
                            Double.parseDouble(d.get("debe").toString()),
                            Double.parseDouble(d.get("haber").toString()),
                            null
                    )).collect(Collectors.toList());

            asientoService.guardarAsiento(concepto, detalles);
            return Map.of("exito", true, "mensaje", "Asiento agregado correctamente");
        } catch (Exception e) {
            return Map.of("exito", false, "mensaje", "Error al agregar el asiento: " + e.getMessage());
        }
    }
}
