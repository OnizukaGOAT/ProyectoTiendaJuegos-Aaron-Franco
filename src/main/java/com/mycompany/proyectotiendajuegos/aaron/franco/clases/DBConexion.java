package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton que gestiona la conexión JDBC a MySQL.
 * Configuración por defecto: db=gamepyramid, user=root, pass=root, port=3306.
 */
public class DBConexion {

    private static final String URL      = "jdbc:mysql://localhost:3306/gamepyramid"
                                         + "?useSSL=false&serverTimezone=Europe/Madrid"
                                         + "&allowPublicKeyRetrieval=true&useUnicode=true"
                                         + "&characterEncoding=UTF-8";
    private static final String USER     = "root";
    private static final String PASSWORD = "CookieClicker2005";

    private static DBConexion instancia;
    private Connection conexion;

    private DBConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL no encontrado. Comprueba el pom.xml.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "No se puede conectar a MySQL. Comprueba que el servidor está activo "
              + "y que la base de datos 'gamepyramid' existe.\n" + e.getMessage(), e);
        }
    }

    public static DBConexion getInstance() {
        if (instancia == null) instancia = new DBConexion();
        return instancia;
    }

    
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al reconectar con MySQL: " + e.getMessage(), e);
        }
        return conexion;
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) conexion.close();
        } catch (SQLException ignored) {}
        instancia = null;
    }
}
