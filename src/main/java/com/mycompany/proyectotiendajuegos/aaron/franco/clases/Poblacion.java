package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

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

    public int    getCodPostal() { return codPostal; }
    public String getNombre()    { return nombre; }
    public int    getNumHabit()  { return numHabit; }
    public int    getCiudad()    { return ciudad; }

    public void setCodPostal(int codPostal) { this.codPostal = codPostal; }
    public void setNombre(String nombre)    { this.nombre    = nombre; }
    public void setNumHabit(int numHabit)   { this.numHabit  = numHabit; }
    public void setCiudad(int ciudad)       { this.ciudad    = ciudad; }

    @Override
    public String toString() { return nombre + " (" + codPostal + ")"; }
}
