package com.proyecto.sa2.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Asiento {
    private int id;
    private LocalDate fecha;
    private String concepto;
    private List<DetalleAsiento> detalles;

    public Asiento(int id, LocalDate fecha, String concepto) {
        this.id = id;
        this.fecha = fecha;
        this.concepto = concepto;
        this.detalles = new ArrayList<>();
    }


    public void agregarDetalle(DetalleAsiento detalle) {
        detalles.add(detalle);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id;
    }

    public LocalDate getFecha() { return fecha; }
    public String getConcepto() { return concepto; }
    public List<DetalleAsiento> getDetalles() { return detalles; }
}
