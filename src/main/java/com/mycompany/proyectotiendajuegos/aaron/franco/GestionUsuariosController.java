package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.DialogUtil;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
/**
 * FXML Controller class
 *
 * @author USUARIO
 */
public class GestionUsuariosController implements Initializable {

    @FXML private TableView<Usuario>           tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colSaldo;
    @FXML private TableColumn<Usuario, Void>   colAcciones;

    // Detalle
    @FXML private Label lblDetNombre;
    @FXML private Label lblDetCorreo;
    @FXML private Label lblDetIdioma;
    @FXML private Label lblDetSaldo;
    @FXML private Label lblDetJuegos;
    @FXML private Label lblDetCompras;
    @FXML private Label lblDetResenas;

    // Formulario
    @FXML private Label         lblTituloForm;
    @FXML private TextField     txNombre;
    @FXML private TextField     txApellidos;
    @FXML private TextField     txCorreo;
    @FXML private PasswordField txContrasena;
    @FXML private TextField     txSaldo;
    @FXML private ComboBox<String> cbIdioma;
    @FXML private Label         lblError;

    private final GestorDatos gd = GestorDatos.getInstance();
    private Usuario usuarioEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbIdioma.setItems(FXCollections.observableArrayList(
                "Español", "English", "Français", "Deutsch", "Português", "Italiano"));
        configurarTabla();
        cargar();
        limpiarFormulario();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getIdUsuario())));
        colNombre.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colCorreo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getCorreo()));
        colSaldo.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f€", c.getValue().getSaldo())));

        colAcciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnVer    = new Button("🔍");
            final Button btnEditar = new Button("✏");
            final Button btnBaja   = new Button("🗑");
            {
                btnVer.getStyleClass().add("btn-gold");
                btnEditar.getStyleClass().add("btn-secondary");
                btnBaja.getStyleClass().add("btn-danger");
                btnVer.setOnAction(e    -> mostrarDetalle(getTableView().getItems().get(getIndex())));
                btnEditar.setOnAction(e -> prepararEdicion(getTableView().getItems().get(getIndex())));
                btnBaja.setOnAction(e   -> darDeBaja(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : new HBox(4, btnVer, btnEditar, btnBaja));
            }
        });

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, ant, nuevo) -> { if (nuevo != null) mostrarDetalle(nuevo); });
    }

    private void cargar() {
        tablaUsuarios.setItems(FXCollections.observableArrayList(gd.getUsuarios()));
    }

    private void mostrarDetalle(Usuario u) {
        List<Juego>  bib     = gd.getBibliotecaUsuario(u.getIdUsuario());
        List<Compra> compras = gd.getComprasUsuario(u.getIdUsuario());
        List<Resena> resenas = gd.getResenasPorUsuario(u);
        lblDetNombre.setText(u.getNombreCompleto());
        lblDetCorreo.setText(u.getCorreo());
        lblDetIdioma.setText(u.getIdioma());
        lblDetSaldo.setText(String.format("%.2f€", u.getSaldo()));
        lblDetJuegos.setText(String.valueOf(bib.size()));
        lblDetCompras.setText(String.valueOf(compras.size()));
        lblDetResenas.setText(String.valueOf(resenas.size()));
    }

    @FXML public void prepararAlta() {
        usuarioEnEdicion = null;
        limpiarFormulario();
    }

    private void prepararEdicion(Usuario u) {
        usuarioEnEdicion = u;
        lblTituloForm.setText("Editar Usuario");
        txNombre.setText(u.getNombre());
        txApellidos.setText(u.getApellidos());
        txCorreo.setText(u.getCorreo());
        txContrasena.clear();
        txContrasena.setPromptText("(vacío = sin cambios)");
        txSaldo.setText(String.valueOf(u.getSaldo()));
        cbIdioma.getSelectionModel().select(u.getIdioma());
        lblError.setText("");
    }

    @FXML public void guardar() {
        String nombre    = txNombre.getText().trim();
        String apellidos = txApellidos.getText().trim();
        String correo    = txCorreo.getText().trim();
        String pass      = txContrasena.getText();
        String idioma    = cbIdioma.getValue();
        if (nombre.isEmpty()) { lblError.setText("El nombre es obligatorio."); return; }
        double saldo;
        try { saldo = Double.parseDouble(txSaldo.getText().replace(",", ".")); }
        catch (NumberFormatException ex) { lblError.setText("Saldo inválido."); return; }

        if (usuarioEnEdicion == null) {
            if (pass.isEmpty()) { lblError.setText("La contraseña es obligatoria."); return; }
            if (!gd.altaUsuario(nombre, apellidos, correo, pass, saldo, idioma)) {
                lblError.setText("El correo ya existe."); return;
            }
            DialogUtil.info("Usuario creado correctamente.");
        } else {
            usuarioEnEdicion.setNombre(nombre); usuarioEnEdicion.setApellidos(apellidos);
            usuarioEnEdicion.setCorreo(correo); usuarioEnEdicion.setSaldo(saldo);
            usuarioEnEdicion.setIdioma(idioma);
            if (!pass.isEmpty()) usuarioEnEdicion.setContrasena(pass);
            gd.actualizarUsuario(usuarioEnEdicion);
            DialogUtil.info("Usuario actualizado correctamente.");
        }
        limpiarFormulario();
        cargar();
    }

    private void darDeBaja(Usuario u) {
        if (DialogUtil.confirmar("¿Dar de baja a " + u.getNombreCompleto() + "?")) {
            gd.bajaUsuario(u.getIdUsuario());
            cargar();
            limpiarFormulario();
        }
    }

    @FXML public void cancelar() { limpiarFormulario(); }

    private void limpiarFormulario() {
        usuarioEnEdicion = null;
        lblTituloForm.setText("Nuevo Usuario");
        txNombre.clear(); txApellidos.clear(); txCorreo.clear();
        txContrasena.clear(); txSaldo.setText("50");
        txContrasena.setPromptText("Contraseña (obligatoria)");
        cbIdioma.getSelectionModel().selectFirst();
        lblError.setText("");
    }

    @FXML public void cerrar() {
        ((Stage) tablaUsuarios.getScene().getWindow()).close();
    }
}
