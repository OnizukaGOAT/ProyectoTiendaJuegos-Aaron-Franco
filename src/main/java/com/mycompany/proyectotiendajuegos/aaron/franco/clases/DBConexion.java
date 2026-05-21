package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import org.apache.ibatis.jdbc.ScriptRunner;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author USUARIO
 */
public class DBConexion {

    private static final String CONFIG_FILE = "config.properties";
    private static final String SQL_FILE    = "gamepyramid.sql";
    private static final String NOMBRE_BD   = "gamepyramid";
    private static final String URL_SERVIDOR = "jdbc:mysql://localhost:3306/";

    private static String url      = URL_SERVIDOR + NOMBRE_BD
                                   + "?useSSL=false&serverTimezone=Europe/Madrid"
                                   + "&allowPublicKeyRetrieval=true&useUnicode=true"
                                   + "&characterEncoding=UTF-8";
    private static String usuario  = "root";
    private static String password = "root";

    private static DBConexion instancia;
    private Connection conexion;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver MySQL no encontrado.");
        }
        cargarConfiguracion();
        inicializarBaseDatos();
    }

    private DBConexion() {
        try {
            conexion = DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            throw new RuntimeException(
                "No se puede conectar a MySQL. Comprueba que el servidor está activo "
              + "y que la base de datos '" + NOMBRE_BD + "' existe.\n" + e.getMessage(), e);
        }
    }

    public static DBConexion getInstance() {
        if (instancia == null) instancia = new DBConexion();
        return instancia;
    }

    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(url, usuario, password);
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

    public static String getUrl()      { return url; }
    public static String getUsuario()  { return usuario; }
    public static String getPassword() { return password; }

    public static void setUrl(String u)      { url      = u; }
    public static void setUsuario(String u)  { usuario  = u; }
    public static void setPassword(String p) { password = p; }

    public static void cargarConfiguracion() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            setUrl(props.getProperty("db.url", url));
            setUsuario(props.getProperty("db.usuario", usuario));
            setPassword(props.getProperty("db.password", password));
            System.out.println("Configuración cargada desde " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("Aviso: No se pudo cargar " + CONFIG_FILE + ", usando valores por defecto.");
        }
    }

    public static void guardarConfiguracion() {
        Properties props = new Properties();
        props.setProperty("db.url",      getUrl());
        props.setProperty("db.usuario",  getUsuario());
        props.setProperty("db.password", getPassword());
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Configuracion DBConexion – GamePyramid");
        } catch (IOException e) {
            System.err.println("Error al guardar " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    private static void inicializarBaseDatos() {
        // 1. Crear la BD si no existe
        try (Connection conServidor = DriverManager.getConnection(URL_SERVIDOR, usuario, password);
             Statement st = conServidor.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + NOMBRE_BD
                           + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("Base de datos '" + NOMBRE_BD + "' lista.");
        } catch (SQLException e) {
            System.err.println("Error al crear la base de datos: " + e.getMessage());
            return;
        }

        // 2. Ejecutar el SQL inicial sólo si no hay tablas todavía
        try (Connection conBD = DriverManager.getConnection(url, usuario, password)) {
            DatabaseMetaData meta = conBD.getMetaData();
            try (ResultSet tablas = meta.getTables(NOMBRE_BD, null, "%", new String[]{"TABLE"})) {
                if (!tablas.next()) {
                    System.out.println("No se encontraron tablas. Importando " + SQL_FILE + "...");
                    ScriptRunner runner = new ScriptRunner(conBD);
                    runner.setLogWriter(null);
                    runner.setErrorLogWriter(null);
                    try (BufferedReader br = new BufferedReader(new FileReader(SQL_FILE))) {
                        runner.runScript(br);
                    }
                    System.out.println("Script SQL ejecutado correctamente.");
                } else {
                    System.out.println("Las tablas ya existen; no se reimporta el SQL.");
                }
            }
        } catch (IOException | SQLException e) {
            System.err.println("Error al importar el archivo SQL: " + e.getMessage());
        }
    }
}
