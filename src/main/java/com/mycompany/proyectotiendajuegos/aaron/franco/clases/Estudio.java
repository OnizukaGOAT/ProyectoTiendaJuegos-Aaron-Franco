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


    public void addDesarrollador(Desarrollador d) {
        if (!desarrolladores.contains(d)) getDesarrolladores().add(d);
    }

    public void removeDesarrollador(int id) {
        getDesarrolladores().removeIf(d -> d.getIdDesarrollador() == id);
    }

    public void addJuego(Juego j) {
        if (!juegos.contains(j)) getJuegos().add(j);
    }

    public void removeJuego(int id) {
        getJuegos().removeIf(j -> j.getIdJuego() == id);
    }

    @Override
    public String toString() { return getNombre(); }

    /**
     * @return the idEstudio
     */
    public int getIdEstudio() {
        return idEstudio;
    }

    /**
     * @param idEstudio the idEstudio to set
     */
    public void setIdEstudio(int idEstudio) {
        this.idEstudio = idEstudio;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the desarrolladores
     */
    public ArrayList<Desarrollador> getDesarrolladores() {
        return desarrolladores;
    }

    /**
     * @param desarrolladores the desarrolladores to set
     */
    public void setDesarrolladores(ArrayList<Desarrollador> desarrolladores) {
        this.desarrolladores = desarrolladores;
    }

    /**
     * @return the juegos
     */
    public ArrayList<Juego> getJuegos() {
        return juegos;
    }

    /**
     * @param juegos the juegos to set
     */
    public void setJuegos(ArrayList<Juego> juegos) {
        this.juegos = juegos;
    }
}
