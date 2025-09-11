package com.proyecto.sa2.models;

public class Usuario {
    private String nombre;
    private String password; //
    private Rol rol;

    public Usuario(String nombre, String password, Rol rol) {
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
    }

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    @Override
    public String toString() {
        return "Usuario{" +
                "nombre='" + nombre + '\'' +
                ", rol=" + rol +
                '}';
    }
}
