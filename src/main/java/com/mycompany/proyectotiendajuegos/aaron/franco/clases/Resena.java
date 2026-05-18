package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Resena {

    private static int contadorId = 1;

    private int       idResena;
    private String    comentario;
    private int       puntuacion;   // 1-10
    private LocalDate fecha;
    private String    idioma;
    private Usuario   autor;
    private Juego     juego;

    public Resena() {
        this.idResena = contadorId++;
        this.fecha    = LocalDate.now();
    }

    public Resena(Usuario autor, Juego juego, String comentario, int puntuacion, String idioma) {
        this.idResena   = contadorId++;
        this.autor      = autor;
        this.juego      = juego;
        this.comentario = comentario;
        this.puntuacion = Math.max(1, Math.min(10, puntuacion));
        this.idioma     = idioma;
        this.fecha      = LocalDate.now();
    }

    // Getters
    public int       getIdResena()   { return idResena; }
    public String    getComentario() { return comentario; }
    public int       getPuntuacion() { return puntuacion; }
    public LocalDate getFecha()      { return fecha; }
    public String    getIdioma()     { return idioma; }
    public Usuario   getAutor()      { return autor; }
    public Juego     getJuego()      { return juego; }

    // Setters
    public void setComentario(String comentario) { this.comentario = comentario; }
    public void setPuntuacion(int puntuacion)    { this.puntuacion = Math.max(1, Math.min(10, puntuacion)); }
    public void setIdioma(String idioma)         { this.idioma     = idioma; }
    public void setAutor(Usuario autor)          { this.autor      = autor; }
    public void setJuego(Juego juego)            { this.juego      = juego; }

    public String getFechaFormateada() {
        return fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public static void resetContador(int valor) { contadorId = valor; }

    @Override
    public String toString() {
        return "[" + idResena + "] " + (juego != null ? juego.getTitulo() : "?")
                + " – " + puntuacion + "/10";
    }

    void setIdResena(int aInt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    void setFecha(LocalDate toLocalDate) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
