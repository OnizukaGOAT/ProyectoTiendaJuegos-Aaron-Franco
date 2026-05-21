package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.App;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.GestorDatos;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Administrador;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
/**
 * FXML Controller class
 *
 * @author USUARIO
 */
public class LoginController {

    @FXML private TextField     txCorreo;
    @FXML private PasswordField txPassword;
    @FXML private Label         lblError;

    private final GestorDatos gd = GestorDatos.getInstance();

    @FXML
    private void loginUsuario() {
        String correo = txCorreo.getText().trim();
        String pass   = txPassword.getText();
        lblError.setVisible(false);

        if (correo.isEmpty() || pass.isEmpty()) {mostrarError("Introduce correo y contraseña.");
            return;
        }
        Usuario u = gd.loginUsuario(correo, pass);
        if (u == null) mostrarError("Correo o contraseña incorrectos.");
        else navegar("main_usuario");
    }

    @FXML
    private void loginAdmin() {                 //Se que no tiene mucho sentido que el login del admin aparezca justo debajo, pero lo dejo para probar la aplicación, porque solo un admin puede registrar admins
        String correo = txCorreo.getText().trim();
        String pass   = txPassword.getText();
        lblError.setVisible(false);

        if (correo.isEmpty() || pass.isEmpty()) {mostrarError("Introduce correo y contraseña.");
            return;
        }
        Administrador a = gd.loginAdmin(correo, pass);
        if (a == null) mostrarError("Credenciales de administrador incorrectas.");
        else navegar("main_admin");
    }

    @FXML
    private void irARegistro() { navegar("registro"); }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    private void navegar(String fxml) {
        try { App.setRoot(fxml); }
        catch (IOException e) { mostrarError("Error al cargar la pantalla."); e.printStackTrace(); }
    }
}
