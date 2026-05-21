package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.App;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.GestorDatos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
/**
 * FXML Controller class
 *
 * @author USUARIO
 */
public class RegistroController implements Initializable {

    @FXML private TextField     txNombre, txApellidos, txCorreo, txSaldo;
    @FXML private PasswordField txPassword;
    @FXML private ComboBox<String> cbIdioma;
    @FXML private Label         lblError;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbIdioma.setItems(FXCollections.observableArrayList("Español", "English", "Français", "Deutsch", "Português", "Italiano")); //Me acabo de dar cuenta de que si pones una reseña en la BD en un idioma que no esté aquí aparece como ?????
        cbIdioma.getSelectionModel().selectFirst();
    }

    @FXML
    private void registrar() {
        String nombre = txNombre.getText().trim();
        String apellidos = txApellidos.getText().trim();
        String correo = txCorreo.getText().trim();
        String pass = txPassword.getText();
        String idioma = cbIdioma.getValue();
        String saldoStr = txSaldo.getText().trim();

        lblError.setVisible(false);

        if (nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || pass.isEmpty()) {mostrarError("Rellena todos los campos obligatorios.");
            return;
        }
        if (pass.length() < 6) {mostrarError("La contraseña debe tener al menos 6 caracteres.");
            return;
        }
        double saldo = 50.0;
        if (!saldoStr.isEmpty()) {
            try { saldo = Double.parseDouble(saldoStr.replace(",", ".")); }
            catch (NumberFormatException e) { mostrarError("El saldo debe ser un número."); return; }
        }

        boolean ok = gd.altaUsuario(nombre, apellidos, correo, pass, saldo, idioma);
        if (!ok) { mostrarError("Ya existe una cuenta con ese correo."); return; }

        gd.loginUsuario(correo, pass);
        navegar("main_usuario");
    }

    @FXML
    private void volver() { navegar("login"); }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    private void navegar(String fxml) {
        try { App.setRoot(fxml); }
        catch (IOException e) { e.printStackTrace(); }
    }
}
