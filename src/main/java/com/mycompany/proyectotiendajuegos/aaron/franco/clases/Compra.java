/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

/**
 *
 * @author USUARIO
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Compra {

    private int       codCompra;
    private LocalDate fecha;
    private int       cantidad;
    private double    coste;
    private Usuario   usuario;
    private Juego     juego;

    public Compra() {
        this.fecha = LocalDate.now();
    }

    public Compra(Usuario usuario, Juego juego, int cantidad) {
        this.usuario  = usuario;
        this.juego    = juego;
        this.cantidad = cantidad;
        this.coste    = juego.getPrecio() * cantidad;
        this.fecha    = LocalDate.now();
    }



    public String getFechaFormateada() {
        return getFecha() != null ? getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    /**
     * @return the codCompra
     */
    public int getCodCompra() {
        return codCompra;
    }

    /**
     * @param codCompra the codCompra to set
     */
    public void setCodCompra(int codCompra) {
        this.codCompra = codCompra;
    }

    /**
     * @return the fecha
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
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

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the juego
     */
    public Juego getJuego() {
        return juego;
    }

    /**
     * @param juego the juego to set
     */
    public void setJuego(Juego juego) {
        this.juego = juego;
    }
}
