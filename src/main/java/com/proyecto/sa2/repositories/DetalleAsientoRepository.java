package com.proyecto.sa2.repositories;

import com.proyecto.sa2.models.DetalleAsiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleAsientoRepository extends JpaRepository<DetalleAsiento, Integer> {}
