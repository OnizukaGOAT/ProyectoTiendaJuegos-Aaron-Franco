package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

public abstract class Persona {

    private String nombre;
    private String apellidos;

    public Persona() {}

    public Persona(String nombre, String apellidos) {
        this.nombre    = nombre;
        this.apellidos = apellidos;
    }

    public String getNombre()    { return nombre; }
    public String getApellidos() { return apellidos; }
    public void setNombre(String nombre)       { this.nombre    = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombreCompleto() {
        return nombre + (apellidos != null && !apellidos.isEmpty() ? " " + apellidos : "");
    }

    @Override
    public String toString() { return getNombreCompleto(); }
}
