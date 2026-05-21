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


    public boolean verificarContrasena(String pass) {
        return getContrasena() != null && getContrasena().equals(pass);
    }

    public boolean poseeJuego(Juego j) {
        if (getIdUsuario() == 0) return false;
        return GestorDatos.getInstance().usuarioPoseeJuego(getIdUsuario(), j.getIdJuego());
    }

    @Override
    public String toString() { return getNombreCompleto() + " <" + getCorreo() + ">"; }

    /**
     * @return the idUsuario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * @return the correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param correo the correo to set
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * @return the contrasena
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * @param contrasena the contrasena to set
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * @return the saldo
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * @param saldo the saldo to set
     */
    public void setSaldo(double saldo) {
        this.saldo = saldo;
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
}
