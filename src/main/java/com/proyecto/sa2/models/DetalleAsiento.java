package com.proyecto.sa2.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class DetalleAsiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double debe;

    private double haber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cuenta_id")
    private Cuenta cuenta;

    @ManyToOne
    @JoinColumn(name = "asiento_id")
    @JsonIgnore // evita ciclos JSON
    private Asiento asiento;

    public DetalleAsiento() {}

    public DetalleAsiento(Cuenta cuenta, double debe, double haber, Asiento asiento) {
        this.cuenta = cuenta;
        this.debe = debe;
        this.haber = haber;
        this.asiento = asiento;
    }

    // Getters y Setters
    public Long getId() { return id; }

    public double getDebe() { return debe; }

    public void setDebe(double debe) { this.debe = debe; }

    public double getHaber() { return haber; }

    public void setHaber(double haber) { this.haber = haber; }

    public Cuenta getCuenta() { return cuenta; }

    public void setCuenta(Cuenta cuenta) { this.cuenta = cuenta; }

    public Asiento getAsiento() { return asiento; }

    public void setAsiento(Asiento asiento) { this.asiento = asiento; }

    // Métodos auxiliares para JS
    public String getCuentaNombre() { return cuenta != null ? cuenta.getNombre() : ""; }

    public String getCuentaCodigo() { return cuenta != null ? cuenta.getCodigo() : ""; }
}
