package com.mycompany.proyectotiendajuegos.aaron.franco.controladores;

import com.mycompany.proyectotiendajuegos.aaron.franco.DialogUtil;
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

public class GestionJuegosController implements Initializable {

    @FXML private TableView<Juego>         tablaJuegos;
    @FXML private TableColumn<Juego,String> colId, colTitulo, colGenero, colPlat,
                                             colPrecio, colStock, colDirector;
    @FXML private TableColumn<Juego,Void>   colAcciones;

    @FXML private Label         lblTitulo, lblError;
    @FXML private TextField     txTitulo, txGenero, txPlat, txPrecio, txStock, txDirector;
    @FXML private ComboBox<Estudio> cbEstudio;

    private final GestorDatos gd = GestorDatos.getInstance();
    private Juego enEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getIdJuego())));
        colTitulo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTitulo()));
        colGenero.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getGenero()));
        colPlat.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPlataforma()));
        colPrecio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.format("%.2f€", c.getValue().getPrecio())));
        colStock.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getStock())));
        colDirector.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDirector()));

        colAcciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnEdit = new Button("✏");
            final Button btnDel  = new Button("🗑");
            {
                btnEdit.getStyleClass().add("btn-secondary");
                btnDel.getStyleClass().add("btn-danger");
                btnEdit.setOnAction(e -> prepararEdicion(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> {
                    Juego j = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Eliminar '" + j.getTitulo() + "'?")) {
                        gd.bajaJuego(j.getIdJuego()); cargar();
                    }
                });
            }
            @Override protected void updateItem(Void i, boolean vacio) {
                super.updateItem(i, vacio);
                setGraphic(vacio ? null : new HBox(5, btnEdit, btnDel));
            }
        });

        cbEstudio.setItems(FXCollections.observableArrayList(gd.getEstudios()));
        cargar();
        limpiar();
    }

    private void cargar() {
        tablaJuegos.setItems(FXCollections.observableArrayList(gd.getJuegos()));
    }

    @FXML public void prepararAlta() {
        enEdicion = null;
        lblTitulo.setText("Nuevo Juego");
        txTitulo.clear(); txGenero.clear(); txPlat.clear();
        txPrecio.clear(); txStock.clear(); txDirector.clear();
        cbEstudio.getSelectionModel().clearSelection();
        lblError.setText("");
    }

    private void prepararEdicion(Juego j) {
        enEdicion = j;
        lblTitulo.setText("Editar Juego");
        txTitulo.setText(j.getTitulo());
        txGenero.setText(j.getGenero());
        txPlat.setText(j.getPlataforma());
        txPrecio.setText(String.valueOf(j.getPrecio()));
        txStock.setText(String.valueOf(j.getStock()));
        txDirector.setText(j.getDirector());
        // Seleccionar estudio actual
        gd.getEstudios().stream()
                .filter(e -> e.getJuegos().stream().anyMatch(jj -> jj.getIdJuego() == j.getIdJuego()))
                .findFirst().ifPresent(cbEstudio.getSelectionModel()::select);
        lblError.setText("");
    }

    @FXML public void guardar() {
        if (txTitulo.getText().trim().isEmpty()) { lblError.setText("El título es obligatorio."); return; }
        double precio; int stock;
        try { precio = Double.parseDouble(txPrecio.getText().replace(",", ".")); }
        catch (Exception e) { lblError.setText("Precio inválido."); return; }
        try { stock = Integer.parseInt(txStock.getText()); }
        catch (Exception e) { lblError.setText("Stock inválido."); return; }

        if (enEdicion == null) {
            Juego j = gd.altaJuego(txTitulo.getText().trim(), txGenero.getText().trim(),
                    txPlat.getText().trim(), precio, stock, txDirector.getText().trim());
            if (j != null && cbEstudio.getValue() != null)
                gd.asignarJuegoAEstudio(j.getIdJuego(), cbEstudio.getValue().getIdEstudio());
            DialogUtil.info("Juego creado.");
        } else {
            enEdicion.setTitulo(txTitulo.getText().trim());
            enEdicion.setGenero(txGenero.getText().trim());
            enEdicion.setPlataforma(txPlat.getText().trim());
            enEdicion.setPrecio(precio);
            enEdicion.setStock(stock);
            enEdicion.setDirector(txDirector.getText().trim());
            gd.actualizarJuego(enEdicion);
            if (cbEstudio.getValue() != null)
                gd.asignarJuegoAEstudio(enEdicion.getIdJuego(), cbEstudio.getValue().getIdEstudio());
            DialogUtil.info("Juego actualizado.");
        }
        limpiar();
        cargar();
    }

    @FXML public void cancelar() { limpiar(); }

    private void limpiar() {
        enEdicion = null;
        lblTitulo.setText("Nuevo Juego");
        txTitulo.clear(); txGenero.clear(); txPlat.clear();
        txPrecio.clear(); txStock.clear(); txDirector.clear();
        cbEstudio.getSelectionModel().clearSelection();
        lblError.setText("");
    }

    @FXML public void cerrar() {
        ((Stage) tablaJuegos.getScene().getWindow()).close();
    }
}
