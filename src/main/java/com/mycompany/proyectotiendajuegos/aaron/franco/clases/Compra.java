package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

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

    // ── Getters ───────────────────────────────────────────
    public int       getCodCompra() { return codCompra; }
    public LocalDate getFecha()     { return fecha; }
    public int       getCantidad()  { return cantidad; }
    public double    getCoste()     { return coste; }
    public Usuario   getUsuario()   { return usuario; }
    public Juego     getJuego()     { return juego; }

    // ── Setters ───────────────────────────────────────────
    public void setCodCompra(int codCompra)   { this.codCompra = codCompra; }
    public void setFecha(LocalDate fecha)     { this.fecha     = fecha; }
    public void setCantidad(int cantidad)     { this.cantidad  = cantidad; }
    public void setCoste(double coste)        { this.coste     = coste; }
    public void setUsuario(Usuario usuario)   { this.usuario   = usuario; }
    public void setJuego(Juego juego)         { this.juego     = juego; }

    public String getFechaFormateada() {
        return fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }
}
