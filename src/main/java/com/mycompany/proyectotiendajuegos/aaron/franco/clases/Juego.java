package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.util.ArrayList;

public class Juego {

    private static int contadorId = 1;

    private int              idJuego;
    private String           titulo;
    private String           genero;
    private String           plataforma;
    private double           precio;
    private int              stock;
    private String           director;
    private ArrayList<Resena> resenas;

    public Juego() {
        this.idJuego = contadorId++;
        this.resenas = new ArrayList<>();
    }

    public Juego(String titulo, String genero, String plataforma,
                 double precio, int stock, String director) {
        this.idJuego    = contadorId++;
        this.titulo     = titulo;
        this.genero     = genero;
        this.plataforma = plataforma;
        this.precio     = precio;
        this.stock      = stock;
        this.director   = director;
        this.resenas    = new ArrayList<>();
    }

    // ── Getters ────────────────────────────────────────────
    public int               getIdJuego()    { return idJuego; }
    public String            getTitulo()     { return titulo; }
    public String            getGenero()     { return genero; }
    public String            getPlataforma() { return plataforma; }
    public double            getPrecio()     { return precio; }
    public int               getStock()      { return stock; }
    public String            getDirector()   { return director; }
    public ArrayList<Resena> getResenas()    { return resenas; }

    // ── Setters ────────────────────────────────────────────
    public void setTitulo(String titulo)         { this.titulo     = titulo; }
    public void setGenero(String genero)         { this.genero     = genero; }
    public void setPlataforma(String plataforma) { this.plataforma = plataforma; }
    public void setPrecio(double precio)         { this.precio     = precio; }
    public void setStock(int stock)              { this.stock      = stock; }
    public void setDirector(String director)     { this.director   = director; }

    // ── Lógica ────────────────────────────────────────────
    public void addResena(Resena r) { resenas.add(r); }

    public void removeResena(int idResena) {
        resenas.removeIf(r -> r.getIdResena() == idResena);
    }

    public double getPuntuacionMedia() {
        if (resenas.isEmpty()) return 0;
        return resenas.stream().mapToInt(Resena::getPuntuacion).average().orElse(0);
    }

    public static void resetContador(int valor) { contadorId = valor; }

    @Override
    public String toString() { return titulo; }
}
