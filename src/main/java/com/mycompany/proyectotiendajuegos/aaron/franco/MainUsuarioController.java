package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
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
public class MainUsuarioController implements Initializable {

    @FXML private Label lblBienvenida;
    @FXML private Label lblSaldo;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario usuario = gd.getUsuarioActual();
        lblBienvenida.setText("Hola, " + usuario.getNombre() + "!");
        actualizarSaldo();
    }

    public void actualizarSaldo() {
        lblSaldo.setText(String.format("💰 %.2f€", gd.getUsuarioActual().getSaldo()));
    }

    // ── CATÁLOGO / BÚSQUEDA ───────────────────────────────────────────────
    @FXML
    public void mostrarCatalogo() {
        VentanaUtil.abrirVentana("catalogo_usuario", "Catálogo de Juegos", 1000, 680);
        actualizarSaldo();
    }

    @FXML
    public void mostrarBuscarJuegos() {
        VentanaUtil.abrirVentana("buscar_juegos", "Buscar Juegos", 1000, 680);
        actualizarSaldo();
    }

    // ── MI CUENTA ────────────────────────────────────────────────────────
    @FXML
    public void mostrarHistorialCompras() {
        VentanaUtil.abrirVentana("historial_compras_usuario", "Historial de Compras", 750, 560);
    }

    @FXML
    public void mostrarBiblioteca() {
        VentanaUtil.abrirVentana("biblioteca_usuario", "Mi Biblioteca", 850, 600);
    }

    @FXML
    public void mostrarMisResenas() {
        VentanaUtil.abrirVentana("mis_resenas_usuario", "Mis Reseñas", 900, 650);
    }

    @FXML
    public void mostrarPerfil() {
        VentanaUtil.abrirVentana("perfil_usuario", "Mi Perfil", 700, 680);
        actualizarSaldo();
    }

    // ── CONSULTAS ────────────────────────────────────────────────────────
    @FXML
    public void mostrarEstadisticas() {
        // Vista de estadísticas dedicada al usuario (EstadisticasController)
        VentanaUtil.abrirVentana("estadisticas_usuario", "Estadísticas", 950, 700);
    }

    // Compatibilidad con botones FXML heredados
    @FXML public void mostrarMejorValorados() { mostrarEstadisticas(); }
    @FXML public void mostrarMasVendidos()    { mostrarEstadisticas(); }

    // ── AYUDA ────────────────────────────────────────────────────────────
    @FXML
    public void mostrarAyuda() {
        VentanaUtil.abrirVentana("ayuda_usuario", "Ayuda", 700, 560);
    }

    // ── SESIÓN ───────────────────────────────────────────────────────────
    @FXML
    public void cerrarSesion() {
        if (DialogUtil.confirmar("¿Seguro que quieres cerrar sesión?")) {
            gd.cerrarSesion();
            try { App.setRoot("login"); }
            catch (IOException e) { e.printStackTrace(); }
        }
    }
}
