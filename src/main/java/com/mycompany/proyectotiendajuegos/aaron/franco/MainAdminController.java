package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.GestorDatos;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author USUARIO
 */
public class MainAdminController implements Initializable {

    @FXML private Label lblAdminNombre;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblAdminNombre.setText("Admin: " + gd.getAdminActual().getNombreCompleto());
    }

    // ── MANTENIMIENTO ──────────────────────────────────────
    @FXML public void abrirGestionUsuarios() {
        VentanaUtil.abrirVentana("gestion_usuarios", "Gestión de Usuarios", 950, 650);
    }

    @FXML public void abrirGestionAdmins() {
        VentanaUtil.abrirVentana("gestion_admins", "Gestión de Administradores", 850, 580);
    }

    @FXML public void abrirGestionJuegos() {
        VentanaUtil.abrirVentana("gestion_juegos", "Gestión de Juegos", 1050, 680);
    }

    @FXML public void abrirGestionEstudios() {
        VentanaUtil.abrirVentana("gestion_estudios", "Gestión de Estudios", 900, 650);
    }

    // ── GESTIÓN ────────────────────────────────────────────
    @FXML public void abrirGestionResenas() {
        VentanaUtil.abrirVentana("gestion_resenas", "Gestión de Reseñas", 900, 650);
    }

    @FXML public void abrirVerCompras() {
        VentanaUtil.abrirVentana("historial_compras_admin", "Historial de Compras Global", 950, 650);
    }

    // ── ESTADÍSTICAS ───────────────────────────────────────
    @FXML public void abrirEstadisticas() {
        VentanaUtil.abrirVentana("estadisticas_admin", "Estadísticas y Consultas", 950, 700);
    }

    // ── SESIÓN ─────────────────────────────────────────────
    @FXML public void cerrarSesion() {
        if (DialogUtil.confirmar("¿Seguro que quieres cerrar sesión?")) {
            gd.cerrarSesion();
            try { App.setRoot("login"); }
            catch (IOException e) { e.printStackTrace(); }
        }
    }
}
