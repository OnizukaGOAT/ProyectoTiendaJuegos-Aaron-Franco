package com.mycompany.proyectotiendajuegos.aaron.franco.controladores;

import com.mycompany.proyectotiendajuegos.aaron.franco.DialogUtil;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana "Mis Reseñas" (usuario).
 * Permite ver, crear, editar y eliminar reseñas propias.
 */
public class MisResenasController implements Initializable {

    @FXML private VBox    vboxResenas;
    @FXML private Label   lblContador;

    // ── Formulario (siempre visible en la parte inferior) ──
    @FXML private ComboBox<Juego>   cbJuego;
    @FXML private Spinner<Integer>  spinPuntuacion;
    @FXML private ComboBox<String>  cbIdioma;
    @FXML private TextArea          txComentario;
    @FXML private Label             lblFormTitulo;
    @FXML private Label             lblError;
    @FXML private Button            btnGuardar;

    private Resena resenaEnEdicion = null;
    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Spinner de puntuación
        spinPuntuacion.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 8));

        // Combo de idioma
        cbIdioma.setItems(FXCollections.observableArrayList(
                "Español", "English", "Français", "Deutsch", "Português", "Italiano"));
        cbIdioma.getSelectionModel().select(gd.getUsuarioActual().getIdioma());

        // Combo de juegos (solo los de la biblioteca del usuario)
        cargarComboJuegos();

        cargarResenas();
    }

    private void cargarComboJuegos() {
        List<Juego> biblioteca = gd.getBibliotecaUsuario(gd.getUsuarioActual().getIdUsuario());
        cbJuego.setItems(FXCollections.observableArrayList(biblioteca));
        cbJuego.setPromptText("Selecciona un juego de tu biblioteca");
    }

    // ── Lista de reseñas ───────────────────────────────────
    private void cargarResenas() {
        List<Resena> lista = gd.getResenasPorUsuario(gd.getUsuarioActual());
        vboxResenas.getChildren().clear();
        lblContador.setText(lista.size() + " reseña(s) escritas");

        if (lista.isEmpty()) {
            Label lbl = new Label("Todavía no has escrito ninguna reseña.");
            lbl.getStyleClass().add("label-muted");
            vboxResenas.getChildren().add(lbl);
            return;
        }
        for (Resena r : lista) vboxResenas.getChildren().add(buildTarjeta(r));
    }

    private VBox buildTarjeta(Resena r) {
        VBox tarjeta = new VBox(6);
        tarjeta.getStyleClass().add("card");
        tarjeta.setPadding(new Insets(12));

        HBox cabecera = new HBox(10);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        Label lblJuego = new Label("🎮 " + r.getJuego().getTitulo());
        lblJuego.getStyleClass().add("label-section");
        Region sep = new Region(); HBox.setHgrow(sep, Priority.ALWAYS);
        Label lblPunt = new Label("⭐ " + r.getPuntuacion() + "/10");
        lblPunt.getStyleClass().add("label-gold");
        Label lblFecha = new Label(r.getFechaFormateada());
        lblFecha.getStyleClass().add("label-muted");
        cabecera.getChildren().addAll(lblJuego, sep, lblPunt, lblFecha);

        Label lblCom = new Label(r.getComentario());
        lblCom.getStyleClass().add("label-normal");
        lblCom.setWrapText(true);

        Label lblIdioma = new Label("🌐 " + r.getIdioma());
        lblIdioma.getStyleClass().add("label-muted");

        Button btnEditar = new Button("✏ Editar");
        btnEditar.getStyleClass().add("btn-secondary");
        btnEditar.setOnAction(e -> cargarEnFormulario(r));

        Button btnEliminar = new Button("🗑 Eliminar");
        btnEliminar.getStyleClass().add("btn-danger");
        btnEliminar.setOnAction(e -> {
            if (DialogUtil.confirmar("¿Eliminar esta reseña?")) {
                gd.eliminarResena(r.getIdResena());
                cargarResenas();
                limpiarFormulario();
            }
        });

        tarjeta.getChildren().addAll(cabecera, lblCom, lblIdioma,
                new HBox(8, btnEditar, btnEliminar));
        return tarjeta;
    }

    // ── Formulario ─────────────────────────────────────────
    private void cargarEnFormulario(Resena r) {
        resenaEnEdicion = r;
        lblFormTitulo.setText("Editar Reseña");
        btnGuardar.setText("Actualizar");

        // Preseleccionar el juego
        gd.getBibliotecaUsuario(gd.getUsuarioActual().getIdUsuario()).stream()
                .filter(j -> j.getIdJuego() == r.getJuego().getIdJuego())
                .findFirst().ifPresent(cbJuego.getSelectionModel()::select);
        cbJuego.setDisable(true);

        spinPuntuacion.getValueFactory().setValue(r.getPuntuacion());
        cbIdioma.getSelectionModel().select(r.getIdioma());
        txComentario.setText(r.getComentario());
        lblError.setText("");
    }

    @FXML
    public void guardar() {
        if (cbJuego.getValue() == null) { lblError.setText("Selecciona un juego."); return; }
        if (txComentario.getText().trim().isEmpty()) { lblError.setText("Escribe un comentario."); return; }

        if (resenaEnEdicion != null) {
            resenaEnEdicion.setComentario(txComentario.getText().trim());
            resenaEnEdicion.setPuntuacion(spinPuntuacion.getValue());
            resenaEnEdicion.setIdioma(cbIdioma.getValue());
            gd.actualizarResena(resenaEnEdicion);
            DialogUtil.info("Reseña actualizada correctamente.");
        } else {
            String resultado = gd.anadirResena(
                    gd.getUsuarioActual(), cbJuego.getValue(),
                    txComentario.getText().trim(),
                    spinPuntuacion.getValue(),
                    cbIdioma.getValue());
            if (!"OK".equals(resultado)) { lblError.setText(resultado); return; }
            DialogUtil.info("Reseña publicada correctamente.");
        }
        limpiarFormulario();
        cargarResenas();
    }

    @FXML
    public void cancelar() { limpiarFormulario(); }

    private void limpiarFormulario() {
        resenaEnEdicion = null;
        lblFormTitulo.setText("Nueva Reseña");
        btnGuardar.setText("Publicar");
        cbJuego.setDisable(false);
        cbJuego.getSelectionModel().clearSelection();
        spinPuntuacion.getValueFactory().setValue(8);
        cbIdioma.getSelectionModel().select(gd.getUsuarioActual().getIdioma());
        txComentario.clear();
        lblError.setText("");
    }
}
