package com.mycompany.proyectotiendajuegos.aaron.franco;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase de utilidad para abrir ventanas secundarias (Stage).
 * Centraliza la carga de FXMLs y la configuración de las ventanas.
 */
public class VentanaUtil {

    private static final String RUTA_BASE =
            "/com/mycompany/proyectotiendajuegos/aaron/franco/vistas/";

    /**
     * Abre una ventana modal con el FXML indicado.
     * El nombre no lleva ruta ni extensión, p.ej. "catalogo".
     */
    public static void abrirVentana(String nombreFxml, String titulo) {
        abrirVentana(nombreFxml, titulo, 900, 620);
    }

    public static void abrirVentana(String nombreFxml, String titulo,
                                    double ancho, double alto) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    VentanaUtil.class.getResource(RUTA_BASE + nombreFxml + ".fxml"));
            Parent raiz = loader.load();

            Stage ventana = new Stage();
            ventana.setTitle("GamePyramid – " + titulo);
            ventana.initModality(Modality.APPLICATION_MODAL);

            Scene escena = new Scene(raiz, ancho, alto);
            escena.getStylesheets().add(
                    VentanaUtil.class.getResource(
                            "/com/mycompany/proyectotiendajuegos/aaron/franco/styles.css")
                                     .toExternalForm());
            ventana.setScene(escena);
            ventana.setMinWidth(700);
            ventana.setMinHeight(500);
            ventana.showAndWait();

        } catch (IOException e) {
            DialogUtil.error("No se pudo abrir la ventana: " + titulo
                    + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Abre una ventana no modal (independiente).
     */
    public static void abrirVentanaLibre(String nombreFxml, String titulo,
                                         double ancho, double alto) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    VentanaUtil.class.getResource(RUTA_BASE + nombreFxml + ".fxml"));
            Parent raiz = loader.load();

            Stage ventana = new Stage();
            ventana.setTitle("GamePyramid – " + titulo);

            Scene escena = new Scene(raiz, ancho, alto);
            escena.getStylesheets().add(
                    VentanaUtil.class.getResource(
                            "/com/mycompany/proyectotiendajuegos/aaron/franco/styles.css")
                                     .toExternalForm());
            ventana.setScene(escena);
            ventana.setMinWidth(700);
            ventana.setMinHeight(500);
            ventana.show();

        } catch (IOException e) {
            DialogUtil.error("No se pudo abrir la ventana: " + titulo
                    + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}
