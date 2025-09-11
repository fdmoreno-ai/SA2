package com.proyecto.sa2.services;

import com.proyecto.sa2.models.Asiento;
import org.springframework.stereotype.Service;  // << importante

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AsientoService {
    private final List<Asiento> asientos = new ArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public Asiento agregarAsiento(Asiento a) {
        double totalDebe = a.getDetalles().stream().mapToDouble(d -> d.getDebe()).sum();
        double totalHaber = a.getDetalles().stream().mapToDouble(d -> d.getHaber()).sum();

        if (totalDebe != totalHaber) {
            return null;
        }

        a.setId(idGenerator.getAndIncrement());
        asientos.add(a);
        return a;
    }

    public List<Asiento> listarAsientos() {
        return new ArrayList<>(asientos);
    }
}
