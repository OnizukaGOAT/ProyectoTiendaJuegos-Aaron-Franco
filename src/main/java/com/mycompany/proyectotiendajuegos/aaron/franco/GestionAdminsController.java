package com.mycompany.proyectotiendajuegos.aaron.franco.controladores;

import com.mycompany.proyectotiendajuegos.aaron.franco.DialogUtil;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Administrador;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.GestorDatos;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GestionAdminsController implements Initializable {

    @FXML private TableView<Administrador>         tablaAdmins;
    @FXML private TableColumn<Administrador,String> colId, colNombre, colCorreo;
    @FXML private TableColumn<Administrador,Void>   colAcciones;

    @FXML private Label         lblTitulo, lblError;
    @FXML private TextField     txNombre, txApellidos, txCorreo;
    @FXML private PasswordField txContrasena;

    private final GestorDatos gd = GestorDatos.getInstance();
    private Administrador enEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getIdAdmin())));
        colNombre.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreCompleto()));
        colCorreo.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getCorreo()));

        colAcciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnEditar = new Button("✏ Editar");
            final Button btnBaja   = new Button("🗑 Baja");
            {
                btnEditar.getStyleClass().add("btn-secondary");
                btnBaja.getStyleClass().add("btn-danger");
                btnEditar.setOnAction(e -> prepararEdicion(getTableView().getItems().get(getIndex())));
                btnBaja.setOnAction(e -> {
                    Administrador a = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Dar de baja a " + a.getNombreCompleto() + "?")) {
                        gd.bajaAdmin(a.getIdAdmin());
                        cargar();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : new HBox(5, btnEditar, btnBaja));
            }
        });

        cargar();
        limpiar();
    }

    private void cargar() {
        tablaAdmins.setItems(FXCollections.observableArrayList(gd.getAdministradores()));
    }

    @FXML public void prepararAlta() {
        enEdicion = null;
        lblTitulo.setText("Nuevo Administrador");
        txNombre.clear(); txApellidos.clear(); txCorreo.clear(); txContrasena.clear();
        lblError.setText("");
    }

    private void prepararEdicion(Administrador a) {
        enEdicion = a;
        lblTitulo.setText("Editar Administrador");
        txNombre.setText(a.getNombre());
        txApellidos.setText(a.getApellidos());
        txCorreo.setText(a.getCorreo());
        txContrasena.clear();
        txContrasena.setPromptText("(dejar vacío = sin cambios)");
        lblError.setText("");
    }

    @FXML public void guardar() {
        String nombre = txNombre.getText().trim();
        if (nombre.isEmpty()) { lblError.setText("El nombre es obligatorio."); return; }

        if (enEdicion == null) {
            if (txContrasena.getText().isEmpty()) { lblError.setText("La contraseña es obligatoria."); return; }
            boolean ok = gd.altaAdmin(nombre, txApellidos.getText().trim(),
                    txCorreo.getText().trim(), txContrasena.getText());
            if (!ok) { lblError.setText("El correo ya existe."); return; }
            DialogUtil.info("Administrador creado.");
        } else {
            enEdicion.setNombre(nombre);
            enEdicion.setApellidos(txApellidos.getText().trim());
            enEdicion.setCorreo(txCorreo.getText().trim());
            if (!txContrasena.getText().isEmpty()) enEdicion.setContrasena(txContrasena.getText());
            gd.actualizarAdmin(enEdicion);
            DialogUtil.info("Administrador actualizado.");
        }
        limpiar();
        cargar();
    }

    @FXML public void cancelar() { limpiar(); }

    private void limpiar() {
        enEdicion = null;
        lblTitulo.setText("Nuevo Administrador");
        txNombre.clear(); txApellidos.clear(); txCorreo.clear();
        txContrasena.clear(); txContrasena.setPromptText("Contraseña");
        lblError.setText("");
    }

    @FXML public void cerrar() {
        ((Stage) tablaAdmins.getScene().getWindow()).close();
    }
}
