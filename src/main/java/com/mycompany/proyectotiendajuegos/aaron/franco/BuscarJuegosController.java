package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador de búsqueda de juegos (usuario).
 * Toda la estructura visual está declarada en buscar_juegos.fxml;
 * este controller únicamente enlaza datos y reacciona a eventos.
 */
public class BuscarJuegosController implements Initializable {

    /* ── Barra de búsqueda ───────────────────────────────── */
    @FXML private TextField         txBusqueda;
    @FXML private ComboBox<String>  cbTipo;
    @FXML private Label             lblResultados;
    @FXML private Button            btnCerrar;

    /* ── Tabla ────────────────────────────────────────────── */
    @FXML private TableView<Juego>          tablaJuegos;
    @FXML private TableColumn<Juego,String> colTitulo;
    @FXML private TableColumn<Juego,String> colGenero;
    @FXML private TableColumn<Juego,String> colPlataforma;
    @FXML private TableColumn<Juego,String> colDirector;
    @FXML private TableColumn<Juego,String> colMedia;
    @FXML private TableColumn<Juego,String> colPrecio;
    @FXML private TableColumn<Juego,String> colStock;

    /* ── Panel de detalle lateral ────────────────────────── */
    @FXML private javafx.scene.layout.VBox panelDetalle;
    @FXML private javafx.scene.layout.VBox panelVacio;
    @FXML private Label  lblDetTitulo;
    @FXML private Label  lblDetGenero;
    @FXML private Label  lblDetPlat;
    @FXML private Label  lblDetDirector;
    @FXML private Label  lblDetMedia;
    @FXML private Label  lblDetPrecio;
    @FXML private Label  lblDetStock;
    @FXML private Label  lblEnBiblioteca;
    @FXML private Button btnComprar;
    @FXML private Button btnVerResenas;

    private final GestorDatos gd = GestorDatos.getInstance();

    // ── Inicialización ────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipo.setItems(FXCollections.observableArrayList(
                "Nombre", "Género", "Director", "Estudio"));
        cbTipo.getSelectionModel().selectFirst();

        configurarColumnas();

        tablaJuegos.getSelectionModel().selectedItemProperty()
                .addListener((obs, ant, nuevo) -> mostrarDetalle(nuevo));

        mostrarJuegos(gd.getJuegos());
    }

    private void configurarColumnas() {
        colTitulo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTitulo()));
        colGenero.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getGenero()));
        colPlataforma.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPlataforma()));
        colDirector.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDirector()));
        colMedia.setCellValueFactory(c -> {
            double m = gd.getPuntuacionMediaJuego(c.getValue().getIdJuego());
            return new SimpleStringProperty(m > 0 ? String.format("%.1f", m) : "–");
        });
        colPrecio.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f€", c.getValue().getPrecio())));
        colStock.setCellValueFactory(c -> {
            int s = c.getValue().getStock();
            return new SimpleStringProperty(s > 0 ? String.valueOf(s) : "Agotado");
        });

        colStock.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                if ("Agotado".equals(item))  setStyle("-fx-text-fill:#e94560;");
                else if (Integer.parseInt(item) <= 10) setStyle("-fx-text-fill:#f5a623;");
                else                                   setStyle("-fx-text-fill:#4caf50;");
            }
        });
    }

    // ── Búsqueda ──────────────────────────────────────────
    @FXML
    public void buscar() {
        String texto = txBusqueda.getText().trim();
        if (texto.isEmpty()) { mostrarJuegos(gd.getJuegos()); return; }
        List<Juego> res;
        switch (cbTipo.getValue()) {
            case "Género":   res = gd.buscarJuegosPorGenero(texto);   break;
            case "Director": res = gd.buscarJuegosPorDirector(texto); break;
            case "Estudio":  res = gd.buscarJuegosPorEstudio(texto);  break;
            default:         res = gd.buscarJuegosPorNombre(texto);
        }
        mostrarJuegos(res);
    }

    @FXML
    public void limpiar() {
        txBusqueda.clear();
        cbTipo.getSelectionModel().selectFirst();
        mostrarJuegos(gd.getJuegos());
    }

    private void mostrarJuegos(List<Juego> lista) {
        tablaJuegos.setItems(FXCollections.observableArrayList(lista));
        lblResultados.setText(lista.size() + " juego(s) encontrado(s)");
        ocultarDetalle();
    }

    // ── Panel de detalle ──────────────────────────────────
    private void mostrarDetalle(Juego j) {
        if (j == null) { ocultarDetalle(); return; }

        lblDetTitulo.setText(j.getTitulo());
        lblDetGenero.setText(j.getGenero());
        lblDetPlat.setText(j.getPlataforma());
        lblDetDirector.setText(j.getDirector());
        double m = gd.getPuntuacionMediaJuego(j.getIdJuego());
        lblDetMedia.setText(m > 0 ? String.format("⭐ %.1f/10", m) : "Sin valoraciones");
        lblDetPrecio.setText(String.format("%.2f€", j.getPrecio()));

        int stock = j.getStock();
        if (stock > 10)     { lblDetStock.setText("✅ " + stock + " en stock");       lblDetStock.getStyleClass().setAll("stock-ok"); }
        else if (stock > 0) { lblDetStock.setText("⚠ Solo " + stock + " en stock");  lblDetStock.getStyleClass().setAll("stock-low"); }
        else                { lblDetStock.setText("❌ Sin stock");                    lblDetStock.getStyleClass().setAll("stock-out"); }

        boolean loTiene = gd.usuarioPoseeJuego(
                gd.getUsuarioActual().getIdUsuario(), j.getIdJuego());

        lblEnBiblioteca.setVisible(loTiene);
        lblEnBiblioteca.setManaged(loTiene);
        btnComprar.setVisible(!loTiene);
        btnComprar.setManaged(!loTiene);
        btnComprar.setDisable(stock <= 0);

        panelDetalle.setVisible(true);
        panelDetalle.setManaged(true);
        panelVacio.setVisible(false);
        panelVacio.setManaged(false);
    }

    private void ocultarDetalle() {
        panelDetalle.setVisible(false);
        panelDetalle.setManaged(false);
        panelVacio.setVisible(true);
        panelVacio.setManaged(true);
    }

    // ── Acciones ──────────────────────────────────────────
    @FXML
    public void comprarSeleccionado() {
        Juego j = tablaJuegos.getSelectionModel().getSelectedItem();
        if (j == null) return;
        String resultado = gd.comprarJuego(gd.getUsuarioActual(), j, 1);
        if ("OK".equals(resultado)) {
            DialogUtil.info("¡Compra realizada! " + j.getTitulo() + " añadido a tu biblioteca.");
            mostrarJuegos(gd.getJuegos());
        } else {
            DialogUtil.error(resultado);
        }
    }

    @FXML
    public void verResenasSeleccionado() {
        Juego j = tablaJuegos.getSelectionModel().getSelectedItem();
        if (j == null) return;
        VentanaUtil.abrirVentanaConDato("resenas_juego",
                "Reseñas – " + j.getTitulo(), 850, 580, j);
    }

    @FXML
    public void cerrar() {
        ((Stage) btnCerrar.getScene().getWindow()).close();
    }
}
