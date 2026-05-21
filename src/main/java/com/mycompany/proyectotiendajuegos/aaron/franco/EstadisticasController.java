package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author USUARIO
 */
public class EstadisticasController implements Initializable {

    @FXML private TableView<Juego>          tablaVentas;
    @FXML private TableColumn<Juego,String> colVTitulo;
    @FXML private TableColumn<Juego,String> colVVentas;
    @FXML private TableColumn<Juego,String> colVIngresos;
    @FXML private TableColumn<Juego,String> colVBarra;
    @FXML private TableView<Juego>          tablaMasVendidos;
    @FXML private TableColumn<Juego,String> colMVPos;
    @FXML private TableColumn<Juego,String> colMVTitulo;
    @FXML private TableColumn<Juego,String> colMVVentas;
    @FXML private TableColumn<Juego,String> colMVIngresos;
    @FXML private TableView<Juego>          tablaMejorVal;
    @FXML private TableColumn<Juego,String> colBVPos;
    @FXML private TableColumn<Juego,String> colBVTitulo;
    @FXML private TableColumn<Juego,String> colBVMedia;
    @FXML private TableColumn<Juego,String> colBVResenas;
    @FXML private TableColumn<Juego,String> colBVDir;
    @FXML private TableView<Juego>           tablaResJuego;
    @FXML private TableColumn<Juego,String>  colRJTitulo;
    @FXML private TableColumn<Juego,String>  colRJMedia;
    @FXML private TableColumn<Juego,String>  colRJTotal;
    @FXML private Label                      lblResJuegoSel;
    @FXML private TableView<Resena>          tablaResDetJuego;
    @FXML private TableColumn<Resena,String> colRJDAutor;
    @FXML private TableColumn<Resena,String> colRJDPunt;
    @FXML private TableColumn<Resena,String> colRJDIdioma;
    @FXML private TableColumn<Resena,String> colRJDCom;
    @FXML private TableView<Usuario>          tablaResUsuario;
    @FXML private TableColumn<Usuario,String> colRUNombre;
    @FXML private TableColumn<Usuario,String> colRUTotal;
    @FXML private Label                       lblResUsuarioSel;
    @FXML private TableView<Resena>           tablaResDetUsuario;
    @FXML private TableColumn<Resena,String>  colRUDJuego;
    @FXML private TableColumn<Resena,String>  colRUDPunt;
    @FXML private TableColumn<Resena,String>  colRUDIdioma;
    @FXML private TableColumn<Resena,String>  colRUDCom;
    private static final String[] IDIOMAS ={"Español","English","Français","Deutsch","Português","Italiano"};
    @FXML private TableView<String>          tablaResIdioma;
    @FXML private TableColumn<String,String> colRIIdioma;
    @FXML private TableColumn<String,String> colRITotal;
    @FXML private Label                      lblResIdiomaSel;
    @FXML private TableView<Resena>          tablaResDetIdioma;
    @FXML private TableColumn<Resena,String> colRIDJuego;
    @FXML private TableColumn<Resena,String> colRIDAutor;
    @FXML private TableColumn<Resena,String> colRIDPunt;
    @FXML private TableColumn<Resena,String> colRIDCom;
    @FXML private TableView<Usuario>          tablaJuegosUsuario;
    @FXML private TableColumn<Usuario,String> colJUNombre;
    @FXML private TableColumn<Usuario,String> colJUTotal;
    @FXML private Label                       lblJuegosUsuarioSel;
    @FXML private TableView<Juego>            tablaJuegosDetUsuario;
    @FXML private TableColumn<Juego,String>   colJUDTitulo;
    @FXML private TableColumn<Juego,String>   colJUDGenero;
    @FXML private TableColumn<Juego,String>   colJUDPlat;
    @FXML private TableView<Estudio>             tablaEstudios;
    @FXML private TableColumn<Estudio,String>    colEstNombre;
    @FXML private VBox                           panelEstudioDetalle;
    @FXML private VBox                           panelEstudioVacio;
    @FXML private Label                          lblEstudioNombre;
    @FXML private Label                          lblEstMejorVal;
    @FXML private Label                          lblEstMasVend;
    @FXML private TableView<Desarrollador>          tablaDevEstudio;
    @FXML private TableColumn<Desarrollador,String> colDevNombre;
    @FXML private TableColumn<Desarrollador,String> colDevPuesto;
    @FXML private TableColumn<Desarrollador,String> colDevMejorVal;
    @FXML private TableColumn<Desarrollador,String> colDevMasVend;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarVentas();
        inicializarMasVendidos();
        inicializarMejorValorados();
        inicializarResJuego();
        inicializarResUsuario();
        inicializarResIdioma();
        inicializarJuegosUsuario();
        inicializarEstudios();
    }
    private void inicializarVentas() {
        List<Juego> juegos = gd.getJuegos();
        int maxV = juegos.stream().mapToInt(gd::getVentasJuego).max().orElse(1);

        colVTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colVVentas.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(gd.getVentasJuego(c.getValue()))));
        colVIngresos.setCellValueFactory(c -> { int v = gd.getVentasJuego(c.getValue());
            return new SimpleStringProperty(String.format("%.2f€", v * c.getValue().getPrecio()));
        });
        colVBarra.setCellValueFactory(c -> {
            int v = gd.getVentasJuego(c.getValue());
            int barras = maxV > 0 ? (int) Math.round(20.0 * v / maxV) : 0;
            return new SimpleStringProperty("█".repeat(barras));
        });
        colVBarra.setStyle("-fx-text-fill:#e94560;");

        tablaVentas.setItems(FXCollections.observableArrayList(juegos));
    }

    private void inicializarMasVendidos() {
        List<Juego> lista = gd.getJuegosMasVendidos();
        colMVPos.setCellValueFactory(c -> {int idx = tablaMasVendidos.getItems().indexOf(c.getValue()) + 1; // idx de index, que no se me olvide, que quien me manda a mi a poner esos nombres
            return new SimpleStringProperty("#" + idx);
        });
        colMVTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colMVVentas.setCellValueFactory(c -> new SimpleStringProperty(gd.getVentasJuego(c.getValue()) + " uds"));
        colMVIngresos.setCellValueFactory(c -> {
            int v = gd.getVentasJuego(c.getValue());
            return new SimpleStringProperty(String.format("%.2f€", v * c.getValue().getPrecio()));
        });
        tablaMasVendidos.setItems(FXCollections.observableArrayList(lista));
    }
    private void inicializarMejorValorados() {
        List<Juego> lista = gd.getJuegosMejorValorados();
        colBVPos.setCellValueFactory(c -> {int idx = tablaMejorVal.getItems().indexOf(c.getValue()) + 1;
            return new SimpleStringProperty("#" + idx);
        });
        colBVTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colBVMedia.setCellValueFactory(c -> { double m = gd.getPuntuacionMediaJuego(c.getValue().getIdJuego());
            return new SimpleStringProperty(String.format("%.1f/10", m));
        });
        colBVResenas.setCellValueFactory(c -> new SimpleStringProperty( String.valueOf(gd.getResenasPorJuego(c.getValue()).size())));
        colBVDir.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDirector()));
        tablaMejorVal.setItems(FXCollections.observableArrayList(lista));
    }

    private void inicializarResJuego() {
        List<Juego> juegosConResenas = gd.getJuegos().stream()
                .filter(j -> !gd.getResenasPorJuego(j).isEmpty())
                .collect(java.util.stream.Collectors.toList());

        colRJTitulo.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getTitulo()));
        colRJMedia.setCellValueFactory(c -> { double m = gd.getPuntuacionMediaJuego(c.getValue().getIdJuego());return new SimpleStringProperty(String.format("%.1f", m));});
        colRJTotal.setCellValueFactory(c ->new SimpleStringProperty(String.valueOf(gd.getResenasPorJuego(c.getValue()).size())));
        tablaResJuego.setItems(FXCollections.observableArrayList(juegosConResenas));

        colRJDAutor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAutor().getNombreCompleto()));
        colRJDPunt.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getPuntuacion() + "/10"));
        colRJDIdioma.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getIdioma()));
        colRJDCom.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getComentario()));

        tablaResJuego.getSelectionModel().selectedItemProperty().addListener((obs, ant, j) -> { if (j == null) { tablaResDetJuego.getItems().clear(); return; }
                    lblResJuegoSel.setText("Reseñas de: " + j.getTitulo());
                    tablaResDetJuego.setItems(FXCollections.observableArrayList(
                    gd.getResenasPorJuego(j)));
                });
    }


    private void inicializarResUsuario() {
        List<Usuario> usuarios = gd.getUsuarios();

        colRUNombre.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colRUTotal.setCellValueFactory(c ->new SimpleStringProperty(String.valueOf(gd.getResenasPorUsuario(c.getValue()).size())));
        tablaResUsuario.setItems(FXCollections.observableArrayList(usuarios));

        colRUDJuego.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getJuego().getTitulo()));
        colRUDPunt.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getPuntuacion() + "/10"));
        colRUDIdioma.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getIdioma()));
        colRUDCom.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getComentario()));

        tablaResUsuario.getSelectionModel().selectedItemProperty().addListener((obs, ant, u) -> { if (u == null) { tablaResDetUsuario.getItems().clear(); return; }
                    lblResUsuarioSel.setText("Reseñas de: " + u.getNombreCompleto());
                    tablaResDetUsuario.setItems(FXCollections.observableArrayList(
                   gd.getResenasPorUsuario(u)));
                });
    }


    private void inicializarResIdioma() {
        List<String> idiomasConResenas = java.util.Arrays.stream(IDIOMAS).filter(i -> !gd.getResenasPorIdioma(i).isEmpty()).collect(java.util.stream.Collectors.toList());

        colRIIdioma.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()));
        colRITotal.setCellValueFactory(c ->new SimpleStringProperty( String.valueOf(gd.getResenasPorIdioma(c.getValue()).size())));
        tablaResIdioma.setItems(FXCollections.observableArrayList(idiomasConResenas));

        colRIDJuego.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getJuego().getTitulo()));
        colRIDAutor.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getAutor().getNombreCompleto()));
        colRIDPunt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPuntuacion() + "/10"));
        colRIDCom.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getComentario()));

        tablaResIdioma.getSelectionModel().selectedItemProperty().addListener((obs, ant, idioma) -> {if (idioma == null) { tablaResDetIdioma.getItems().clear(); return; }
                    lblResIdiomaSel.setText("Reseñas en: " + idioma);
                    tablaResDetIdioma.setItems(FXCollections.observableArrayList(
                            gd.getResenasPorIdioma(idioma)));
                });
    }

    private void inicializarJuegosUsuario() {
        List<Usuario> usuarios = gd.getUsuarios();

        colJUNombre.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colJUTotal.setCellValueFactory(c ->new SimpleStringProperty(String.valueOf(
        gd.getBibliotecaUsuario(c.getValue().getIdUsuario()).size())));
        tablaJuegosUsuario.setItems(FXCollections.observableArrayList(usuarios));

        colJUDTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colJUDGenero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenero()));
        colJUDPlat.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getPlataforma()));

        tablaJuegosUsuario.getSelectionModel().selectedItemProperty().addListener((obs, ant, u) -> {
                    if (u == null) { tablaJuegosDetUsuario.getItems().clear(); return; }
                    lblJuegosUsuarioSel.setText("Biblioteca de: " + u.getNombreCompleto());
                    tablaJuegosDetUsuario.setItems(FXCollections.observableArrayList(
                   gd.getBibliotecaUsuario(u.getIdUsuario())));
                });
    }

    private void inicializarEstudios() {
        colEstNombre.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getNombre()));
        tablaEstudios.setItems(FXCollections.observableArrayList(gd.getEstudios()));

        colDevNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colDevPuesto.setCellValueFactory(c ->new SimpleStringProperty(c.getValue().getPuestoActual()));
        colDevMejorVal.setCellValueFactory(c -> {Juego j = gd.getJuegoMejorValoradoDesarrollador(c.getValue());
            return new SimpleStringProperty(j != null ? j.getTitulo() : "–");
        });
        colDevMasVend.setCellValueFactory(c -> {
            Juego j = gd.getJuegoMasVendidoDesarrollador(c.getValue()); return new SimpleStringProperty(j != null ? j.getTitulo() : "–");
        });

        tablaEstudios.getSelectionModel().selectedItemProperty().addListener((obs, ant, est) -> {
                    if (est == null) {
                        panelEstudioDetalle.setVisible(false);
                        panelEstudioDetalle.setManaged(false);
                        panelEstudioVacio.setVisible(true);
                        panelEstudioVacio.setManaged(true);
                        return;
                    }
                    lblEstudioNombre.setText("🏢 " + est.getNombre());

                    Juego mejorVal   = gd.getJuegoMejorValoradoEstudio(est);
                    Juego masVendido = gd.getJuegoMasVendidoEstudio(est);
                    lblEstMejorVal.setText(mejorVal != null ? mejorVal.getTitulo() + String.format(" (%.1f/10)",
                                gd.getPuntuacionMediaJuego(mejorVal.getIdJuego())): "–");
                    lblEstMasVend.setText(masVendido != null ? masVendido.getTitulo()+ " (" + gd.getVentasJuego(masVendido) + " uds)": "–");

                    tablaDevEstudio.setItems(FXCollections.observableArrayList(gd.getDesarrolladoresDeEstudio(est)));

                    panelEstudioDetalle.setVisible(true);
                    panelEstudioDetalle.setManaged(true);
                    panelEstudioVacio.setVisible(false);
                    panelEstudioVacio.setManaged(false);
                });

        panelEstudioDetalle.setVisible(false);
        panelEstudioDetalle.setManaged(false);
        panelEstudioVacio.setVisible(true);
        panelEstudioVacio.setManaged(true);
    }

    @FXML
    public void cerrar() {
        ((Stage) tablaVentas.getScene().getWindow()).close();
    }
}
