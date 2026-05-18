package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.util.ArrayList;

public class Empleado extends Persona {

    private String           posicion;
    private ArrayList<Venta> historialVentas;
    private Tienda           tienda;

    public Empleado() {
        super();
        historialVentas = new ArrayList<>();
    }

    public Empleado(String nombre, String apellidos, String posicion, Tienda tienda) {
        super(nombre, apellidos);
        this.posicion        = posicion;
        this.tienda          = tienda;
        this.historialVentas = new ArrayList<>();
    }

    public String           getPosicion()          { return posicion; }
    public ArrayList<Venta> getHistorialVentas()   { return historialVentas; }
    public Tienda           getTienda()            { return tienda; }

    public void setPosicion(String posicion)         { this.posicion = posicion; }
    public void setTienda(Tienda tienda)             { this.tienda   = tienda; }

    public void addVenta(Venta v) { historialVentas.add(v); }
}
