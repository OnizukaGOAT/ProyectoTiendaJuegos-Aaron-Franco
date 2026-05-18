package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

public class Tienda {

    private int codTienda;

    public Tienda() {}
    public Tienda(int codTienda) { this.codTienda = codTienda; }

    public int getCodTienda()              { return codTienda; }
    public void setCodTienda(int codTienda) { this.codTienda = codTienda; }

    @Override
    public String toString() { return "Tienda#" + codTienda; }
}
