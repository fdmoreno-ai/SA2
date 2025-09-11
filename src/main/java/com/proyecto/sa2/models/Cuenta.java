package com.proyecto.sa2.models;

public class Cuenta {

    private String codigo;
    private String nombre;
    private TipoCuenta tipo;
    private double saldo;

    public Cuenta(String codigo, String nombre, TipoCuenta tipo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.saldo = 0.0;
    }

    // Getters y setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoCuenta getTipo() { return tipo; }
    public void setTipo(TipoCuenta tipo) { this.tipo = tipo; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}