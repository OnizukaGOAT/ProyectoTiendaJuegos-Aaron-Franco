package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

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

    // ── Getters ───────────────────────────────────────────
    public int              getIdDesarrollador()            { return idDesarrollador; }
    public int              getAnosExperiencia()            { return anosExperiencia; }
    public String           getPuestoActual()               { return puestoActual; }
    public ArrayList<Juego> getJuegosEnLosQueHaTrabajado()  { return juegosEnLosQueHaTrabajado; }

    // ── Setters ───────────────────────────────────────────
    public void setIdDesarrollador(int id)              { this.idDesarrollador = id; }
    public void setAnosExperiencia(int anosExperiencia) { this.anosExperiencia = anosExperiencia; }
    public void setPuestoActual(String puestoActual)    { this.puestoActual    = puestoActual; }

    public void addJuego(Juego j) {
        if (!juegosEnLosQueHaTrabajado.contains(j))
            juegosEnLosQueHaTrabajado.add(j);
    }

    public void removeJuego(Juego j) {
        juegosEnLosQueHaTrabajado.remove(j);
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " – " + puestoActual;
    }
}
