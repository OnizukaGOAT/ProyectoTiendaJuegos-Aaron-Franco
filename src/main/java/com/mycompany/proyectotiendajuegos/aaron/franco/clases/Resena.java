package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Resena {

    private int       idResena;
    private String    comentario;
    private int       puntuacion;   // 1-10
    private LocalDate fecha;
    private String    idioma;
    private Usuario   autor;
    private Juego     juego;

    public Resena() {
        this.fecha = LocalDate.now();
    }

    public Resena(Usuario autor, Juego juego, String comentario, int puntuacion, String idioma) {
        this.autor      = autor;
        this.juego      = juego;
        this.comentario = comentario;
        this.puntuacion = Math.max(1, Math.min(10, puntuacion));
        this.idioma     = idioma;
        this.fecha      = LocalDate.now();
    }

    // ── Getters ───────────────────────────────────────────
    public int       getIdResena()   { return idResena; }
    public String    getComentario() { return comentario; }
    public int       getPuntuacion() { return puntuacion; }
    public LocalDate getFecha()      { return fecha; }
    public String    getIdioma()     { return idioma; }
    public Usuario   getAutor()      { return autor; }
    public Juego     getJuego()      { return juego; }

    // ── Setters ───────────────────────────────────────────
    public void setIdResena(int idResena)        { this.idResena   = idResena; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public void setPuntuacion(int puntuacion)    { this.puntuacion = Math.max(1, Math.min(10, puntuacion)); }
    public void setFecha(LocalDate fecha)        { this.fecha      = fecha; }
    public void setIdioma(String idioma)         { this.idioma     = idioma; }
    public void setAutor(Usuario autor)          { this.autor      = autor; }
    public void setJuego(Juego juego)            { this.juego      = juego; }

    public String getFechaFormateada() {
        return fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    @Override
    public String toString() {
        return "[" + idResena + "] " + (juego != null ? juego.getTitulo() : "?")
                + " – " + puntuacion + "/10";
    }
}
