package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.util.ArrayList;

public class Desarrollador extends Persona {

    private static int contadorId = 1;

    private int             idDesarrollador;
    private int             anosExperiencia;
    private int             nJuegos;
    private String          puestoActual;
    private ArrayList<Juego> juegosEnLosQueHaTrabajado;

    public Desarrollador() {
        super();
        this.idDesarrollador           = contadorId++;
        this.juegosEnLosQueHaTrabajado = new ArrayList<>();
    }

    public Desarrollador(String nombre, String apellidos,
                         int anosExperiencia, String puestoActual) {
        super(nombre, apellidos);
        this.idDesarrollador           = contadorId++;
        this.anosExperiencia           = anosExperiencia;
        this.puestoActual              = puestoActual;
        this.juegosEnLosQueHaTrabajado = new ArrayList<>();
        this.nJuegos                   = 0;
    }

    // ── Getters ────────────────────────────────────────────
    public int              getIdDesarrollador()           { return idDesarrollador; }
    public int              getAnosExperiencia()           { return anosExperiencia; }
    public int              getNJuegos()                   { return nJuegos; }
    public String           getPuestoActual()              { return puestoActual; }
    public ArrayList<Juego> getJuegosEnLosQueHaTrabajado() { return juegosEnLosQueHaTrabajado; }

    // ── Setters ────────────────────────────────────────────
    public void setAnosExperiencia(int anosExperiencia) { this.anosExperiencia = anosExperiencia; }
    public void setPuestoActual(String puestoActual)    { this.puestoActual    = puestoActual; }

    // ── Lógica ────────────────────────────────────────────
    public void addJuego(Juego j) {
        if (!juegosEnLosQueHaTrabajado.contains(j)) {
            juegosEnLosQueHaTrabajado.add(j);
            nJuegos = juegosEnLosQueHaTrabajado.size();
        }
    }

    public void removeJuego(Juego j) {
        juegosEnLosQueHaTrabajado.remove(j);
        nJuegos = juegosEnLosQueHaTrabajado.size();
    }

    public static void resetContador(int valor) { contadorId = valor; }

    @Override
    public String toString() {
        return getNombreCompleto() + " – " + puestoActual;
    }
}
