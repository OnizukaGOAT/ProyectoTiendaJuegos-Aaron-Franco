package com.mycompany.proyectotiendajuegos.aaron.franco.controladores;

import com.mycompany.proyectotiendajuegos.aaron.franco.DialogUtil;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana Mi Perfil (usuario).
 * Muestra estadísticas personales y permite editar datos y recargar saldo.
 */
public class PerfilUsuarioController implements Initializable {

    // ── Datos actuales ─────────────────────────────────────
    @FXML private Label lblNombre;
    @FXML private Label lblCorreo;
    @FXML private Label lblIdioma;
    @FXML private Label lblSaldo;
    @FXML private Label lblNumJuegos;
    @FXML private Label lblNumCompras;
    @FXML private Label lblNumResenas;

    // ── Recarga de saldo ───────────────────────────────────
    @FXML private TextField txCantidad;
    @FXML private Label     lblErrorSaldo;

    // ── Edición de datos ───────────────────────────────────
    @FXML private TextField     txNombre;
    @FXML private TextField     txApellidos;
    @FXML private TextField     txCorreo;
    @FXML private PasswordField txContrasena;
    @FXML private ComboBox<String> cbIdioma;
    @FXML private Label         lblErrorEdicion;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbIdioma.setItems(FXCollections.observableArrayList(
                "Español", "English", "Français", "Deutsch", "Português", "Italiano"));
        cargar();
    }

    private void cargar() {
        Usuario u = gd.getUsuarioActual();
        lblNombre.setText(u.getNombreCompleto());
        lblCorreo.setText(u.getCorreo());
        lblIdioma.setText(u.getIdioma());
        lblSaldo.setText(String.format("%.2f€", u.getSaldo()));
        lblNumJuegos.setText(String.valueOf(gd.getBibliotecaUsuario(u.getIdUsuario()).size()));
        lblNumCompras.setText(String.valueOf(gd.getComprasUsuario(u.getIdUsuario()).size()));
        lblNumResenas.setText(String.valueOf(gd.getResenasPorUsuario(u).size()));

        // Prellenar formulario de edición
        txNombre.setText(u.getNombre());
        txApellidos.setText(u.getApellidos());
        txCorreo.setText(u.getCorreo());
        txContrasena.clear();
        cbIdioma.getSelectionModel().select(u.getIdioma());
    }

    // ── Saldo rápido ───────────────────────────────────────
    @FXML public void recargar5()   { recargar(5); }
    @FXML public void recargar10()  { recargar(10); }
    @FXML public void recargar20()  { recargar(20); }
    @FXML public void recargar50()  { recargar(50); }

    @FXML
    public void recargarPersonalizado() {
        try {
            double cantidad = Double.parseDouble(txCantidad.getText().replace(",", "."));
            recargar(cantidad);
        } catch (NumberFormatException ex) {
            lblErrorSaldo.setText("Introduce una cantidad válida.");
        }
    }

    private void recargar(double cantidad) {
        lblErrorSaldo.setText("");
        if (cantidad <= 0)   { lblErrorSaldo.setText("La cantidad debe ser mayor que 0."); return; }
        if (cantidad > 500)  { lblErrorSaldo.setText("No puedes añadir más de 500€ de una vez."); return; }
        Usuario u = gd.getUsuarioActual();
        u.setSaldo(u.getSaldo() + cantidad);
        gd.actualizarUsuario(u);
        DialogUtil.info(String.format("✅ %.2f€ añadidos. Nuevo saldo: %.2f€", cantidad, u.getSaldo()));
        txCantidad.clear();
        cargar();
    }

    // ── Editar datos ───────────────────────────────────────
    @FXML
    public void guardarPerfil() {
        lblErrorEdicion.setText("");
        if (txNombre.getText().trim().isEmpty()) {
            lblErrorEdicion.setText("El nombre no puede estar vacío.");
            return;
        }
        Usuario u = gd.getUsuarioActual();
        u.setNombre(txNombre.getText().trim());
        u.setApellidos(txApellidos.getText().trim());
        u.setCorreo(txCorreo.getText().trim());
        u.setIdioma(cbIdioma.getValue());
        if (!txContrasena.getText().isEmpty()) u.setContrasena(txContrasena.getText());
        gd.actualizarUsuario(u);
        DialogUtil.info("Perfil actualizado correctamente.");
        cargar();
    }
}
