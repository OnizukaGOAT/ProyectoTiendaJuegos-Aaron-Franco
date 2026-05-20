package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador de reseñas de un juego concreto.
 * Toda la estructura visual está declarada en resenas_juego.fxml;
 * este controller únicamente enlaza datos y reacciona a eventos.
 */
public class ResenasJuegoController implements Initializable {

    /* ── Cabecera ─────────────────────────────────────────── */
    @FXML private Label              lblTituloJuego;
    @FXML private Label              lblMedia;
    @FXML private ComboBox<String>   cbFiltroIdioma;
    @FXML private Button             btnCerrar;

    /* ── Tabla ────────────────────────────────────────────── */
    @FXML private TableView<Resena>          tablaResenas;
    @FXML private TableColumn<Resena,String> colAutor;
    @FXML private TableColumn<Resena,String> colPunt;
    @FXML private TableColumn<Resena,String> colIdioma;
    @FXML private TableColumn<Resena,String> colFecha;
    @FXML private TableColumn<Resena,String> colCom;

    /* ── Panel lateral de detalle de reseña ─────────────── */
    @FXML private javafx.scene.layout.VBox panelResena;
    @FXML private javafx.scene.layout.VBox panelVacio;
    @FXML private Label lblResAutor;
    @FXML private Label lblResPunt;
    @FXML private Label lblResIdioma;
    @FXML private Label lblResFecha;
    @FXML private Label lblResCom;

    private final GestorDatos gd = GestorDatos.getInstance();
    private Juego juego;
    private ObservableList<Resena> todasLasResenas;

    // ── Punto de entrada: VentanaUtil llama a setJuego() ──
    public void setJuego(Juego juego) {
        this.juego = juego;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // La carga real ocurre en cargar(), invocado desde VentanaUtil
        // tras llamar a setJuego().
    }

    /** Invocado por VentanaUtil tras inyectar el juego. */
    public void cargar() {
        if (juego == null) return;

        lblTituloJuego.setText("🎮 " + juego.getTitulo());
        List<Resena> resenas = gd.getResenasPorJuego(juego);
        double media = gd.getPuntuacionMediaJuego(juego.getIdJuego());
        lblMedia.setText(media > 0
                ? String.format("⭐ %.1f/10  (%d reseñas)", media, resenas.size())
                : "Sin valoraciones todavía");

        todasLasResenas = FXCollections.observableArrayList(resenas);

        // Poblamos el ComboBox de idiomas con los idiomas presentes
        List<String> idiomas = resenas.stream()
                .map(Resena::getIdioma).distinct().sorted()
                .collect(java.util.stream.Collectors.toList());
        cbFiltroIdioma.setItems(FXCollections.observableArrayList(idiomas));

        configurarColumnas();
        tablaResenas.getSelectionModel().selectedItemProperty()
                .addListener((obs, ant, nuevo) -> mostrarDetalleResena(nuevo));

        tablaResenas.setItems(todasLasResenas);
        ocultarDetalleResena();
    }

    private void configurarColumnas() {
        colAutor.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getAutor().getNombreCompleto()));
        colPunt.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPuntuacion() + "/10"));
        colIdioma.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getIdioma()));
        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFechaFormateada()));
        colCom.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getComentario()));
    }

    // ── Filtros ───────────────────────────────────────────
    @FXML
    public void filtrarPorIdioma() {
        String idiomaSeleccionado = cbFiltroIdioma.getValue();
        if (idiomaSeleccionado == null || idiomaSeleccionado.isEmpty()) {
            tablaResenas.setItems(todasLasResenas);
            return;
        }
        ObservableList<Resena> filtradas = todasLasResenas.filtered(
                r -> idiomaSeleccionado.equals(r.getIdioma()));
        tablaResenas.setItems(filtradas);
        ocultarDetalleResena();
    }

    @FXML
    public void mostrarTodas() {
        cbFiltroIdioma.getSelectionModel().clearSelection();
        tablaResenas.setItems(todasLasResenas);
        ocultarDetalleResena();
    }

    // ── Panel de detalle de reseña ────────────────────────
    private void mostrarDetalleResena(Resena r) {
        if (r == null) { ocultarDetalleResena(); return; }

        lblResAutor.setText(r.getAutor().getNombreCompleto());
        lblResPunt.setText("⭐ " + r.getPuntuacion() + "/10");
        lblResIdioma.setText(r.getIdioma());
        lblResFecha.setText(r.getFechaFormateada());
        lblResCom.setText(r.getComentario());

        panelResena.setVisible(true);
        panelResena.setManaged(true);
        panelVacio.setVisible(false);
        panelVacio.setManaged(false);
    }

    private void ocultarDetalleResena() {
        panelResena.setVisible(false);
        panelResena.setManaged(false);
        panelVacio.setVisible(true);
        panelVacio.setManaged(true);
    }

    // ── Cerrar ────────────────────────────────────────────
    @FXML
    public void cerrar() {
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
