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
 * FXML Controller class
 *
 * @author USUARIO
 */
public class BibliotecaController implements Initializable {


    @FXML private Label lblContador;
    @FXML private TableView<Juego> tablaJuegos;
    @FXML private TableColumn<Juego,String> colTitulo;
    @FXML private TableColumn<Juego,String> colGenero;
    @FXML private TableColumn<Juego,String> colPlataforma;
    @FXML private TableColumn<Juego,String> colDirector;
    @FXML private TableColumn<Juego,String> colMedia;
    @FXML private javafx.scene.layout.VBox panelDetalle;
    @FXML private javafx.scene.layout.VBox panelVacio;
    @FXML private Label  lblDetTitulo;
    @FXML private Label  lblDetGenero;
    @FXML private Label  lblDetPlat;
    @FXML private Label  lblDetDirector;
    @FXML private Label  lblDetMedia;
    @FXML private Button btnVerResenas;
    @FXML private Button btnResenar;

    private final GestorDatos gd = GestorDatos.getInstance(); //final porque no debería poder editarse bajo ningún concepto

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();

        tablaJuegos.getSelectionModel().selectedItemProperty().addListener((obs, ant, nuevo) -> mostrarDetalle(nuevo));

        cargar();
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
    }

    private void cargar() {
        List<Juego> biblioteca = gd.getBibliotecaUsuario(gd.getUsuarioActual().getIdUsuario());
        tablaJuegos.setItems(FXCollections.observableArrayList(biblioteca));
        lblContador.setText(biblioteca.size() + " juego(s) en tu biblioteca");
        ocultarDetalle();
    }
    private void mostrarDetalle(Juego j) {
        if (j == null) { ocultarDetalle(); return; }

        lblDetTitulo.setText(j.getTitulo());
        lblDetGenero.setText(j.getGenero());
        lblDetPlat.setText(j.getPlataforma());
        lblDetDirector.setText(j.getDirector());
        double m = gd.getPuntuacionMediaJuego(j.getIdJuego());
        lblDetMedia.setText(m > 0 ? String.format("⭐ %.1f/10", m) : "Sin valoraciones");

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

    @FXML
    public void verResenasSeleccionado() {
        Juego j = tablaJuegos.getSelectionModel().getSelectedItem();
        if (j == null) return;
        VentanaUtil.abrirVentanaConDato("resenas_juego",
                "Reseñas – " + j.getTitulo(), 850, 580, j);
    }

    @FXML
    public void resenarSeleccionado() { VentanaUtil.abrirVentana("mis_resenas_usuario", "Mis Reseñas", 900, 650);}

    @FXML
    public void cerrar() {((Stage) tablaJuegos.getScene().getWindow()).close();}
}
