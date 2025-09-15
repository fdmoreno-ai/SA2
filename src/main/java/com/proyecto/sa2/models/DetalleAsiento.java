package com.proyecto.sa2.models;

import jakarta.persistence.*;

@Entity
public class DetalleAsiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String cuenta; // solo almacenamos el código de la cuenta

    private Double debe;
    private Double haber;

    @ManyToOne
    @JoinColumn(name = "asiento_id")
    private Asiento asiento;

    public DetalleAsiento() {}

    public DetalleAsiento(String cuenta, Double debe, Double haber, Asiento asiento) {
        this.cuenta = cuenta;
        this.debe = debe;
        this.haber = haber;
        this.asiento = asiento;
    }

    // Getters y setters
    public Integer getId() { return id; }

    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }

    public Double getDebe() { return debe; }
    public void setDebe(Double debe) { this.debe = debe; }

    public Double getHaber() { return haber; }
    public void setHaber(Double haber) { this.haber = haber; }

    public Asiento getAsiento() { return asiento; }
    public void setAsiento(Asiento asiento) { this.asiento = asiento; }

    // Getter extra para frontend
    public String getCuentaNombre() {
        return cuenta; // devuelve el código o nombre según lo uses en frontend
    }

    public String getCuentaCodigo() {
        return cuenta; // idem
    }
}
