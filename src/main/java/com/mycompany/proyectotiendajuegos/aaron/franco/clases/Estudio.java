package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.util.ArrayList;

public class Estudio {

    private static int contadorId = 1;

    private int                   idEstudio;
    private String                nombre;
    private ArrayList<Desarrollador> desarrolladores;
    private ArrayList<Juego>         juegos;

    public Estudio() {
        this.idEstudio      = contadorId++;
        this.desarrolladores = new ArrayList<>();
        this.juegos          = new ArrayList<>();
    }

    public Estudio(String nombre) {
        this.idEstudio      = contadorId++;
        this.nombre         = nombre;
        this.desarrolladores = new ArrayList<>();
        this.juegos          = new ArrayList<>();
    }

    // ── Getters ────────────────────────────────────────────
    public int                      getIdEstudio()       { return idEstudio; }
    public String                   getNombre()          { return nombre; }
    public ArrayList<Desarrollador> getDesarrolladores() { return desarrolladores; }
    public ArrayList<Juego>         getJuegos()          { return juegos; }

    // ── Setters ────────────────────────────────────────────
    public void setNombre(String nombre) { this.nombre = nombre; }

    // ── Lógica ────────────────────────────────────────────
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

    public static void resetContador(int valor) { contadorId = valor; }

    @Override
    public String toString() { return nombre; }
}
