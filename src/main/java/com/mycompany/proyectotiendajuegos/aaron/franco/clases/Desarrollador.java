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

public class Desarrollador extends Persona {

    private int              idDesarrollador;
    private int              anosExperiencia;
    private String           puestoActual;
    private ArrayList<Juego> juegosEnLosQueHaTrabajado;

    public Desarrollador() {
        super();
        this.juegosEnLosQueHaTrabajado = new ArrayList<>();
    }

    public Desarrollador(String nombre, String apellidos,
                         int anosExperiencia, String puestoActual) {
        super(nombre, apellidos);
        this.anosExperiencia           = anosExperiencia;
        this.puestoActual              = puestoActual;
        this.juegosEnLosQueHaTrabajado = new ArrayList<>();
    }


    public void addJuego(Juego j) {
        if (!juegosEnLosQueHaTrabajado.contains(j))
            getJuegosEnLosQueHaTrabajado().add(j);
    }

    public void removeJuego(Juego j) {
        getJuegosEnLosQueHaTrabajado().remove(j);
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " – " + getPuestoActual();
    }

    /**
     * @return the idDesarrollador
     */
    public int getIdDesarrollador() {
        return idDesarrollador;
    }

    /**
     * @param idDesarrollador the idDesarrollador to set
     */
    public void setIdDesarrollador(int idDesarrollador) {
        this.idDesarrollador = idDesarrollador;
    }

    /**
     * @return the anosExperiencia
     */
    public int getAnosExperiencia() {
        return anosExperiencia;
    }

    /**
     * @param anosExperiencia the anosExperiencia to set
     */
    public void setAnosExperiencia(int anosExperiencia) {
        this.anosExperiencia = anosExperiencia;
    }

    /**
     * @return the puestoActual
     */
    public String getPuestoActual() {
        return puestoActual;
    }

    /**
     * @param puestoActual the puestoActual to set
     */
    public void setPuestoActual(String puestoActual) {
        this.puestoActual = puestoActual;
    }

    /**
     * @return the juegosEnLosQueHaTrabajado
     */
    public ArrayList<Juego> getJuegosEnLosQueHaTrabajado() {
        return juegosEnLosQueHaTrabajado;
    }

    /**
     * @param juegosEnLosQueHaTrabajado the juegosEnLosQueHaTrabajado to set
     */
    public void setJuegosEnLosQueHaTrabajado(ArrayList<Juego> juegosEnLosQueHaTrabajado) {
        this.juegosEnLosQueHaTrabajado = juegosEnLosQueHaTrabajado;
    }
}
