package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

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

    public int      getCodVenta() { return codVenta; }
    public int      getCantidad() { return cantidad; }
    public Calendar getFecha()    { return fecha; }
    public double   getCoste()    { return coste; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setCoste(double coste)    { this.coste    = coste; }
    public void setFecha(Calendar fecha)  { this.fecha    = fecha; }

    public static void resetContador(int valor) { contadorId = valor; }
}
