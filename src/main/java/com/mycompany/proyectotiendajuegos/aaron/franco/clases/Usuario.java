package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.util.ArrayList;

public class Usuario extends Persona {

    private int               idUsuario;
    private String            correo;
    private String            contrasena;
    private double            saldo;
    private String            idioma;

    public Usuario() {
        super();
        this.idUsuario = 0;
    }

    public Usuario(String nombre, String apellidos, String correo,
                   String contrasena, double saldo, String idioma) {
        super(nombre, apellidos);
        this.correo      = correo;
        this.contrasena  = contrasena;
        this.saldo       = saldo;
        this.idioma      = idioma;
    }

    // ── Getters ────────────────────────────────────────────
    public int    getIdUsuario()  { return idUsuario; }
    public String getCorreo()     { return correo; }
    public String getContrasena() { return contrasena; }
    public double getSaldo()      { return saldo; }
    public String getIdioma()     { return idioma; }

    /**
     * Estos métodos delegan en GestorDatos para consultar la BD.
     * Se mantienen por compatibilidad con el código existente del controlador.
     */
    public ArrayList<Juego> getBiblioteca() {
        if (idUsuario == 0) return new ArrayList<>();
        return GestorDatos.getInstance().getBibliotecaUsuario(idUsuario);
    }

    public ArrayList<Compra> getHistorialCompras() {
        if (idUsuario == 0) return new ArrayList<>();
        return GestorDatos.getInstance().getComprasUsuario(idUsuario);
    }

    // ── Setters ────────────────────────────────────────────
    public void setIdUsuario(int idUsuario)       { this.idUsuario  = idUsuario; }
    public void setCorreo(String correo)          { this.correo     = correo; }
    public void setContrasena(String contrasena)  { this.contrasena = contrasena; }
    public void setSaldo(double saldo)            { this.saldo      = saldo; }
    public void setIdioma(String idioma)          { this.idioma     = idioma; }

    // ── Lógica ────────────────────────────────────────────
    public boolean verificarContrasena(String pass) {
        return contrasena != null && contrasena.equals(pass);
    }

    /** Comprueba en BD si el usuario posee el juego. */
    public boolean poseeJuego(Juego j) {
        if (idUsuario == 0) return false;
        return GestorDatos.getInstance().usuarioPoseeJuego(idUsuario, j.getIdJuego());
    }

    @Override
    public String toString() { return getNombreCompleto() + " <" + correo + ">"; }
}
