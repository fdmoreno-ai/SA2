package com.proyecto.sa2.services;

import com.proyecto.sa2.models.Cuenta;
import com.proyecto.sa2.models.TipoCuenta;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CuentaService {

    private final List<Cuenta> cuentas = new ArrayList<>();

    // Listar todas las cuentas
    public List<Cuenta> listarCuentas() {
        return cuentas;
    }

    // Agregar una cuenta nueva
    public void agregarCuenta(String codigo, String nombre, TipoCuenta tipo) {
        Cuenta c = new Cuenta(codigo, nombre, tipo);
        cuentas.add(c);
    }

    // Eliminar una cuenta por código
    public void eliminarCuenta(String codigo) {
        cuentas.removeIf(c -> c.getCodigo().equals(codigo));
    }

    // Verificar si una cuenta existe por código
    public boolean existeCuenta(String codigo) {
        return cuentas.stream().anyMatch(c -> c.getCodigo().equals(codigo));
    }

    // Buscar una cuenta por código
    public Cuenta buscarCuenta(String codigo) {
        for (Cuenta c : cuentas) {
            if (c.getCodigo().equals(codigo)) {
                return c;
            }
        }
        return null; // si no encuentra la cuenta
    }

}
