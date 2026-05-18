package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

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

    // ── Getters ────────────────────────────────────────────
    public int    getIdJuego()    { return idJuego; }
    public String getTitulo()     { return titulo; }
    public String getGenero()     { return genero; }
    public String getPlataforma() { return plataforma; }
    public double getPrecio()     { return precio; }
    public int    getStock()      { return stock; }
    public String getDirector()   { return director; }

    /**
     * Devuelve la lista de reseñas consultando la BD.
     * Usada solo cuando se necesita iterar (p.ej. detalle de juego en admin).
     */
    public ArrayList<Resena> getResenas() {
        if (idJuego == 0) return new ArrayList<>();
        return new ArrayList<>(GestorDatos.getInstance().getResenasPorJuego(this));
    }

    /** Media de puntuación calculada en BD. */
    public double getPuntuacionMedia() {
        if (idJuego == 0) return 0;
        return GestorDatos.getInstance().getPuntuacionMediaJuego(idJuego);
    }

    // ── Setters ────────────────────────────────────────────
    public void setIdJuego(int idJuego)          { this.idJuego    = idJuego; }
    public void setTitulo(String titulo)         { this.titulo     = titulo; }
    public void setGenero(String genero)         { this.genero     = genero; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }
    public void setPrecio(double precio)         { this.precio     = precio; }
    public void setStock(int stock)              { this.stock      = stock; }
    public void setDirector(String director)     { this.director   = director; }

    @Override
    public String toString() { return titulo; }
}
