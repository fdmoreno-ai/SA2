package com.proyecto.sa2.services;

import com.proyecto.sa2.models.Asiento;
import com.proyecto.sa2.models.DetalleAsiento;
import com.proyecto.sa2.repositories.AsientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsientoService {

    private final AsientoRepository asientoRepository;

    public AsientoService(AsientoRepository asientoRepository) {
        this.asientoRepository = asientoRepository;
    }

    @Transactional
    public Asiento guardarAsiento(String concepto, List<DetalleAsiento> detalles) {
        Asiento asiento = new Asiento(concepto);
        detalles.forEach(d -> d.setAsiento(asiento));
        asiento.setDetalles(detalles);
        return asientoRepository.save(asiento);
    }

    public List<Asiento> listarAsientos() {
        return asientoRepository.findAll();
    }
}
