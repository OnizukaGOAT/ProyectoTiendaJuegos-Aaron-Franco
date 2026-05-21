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

public class Juego {

    private int    idJuego;
    private String titulo;
    private String genero;
    private String plataforma;
    private double precio;
    private int    stock;
    private String director;

    public Juego() {}

    public Juego(String titulo, String genero, String plataforma,
                 double precio, int stock, String director) {
        this.titulo     = titulo;
        this.genero     = genero;
        this.plataforma = plataforma;
        this.precio     = precio;
        this.stock      = stock;
        this.director   = director;
    }



    @Override
    public String toString() { return getTitulo(); }

    /**
     * @return the idJuego
     */
    public int getIdJuego() {
        return idJuego;
    }

    /**
     * @param idJuego the idJuego to set
     */
    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the genero
     */
    public String getGenero() {
        return genero;
    }

    /**
     * @param genero the genero to set
     */
    public void setGenero(String genero) {
        this.genero = genero;
    }

    /**
     * @return the plataforma
     */
    public String getPlataforma() {
        return plataforma;
    }

    /**
     * @param plataforma the plataforma to set
     */
    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    /**
     * @return the precio
     */
    public double getPrecio() {
        return precio;
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /**
     * @return the stock
     */
    public int getStock() {
        return stock;
    }

    /**
     * @param stock the stock to set
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * @return the director
     */
    public String getDirector() {
        return director;
    }

    /**
     * @param director the director to set
     */
    public void setDirector(String director) {
        this.director = director;
    }
}
