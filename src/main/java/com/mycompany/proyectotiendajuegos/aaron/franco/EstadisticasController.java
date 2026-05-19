package com.mycompany.proyectotiendajuegos.aaron.franco.controladores;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.*;
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
 * Controlador de la ventana de Estadísticas.
 * Usada tanto desde el panel de usuario como desde el panel de administración.
 * Contiene rankings y acordeones desplegables con toda la información estadística.
 */
public class EstadisticasController implements Initializable {

    // VBox de cada pestaña — inyectados desde el FXML con fx:id
    @FXML private VBox panelVentas;
    @FXML private VBox panelMejorVal;
    @FXML private VBox panelMasVend;
    @FXML private VBox panelResIdioma;
    @FXML private VBox panelResJuego;
    @FXML private VBox panelEstudio;
    @FXML private VBox panelJuegosUsuario;
    @FXML private VBox panelResenasUsuario;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        construirVentas();
        construirMejorValorados();
        construirMasVendidos();
        construirResIdioma();
        construirResJuego();
        construirEstudio();
        construirJuegosUsuario();
        construirResenasUsuario();
    }

    // ══════════════════════════════════════════════════════
    // VENTAS POR JUEGO
    // ══════════════════════════════════════════════════════
    private void construirVentas() {
        int maxV = gd.getJuegos().stream().mapToInt(gd::getVentasJuego).max().orElse(1);
        for (Juego j : gd.getJuegos()) {
            int ventas = gd.getVentasJuego(j);
            HBox fila = new HBox(15);
            fila.getStyleClass().add("card");
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setPadding(new Insets(8));

            VBox info = new VBox(3);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label lblT = new Label(j.getTitulo()); lblT.getStyleClass().add("label-section");
            Label lblV = new Label("Ventas: " + ventas + "  |  Ingresos: "
                    + String.format("%.2f€", ventas * j.getPrecio()));
            lblV.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblV);

            double pct = maxV > 0 ? (double) ventas / maxV : 0;
            Region fill = new Region();
            fill.setStyle("-fx-background-color:#e94560; -fx-background-radius:4;");
            fill.setPrefWidth(200 * pct); fill.setPrefHeight(10);
            HBox barra = new HBox(fill);
            barra.setMinWidth(200);
            barra.setStyle("-fx-background-color:#16213e; -fx-background-radius:4;");
            barra.setAlignment(Pos.CENTER_LEFT);

            fila.getChildren().addAll(info, barra);
            panelVentas.getChildren().add(fila);
        }
    }

    // ══════════════════════════════════════════════════════
    // MEJOR VALORADOS
    // ══════════════════════════════════════════════════════
    private void construirMejorValorados() {
        int pos = 1;
        for (Juego j : gd.getJuegosMejorValorados()) {
            double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
            panelMejorVal.getChildren().add(filaRanking(pos++, j.getTitulo(),
                    String.format("⭐ %.1f/10  (%d reseñas)  |  Director: %s",
                            media, gd.getResenasPorJuego(j).size(), j.getDirector())));
        }
        if (panelMejorVal.getChildren().isEmpty())
            panelMejorVal.getChildren().add(lbl("Sin valoraciones todavía."));
    }

    // ══════════════════════════════════════════════════════
    // MÁS VENDIDOS
    // ══════════════════════════════════════════════════════
    private void construirMasVendidos() {
        int pos = 1;
        for (Juego j : gd.getJuegosMasVendidos()) {
            int ventas = gd.getVentasJuego(j);
            panelMasVend.getChildren().add(filaRanking(pos++, j.getTitulo(),
                    "🔥 " + ventas + " uds  |  "
                            + String.format("%.2f€ ingresos", ventas * j.getPrecio())));
        }
    }

    // ══════════════════════════════════════════════════════
    // RESEÑAS POR IDIOMA
    // ══════════════════════════════════════════════════════
    private void construirResIdioma() {
        String[] idiomas = {"Español","English","Français","Deutsch","Português","Italiano"};
        boolean hay = false;
        for (String idioma : idiomas) {
            List<Resena> lista = gd.getResenasPorIdioma(idioma);
            if (lista.isEmpty()) continue;
            hay = true;
            panelResIdioma.getChildren().add(
                    acordeon("🌐 " + idioma + "  (" + lista.size() + " reseñas)", () -> {
                        VBox c = new VBox(4);
                        for (Resena r : lista) {
                            Label l = new Label("• " + r.getJuego().getTitulo()
                                    + " – " + r.getAutor().getNombreCompleto()
                                    + "  ⭐" + r.getPuntuacion());
                            l.getStyleClass().add("label-normal"); c.getChildren().add(l);
                        }
                        return c;
                    }));
        }
        if (!hay) panelResIdioma.getChildren().add(lbl("No hay reseñas registradas."));
    }

    // ══════════════════════════════════════════════════════
    // RESEÑAS POR JUEGO
    // ══════════════════════════════════════════════════════
    private void construirResJuego() {
        boolean hay = false;
        for (Juego j : gd.getJuegos()) {
            List<Resena> lista = gd.getResenasPorJuego(j);
            if (lista.isEmpty()) continue;
            hay = true;
            double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
            panelResJuego.getChildren().add(
                    acordeon("🎮 " + j.getTitulo()
                            + String.format("  ⭐ %.1f/10  (%d reseñas)", media, lista.size()), () -> {
                        VBox c = new VBox(4);
                        for (Resena r : lista) {
                            Label l = new Label("  • " + r.getAutor().getNombreCompleto()
                                    + "  ⭐" + r.getPuntuacion() + "/10  ["
                                    + r.getIdioma() + "]  – " + r.getComentario());
                            l.getStyleClass().add("label-normal"); l.setWrapText(true);
                            c.getChildren().add(l);
                        }
                        return c;
                    }));
        }
        if (!hay) panelResJuego.getChildren().add(lbl("No hay reseñas registradas."));
    }

    // ══════════════════════════════════════════════════════
    // ESTADÍSTICAS POR ESTUDIO
    // ══════════════════════════════════════════════════════
    private void construirEstudio() {
        for (Estudio est : gd.getEstudios()) {
            panelEstudio.getChildren().add(acordeon("🏢 " + est.getNombre(), () -> {
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
                List<Desarrollador> devs = gd.getDesarrolladoresDeEstudio(est);
                if (!devs.isEmpty()) {
                    Label lblD = new Label("👨‍💻 Desarrolladores:");
                    lblD.getStyleClass().add("label-muted"); c.getChildren().add(lblD);
                    for (Desarrollador d : devs) {
                        Juego mD = gd.getJuegoMejorValoradoDesarrollador(d);
                        Juego mvD = gd.getJuegoMasVendidoDesarrollador(d);
                        StringBuilder sb = new StringBuilder("  • " + d.getNombreCompleto()
                                + " (" + d.getPuestoActual() + ")");
                        if (mD  != null) sb.append("  |  ⭐ ").append(mD.getTitulo());
                        if (mvD != null) sb.append("  |  🔥 ").append(mvD.getTitulo());
                        Label l = new Label(sb.toString());
                        l.getStyleClass().add("label-muted"); c.getChildren().add(l);
                    }
                }
                return c;
            }));
        }
    }

    // ══════════════════════════════════════════════════════
    // JUEGOS POR USUARIO
    // ══════════════════════════════════════════════════════
    private void construirJuegosUsuario() {
        for (Usuario u : gd.getUsuarios()) {
            List<Juego> bib = gd.getBibliotecaUsuario(u.getIdUsuario());
            panelJuegosUsuario.getChildren().add(
                    acordeon("👤 " + u.getNombreCompleto() + "  (" + bib.size() + " juegos)", () -> {
                        VBox c = new VBox(4);
                        if (bib.isEmpty()) c.getChildren().add(lbl("Sin compras."));
                        else for (Juego j : bib) {
                            Label l = new Label("  • " + j.getTitulo() + " (" + j.getGenero() + ")");
                            l.getStyleClass().add("label-normal"); c.getChildren().add(l);
                        }
                        return c;
                    }));
        }
    }

    // ══════════════════════════════════════════════════════
    // RESEÑAS POR USUARIO
    // ══════════════════════════════════════════════════════
    private void construirResenasUsuario() {
        boolean hay = false;
        for (Usuario u : gd.getUsuarios()) {
            List<Resena> lista = gd.getResenasPorUsuario(u);
            if (lista.isEmpty()) continue;
            hay = true;
            panelResenasUsuario.getChildren().add(
                    acordeon("👤 " + u.getNombreCompleto() + "  (" + lista.size() + " reseñas)", () -> {
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
        if (!hay) panelResenasUsuario.getChildren().add(lbl("No hay reseñas."));
    }

    // ══════════════════════════════════════════════════════
    // CERRAR
    // ══════════════════════════════════════════════════════
    @FXML
    public void cerrar() {
        ((Stage) panelVentas.getScene().getWindow()).close();
    }

    // ══════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════
    private HBox filaRanking(int pos, String titulo, String detalle) {
        HBox fila = new HBox(15);
        fila.getStyleClass().add("card");
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(10));
        Label lblPos = new Label("#" + pos); lblPos.getStyleClass().add("label-gold"); lblPos.setMinWidth(32);
        VBox info = new VBox(3);
        Label lblT = new Label(titulo); lblT.getStyleClass().add("label-section");
        Label lblD = new Label(detalle); lblD.getStyleClass().add("label-normal");
        info.getChildren().addAll(lblT, lblD);
        fila.getChildren().addAll(lblPos, info);
        return fila;
    }

    private VBox acordeon(String etiqueta, Supplier<Node> constructor) {
        VBox panel = new VBox(0); panel.getStyleClass().add("card");
        HBox cab = new HBox(10);
        cab.setAlignment(Pos.CENTER_LEFT);
        cab.setPadding(new Insets(10, 14, 10, 14));
        cab.setStyle("-fx-cursor:hand;");
        Label flecha = new Label("▶"); flecha.getStyleClass().add("label-gold");
        Label etiq   = new Label(etiqueta); etiq.getStyleClass().add("label-section");
        cab.getChildren().addAll(flecha, etiq);
        VBox cuerpo = new VBox(); cuerpo.setPadding(new Insets(0, 14, 10, 28));
        cuerpo.setVisible(false); cuerpo.setManaged(false);
        cab.setOnMouseClicked(e -> {
            boolean abierto = cuerpo.isVisible();
            if (!abierto && cuerpo.getChildren().isEmpty())
                cuerpo.getChildren().add(constructor.get());
            flecha.setText(abierto ? "▶" : "▼");
            cuerpo.setVisible(!abierto); cuerpo.setManaged(!abierto);
        });
        panel.getChildren().addAll(cab, cuerpo);
        return panel;
    }

    private Label lbl(String txt) {
        Label l = new Label(txt); l.getStyleClass().add("label-normal"); return l;
    }
}
