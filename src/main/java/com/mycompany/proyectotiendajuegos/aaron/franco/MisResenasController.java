package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author USUARIO
 */
public class MisResenasController implements Initializable {

    /* ── Cabecera ─────────────────────────────────────────── */
    @FXML private Label lblContador;

    /* ── Tabla de reseñas ────────────────────────────────── */
    @FXML private TableView<Resena>          tablaResenas;
    @FXML private TableColumn<Resena,String> colJuego;
    @FXML private TableColumn<Resena,String> colPunt;
    @FXML private TableColumn<Resena,String> colIdioma;
    @FXML private TableColumn<Resena,String> colFecha;
    @FXML private TableColumn<Resena,String> colCom;

    /* ── Botones de acción sobre la tabla ────────────────── */
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    /* ── Formulario nueva / editar reseña ────────────────── */
    @FXML private Label             lblFormTitulo;
    @FXML private ComboBox<Juego>   cbJuego;
    @FXML private Spinner<Integer>  spinPuntuacion;
    @FXML private ComboBox<String>  cbIdioma;
    @FXML private TextArea          txComentario;
    @FXML private Label             lblError;
    @FXML private Button            btnGuardar;

    private Resena resenaEnEdicion = null;
    private final GestorDatos gd   = GestorDatos.getInstance();

    // ── Inicialización ────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Spinner de puntuación
        spinPuntuacion.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 8));

        // Combos del formulario
        cbIdioma.setItems(FXCollections.observableArrayList(
                "Español", "English", "Français", "Deutsch", "Português", "Italiano"));
        cbIdioma.getSelectionModel().select(gd.getUsuarioActual().getIdioma());

        cargarComboJuegos();

        // Columnas de la tabla
        colJuego.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getJuego().getTitulo()));
        colPunt.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPuntuacion() + "/10"));
        colIdioma.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getIdioma()));
        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFechaFormateada()));
        colCom.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getComentario()));

        // Habilitar botones sólo con selección
        tablaResenas.getSelectionModel().selectedItemProperty()
                .addListener((obs, ant, nuevo) -> {
                    boolean haySeleccion = nuevo != null;
                    btnEditar.setDisable(!haySeleccion);
                    btnEliminar.setDisable(!haySeleccion);
                });

        cargarResenas();
    }

    private void cargarComboJuegos() {
        List<Juego> biblioteca = gd.getBibliotecaUsuario(
                gd.getUsuarioActual().getIdUsuario());
        cbJuego.setItems(FXCollections.observableArrayList(biblioteca));
        cbJuego.setPromptText("Selecciona un juego de tu biblioteca");
    }

    // ── Tabla ─────────────────────────────────────────────
    private void cargarResenas() {
        List<Resena> lista = gd.getResenasPorUsuario(gd.getUsuarioActual());
        tablaResenas.setItems(FXCollections.observableArrayList(lista));
        lblContador.setText(lista.size() + " reseña(s) escritas");
    }

    // ── Botones de tabla ──────────────────────────────────
    @FXML
    public void editarSeleccionada() {
        Resena r = tablaResenas.getSelectionModel().getSelectedItem();
        if (r != null) cargarEnFormulario(r);
    }

    @FXML
    public void eliminarSeleccionada() {
        Resena r = tablaResenas.getSelectionModel().getSelectedItem();
        if (r == null) return;
        if (DialogUtil.confirmar("¿Eliminar esta reseña?")) {
            gd.eliminarResena(r.getIdResena());
            cargarResenas();
            limpiarFormulario();
        }
    }

    // ── Formulario ────────────────────────────────────────
    private void cargarEnFormulario(Resena r) {
        resenaEnEdicion = r;
        lblFormTitulo.setText("Editar Reseña");
        btnGuardar.setText("Actualizar");

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
        if (cbJuego.getValue() == null)         { lblError.setText("Selecciona un juego.");     return; }
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
        tablaResenas.getSelectionModel().clearSelection();
    }
}
