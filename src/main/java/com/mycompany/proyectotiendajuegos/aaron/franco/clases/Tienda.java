/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

/**
 *
 * @author USUARIO
 */
public class Tienda {

    private int codTienda;

    public Tienda() {}
    public Tienda(int codTienda) { this.codTienda = codTienda; }


    @Override
    public String toString() { return "Tienda#" + getCodTienda(); }

    /**
     * @return the codTienda
     */
    public int getCodTienda() {
        return codTienda;
    }

    /**
     * @param codTienda the codTienda to set
     */
    public void setCodTienda(int codTienda) {
        this.codTienda = codTienda;
    }
}
