package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Estudio;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.GestorDatos;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Juego;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana de Gestión de Juegos.
 */
public class GestionJuegosController implements Initializable {

    // ── Tabla ──────────────────────────────────────────────
    @FXML private TableView<Juego>          tablaJuegos;
    @FXML private TableColumn<Juego, String> colId;
    @FXML private TableColumn<Juego, String> colTitulo;
    @FXML private TableColumn<Juego, String> colGenero;
    @FXML private TableColumn<Juego, String> colPlataforma;
    @FXML private TableColumn<Juego, String> colPrecio;
    @FXML private TableColumn<Juego, String> colStock;
    @FXML private TableColumn<Juego, String> colDirector;
    @FXML private TableColumn<Juego, Void>   colAcciones;

    // ── Panel detalle ──────────────────────────────────────
    @FXML private Label lblDetTitulo;
    @FXML private Label lblDetGenero;
    @FXML private Label lblDetPlataforma;
    @FXML private Label lblDetPrecio;
    @FXML private Label lblDetStock;
    @FXML private Label lblDetDirector;
    @FXML private Label lblDetMedia;
    @FXML private Label lblDetVentas;

    // ── Formulario ─────────────────────────────────────────
    @FXML private Label            lblTituloForm;
    @FXML private TextField        txTitulo;
    @FXML private TextField        txGenero;
    @FXML private TextField        txPlataforma;
    @FXML private TextField        txPrecio;
    @FXML private TextField        txStock;
    @FXML private TextField        txDirector;
    @FXML private ComboBox<Estudio> cbEstudio;
    @FXML private Label            lblError;

    private final GestorDatos gd = GestorDatos.getInstance();
    private Juego juegoEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbEstudio.setItems(FXCollections.observableArrayList(gd.getEstudios()));
        cbEstudio.setPromptText("Asignar a estudio (opcional)");
        configurarTabla();
        cargarJuegos();
        limpiarFormulario();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getIdJuego())));
        colTitulo.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getTitulo()));
        colGenero.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getGenero()));
        colPlataforma.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getPlataforma()));
        colPrecio.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f€", c.getValue().getPrecio())));
        colStock.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getStock())));
        colDirector.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getDirector()));

        colAcciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnVer    = new Button("🔍 Ver");
            final Button btnEditar = new Button("✏ Editar");
            final Button btnBaja   = new Button("🗑 Eliminar");
            {
                btnVer.getStyleClass().add("btn-gold");
                btnEditar.getStyleClass().add("btn-secondary");
                btnBaja.getStyleClass().add("btn-danger");
                btnVer.setOnAction(e    -> mostrarDetalle(getTableView().getItems().get(getIndex())));
                btnEditar.setOnAction(e -> prepararEdicion(getTableView().getItems().get(getIndex())));
                btnBaja.setOnAction(e   -> darDeBaja(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : new HBox(5, btnVer, btnEditar, btnBaja));
            }
        });

        tablaJuegos.getSelectionModel().selectedItemProperty().addListener(
            (obs, ant, nuevo) -> { if (nuevo != null) mostrarDetalle(nuevo); });
    }

    private void cargarJuegos() {
        tablaJuegos.setItems(FXCollections.observableArrayList(gd.getJuegos()));
    }

    private void mostrarDetalle(Juego j) {
        lblDetTitulo.setText(j.getTitulo());
        lblDetGenero.setText(j.getGenero());
        lblDetPlataforma.setText(j.getPlataforma());
        lblDetPrecio.setText(String.format("%.2f€", j.getPrecio()));
        lblDetStock.setText(String.valueOf(j.getStock()));
        lblDetDirector.setText(j.getDirector());
        double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
        lblDetMedia.setText(media > 0 ? String.format("⭐ %.1f/10", media) : "Sin valoraciones");
        lblDetVentas.setText(String.valueOf(gd.getVentasJuego(j)) + " unidades");
    }

    // ── Formulario ─────────────────────────────────────────
    @FXML
    public void prepararAlta() {
        juegoEnEdicion = null;
        limpiarFormulario();
        lblTituloForm.setText("Nuevo Juego");
    }

    private void prepararEdicion(Juego j) {
        juegoEnEdicion = j;
        lblTituloForm.setText("Editar Juego");
        txTitulo.setText(j.getTitulo());
        txGenero.setText(j.getGenero());
        txPlataforma.setText(j.getPlataforma());
        txPrecio.setText(String.valueOf(j.getPrecio()));
        txStock.setText(String.valueOf(j.getStock()));
        txDirector.setText(j.getDirector());
        // Preseleccionar estudio
        gd.getEstudios().stream()
            .filter(e -> e.getJuegos().stream()
                .anyMatch(jj -> jj.getIdJuego() == j.getIdJuego()))
            .findFirst().ifPresent(cbEstudio.getSelectionModel()::select);
        lblError.setText("");
    }

    @FXML
    public void guardar() {
        String titulo    = txTitulo.getText().trim();
        String genero    = txGenero.getText().trim();
        String plat      = txPlataforma.getText().trim();
        String director  = txDirector.getText().trim();

        if (titulo.isEmpty()) { lblError.setText("El título es obligatorio."); return; }

        double precio;
        int    stock;
        try { precio = Double.parseDouble(txPrecio.getText().replace(",", ".")); }
        catch (NumberFormatException ex) { lblError.setText("Precio inválido."); return; }
        try { stock = Integer.parseInt(txStock.getText()); }
        catch (NumberFormatException ex) { lblError.setText("Stock inválido."); return; }

        if (juegoEnEdicion == null) {
            Juego nuevo = gd.altaJuego(titulo, genero, plat, precio, stock, director);
            if (nuevo != null && cbEstudio.getValue() != null) {
                gd.asignarJuegoAEstudio(nuevo.getIdJuego(),
                    cbEstudio.getValue().getIdEstudio());
            }
            DialogUtil.info("Juego creado correctamente.");
        } else {
            juegoEnEdicion.setTitulo(titulo);
            juegoEnEdicion.setGenero(genero);
            juegoEnEdicion.setPlataforma(plat);
            juegoEnEdicion.setPrecio(precio);
            juegoEnEdicion.setStock(stock);
            juegoEnEdicion.setDirector(director);
            gd.actualizarJuego(juegoEnEdicion);
            if (cbEstudio.getValue() != null) {
                gd.asignarJuegoAEstudio(juegoEnEdicion.getIdJuego(),
                    cbEstudio.getValue().getIdEstudio());
            }
            DialogUtil.info("Juego actualizado correctamente.");
        }
        limpiarFormulario();
        cargarJuegos();
    }

    private void darDeBaja(Juego j) {
        if (DialogUtil.confirmar("¿Eliminar el juego '" + j.getTitulo() + "'?")) {
            gd.bajaJuego(j.getIdJuego());
            cargarJuegos();
            limpiarFormulario();
        }
    }

    @FXML
    public void cancelar() { limpiarFormulario(); }

    private void limpiarFormulario() {
        juegoEnEdicion = null;
        lblTituloForm.setText("Nuevo Juego");
        txTitulo.clear(); txGenero.clear(); txPlataforma.clear();
        txPrecio.clear(); txStock.clear(); txDirector.clear();
        cbEstudio.getSelectionModel().clearSelection();
        lblError.setText("");
        lblDetTitulo.setText("–"); lblDetGenero.setText("–");
        lblDetPlataforma.setText("–"); lblDetPrecio.setText("–");
        lblDetStock.setText("–"); lblDetDirector.setText("–");
        lblDetMedia.setText("–"); lblDetVentas.setText("–");
    }

    @FXML
    public void cerrar() {
        ((Stage) tablaJuegos.getScene().getWindow()).close();
    }
}
