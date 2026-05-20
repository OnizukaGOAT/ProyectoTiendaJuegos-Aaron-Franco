package com.mycompany.proyectotiendajuegos.aaron.franco;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author USUARIO
 */

//La lógica de contenido está completamente en el FXML (lo mismo que la anterior)
public class AyudaUsuarioController {

    @FXML private Button btnCerrar;

    @FXML
    public void cerrar() {
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
