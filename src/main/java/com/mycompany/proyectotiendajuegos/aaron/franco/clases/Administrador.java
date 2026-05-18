package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

public class Administrador extends Persona {

    private static int contadorId = 1;

    private int    idAdmin;
    private String correo;
    private String contrasena;

    public Administrador() {
        super();
        this.idAdmin = contadorId++;
    }

    public Administrador(String nombre, String apellidos, String correo, String contrasena) {
        super(nombre, apellidos);
        this.idAdmin    = contadorId++;
        this.correo     = correo;
        this.contrasena = contrasena;
    }

    public int    getIdAdmin()    { return idAdmin; }
    public String getCorreo()     { return correo; }
    public String getContrasena() { return contrasena; }

    public void setCorreo(String correo)          { this.correo     = correo; }
    public void setContrasena(String contrasena)  { this.contrasena = contrasena; }

    public boolean verificarContrasena(String pass) {
        return contrasena != null && contrasena.equals(pass);
    }

    /** Reinicia el contador (útil para tests o carga de datos). */
    public static void resetContador(int valor) { contadorId = valor; }
}
