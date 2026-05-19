package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Compra;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.GestorDatos;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana de Historial de Compras Global (admin).
 */
public class HistorialComprasAdminController implements Initializable {

    // ── Filtros ────────────────────────────────────────────
    @FXML private TextField         txFiltro;
    @FXML private ComboBox<Usuario>  cbFiltroUsuario;

    // ── Tabla ──────────────────────────────────────────────
    @FXML private TableView<Compra>          tablaCompras;
    @FXML private TableColumn<Compra, String> colId;
    @FXML private TableColumn<Compra, String> colUsuario;
    @FXML private TableColumn<Compra, String> colJuego;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colCantidad;
    @FXML private TableColumn<Compra, String> colCoste;

    // ── Resumen ────────────────────────────────────────────
    @FXML private Label lblTotalCompras;
    @FXML private Label lblTotalIngresos;

    private final GestorDatos gd = GestorDatos.getInstance();
    private FilteredList<Compra> listaFiltrada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbFiltroUsuario.setItems(FXCollections.observableArrayList(gd.getUsuarios()));
        cbFiltroUsuario.setPromptText("Todos los usuarios");

        configurarTabla();
        cargarCompras();

        txFiltro.textProperty().addListener((obs, ant, nuevo) -> aplicarFiltro());
        cbFiltroUsuario.valueProperty().addListener((obs, ant, nuevo) -> aplicarFiltro());
    }

    private void configurarTabla() {
        colId.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getCodCompra())));
        colUsuario.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getUsuario().getNombreCompleto()));
        colJuego.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getJuego().getTitulo()));
        colFecha.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getFechaFormateada()));
        colCantidad.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getCantidad())));
        colCoste.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f€", c.getValue().getCoste())));
    }

    private void cargarCompras() {
        listaFiltrada = new FilteredList<>(
            FXCollections.observableArrayList(gd.getHistorialComprasGlobal()));
        tablaCompras.setItems(listaFiltrada);
        actualizarResumen();
    }

    private void aplicarFiltro() {
        String texto   = txFiltro.getText().toLowerCase();
        Usuario usuario = cbFiltroUsuario.getValue();

        listaFiltrada.setPredicate(c -> {
            boolean coincideTexto = texto.isEmpty()
                || c.getJuego().getTitulo().toLowerCase().contains(texto)
                || c.getUsuario().getNombreCompleto().toLowerCase().contains(texto);
            boolean coincideUsuario = usuario == null
                || c.getUsuario().getNombreCompleto()
                    .equalsIgnoreCase(usuario.getNombreCompleto());
            return coincideTexto && coincideUsuario;
        });
        actualizarResumen();
    }

    @FXML
    public void limpiarFiltros() {
        txFiltro.clear();
        cbFiltroUsuario.getSelectionModel().clearSelection();
    }

    private void actualizarResumen() {
        int total = listaFiltrada.size();
        double ingresos = listaFiltrada.stream().mapToDouble(Compra::getCoste).sum();
        lblTotalCompras.setText("Total compras: " + total);
        lblTotalIngresos.setText(String.format("Ingresos: %.2f€", ingresos));
    }

    @FXML
    public void cerrar() {
        ((Stage) tablaCompras.getScene().getWindow()).close();
    }
}
