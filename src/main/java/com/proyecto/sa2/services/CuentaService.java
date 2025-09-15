package com.proyecto.sa2.services;

import com.proyecto.sa2.models.Cuenta;
import com.proyecto.sa2.models.TipoCuenta;
import com.proyecto.sa2.repositories.CuentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public CuentaService(CuentaRepository repo) { this.cuentaRepository = repo; }

    public List<Cuenta> listarCuentas() { return cuentaRepository.findAll(); }

    public void agregarCuenta(String codigo, String nombre, TipoCuenta tipo) {
        Cuenta c = new Cuenta(codigo, nombre, tipo);
        cuentaRepository.save(c);
    }

    public void eliminarCuenta(String codigo) { cuentaRepository.deleteById(codigo); }

    public boolean existeCuenta(String codigo) { return cuentaRepository.existsById(codigo); }

    public Cuenta buscarCuenta(String codigo) { return cuentaRepository.findById(codigo).orElse(null); }
}
