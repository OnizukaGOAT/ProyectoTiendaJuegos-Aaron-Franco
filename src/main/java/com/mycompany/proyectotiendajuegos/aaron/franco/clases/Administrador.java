/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

/**
 *
 * @author USUARIO
 */
public class Administrador extends Persona {

    private int    idAdmin; //Se utiliza unicamente por la base de datos, que necesitaba un ID.
    private String correo;
    private String contrasena;

    public Administrador() {
        super();
    }

    public Administrador(String nombre, String apellidos, String correo, String contrasena) {
        super(nombre, apellidos);
        this.correo     = correo;
        this.contrasena = contrasena;
    }


    public boolean verificarContrasena(String pass) {
        return getContrasena() != null && getContrasena().equals(pass);
    }

    /**
     * @return the idAdmin
     */
    public int getIdAdmin() {
        return idAdmin;
    }

    /**
     * @param idAdmin the idAdmin to set
     */
    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
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
}
