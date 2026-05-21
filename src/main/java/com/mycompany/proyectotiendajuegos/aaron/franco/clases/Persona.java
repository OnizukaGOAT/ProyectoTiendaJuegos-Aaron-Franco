/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

/**
 *
 * @author USUARIO
 */
public abstract class Persona {

    private String nombre;
    private String apellidos;

    public Persona() {}

    public Persona(String nombre, String apellidos) {
        this.nombre    = nombre;
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return getNombre() + (getApellidos() != null && !apellidos.isEmpty() ? " " + getApellidos() : "");
    }

    @Override
    public String toString() { return getNombreCompleto(); }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the apellidos
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * @param apellidos the apellidos to set
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}
