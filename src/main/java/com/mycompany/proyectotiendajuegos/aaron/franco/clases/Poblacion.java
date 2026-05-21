/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

/**
 *
 * @author USUARIO
 */
public class Poblacion {

    private int    codPostal;
    private String nombre;
    private int    numHabit;
    private int    ciudad;

    public Poblacion() {}

    public Poblacion(int codPostal, String nombre, int numHabit, int ciudad) {
        this.codPostal = codPostal;
        this.nombre    = nombre;
        this.numHabit  = numHabit;
        this.ciudad    = ciudad;
    }

    @Override
    public String toString() { return getNombre() + " (" + getCodPostal() + ")"; }

    /**
     * @return the codPostal
     */
    public int getCodPostal() {
        return codPostal;
    }

    /**
     * @param codPostal the codPostal to set
     */
    public void setCodPostal(int codPostal) {
        this.codPostal = codPostal;
    }

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
     * @return the numHabit
     */
    public int getNumHabit() {
        return numHabit;
    }

    /**
     * @param numHabit the numHabit to set
     */
    public void setNumHabit(int numHabit) {
        this.numHabit = numHabit;
    }

    /**
     * @return the ciudad
     */
    public int getCiudad() {
        return ciudad;
    }

    /**
     * @param ciudad the ciudad to set
     */
    public void setCiudad(int ciudad) {
        this.ciudad = ciudad;
    }
}
