package com.proyecto.sa2.repositories;

import com.proyecto.sa2.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, String> {
    Optional<Cuenta> findByCodigo(String codigo); // opcional, si querés usarlo
}
