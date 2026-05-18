package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

public class Proveedor {

    private int    codProveedor;
    private double descuento;

    public Proveedor() {}

    public Proveedor(int codProveedor, double descuento) {
        this.codProveedor = codProveedor;
        this.descuento    = descuento;
    }

    public int    getCodProveedor()                { return codProveedor; }
    public double getDescuento()                   { return descuento; }
    public void setCodProveedor(int codProveedor)   { this.codProveedor = codProveedor; }
    public void setDescuento(double descuento)      { this.descuento    = descuento; }

    @Override
    public String toString() { return "Proveedor#" + codProveedor; }
}
