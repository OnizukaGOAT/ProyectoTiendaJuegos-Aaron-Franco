package com.mycompany.proyectotiendajuegos.aaron.franco;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Clase de utilidad para abrir ventanas secundarias (Stage).
 * Los FXMLs de ventanas están en el mismo paquete de recursos que los demás.
 */
public class VentanaUtil {

    private static final String RUTA_BASE =
            "/com/mycompany/proyectotiendajuegos/aaron/franco/";

    /** Abre una ventana modal con tamaño por defecto. */
    public static void abrirVentana(String nombreFxml, String titulo) {
        abrirVentana(nombreFxml, titulo, 900, 620);
    }

    /** Abre una ventana modal con tamaño personalizado. */
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
                    VentanaUtil.class.getResource(RUTA_BASE + "styles.css").toExternalForm());
            ventana.setScene(escena);
            ventana.setMinWidth(700);
            ventana.setMinHeight(500);
            ventana.showAndWait();

        } catch (IOException e) {
            DialogUtil.error("No se pudo abrir la ventana: " + titulo + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Abre una ventana modal pasando un dato directamente a su controlador. */
    public static void abrirVentanaConDato(String nombreFxml, String titulo,
                                           double ancho, double alto, Object dato) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    VentanaUtil.class.getResource(RUTA_BASE + nombreFxml + ".fxml"));
            Parent raiz = loader.load();

            // Obtiene el controlador de destino de forma dinámica
            Object controlador = loader.getController();
            if (controlador != null && dato != null) {
                try {
                    // Busca automáticamente un método llamado 'setJuego' o 'initData' que acepte el objeto
                    Method metodo = controlador.getClass().getMethod("setJuego", dato.getClass());
                    metodo.invoke(controlador, dato);
                } catch (NoSuchMethodException e) {
                    try {
                        // Alternativa por si el método de destino se llama 'initData'
                        Method metodoAlt = controlador.getClass().getMethod("initData", dato.getClass());
                        metodoAlt.invoke(controlador, dato);
                    } catch (Exception ex) {
                        System.out.println("Aviso: El controlador de destino no tiene un método compatible para recibir el dato.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Stage ventana = new Stage();
            ventana.setTitle("GamePyramid – " + titulo);
            ventana.initModality(Modality.APPLICATION_MODAL);

            Scene escena = new Scene(raiz, ancho, alto);
            escena.getStylesheets().add(
                    VentanaUtil.class.getResource(RUTA_BASE + "styles.css").toExternalForm());
            ventana.setScene(escena);
            ventana.setMinWidth(700);
            ventana.setMinHeight(500);
            ventana.showAndWait();

        } catch (IOException e) {
            DialogUtil.error("No se pudo abrir la ventana: " + titulo + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }
}
