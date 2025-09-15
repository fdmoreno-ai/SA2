package com.proyecto.sa2.repositories;

import com.proyecto.sa2.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, String> {}
