package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author USUARIO
 */
public class BibliotecaController implements Initializable {

    @FXML private VBox  vboxJuegos;
    @FXML private Label lblContador;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargar();
    }

    private void cargar() {
        List<Juego> biblioteca = gd.getBibliotecaUsuario(gd.getUsuarioActual().getIdUsuario());
        vboxJuegos.getChildren().clear();
        lblContador.setText(biblioteca.size() + " juego(s) en tu biblioteca");

        if (biblioteca.isEmpty()) {
            Label lbl = new Label("No tienes juegos en tu biblioteca todavía.\nVisita el catálogo para comprar.");
            lbl.getStyleClass().add("label-muted");
            lbl.setWrapText(true);
            vboxJuegos.getChildren().add(lbl);
            return;
        }

        for (Juego j : biblioteca) vboxJuegos.getChildren().add(buildTarjeta(j));
    }

    private HBox buildTarjeta(Juego j) {
        HBox tarjeta = new HBox(15);
        tarjeta.getStyleClass().add("card");
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setPadding(new Insets(12));

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblTitulo = new Label(j.getTitulo());
        lblTitulo.getStyleClass().add("label-section");

        Label lblMeta = new Label("🎮 " + j.getPlataforma()
                + "  |  📂 " + j.getGenero()
                + "  |  🎬 " + j.getDirector());
        lblMeta.getStyleClass().add("label-muted");

        double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
        Label lblMedia = new Label(media > 0
                ? String.format("⭐ %.1f/10", media) : "Sin valoraciones");
        lblMedia.getStyleClass().add("label-normal");

        info.getChildren().addAll(lblTitulo, lblMeta, lblMedia);

        VBox panelAccion = new VBox(6);
        panelAccion.setAlignment(Pos.CENTER);

        Button btnVerResenas = new Button("Ver reseñas");
        btnVerResenas.getStyleClass().add("btn-secondary");
        btnVerResenas.setOnAction(e ->
                VentanaUtil.abrirVentanaConDato("resenas_juego",
                        "Reseñas – " + j.getTitulo(), 750, 550, j));

        Button btnResenar = new Button("✍ Reseñar");
        btnResenar.getStyleClass().add("btn-gold");
        btnResenar.setOnAction(e ->
                VentanaUtil.abrirVentana("mis_resenas_usuario", "Mis Reseñas", 900, 650));

        panelAccion.getChildren().addAll(btnVerResenas, btnResenar);
        tarjeta.getChildren().addAll(info, panelAccion);
        return tarjeta;
    }

    @FXML
    public void cerrar() {
        ((Stage) vboxJuegos.getScene().getWindow()).close();
    }
}
