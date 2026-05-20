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
public class ResenasJuegoController implements Initializable {

    @FXML private Label lblTituloJuego;
    @FXML private Label lblMedia;
    @FXML private VBox  vboxResenas;
    @FXML private Button btnCerrar;

    private final GestorDatos gd = GestorDatos.getInstance();
    private Juego juego;

    /** Llamado por VentanaUtil antes de mostrar la ventana. */
    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // juego puede ser null si initialize() se llama antes de setJuego()
        // La carga real ocurre en cargar(), que llamamos desde VentanaUtil o al setJuego.
    }

    /** Punto de entrada principal tras inyectar el juego. */
    public void cargar() {
        if (juego == null) return;
        lblTituloJuego.setText("🎮 " + juego.getTitulo());
        double media = gd.getPuntuacionMediaJuego(juego.getIdJuego());
        List<Resena> resenas = gd.getResenasPorJuego(juego);
        lblMedia.setText(media > 0
                ? String.format("⭐ %.1f/10  (%d reseñas)", media, resenas.size())
                : "Sin valoraciones todavía");

        vboxResenas.getChildren().clear();
        if (resenas.isEmpty()) {
            Label lbl = new Label("Nadie ha reseñado este juego aún.");
            lbl.getStyleClass().add("label-muted");
            vboxResenas.getChildren().add(lbl);
            return;
        }
        for (Resena r : resenas) vboxResenas.getChildren().add(buildTarjeta(r));
    }

    private VBox buildTarjeta(Resena r) {
        VBox tarjeta = new VBox(6);
        tarjeta.getStyleClass().add("card");
        tarjeta.setPadding(new Insets(12));

        HBox cabecera = new HBox(10);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        Label lblAutor = new Label("👤 " + r.getAutor().getNombreCompleto());
        lblAutor.getStyleClass().add("label-section");
        Region sep = new Region(); HBox.setHgrow(sep, Priority.ALWAYS);
        Label lblPunt = new Label("⭐ " + r.getPuntuacion() + "/10");
        lblPunt.getStyleClass().add("label-gold");
        Label lblFecha = new Label(r.getFechaFormateada());
        lblFecha.getStyleClass().add("label-muted");
        cabecera.getChildren().addAll(lblAutor, sep, lblPunt, lblFecha);

        Label lblCom = new Label(r.getComentario());
        lblCom.getStyleClass().add("label-normal");
        lblCom.setWrapText(true);

        Label lblIdioma = new Label("🌐 " + r.getIdioma());
        lblIdioma.getStyleClass().add("label-muted");

        tarjeta.getChildren().addAll(cabecera, lblCom, lblIdioma);
        return tarjeta;
    }

    @FXML
    public void cerrar() {
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
