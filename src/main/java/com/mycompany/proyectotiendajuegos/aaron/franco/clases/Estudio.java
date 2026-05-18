package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.util.ArrayList;

public class Estudio {

    private int                      idEstudio;
    private String                   nombre;
    private ArrayList<Desarrollador> desarrolladores;
    private ArrayList<Juego>         juegos;

    public Estudio() {
        this.desarrolladores = new ArrayList<>();
        this.juegos          = new ArrayList<>();
    }

    public Estudio(String nombre) {
        this.nombre         = nombre;
        this.desarrolladores = new ArrayList<>();
        this.juegos          = new ArrayList<>();
    }

    // ── Getters ───────────────────────────────────────────
    public int                      getIdEstudio()       { return idEstudio; }
    public String                   getNombre()          { return nombre; }
    public ArrayList<Desarrollador> getDesarrolladores() { return desarrolladores; }
    public ArrayList<Juego>         getJuegos()          { return juegos; }

    // ── Setters ───────────────────────────────────────────
    public void setIdEstudio(int idEstudio) { this.idEstudio = idEstudio; }
    public void setNombre(String nombre)    { this.nombre    = nombre; }

    public void addDesarrollador(Desarrollador d) {
        if (!desarrolladores.contains(d)) desarrolladores.add(d);
    }

    public void removeDesarrollador(int id) {
        desarrolladores.removeIf(d -> d.getIdDesarrollador() == id);
    }

    public void addJuego(Juego j) {
        if (!juegos.contains(j)) juegos.add(j);
    }

    public void removeJuego(int id) {
        juegos.removeIf(j -> j.getIdJuego() == id);
    }

    @Override
    public String toString() { return nombre; }
}
