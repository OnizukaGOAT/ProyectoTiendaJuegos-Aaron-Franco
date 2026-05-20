package com.mycompany.proyectotiendajuegos.aaron.franco;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;
/**
 * FXML Controller class
 *
 * @author USUARIO
 */
public class DialogUtil {     //Esto al final son solo alertas útiles por si fueran necesarias

    public static void info(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("GamePyramid");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void error(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("GamePyramid – Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static boolean confirmar(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("GamePyramid – Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}
