/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

/**
 *
 * @author USUARIO
 */
public class Proveedor {

    private int    codProveedor;
    private double descuento;

    public Proveedor() {}

    public Proveedor(int codProveedor, double descuento) {
        this.codProveedor = codProveedor;
        this.descuento    = descuento;
    }


    @Override
    public String toString() { return "Proveedor#" + getCodProveedor(); }

    /**
     * @return the codProveedor
     */
    public int getCodProveedor() {
        return codProveedor;
    }

    /**
     * @param codProveedor the codProveedor to set
     */
    public void setCodProveedor(int codProveedor) {
        this.codProveedor = codProveedor;
    }

    /**
     * @return the descuento
     */
    public double getDescuento() {
        return descuento;
    }

    /**
     * @param descuento the descuento to set
     */
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }
}
