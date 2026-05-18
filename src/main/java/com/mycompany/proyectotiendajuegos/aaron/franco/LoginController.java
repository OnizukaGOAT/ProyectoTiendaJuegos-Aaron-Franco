package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.App;
import com.mycompany.proyectotiendajuegos.aaron.franco.GestorDatos;
import com.mycompany.proyectotiendajuegos.aaron.franco.Administrador;
import com.mycompany.proyectotiendajuegos.aaron.franco.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txCorreo;
    @FXML private PasswordField txPassword;
    @FXML private Label lblError;

    private final GestorDatos gd = GestorDatos.getInstance();

    @FXML
    private void loginUsuario() {
        String correo = txCorreo.getText().trim();
        String pass   = txPassword.getText();
        Usuario u = gd.loginUsuario(correo, pass);
        if (u == null) {
            mostrarError("Correo o contraseña incorrectos.");
        } else {
            navegar("main_usuario");
        }
    }

    @FXML
    private void loginAdmin() {
        String correo = txCorreo.getText().trim();
        String pass   = txPassword.getText();
        Administrador a = gd.loginAdmin(correo, pass);
        if (a == null) {
            mostrarError("Correo o contraseña de administrador incorrectos.");
        } else {
            navegar("main_admin");
        }
    }

    @FXML
    private void irARegistro() {
        navegar("registro");
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    private void navegar(String fxml) {
        try { App.setRoot(fxml); }
        catch (IOException e) { mostrarError("Error al cargar la pantalla."); e.printStackTrace(); }
    }
}
