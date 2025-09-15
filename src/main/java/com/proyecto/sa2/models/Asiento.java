package com.proyecto.sa2.models;

import com.proyecto.sa2.models.DetalleAsiento;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate fecha;

    private String concepto;

    @OneToMany(mappedBy = "asiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleAsiento> detalles = new ArrayList<>();

    public Asiento() {
        this.fecha = LocalDate.now();
    }

    public Asiento(String concepto) {
        this.fecha = LocalDate.now();
        this.concepto = concepto;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public List<DetalleAsiento> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleAsiento> detalles) { this.detalles = detalles; }
}
