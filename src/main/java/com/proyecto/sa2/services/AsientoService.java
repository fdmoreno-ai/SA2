package com.proyecto.sa2.services;

import com.proyecto.sa2.models.Asiento;
import com.proyecto.sa2.models.Cuenta;
import com.proyecto.sa2.models.DetalleAsiento;
import com.proyecto.sa2.repositories.AsientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AsientoService {

    private final AsientoRepository asientoRepository;
    private final CuentaService cuentaService;

    public AsientoService(AsientoRepository asientoRepository, CuentaService cuentaService) {
        this.asientoRepository = asientoRepository;
        this.cuentaService = cuentaService;
    }

    @Transactional
    public Asiento guardarAsiento(String concepto, List<Map<String,Object>> detallesRaw) {
        Asiento asiento = new Asiento(concepto);

        List<DetalleAsiento> detalles = detallesRaw.stream()
                .map(d -> {
                    String codigoCuenta = (String) d.get("cuentaCodigo"); // desde JS
                    Cuenta cuenta = cuentaService.buscarCuenta(codigoCuenta); // <-- cambio de nombre
                    if (cuenta == null) {
                        throw new RuntimeException("Cuenta no encontrada: " + codigoCuenta);
                    }
                    double debe = Double.parseDouble(d.get("debe").toString());
                    double haber = Double.parseDouble(d.get("haber").toString());
                    return new DetalleAsiento(cuenta, debe, haber, asiento);
                })
                .collect(Collectors.toList());

        asiento.setDetalles(detalles);
        return asientoRepository.save(asiento);
    }

    public List<Asiento> listarAsientos() {
        return asientoRepository.findAll();
    }
}
