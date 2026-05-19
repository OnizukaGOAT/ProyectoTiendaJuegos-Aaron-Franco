package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * Controlador de la ventana de Estadísticas y Consultas (admin).
 *
 * Cubre todas las consultas del PDF §3:
 *  3.1  Juegos comprados por usuario
 *  3.2  Reseñas (por usuario / por juego / por idioma)
 *  3.3  Juegos mejor valorados (global / por estudio / por desarrollador)
 *  3.4  Juegos más vendidos   (global / por estudio / por desarrollador)
 */
public class EstadisticasAdminController implements Initializable {

    @FXML private StackPane contentPane;
    @FXML private Label     lblSeccion;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mostrarVentas();
    }

    private void setContent(Node nodo) {
        contentPane.getChildren().setAll(nodo);
    }

    // ══════════════════════════════════════════════════════
    // BOTONES DEL MENÚ LATERAL
    // ══════════════════════════════════════════════════════

    @FXML public void mostrarVentas() {
        lblSeccion.setText("📊 Ventas por Juego");
        VBox raiz = new VBox(12); raiz.setPadding(new Insets(10));

        int maxVentas = gd.getJuegos().stream().mapToInt(gd::getVentasJuego).max().orElse(1);
        for (Juego j : gd.getJuegos()) {
            int ventas = gd.getVentasJuego(j);
            HBox fila = new HBox(15); fila.getStyleClass().add("card");
            fila.setAlignment(Pos.CENTER_LEFT); fila.setPadding(new Insets(8));
            VBox info = new VBox(3); HBox.setHgrow(info, Priority.ALWAYS);
            Label lblT = new Label(j.getTitulo()); lblT.getStyleClass().add("label-section");
            Label lblV = new Label("Ventas: " + ventas + "  |  Ingresos: "
                + String.format("%.2f€", ventas * j.getPrecio()));
            lblV.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblV);
            double pct = maxVentas > 0 ? (double) ventas / maxVentas : 0;
            Region fill = new Region();
            fill.setStyle("-fx-background-color:#e94560; -fx-background-radius:4;");
            fill.setPrefWidth(200 * pct); fill.setPrefHeight(10);
            HBox barra = new HBox(fill);
            barra.setMinWidth(200);
            barra.setStyle("-fx-background-color:#16213e; -fx-background-radius:4;");
            barra.setAlignment(Pos.CENTER_LEFT);
            fila.getChildren().addAll(info, barra);
            raiz.getChildren().add(fila);
        }
        setContent(scroll(raiz));
    }

    @FXML public void mostrarMejorValorados() {
        lblSeccion.setText("⭐ Juegos Mejor Valorados");
        VBox raiz = new VBox(10); raiz.setPadding(new Insets(10));
        int pos = 1;
        for (Juego j : gd.getJuegosMejorValorados()) {
            double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
            HBox fila = buildFilaRanking(pos++, j.getTitulo(),
                String.format("⭐ %.1f/10  (%d reseñas)  |  Director: %s",
                    media, gd.getResenasPorJuego(j).size(), j.getDirector()));
            raiz.getChildren().add(fila);
        }
        if (raiz.getChildren().isEmpty())
            raiz.getChildren().add(lbl("Sin valoraciones todavía."));
        setContent(scroll(raiz));
    }

    @FXML public void mostrarMasVendidos() {
        lblSeccion.setText("🔥 Juegos Más Vendidos");
        VBox raiz = new VBox(10); raiz.setPadding(new Insets(10));
        int pos = 1;
        for (Juego j : gd.getJuegosMasVendidos()) {
            int ventas = gd.getVentasJuego(j);
            HBox fila = buildFilaRanking(pos++, j.getTitulo(),
                "🔥 " + ventas + " uds  |  "
                + String.format("%.2f€ ingresos", ventas * j.getPrecio()));
            raiz.getChildren().add(fila);
        }
        setContent(scroll(raiz));
    }

    @FXML public void mostrarResenasPorIdioma() {
        lblSeccion.setText("💬 Reseñas por Idioma");
        VBox raiz = new VBox(10); raiz.setPadding(new Insets(10));
        String[] idiomas = {"Español","English","Français","Deutsch","Português","Italiano"};
        boolean hay = false;
        for (String idioma : idiomas) {
            List<Resena> lista = gd.getResenasPorIdioma(idioma);
            if (lista.isEmpty()) continue;
            hay = true;
            raiz.getChildren().add(acordeon("🌐 " + idioma + "  (" + lista.size() + " reseñas)", () -> {
                VBox c = new VBox(4);
                for (Resena r : lista) {
                    Label l = new Label("• " + r.getJuego().getTitulo() + " – "
                        + r.getAutor().getNombreCompleto() + "  ⭐" + r.getPuntuacion());
                    l.getStyleClass().add("label-normal"); c.getChildren().add(l);
                }
                return c;
            }));
        }
        if (!hay) raiz.getChildren().add(lbl("No hay reseñas registradas."));
        setContent(scroll(raiz));
    }

    @FXML public void mostrarResenasPorJuego() {
        lblSeccion.setText("🎮 Reseñas por Juego");
        VBox raiz = new VBox(10); raiz.setPadding(new Insets(10));
        boolean hay = false;
        for (Juego j : gd.getJuegos()) {
            List<Resena> lista = gd.getResenasPorJuego(j);
            if (lista.isEmpty()) continue;
            hay = true;
            double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
            raiz.getChildren().add(acordeon(
                "🎮 " + j.getTitulo() + String.format("  ⭐ %.1f/10  (%d reseñas)", media, lista.size()),
                () -> {
                    VBox c = new VBox(4);
                    for (Resena r : lista) {
                        Label l = new Label("  • " + r.getAutor().getNombreCompleto()
                            + "  ⭐" + r.getPuntuacion() + "/10  [" + r.getIdioma() + "]  – "
                            + r.getComentario());
                        l.getStyleClass().add("label-normal"); l.setWrapText(true);
                        c.getChildren().add(l);
                    }
                    return c;
                }));
        }
        if (!hay) raiz.getChildren().add(lbl("No hay reseñas registradas."));
        setContent(scroll(raiz));
    }

    @FXML public void mostrarResenasPorUsuario() {
        lblSeccion.setText("✍ Reseñas por Usuario");
        VBox raiz = new VBox(10); raiz.setPadding(new Insets(10));
        boolean hay = false;
        for (Usuario u : gd.getUsuarios()) {
            List<Resena> lista = gd.getResenasPorUsuario(u);
            if (lista.isEmpty()) continue;
            hay = true;
            raiz.getChildren().add(acordeon(
                "👤 " + u.getNombreCompleto() + "  (" + lista.size() + " reseñas)", () -> {
                    VBox c = new VBox(4);
                    for (Resena r : lista) {
                        Label l = new Label("  • " + r.getJuego().getTitulo()
                            + "  ⭐" + r.getPuntuacion() + "/10  –  " + r.getComentario());
                        l.getStyleClass().add("label-normal"); l.setWrapText(true);
                        c.getChildren().add(l);
                    }
                    return c;
                }));
        }
        if (!hay) raiz.getChildren().add(lbl("No hay reseñas."));
        setContent(scroll(raiz));
    }

    @FXML public void mostrarJuegosPorUsuario() {
        lblSeccion.setText("🛒 Juegos Comprados por Usuario");
        VBox raiz = new VBox(10); raiz.setPadding(new Insets(10));
        for (Usuario u : gd.getUsuarios()) {
            List<Juego> bib = gd.getBibliotecaUsuario(u.getIdUsuario());
            raiz.getChildren().add(acordeon(
                "👤 " + u.getNombreCompleto() + "  (" + bib.size() + " juegos)", () -> {
                    VBox c = new VBox(4);
                    if (bib.isEmpty()) c.getChildren().add(lbl("Sin compras."));
                    else for (Juego j : bib) {
                        Label l = new Label("  • " + j.getTitulo() + " (" + j.getGenero() + ")");
                        l.getStyleClass().add("label-normal"); c.getChildren().add(l);
                    }
                    return c;
                }));
        }
        setContent(scroll(raiz));
    }

    @FXML public void mostrarEstadisticasPorEstudio() {
        lblSeccion.setText("🏢 Estadísticas por Estudio");
        VBox raiz = new VBox(10); raiz.setPadding(new Insets(10));
        for (Estudio est : gd.getEstudios()) {
            raiz.getChildren().add(acordeon("🏢 " + est.getNombre(), () -> {
                VBox c = new VBox(6);
                Juego mejorVal   = gd.getJuegoMejorValoradoEstudio(est);
                Juego masVendido = gd.getJuegoMasVendidoEstudio(est);
                if (mejorVal != null) {
                    Label l = new Label("⭐ Mejor valorado: " + mejorVal.getTitulo()
                        + String.format("  (%.1f/10)",
                            gd.getPuntuacionMediaJuego(mejorVal.getIdJuego())));
                    l.getStyleClass().add("label-normal"); c.getChildren().add(l);
                }
                if (masVendido != null) {
                    Label l = new Label("🔥 Más vendido: " + masVendido.getTitulo()
                        + "  (" + gd.getVentasJuego(masVendido) + " uds)");
                    l.getStyleClass().add("label-normal"); c.getChildren().add(l);
                }
                // Desarrolladores del estudio
                List<Desarrollador> devs = gd.getDesarrolladoresDeEstudio(est);
                if (!devs.isEmpty()) {
                    Label lblDevs = new Label("👨‍💻 Desarrolladores notables:");
                    lblDevs.getStyleClass().add("label-muted");
                    c.getChildren().add(lblDevs);
                    for (Desarrollador d : devs) {
                        Juego mejorD = gd.getJuegoMejorValoradoDesarrollador(d);
                        Juego masVD  = gd.getJuegoMasVendidoDesarrollador(d);
                        StringBuilder sb = new StringBuilder("  • " + d.getNombreCompleto()
                            + " (" + d.getPuestoActual() + ")");
                        if (mejorD != null) sb.append("  |  ⭐ ").append(mejorD.getTitulo());
                        if (masVD  != null) sb.append("  |  🔥 ").append(masVD.getTitulo());
                        Label l = new Label(sb.toString());
                        l.getStyleClass().add("label-muted"); c.getChildren().add(l);
                    }
                }
                return c;
            }));
        }
        setContent(scroll(raiz));
    }

    // ══════════════════════════════════════════════════════
    // CERRAR
    // ══════════════════════════════════════════════════════
    @FXML
    public void cerrar() {
        ((Stage) contentPane.getScene().getWindow()).close();
    }

    // ══════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════
    private HBox buildFilaRanking(int pos, String titulo, String detalle) {
        HBox fila = new HBox(15); fila.getStyleClass().add("card");
        fila.setAlignment(Pos.CENTER_LEFT); fila.setPadding(new Insets(10));
        Label lblPos = new Label("#" + pos); lblPos.getStyleClass().add("label-gold");
        lblPos.setMinWidth(32);
        VBox info = new VBox(3);
        Label lblT = new Label(titulo); lblT.getStyleClass().add("label-section");
        Label lblD = new Label(detalle); lblD.getStyleClass().add("label-normal");
        info.getChildren().addAll(lblT, lblD);
        fila.getChildren().addAll(lblPos, info);
        return fila;
    }

    /** Acordeón desplegable con cabecera clicable. */
    private VBox acordeon(String etiqueta, Supplier<Node> constructorContenido) {
        VBox panel = new VBox(0); panel.getStyleClass().add("card");
        HBox cabecera = new HBox(10);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(new Insets(10, 14, 10, 14));
        cabecera.setStyle("-fx-cursor:hand;");
        Label flecha = new Label("▶"); flecha.getStyleClass().add("label-gold");
        Label etiq   = new Label(etiqueta); etiq.getStyleClass().add("label-section");
        cabecera.getChildren().addAll(flecha, etiq);
        VBox cuerpo = new VBox(); cuerpo.setPadding(new Insets(0, 14, 10, 28));
        cuerpo.setVisible(false); cuerpo.setManaged(false);
        cabecera.setOnMouseClicked(e -> {
            boolean abierto = cuerpo.isVisible();
            if (!abierto && cuerpo.getChildren().isEmpty())
                cuerpo.getChildren().add(constructorContenido.get());
            flecha.setText(abierto ? "▶" : "▼");
            cuerpo.setVisible(!abierto); cuerpo.setManaged(!abierto);
        });
        panel.getChildren().addAll(cabecera, cuerpo);
        return panel;
    }

    private ScrollPane scroll(Node nodo) {
        ScrollPane sp = new ScrollPane(nodo);
        sp.setFitToWidth(true); sp.getStyleClass().add("scroll-pane");
        return sp;
    }

    private Label lbl(String txt) {
        Label l = new Label(txt); l.getStyleClass().add("label-normal"); return l;
    }
}
