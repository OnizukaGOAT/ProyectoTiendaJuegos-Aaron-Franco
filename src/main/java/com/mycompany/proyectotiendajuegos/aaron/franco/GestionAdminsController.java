package com.mycompany.proyectotiendajuegos.aaron.franco;

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

/**
 * Controlador de la ventana de Gestión de Administradores.
 */
public class GestionAdminsController implements Initializable {

    // ── Tabla ──────────────────────────────────────────────
    @FXML private TableView<Administrador>          tablaAdmins;
    @FXML private TableColumn<Administrador, String> colId;
    @FXML private TableColumn<Administrador, String> colNombre;
    @FXML private TableColumn<Administrador, String> colCorreo;
    @FXML private TableColumn<Administrador, Void>   colAcciones;

    // ── Formulario ─────────────────────────────────────────
    @FXML private Label         lblTituloForm;
    @FXML private TextField     txNombre;
    @FXML private TextField     txApellidos;
    @FXML private TextField     txCorreo;
    @FXML private PasswordField txContrasena;
    @FXML private Label         lblError;

    private final GestorDatos gd = GestorDatos.getInstance();
    private Administrador adminEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarAdmins();
        limpiarFormulario();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getIdAdmin())));
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
                btnBaja.setOnAction(e   -> darDeBaja(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : new HBox(5, btnEditar, btnBaja));
            }
        });
    }

    private void cargarAdmins() {
        tablaAdmins.setItems(FXCollections.observableArrayList(gd.getAdministradores()));
    }

    // ── Formulario alta / edición ──────────────────────────
    @FXML
    public void prepararAlta() {
        adminEnEdicion = null;
        limpiarFormulario();
        lblTituloForm.setText("Nuevo Administrador");
    }

    private void prepararEdicion(Administrador a) {
        adminEnEdicion = a;
        lblTituloForm.setText("Editar Administrador");
        txNombre.setText(a.getNombre());
        txApellidos.setText(a.getApellidos());
        txCorreo.setText(a.getCorreo());
        txContrasena.clear();
        txContrasena.setPromptText("(dejar vacío = sin cambios)");
        lblError.setText("");
    }

    @FXML
    public void guardar() {
        String nombre    = txNombre.getText().trim();
        String apellidos = txApellidos.getText().trim();
        String correo    = txCorreo.getText().trim();
        String pass      = txContrasena.getText();

        if (nombre.isEmpty()) { lblError.setText("El nombre es obligatorio."); return; }

        if (adminEnEdicion == null) {
            if (pass.isEmpty()) { lblError.setText("La contraseña es obligatoria."); return; }
            boolean ok = gd.altaAdmin(nombre, apellidos, correo, pass);
            if (!ok) { lblError.setText("El correo ya existe."); return; }
            DialogUtil.info("Administrador creado correctamente.");
        } else {
            adminEnEdicion.setNombre(nombre);
            adminEnEdicion.setApellidos(apellidos);
            adminEnEdicion.setCorreo(correo);
            if (!pass.isEmpty()) adminEnEdicion.setContrasena(pass);
            gd.actualizarAdmin(adminEnEdicion);
            DialogUtil.info("Administrador actualizado correctamente.");
        }
        limpiarFormulario();
        cargarAdmins();
    }

    private void darDeBaja(Administrador a) {
        if (DialogUtil.confirmar("¿Dar de baja a " + a.getNombreCompleto() + "?")) {
            gd.bajaAdmin(a.getIdAdmin());
            cargarAdmins();
            limpiarFormulario();
        }
    }

    @FXML
    public void cancelar() { limpiarFormulario(); }

    private void limpiarFormulario() {
        adminEnEdicion = null;
        lblTituloForm.setText("Nuevo Administrador");
        txNombre.clear(); txApellidos.clear(); txCorreo.clear(); txContrasena.clear();
        txContrasena.setPromptText("Contraseña (obligatoria)");
        lblError.setText("");
    }

    @FXML
    public void cerrar() {
        ((Stage) tablaAdmins.getScene().getWindow()).close();
    }
}
