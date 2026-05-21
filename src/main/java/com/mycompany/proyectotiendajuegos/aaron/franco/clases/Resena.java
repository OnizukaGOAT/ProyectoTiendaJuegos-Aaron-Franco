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



    public String getFechaFormateada() {
        return getFecha() != null ? getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    @Override
    public String toString() {
        return "[" + getIdResena() + "] " + (getJuego() != null ? getJuego().getTitulo() : "?")+ " – " + getPuntuacion() + "/10";
    }

    /**
     * @return the idResena
     */
    public int getIdResena() {
        return idResena;
    }

    /**
     * @param idResena the idResena to set
     */
    public void setIdResena(int idResena) {
        this.idResena = idResena;
    }

    /**
     * @return the comentario
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * @param comentario the comentario to set
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    /**
     * @return the puntuacion
     */
    public int getPuntuacion() {
        return puntuacion;
    }

    /**
     * @param puntuacion the puntuacion to set
     */
    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
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
     * @return the idioma
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * @param idioma the idioma to set
     */
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    /**
     * @return the autor
     */
    public Usuario getAutor() {
        return autor;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(Usuario autor) {
        this.autor = autor;
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
