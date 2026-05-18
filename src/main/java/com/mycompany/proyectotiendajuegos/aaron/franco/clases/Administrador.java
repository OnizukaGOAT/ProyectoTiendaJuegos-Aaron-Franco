package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

public class Administrador extends Persona {

    private int    idAdmin;
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

    public int    getIdAdmin()    { return idAdmin; }
    public String getCorreo()     { return correo; }
    public String getContrasena() { return contrasena; }

    public void setIdAdmin(int idAdmin)           { this.idAdmin    = idAdmin; }
    public void setCorreo(String correo)          { this.correo     = correo; }
    public void setContrasena(String contrasena)  { this.contrasena = contrasena; }

    public boolean verificarContrasena(String pass) {
        return contrasena != null && contrasena.equals(pass);
    }
}
