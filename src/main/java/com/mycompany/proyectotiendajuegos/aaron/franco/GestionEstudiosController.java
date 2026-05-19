package com.mycompany.proyectotiendajuegos.aaron.franco.controladores;

import com.mycompany.proyectotiendajuegos.aaron.franco.DialogUtil;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GestionEstudiosController implements Initializable {

    // ── Tabla estudios ─────────────────────────────────────
    @FXML private TableView<Estudio>         tablaEstudios;
    @FXML private TableColumn<Estudio,String> colEstId, colEstNombre, colEstJuegos, colEstDevs;
    @FXML private TableColumn<Estudio,Void>   colEstAcc;

    // ── Tabla desarrolladores ──────────────────────────────
    @FXML private TableView<Desarrollador>         tablaDevs;
    @FXML private TableColumn<Desarrollador,String> colDevId, colDevNombre, colDevPuesto, colDevAnos;
    @FXML private TableColumn<Desarrollador,Void>   colDevAcc;
    @FXML private Label lblDevEstudio;

    // ── Formulario ─────────────────────────────────────────
    @FXML private Label   lblFormTitulo, lblError;
    @FXML private VBox    panelEstudio, panelDev;

    // Campos estudio
    @FXML private TextField txEstNombre;

    // Campos desarrollador
    @FXML private TextField txDevNombre, txDevApellidos, txDevPuesto, txDevAnos;
    @FXML private ListView<String> lstJuegosDev;   // muestra títulos, selección múltiple

    private final GestorDatos gd = GestorDatos.getInstance();
    private Estudio     estudioSeleccionado = null;
    private Estudio     estudioEnEdicion    = null;
    private Desarrollador devEnEdicion      = null;
    private boolean modoEstudio = true;  // true = form para estudio, false = form para dev

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablaEstudios();
        configurarTablaDevs();
        cargarEstudios();
        mostrarFormEstudio();
    }

    // ── Configuración de tablas ────────────────────────────
    private void configurarTablaEstudios() {
        colEstId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getIdEstudio())));
        colEstNombre.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colEstJuegos.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getJuegos().size())));
        colEstDevs.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getDesarrolladores().size())));

        colEstAcc.setCellFactory(tc -> new TableCell<>() {
            final Button btnEdit = new Button("✏");
            final Button btnDel  = new Button("🗑");
            {
                btnEdit.getStyleClass().add("btn-secondary");
                btnDel.getStyleClass().add("btn-danger");
                btnEdit.setOnAction(e -> prepararEdicionEstudio(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> {
                    Estudio est = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Eliminar estudio '" + est.getNombre() + "'?")) {
                        gd.bajaEstudio(est.getIdEstudio());
                        cargarEstudios();
                    }
                });
            }
            @Override protected void updateItem(Void i, boolean vacio) {
                super.updateItem(i, vacio);
                setGraphic(vacio ? null : new HBox(4, btnEdit, btnDel));
            }
        });

        // Al seleccionar un estudio se cargan sus devs
        tablaEstudios.getSelectionModel().selectedItemProperty().addListener((obs, ant, nuevo) -> {
            if (nuevo != null) {
                estudioSeleccionado = nuevo;
                lblDevEstudio.setText("Desarrolladores de: " + nuevo.getNombre());
                tablaDevs.setItems(FXCollections.observableArrayList(nuevo.getDesarrolladores()));
            }
        });
    }

    private void configurarTablaDevs() {
        colDevId.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getIdDesarrollador())));
        colDevNombre.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreCompleto()));
        colDevPuesto.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getPuestoActual()));
        colDevAnos.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getAnosExperiencia())));

        colDevAcc.setCellFactory(tc -> new TableCell<>() {
            final Button btnEdit = new Button("✏");
            final Button btnDel  = new Button("🗑");
            {
                btnEdit.getStyleClass().add("btn-secondary");
                btnDel.getStyleClass().add("btn-danger");
                btnEdit.setOnAction(e -> prepararEdicionDev(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> {
                    Desarrollador d = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Dar de baja a " + d.getNombreCompleto() + "?")) {
                        gd.bajaDesarrollador(d.getIdDesarrollador());
                        cargarEstudios();
                    }
                });
            }
            @Override protected void updateItem(Void i, boolean vacio) {
                super.updateItem(i, vacio);
                setGraphic(vacio ? null : new HBox(4, btnEdit, btnDel));
            }
        });
    }

    // ── Carga de datos ─────────────────────────────────────
    private void cargarEstudios() {
        List<Estudio> lista = gd.getEstudios();
        tablaEstudios.setItems(FXCollections.observableArrayList(lista));
        tablaDevs.getItems().clear();
        // Restaurar selección si la había
        if (estudioSeleccionado != null) {
            lista.stream().filter(e -> e.getIdEstudio() == estudioSeleccionado.getIdEstudio())
                    .findFirst().ifPresent(e -> {
                        estudioSeleccionado = e;
                        tablaEstudios.getSelectionModel().select(e);
                    });
        }
    }

    // ── Formulario ESTUDIO ─────────────────────────────────
    @FXML public void prepararAltaEstudio() {
        estudioEnEdicion = null;
        mostrarFormEstudio();
        txEstNombre.clear();
        lblFormTitulo.setText("Nuevo Estudio");
        lblError.setText("");
    }

    private void prepararEdicionEstudio(Estudio est) {
        estudioEnEdicion = est;
        mostrarFormEstudio();
        txEstNombre.setText(est.getNombre());
        lblFormTitulo.setText("Editar Estudio");
        lblError.setText("");
    }

    // ── Formulario DESARROLLADOR ───────────────────────────
    @FXML public void prepararAltaDev() {
        if (estudioSeleccionado == null) {
            DialogUtil.info("Selecciona primero un estudio.");
            return;
        }
        devEnEdicion = null;
        mostrarFormDev();
        txDevNombre.clear(); txDevApellidos.clear(); txDevPuesto.clear(); txDevAnos.clear();
        cargarCheckboxJuegos(null);
        lblFormTitulo.setText("Nuevo Desarrollador");
        lblError.setText("");
    }

    private void prepararEdicionDev(Desarrollador d) {
        devEnEdicion = d;
        mostrarFormDev();
        txDevNombre.setText(d.getNombre());
        txDevApellidos.setText(d.getApellidos());
        txDevPuesto.setText(d.getPuestoActual());
        txDevAnos.setText(String.valueOf(d.getAnosExperiencia()));
        cargarCheckboxJuegos(gd.getJuegosDesarrollador(d.getIdDesarrollador()));
        lblFormTitulo.setText("Editar Desarrollador");
        lblError.setText("");
    }

    private void cargarCheckboxJuegos(List<Juego> seleccionados) {
        // Usamos ListView con selección múltiple
        lstJuegosDev.setItems(FXCollections.observableArrayList(
                gd.getJuegos().stream().map(Juego::getTitulo).collect(java.util.stream.Collectors.toList())));
        lstJuegosDev.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (seleccionados != null) {
            List<Juego> todos = gd.getJuegos();
            for (int i = 0; i < todos.size(); i++) {
                final int idx = i;
                if (seleccionados.stream().anyMatch(s -> s.getIdJuego() == todos.get(idx).getIdJuego()))
                    lstJuegosDev.getSelectionModel().select(i);
            }
        }
    }

    // ── Guardar (estudio o dev según modo) ─────────────────
    @FXML public void guardar() {
        if (modoEstudio) guardarEstudio();
        else             guardarDev();
    }

    private void guardarEstudio() {
        String nombre = txEstNombre.getText().trim();
        if (nombre.isEmpty()) { lblError.setText("El nombre es obligatorio."); return; }
        if (estudioEnEdicion == null) {
            gd.altaEstudio(nombre);
            DialogUtil.info("Estudio creado.");
        } else {
            estudioEnEdicion.setNombre(nombre);
            gd.actualizarEstudio(estudioEnEdicion);
            DialogUtil.info("Estudio actualizado.");
        }
        limpiar();
        cargarEstudios();
    }

    private void guardarDev() {
        String nombre = txDevNombre.getText().trim();
        if (nombre.isEmpty()) { lblError.setText("El nombre es obligatorio."); return; }
        int anos = 0;
        try { anos = Integer.parseInt(txDevAnos.getText()); } catch (Exception ignored) {}

        // Ids de juegos seleccionados
        List<Juego> todosJuegos = gd.getJuegos();
        List<Integer> idsJuegos = new ArrayList<>();
        for (int idx : lstJuegosDev.getSelectionModel().getSelectedIndices())
            idsJuegos.add(todosJuegos.get(idx).getIdJuego());

        if (devEnEdicion == null) {
            Desarrollador d = gd.altaDesarrollador(nombre, txDevApellidos.getText().trim(),
                    anos, txDevPuesto.getText().trim(), estudioSeleccionado);
            if (d != null) gd.setJuegosDesarrollador(d.getIdDesarrollador(), idsJuegos);
            DialogUtil.info("Desarrollador creado.");
        } else {
            devEnEdicion.setNombre(nombre);
            devEnEdicion.setApellidos(txDevApellidos.getText().trim());
            devEnEdicion.setPuestoActual(txDevPuesto.getText().trim());
            devEnEdicion.setAnosExperiencia(anos);
            gd.actualizarDesarrollador(devEnEdicion);
            gd.setJuegosDesarrollador(devEnEdicion.getIdDesarrollador(), idsJuegos);
            DialogUtil.info("Desarrollador actualizado.");
        }
        limpiar();
        cargarEstudios();
    }

    @FXML public void cancelar() { limpiar(); }

    private void limpiar() {
        estudioEnEdicion = null; devEnEdicion = null;
        txEstNombre.clear(); txDevNombre.clear(); txDevApellidos.clear();
        txDevPuesto.clear(); txDevAnos.clear(); lstJuegosDev.getSelectionModel().clearSelection();
        lblError.setText("");
        mostrarFormEstudio();
    }

    // ── Cambio de formulario visible ───────────────────────
    private void mostrarFormEstudio() {
        modoEstudio = true;
        panelEstudio.setVisible(true);  panelEstudio.setManaged(true);
        panelDev.setVisible(false);     panelDev.setManaged(false);
        lblFormTitulo.setText("Nuevo Estudio");
    }

    private void mostrarFormDev() {
        modoEstudio = false;
        panelEstudio.setVisible(false); panelEstudio.setManaged(false);
        panelDev.setVisible(true);      panelDev.setManaged(true);
    }

    @FXML public void cerrar() {
        ((Stage) tablaEstudios.getScene().getWindow()).close();
    }
}
