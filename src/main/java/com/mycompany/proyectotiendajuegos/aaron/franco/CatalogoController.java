package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.DialogUtil;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana de Catálogo / Búsqueda de juegos (usuario).
 * Se abre como ventana secundaria desde MainUsuarioController.
 */
public class CatalogoController implements Initializable {

    @FXML private VBox        vboxJuegos;
    @FXML private TextField   txBusqueda;
    @FXML private ComboBox<String> cbTipo;
    @FXML private Label       lblResultados;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipo.setItems(FXCollections.observableArrayList(
                "Nombre", "Género", "Director", "Estudio"));
        cbTipo.getSelectionModel().selectFirst();
        cargarTodos();
    }

    private void cargarTodos() {
        mostrarJuegos(gd.getJuegos());
    }

    @FXML
    public void buscar() {
        String texto = txBusqueda.getText().trim();
        if (texto.isEmpty()) { cargarTodos(); return; }
        List<Juego> resultados;
        switch (cbTipo.getValue()) {
            case "Género":   resultados = gd.buscarJuegosPorGenero(texto);   break;
            case "Director": resultados = gd.buscarJuegosPorDirector(texto); break;
            case "Estudio":  resultados = gd.buscarJuegosPorEstudio(texto);  break;
            default:         resultados = gd.buscarJuegosPorNombre(texto);
        }
        mostrarJuegos(resultados);
    }

    @FXML
    public void limpiar() {
        txBusqueda.clear();
        cbTipo.getSelectionModel().selectFirst();
        cargarTodos();
    }

    private void mostrarJuegos(List<Juego> lista) {
        vboxJuegos.getChildren().clear();
        lblResultados.setText(lista.size() + " juego(s) encontrado(s)");
        if (lista.isEmpty()) {
            Label lbl = new Label("No se encontraron juegos.");
            lbl.getStyleClass().add("label-muted");
            vboxJuegos.getChildren().add(lbl);
            return;
        }
        for (Juego j : lista) vboxJuegos.getChildren().add(buildTarjeta(j));
    }

    private HBox buildTarjeta(Juego j) {
        HBox tarjeta = new HBox(15);
        tarjeta.getStyleClass().add("card");
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setPadding(new Insets(12));

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblTitulo = new Label(j.getTitulo());
        lblTitulo.getStyleClass().add("label-section");

        Label lblMeta = new Label("🎮 " + j.getPlataforma()
                + "  |  📂 " + j.getGenero()
                + "  |  🎬 " + j.getDirector());
        lblMeta.getStyleClass().add("label-muted");

        double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
        Label lblMedia = new Label(media > 0
                ? String.format("⭐ %.1f/10", media) : "Sin valoraciones");
        lblMedia.getStyleClass().add("label-normal");

        String textoStock = j.getStock() > 10 ? "✅ " + j.getStock() + " en stock"
                : j.getStock() > 0 ? "⚠ Solo " + j.getStock() + " en stock"
                : "❌ Sin stock";
        Label lblStock = new Label(textoStock);
        lblStock.getStyleClass().add(
                j.getStock() > 10 ? "stock-ok" : j.getStock() > 0 ? "stock-low" : "stock-out");

        info.getChildren().addAll(lblTitulo, lblMeta, lblMedia, lblStock);

        VBox panelAccion = new VBox(6);
        panelAccion.setAlignment(Pos.CENTER);

        Label lblPrecio = new Label(String.format("%.2f€", j.getPrecio()));
        lblPrecio.getStyleClass().add("price-badge");

        Button btnVerResenas = new Button("Ver reseñas");
        btnVerResenas.getStyleClass().add("btn-secondary");
        btnVerResenas.setOnAction(e -> abrirResenas(j));

        boolean loTiene = gd.usuarioPoseeJuego(
                gd.getUsuarioActual().getIdUsuario(), j.getIdJuego());

        if (loTiene) {
            Label lbl = new Label("✅ En biblioteca");
            lbl.getStyleClass().add("stock-ok");
            panelAccion.getChildren().addAll(lblPrecio, lbl, btnVerResenas);
        } else {
            Button btnComprar = new Button("🛒 Comprar");
            btnComprar.getStyleClass().add("btn-primary");
            btnComprar.setDisable(j.getStock() <= 0);
            btnComprar.setOnAction(e -> comprar(j, btnComprar));
            panelAccion.getChildren().addAll(lblPrecio, btnComprar, btnVerResenas);
        }

        tarjeta.getChildren().addAll(info, panelAccion);
        return tarjeta;
    }

    private void comprar(Juego j, Button btn) {
        String resultado = gd.comprarJuego(gd.getUsuarioActual(), j, 1);
        if ("OK".equals(resultado)) {
            DialogUtil.info("¡Compra realizada! " + j.getTitulo() + " añadido a tu biblioteca.");
            cargarTodos(); // refresca el catálogo
        } else {
            DialogUtil.error(resultado);
        }
    }

    private void abrirResenas(Juego j) {
        com.mycompany.proyectotiendajuegos.aaron.franco.VentanaUtil
                .abrirVentanaConDato("resenas_juego", "Reseñas – " + j.getTitulo(), 750, 550, j);
    }
}
