package com.proyecto.sa2.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    private String concepto;

    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<DetalleAsiento> detalles = new ArrayList<>();

    public Asiento() {
        this.fecha = LocalDateTime.now();
    }

    public Asiento(String concepto) {
        this.concepto = concepto;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }

    public LocalDateTime getFecha() { return fecha; }

    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getConcepto() { return concepto; }

    public void setConcepto(String concepto) { this.concepto = concepto; }

    public List<DetalleAsiento> getDetalles() { return detalles; }

    public void setDetalles(List<DetalleAsiento> detalles) { this.detalles = detalles; }

    public void agregarDetalle(DetalleAsiento detalle) {
        detalle.setAsiento(this);
        this.detalles.add(detalle);
    }
}
