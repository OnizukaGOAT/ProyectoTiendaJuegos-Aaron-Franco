/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

/**
 *
 * @author USUARIO
 */
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


    public void addVenta(Venta v) { getHistorialVentas().add(v); }

    /**
     * @return the posicion
     */
    public String getPosicion() {
        return posicion;
    }

    /**
     * @param posicion the posicion to set
     */
    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }

    /**
     * @return the historialVentas
     */
    public ArrayList<Venta> getHistorialVentas() {
        return historialVentas;
    }

    /**
     * @param historialVentas the historialVentas to set
     */
    public void setHistorialVentas(ArrayList<Venta> historialVentas) {
        this.historialVentas = historialVentas;
    }

    /**
     * @return the tienda
     */
    public Tienda getTienda() {
        return tienda;
    }

    /**
     * @param tienda the tienda to set
     */
    public void setTienda(Tienda tienda) {
        this.tienda = tienda;
    }
}
