package com.proyecto.sa2.models;

public class DetalleAsiento {
    private Cuenta cuenta;
    private double debe;
    private double haber;

    public DetalleAsiento(Cuenta cuenta, double debe, double haber) {
        this.cuenta = cuenta;
        this.debe = debe;
        this.haber = haber;
    }

    public Cuenta getCuenta() { return cuenta; }
    public double getDebe() { return debe; }
    public double getHaber() { return haber; }
}
