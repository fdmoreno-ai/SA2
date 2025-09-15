package com.proyecto.sa2.services;

import com.proyecto.sa2.models.Asiento;
import com.proyecto.sa2.models.Cuenta;
import com.proyecto.sa2.models.DetalleAsiento;
import com.proyecto.sa2.repositories.AsientoRepository;
import com.proyecto.sa2.repositories.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AsientoService {

    private final AsientoRepository asientoRepository;
    private final CuentaRepository cuentaRepository;

    public AsientoService(CuentaRepository cuentaRepository, AsientoRepository asientoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.asientoRepository = asientoRepository;
    }

    @Transactional
    public Asiento guardarAsiento(String concepto, List<Map<String, Object>> detallesRaw) {
        Asiento asiento = new Asiento(concepto);

        List<DetalleAsiento> detalles = detallesRaw.stream()
                .map(d -> new DetalleAsiento(
                        (String) d.get("cuenta"),
                        Double.parseDouble(d.get("debe").toString()),
                        Double.parseDouble(d.get("haber").toString()),
                        asiento
                ))
                .collect(Collectors.toList());

        asiento.setDetalles(detalles);
        return asientoRepository.save(asiento);
    }

    public List<Asiento> listarAsientos() {
        return asientoRepository.findAll();
    }

    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }
}
