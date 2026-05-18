package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.util.ArrayList;

public class Usuario extends Persona {

    private static int contadorId = 1;

    private int               idUsuario;
    private String            correo;
    private String            contrasena;
    private double            saldo;
    private String            idioma;
    private ArrayList<Compra> historialCompras;
    private ArrayList<Juego>  biblioteca;

    public Usuario() {
        super();
        this.idUsuario       = contadorId++;
        this.historialCompras = new ArrayList<>();
        this.biblioteca       = new ArrayList<>();
    }

    public Usuario(String nombre, String apellidos, String correo,
                   String contrasena, double saldo, String idioma) {
        super(nombre, apellidos);
        this.idUsuario        = contadorId++;
        this.correo           = correo;
        this.contrasena       = contrasena;
        this.saldo            = saldo;
        this.idioma           = idioma;
        this.historialCompras = new ArrayList<>();
        this.biblioteca       = new ArrayList<>();
    }

    // ── Getters ────────────────────────────────────────────
    public int               getIdUsuario()       { return idUsuario; }
    public String            getCorreo()          { return correo; }
    public String            getContrasena()      { return contrasena; }
    public double            getSaldo()           { return saldo; }
    public String            getIdioma()          { return idioma; }
    public ArrayList<Compra> getHistorialCompras() { return historialCompras; }
    public ArrayList<Juego>  getBiblioteca()       { return biblioteca; }

    // ── Setters ────────────────────────────────────────────
    public void setCorreo(String correo)          { this.correo     = correo; }
    public void setContrasena(String contrasena)  { this.contrasena = contrasena; }
    public void setSaldo(double saldo)            { this.saldo      = saldo; }
    public void setIdioma(String idioma)          { this.idioma     = idioma; }

    // ── Lógica ────────────────────────────────────────────
    public boolean verificarContrasena(String pass) {
        return contrasena != null && contrasena.equals(pass);
    }

    public boolean poseeJuego(Juego j) {
        return biblioteca.contains(j);
    }

    public void addCompra(Compra c) {
        historialCompras.add(c);
        if (!biblioteca.contains(c.getJuego())) {
            biblioteca.add(c.getJuego());
        }
    }

    public static void resetContador(int valor) { contadorId = valor; }

    @Override
    public String toString() { return getNombreCompleto() + " <" + correo + ">"; }

    void setIdUsuario(int aInt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
