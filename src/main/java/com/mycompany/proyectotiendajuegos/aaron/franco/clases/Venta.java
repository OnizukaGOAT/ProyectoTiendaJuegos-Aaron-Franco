/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

/**
 *
 * @author USUARIO
 */
import java.util.Calendar;

public class Venta {

    private static int contadorId = 1;

    private int      codVenta;
    private int      cantidad;
    private Calendar fecha;
    private double   coste;

    public Venta() {
        this.codVenta = contadorId++;
        this.fecha    = Calendar.getInstance();
    }

    public Venta(int cantidad, double coste) {
        this.codVenta = contadorId++;
        this.cantidad = cantidad;
        this.coste    = coste;
        this.fecha    = Calendar.getInstance();
    }


    public static void resetContador(int valor) { setContadorId(valor); }

    /**
     * @return the contadorId
     */
    public static int getContadorId() {
        return contadorId;
    }

    /**
     * @param aContadorId the contadorId to set
     */
    public static void setContadorId(int aContadorId) {
        contadorId = aContadorId;
    }

    /**
     * @return the codVenta
     */
    public int getCodVenta() {
        return codVenta;
    }

    /**
     * @param codVenta the codVenta to set
     */
    public void setCodVenta(int codVenta) {
        this.codVenta = codVenta;
    }

    /**
     * @return the cantidad
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @return the fecha
     */
    public Calendar getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the coste
     */
    public double getCoste() {
        return coste;
    }

    /**
     * @param coste the coste to set
     */
    public void setCoste(double coste) {
        this.coste = coste;
    }
}
