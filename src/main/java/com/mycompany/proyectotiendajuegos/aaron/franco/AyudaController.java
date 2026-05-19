package com.mycompany.proyectotiendajuegos.aaron.franco.controladores;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;

/**
 * Controlador de la ventana de Ayuda (usuario).
 * La lógica de contenido está completamente en el FXML (solo texto estático).
 */
public class AyudaController {

    @FXML private Button btnCerrar;

    @FXML
    public void cerrar() {
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
