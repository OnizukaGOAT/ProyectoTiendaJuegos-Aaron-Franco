package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.DialogUtil;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.GestorDatos;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Resena;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GestionResenasController implements Initializable {

    @FXML private TableView<Resena>          tablaResenas;
    @FXML private TableColumn<Resena, String> colId, colJuego, colAutor, colPunt,
                                              colIdioma, colFecha, colCom;
    @FXML private TableColumn<Resena, Void>   colAcciones;
    @FXML private TextField txFiltro;
    @FXML private Label     lblTotal;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getIdResena())));
        colJuego.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getJuego().getTitulo()));
        colAutor.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getAutor().getNombreCompleto()));
        colPunt.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getPuntuacion())));
        colIdioma.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getIdioma()));
        colFecha.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFechaFormateada()));
        colCom.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getComentario()));

        colAcciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnDel = new Button("🗑 Eliminar");
            {
                btnDel.getStyleClass().add("btn-danger");
                btnDel.setOnAction(e -> {
                    Resena r = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Eliminar esta reseña?")) {
                        gd.eliminarResena(r.getIdResena());
                        cargar(null);
                    }
                });
            }
            @Override protected void updateItem(Void i, boolean vacio) {
                super.updateItem(i, vacio);
                setGraphic(vacio ? null : btnDel);
            }
        });

        cargar(null);
    }

    private void cargar(String filtro) {
        List<Resena> todas = gd.getResenas();
        if (filtro != null && !filtro.isEmpty()) {
            String f = filtro.toLowerCase();
            todas = todas.stream()
                    .filter(r -> r.getJuego().getTitulo().toLowerCase().contains(f)
                            || r.getAutor().getNombreCompleto().toLowerCase().contains(f))
                    .collect(Collectors.toList());
        }
        tablaResenas.setItems(FXCollections.observableArrayList(todas));
        lblTotal.setText("Total reseñas: " + todas.size());
    }

    @FXML public void filtrar()      { cargar(txFiltro.getText()); }
    @FXML public void mostrarTodas() { txFiltro.clear(); cargar(null); }

    @FXML public void cerrar() {
        ((Stage) tablaResenas.getScene().getWindow()).close();
    }
}
