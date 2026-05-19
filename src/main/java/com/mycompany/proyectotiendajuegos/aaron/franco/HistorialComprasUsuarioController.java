package com.mycompany.proyectotiendajuegos.aaron.franco.controladores;

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
 * Controlador de la ventana Historial de Compras (usuario).
 */
public class HistorialComprasUsuarioController implements Initializable {

    @FXML private TableView<Compra>           tablaCompras;
    @FXML private TableColumn<Compra, String> colJuego;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colCantidad;
    @FXML private TableColumn<Compra, String> colCoste;
    @FXML private Label lblTotalGastado;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colJuego.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getJuego().getTitulo()));
        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFechaFormateada()));
        colCantidad.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getCantidad())));
        colCoste.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f€", c.getValue().getCoste())));

        cargar();
    }

    private void cargar() {
        List<Compra> compras = gd.getComprasUsuario(gd.getUsuarioActual().getIdUsuario());
        tablaCompras.setItems(FXCollections.observableArrayList(compras));
        double total = compras.stream().mapToDouble(Compra::getCoste).sum();
        lblTotalGastado.setText(String.format("Total gastado: %.2f€", total));
    }
}
