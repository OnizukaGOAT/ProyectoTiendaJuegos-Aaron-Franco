package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Compra;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.GestorDatos;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
/**
 * FXML Controller class
 *
 * @author USUARIO
 */
public class HistorialComprasAdminController implements Initializable {

    @FXML private TextField        txFiltro;
    @FXML private ComboBox<Usuario> cbFiltroUsuario;

    @FXML private TableView<Compra>           tablaCompras;
    @FXML private TableColumn<Compra, String> colId;
    @FXML private TableColumn<Compra, String> colUsuario;
    @FXML private TableColumn<Compra, String> colJuego;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colCantidad;
    @FXML private TableColumn<Compra, String> colCoste;

    @FXML private Label lblTotalCompras;
    @FXML private Label lblTotalIngresos;

    private final GestorDatos gd = GestorDatos.getInstance();
    private FilteredList<Compra> listaFiltrada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbFiltroUsuario.setItems(FXCollections.observableArrayList(gd.getUsuarios()));
        cbFiltroUsuario.setPromptText("Todos los usuarios");

        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getCodCompra())));
        colUsuario.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getUsuario().getNombreCompleto()));
        colJuego.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getJuego().getTitulo()));
        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFechaFormateada()));
        colCantidad.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getCantidad())));
        colCoste.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f€", c.getValue().getCoste())));

        listaFiltrada = new FilteredList<>(
                FXCollections.observableArrayList(gd.getHistorialComprasGlobal()));
        tablaCompras.setItems(listaFiltrada);
        actualizarResumen();

        txFiltro.textProperty().addListener((obs, ant, nuevo) -> aplicarFiltro());
        cbFiltroUsuario.valueProperty().addListener((obs, ant, nuevo) -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        String texto    = txFiltro.getText().toLowerCase();
        Usuario usuario = cbFiltroUsuario.getValue();
        listaFiltrada.setPredicate(c -> {
            boolean txt = texto.isEmpty()
                    || c.getJuego().getTitulo().toLowerCase().contains(texto)
                    || c.getUsuario().getNombreCompleto().toLowerCase().contains(texto);
            boolean usr = usuario == null
                    || c.getUsuario().getNombreCompleto().equalsIgnoreCase(usuario.getNombreCompleto());
            return txt && usr;
        });
        actualizarResumen();
    }

    @FXML public void limpiarFiltros() {
        txFiltro.clear();
        cbFiltroUsuario.getSelectionModel().clearSelection();
    }

    private void actualizarResumen() {
        lblTotalCompras.setText("Total compras: " + listaFiltrada.size());
        double ingresos = listaFiltrada.stream().mapToDouble(Compra::getCoste).sum();
        lblTotalIngresos.setText(String.format("Ingresos: %.2f€", ingresos));
    }

    @FXML public void cerrar() {
        ((Stage) tablaCompras.getScene().getWindow()).close();
    }
}
