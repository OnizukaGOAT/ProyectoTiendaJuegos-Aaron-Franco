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

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainUsuarioController implements Initializable {

    @FXML private StackPane contentPane;
    @FXML private Label     lblBienvenida, lblSaldo;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario u = gd.getUsuarioActual();
        lblBienvenida.setText("Hola, " + u.getNombre() + "!");
        actualizarSaldo();
        mostrarCatalogo();
    }

    private void actualizarSaldo() {
        lblSaldo.setText(String.format("💰 %.2f€", gd.getUsuarioActual().getSaldo()));
    }

    private void setContent(Node node) { contentPane.getChildren().setAll(node); }

    // ─────────────────────────────────────────────────────────
    // CATÁLOGO / BÚSQUEDA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarCatalogo() {
        setContent(buildCatalogo(gd.getJuegos()));
    }

    @FXML public void mostrarBuscarJuegos() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🔍 Buscar Juegos"); titulo.getStyleClass().add("label-title");

        HBox barra = new HBox(10);
        TextField txBusqueda = new TextField(); txBusqueda.setPromptText("Nombre, género, director o estudio...");
        txBusqueda.setPrefWidth(280); txBusqueda.getStyleClass().add("text-field");
        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList("Nombre","Género","Director","Estudio"));
        cbTipo.getSelectionModel().selectFirst(); cbTipo.getStyleClass().add("combo-box");
        Button btnBuscar = new Button("Buscar"); btnBuscar.getStyleClass().add("btn-primary");
        barra.getChildren().addAll(txBusqueda, cbTipo, btnBuscar);

        VBox resultados = new VBox(10);
        btnBuscar.setOnAction(e -> {
    String texto = txBusqueda.getText().trim(); 
    if (texto.isEmpty()) return;
    
    List<Juego> lista;
    switch (cbTipo.getValue()) {
        case "Género":
            lista = gd.buscarJuegosPorGenero(texto);
            break;
        case "Director":
            lista = gd.buscarJuegosPorDirector(texto);
            break;
        case "Estudio":
            lista = gd.buscarJuegosPorEstudio(texto);
            break;
        default:
            lista = gd.buscarJuegosPorNombre(texto);
            break;
    }
    
    resultados.getChildren().setAll(buildCatalogo(lista));
});
txBusqueda.setOnAction(e -> btnBuscar.fire());
root.getChildren().addAll(titulo, barra, resultados);
setContent(scroll(root));
    }

    // ─────────────────────────────────────────────────────────
    // HISTORIAL DE COMPRAS
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarHistorialCompras() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🛒 Historial de Compras"); titulo.getStyleClass().add("label-title");

        List<Compra> compras = gd.getComprasUsuario(gd.getUsuarioActual().getIdUsuario());
        TableView<Compra> tabla = new TableView<>(FXCollections.observableArrayList(compras));
        tabla.getStyleClass().add("table-view"); tabla.setPrefHeight(380);
        col(tabla, "Juego",    c -> c.getJuego().getTitulo(),         200);
        col(tabla, "Fecha",    c -> c.getFechaFormateada(),            100);
        col(tabla, "Cantidad", c -> String.valueOf(c.getCantidad()),    80);
        col(tabla, "Coste",    c -> String.format("%.2f€", c.getCoste()), 100);

        double totalGastado = compras.stream().mapToDouble(Compra::getCoste).sum();
        Label lblTotal = new Label(String.format("Total gastado: %.2f€", totalGastado)); lblTotal.getStyleClass().add("label-gold");
        root.getChildren().addAll(titulo, tabla, lblTotal);
        setContent(scroll(root));
    }

    // ─────────────────────────────────────────────────────────
    // BIBLIOTECA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarBiblioteca() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("📚 Mi Biblioteca"); titulo.getStyleClass().add("label-title");
        List<Juego> biblioteca = gd.getBibliotecaUsuario(gd.getUsuarioActual().getIdUsuario());
        if (biblioteca.isEmpty()) {
            Label lbl = new Label("No tienes juegos en tu biblioteca todavía."); lbl.getStyleClass().add("label-muted");
            root.getChildren().addAll(titulo, lbl);
        } else {
            root.getChildren().add(titulo);
            for (Juego j : biblioteca) root.getChildren().add(buildJuegoCard(j, false));
        }
        setContent(scroll(root));
    }

    // ─────────────────────────────────────────────────────────
    // MIS RESEÑAS
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarMisResenas() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("✍ Mis Reseñas"); titulo.getStyleClass().add("label-title");
        List<Resena> misResenas = gd.getResenasPorUsuario(gd.getUsuarioActual());
        Button btnNueva = new Button("+ Nueva Reseña"); btnNueva.getStyleClass().add("btn-primary");
        btnNueva.setOnAction(e -> mostrarFormularioResena(null));
        root.getChildren().addAll(titulo, btnNueva);
        if (misResenas.isEmpty()) {
            Label lbl = new Label("Aún no has escrito ninguna reseña."); lbl.getStyleClass().add("label-muted");
            root.getChildren().add(lbl);
        } else {
            for (Resena r : misResenas) root.getChildren().add(buildResenaCard(r, true));
        }
        setContent(scroll(root));
    }

    private void mostrarFormularioResena(Resena resenaEditar) {
        VBox form = new VBox(12); form.setPadding(new Insets(20)); form.setMaxWidth(520); form.getStyleClass().add("card");
        Label titulo = new Label(resenaEditar == null ? "Nueva Reseña" : "Editar Reseña"); titulo.getStyleClass().add("label-section");

        List<Juego> biblioteca = gd.getBibliotecaUsuario(gd.getUsuarioActual().getIdUsuario());
        ComboBox<Juego> cbJuego = new ComboBox<>(FXCollections.observableArrayList(biblioteca));
        cbJuego.setPromptText("Selecciona un juego de tu biblioteca"); cbJuego.getStyleClass().add("combo-box"); cbJuego.setMaxWidth(Double.MAX_VALUE);

        Spinner<Integer> spinPunt = new Spinner<>(1, 10, 8); spinPunt.getStyleClass().add("spinner");
        ComboBox<String> cbIdioma = new ComboBox<>(FXCollections.observableArrayList("Español","English","Français","Deutsch","Português","Italiano"));
        cbIdioma.getSelectionModel().select(gd.getUsuarioActual().getIdioma()); cbIdioma.getStyleClass().add("combo-box");
        TextArea txComentario = new TextArea(); txComentario.setPromptText("Escribe tu reseña aquí...");
        txComentario.setPrefRowCount(4); txComentario.getStyleClass().add("text-area"); txComentario.setWrapText(true);

        if (resenaEditar != null) {
            // Buscar el juego en la biblioteca por id
            biblioteca.stream().filter(j -> j.getIdJuego() == resenaEditar.getJuego().getIdJuego()).findFirst().ifPresent(cbJuego.getSelectionModel()::select);
            cbJuego.setDisable(true);
            spinPunt.getValueFactory().setValue(resenaEditar.getPuntuacion());
            cbIdioma.getSelectionModel().select(resenaEditar.getIdioma());
            txComentario.setText(resenaEditar.getComentario());
        }

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnG = new Button("Guardar"); btnG.getStyleClass().add("btn-primary");
        Button btnC = new Button("Cancelar"); btnC.getStyleClass().add("btn-secondary");
        btnC.setOnAction(e -> mostrarMisResenas());

        btnG.setOnAction(e -> {
            if (cbJuego.getValue() == null)            { lblErr.setText("Selecciona un juego."); return; }
            if (txComentario.getText().trim().isEmpty()) { lblErr.setText("Escribe un comentario."); return; }
            if (resenaEditar != null) {
                resenaEditar.setComentario(txComentario.getText().trim());
                resenaEditar.setPuntuacion(spinPunt.getValue());
                resenaEditar.setIdioma(cbIdioma.getValue());
                gd.actualizarResena(resenaEditar);
                DialogUtil.info("Reseña actualizada.");
            } else {
                String resultado = gd.anadirResena(gd.getUsuarioActual(), cbJuego.getValue(),
                        txComentario.getText().trim(), spinPunt.getValue(), cbIdioma.getValue());
                if (!"OK".equals(resultado)) { lblErr.setText(resultado); return; }
                DialogUtil.info("Reseña publicada.");
            }
            mostrarMisResenas();
        });

        form.getChildren().addAll(titulo, lbl("Juego:"), cbJuego, lbl("Puntuación (1-10):"), spinPunt,
                lbl("Idioma:"), cbIdioma, lbl("Comentario:"), txComentario, lblErr, new HBox(10, btnG, btnC));
        setContent(scroll(form));
    }

    // ─────────────────────────────────────────────────────────
    // PERFIL
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarPerfil() {
        Usuario u = gd.getUsuarioActual();
        VBox root = new VBox(20); root.setPadding(new Insets(10));
        Label titulo = new Label("👤 Mi Perfil"); titulo.getStyleClass().add("label-title");

        VBox card = new VBox(10); card.getStyleClass().add("card"); card.setMaxWidth(460);
        card.getChildren().addAll(
                parLabel("ID:",                  String.valueOf(u.getIdUsuario())),
                parLabel("Nombre:",              u.getNombreCompleto()),
                parLabel("Correo:",              u.getCorreo()),
                parLabel("Idioma:",              u.getIdioma()),
                parLabel("Saldo:",               String.format("%.2f€", u.getSaldo())),
                parLabel("Juegos en biblioteca:", String.valueOf(gd.getBibliotecaUsuario(u.getIdUsuario()).size())),
                parLabel("Compras realizadas:",  String.valueOf(gd.getComprasUsuario(u.getIdUsuario()).size())),
                parLabel("Reseñas escritas:",    String.valueOf(gd.getResenasPorUsuario(u).size()))
        );

        Label lblEdit = new Label("✏ Editar datos"); lblEdit.getStyleClass().add("label-section");
        GridPane grid = new GridPane(); grid.setHgap(10); grid.setVgap(8);
        TextField txNombre    = tf(u.getNombre());
        TextField txApellidos = tf(u.getApellidos());
        TextField txCorreo    = tf(u.getCorreo());
        PasswordField txPass  = new PasswordField(); txPass.setPromptText("Nueva contraseña (opcional)"); txPass.getStyleClass().add("password-field");
        grid.addRow(0, lbl("Nombre:"), txNombre);
        grid.addRow(1, lbl("Apellidos:"), txApellidos);
        grid.addRow(2, lbl("Correo:"), txCorreo);
        grid.addRow(3, lbl("Contraseña:"), txPass);

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnG = new Button("Guardar cambios"); btnG.getStyleClass().add("btn-primary");
        btnG.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre no puede estar vacío."); return; }
            u.setNombre(txNombre.getText().trim()); u.setApellidos(txApellidos.getText().trim());
            u.setCorreo(txCorreo.getText().trim());
            if (!txPass.getText().isEmpty()) u.setContrasena(txPass.getText());
            gd.actualizarUsuario(u);
            lblBienvenida.setText("Hola, " + u.getNombre() + "!");
            DialogUtil.info("Perfil actualizado."); mostrarPerfil();
        });
        root.getChildren().addAll(titulo, card, lblEdit, grid, lblErr, btnG);
        setContent(scroll(root));
    }

    // ─────────────────────────────────────────────────────────
    // ESTADÍSTICAS – Panel con acordeón desplegable
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarEstadisticas() {
        VBox root = new VBox(12); root.setPadding(new Insets(10));
        Label titulo = new Label("📊 Estadísticas"); titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        root.getChildren().add(buildAcordeon("⭐ Juegos Mejor Valorados",  this::buildMejorValorados));
        root.getChildren().add(buildAcordeon("🔥 Juegos Más Vendidos",     this::buildMasVendidos));
        root.getChildren().add(buildAcordeon("💬 Reseñas por Idioma",      this::buildResenasPorIdioma));
        root.getChildren().add(buildAcordeon("🎮 Reseñas por Juego",       this::buildResenasPorJuego));
        root.getChildren().add(buildAcordeon("🏢 Estadísticas por Estudio",this::buildEstadisticasEstudio));
        root.getChildren().add(buildAcordeon("🛒 Juegos por Usuario",      this::buildJuegosPorUsuario));
        root.getChildren().add(buildAcordeon("✍ Reseñas por Usuario",      this::buildResenasPorUsuario));

        setContent(scroll(root));
    }

    /** Crea un panel acordeón: cabecera clicable que muestra/oculta el contenido. */
    private VBox buildAcordeon(String etiqueta, java.util.function.Supplier<Node> contenidoBuilder) {
        VBox panel = new VBox(0);
        panel.getStyleClass().add("card");

        // Cabecera
        HBox cabecera = new HBox(10);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(new Insets(10, 14, 10, 14));
        cabecera.setStyle("-fx-cursor: hand;");

        Label lblFlecha = new Label("▶"); lblFlecha.getStyleClass().add("label-gold");
        Label lblTitulo = new Label(etiqueta); lblTitulo.getStyleClass().add("label-section");
        cabecera.getChildren().addAll(lblFlecha, lblTitulo);

        // Contenedor del contenido (inicialmente oculto)
        VBox contenedorContenido = new VBox();
        contenedorContenido.setPadding(new Insets(0, 14, 10, 14));
        contenedorContenido.setVisible(false);
        contenedorContenido.setManaged(false);

        // Toggle al pulsar la cabecera
        cabecera.setOnMouseClicked(e -> {
            boolean abierto = contenedorContenido.isVisible();
            if (!abierto) {
                // Generar contenido solo al abrir (lazy)
                if (contenedorContenido.getChildren().isEmpty()) {
                    Node contenido = contenidoBuilder.get();
                    contenedorContenido.getChildren().add(contenido);
                }
                lblFlecha.setText("▼");
                contenedorContenido.setVisible(true);
                contenedorContenido.setManaged(true);
            } else {
                lblFlecha.setText("▶");
                contenedorContenido.setVisible(false);
                contenedorContenido.setManaged(false);
            }
        });

        panel.getChildren().addAll(cabecera, contenedorContenido);
        return panel;
    }

    // Constructores de contenido para cada sección del acordeón

    private Node buildMejorValorados() {
        VBox box = new VBox(8);
        List<Juego> lista = gd.getJuegosMejorValorados();
        if (lista.isEmpty()) { box.getChildren().add(new Label("Sin valoraciones todavía.")); return box; }
        int pos = 1;
        for (Juego j : lista) {
            double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
            int nRes = gd.getResenasPorJuego(j).size();
            HBox row = new HBox(10); row.setAlignment(Pos.CENTER_LEFT);
            Label lp = new Label("#" + pos++); lp.getStyleClass().add("label-gold"); lp.setMinWidth(28);
            VBox info = new VBox(2);
            Label lt = new Label(j.getTitulo()); lt.getStyleClass().add("label-section");
            Label lm = new Label(String.format("⭐ %.1f/10  (%d reseñas)", media, nRes)); lm.getStyleClass().add("label-normal");
            info.getChildren().addAll(lt, lm); row.getChildren().addAll(lp, info); box.getChildren().add(row);
        }
        return box;
    }

    private Node buildMasVendidos() {
        VBox box = new VBox(8); int pos = 1;
        for (Juego j : gd.getJuegosMasVendidos()) {
            int ventas = gd.getVentasJuego(j);
            HBox row = new HBox(10); row.setAlignment(Pos.CENTER_LEFT);
            Label lp = new Label("#" + pos++); lp.getStyleClass().add("label-gold"); lp.setMinWidth(28);
            VBox info = new VBox(2);
            Label lt = new Label(j.getTitulo()); lt.getStyleClass().add("label-section");
            Label lv = new Label("🔥 " + ventas + " unidades vendidas"); lv.getStyleClass().add("label-normal");
            info.getChildren().addAll(lt, lv); row.getChildren().addAll(lp, info); box.getChildren().add(row);
        }
        return box;
    }

    private Node buildResenasPorIdioma() {
        VBox box = new VBox(8);
        String[] idiomas = {"Español","English","Français","Deutsch","Português","Italiano"};
        boolean hay = false;
        for (String idioma : idiomas) {
            List<Resena> lista = gd.getResenasPorIdioma(idioma); if (lista.isEmpty()) continue; hay = true;
            // Sub-acordeón por idioma
            VBox subPanel = new VBox(0);
            subPanel.setStyle("-fx-background-color:#1a2545; -fx-background-radius:6;");
            HBox subCab = new HBox(8); subCab.setAlignment(Pos.CENTER_LEFT); subCab.setPadding(new Insets(6, 10, 6, 10)); subCab.setStyle("-fx-cursor:hand;");
            Label subFlecha = new Label("▶"); subFlecha.getStyleClass().add("label-gold");
            Label subTit = new Label("🌐 " + idioma + "  (" + lista.size() + " reseñas)"); subTit.getStyleClass().add("label-normal");
            subCab.getChildren().addAll(subFlecha, subTit);
            VBox subContenido = new VBox(4); subContenido.setPadding(new Insets(4, 10, 8, 22)); subContenido.setVisible(false); subContenido.setManaged(false);
            for (Resena r : lista) {
                Label l = new Label("• " + r.getJuego().getTitulo() + " – " + r.getAutor().getNombreCompleto() + "  ⭐" + r.getPuntuacion());
                l.getStyleClass().add("label-normal"); subContenido.getChildren().add(l);
            }
            subCab.setOnMouseClicked(e -> {
                boolean ab = subContenido.isVisible();
                subFlecha.setText(ab ? "▶" : "▼"); subContenido.setVisible(!ab); subContenido.setManaged(!ab);
            });
            subPanel.getChildren().addAll(subCab, subContenido); box.getChildren().add(subPanel);
        }
        if (!hay) box.getChildren().add(new Label("No hay reseñas."));
        return box;
    }

    private Node buildResenasPorJuego() {
        VBox box = new VBox(8); boolean hay = false;
        for (Juego j : gd.getJuegos()) {
            List<Resena> lista = gd.getResenasPorJuego(j); if (lista.isEmpty()) continue; hay = true;
            double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
            VBox subPanel = new VBox(0); subPanel.setStyle("-fx-background-color:#1a2545; -fx-background-radius:6;");
            HBox subCab = new HBox(8); subCab.setAlignment(Pos.CENTER_LEFT); subCab.setPadding(new Insets(6, 10, 6, 10)); subCab.setStyle("-fx-cursor:hand;");
            Label sf = new Label("▶"); sf.getStyleClass().add("label-gold");
            Label st = new Label("🎮 " + j.getTitulo() + String.format("  ⭐ %.1f/10  (%d reseñas)", media, lista.size())); st.getStyleClass().add("label-normal");
            subCab.getChildren().addAll(sf, st);
            VBox sc = new VBox(4); sc.setPadding(new Insets(4, 10, 8, 22)); sc.setVisible(false); sc.setManaged(false);
            for (Resena r : lista) {
                Label l = new Label("  • " + r.getAutor().getNombreCompleto() + "  ⭐" + r.getPuntuacion() + "/10  [" + r.getIdioma() + "]  – " + r.getComentario());
                l.getStyleClass().add("label-normal"); l.setWrapText(true); sc.getChildren().add(l);
            }
            subCab.setOnMouseClicked(e -> { boolean ab = sc.isVisible(); sf.setText(ab ? "▶" : "▼"); sc.setVisible(!ab); sc.setManaged(!ab); });
            subPanel.getChildren().addAll(subCab, sc); box.getChildren().add(subPanel);
        }
        if (!hay) box.getChildren().add(new Label("No hay reseñas."));
        return box;
    }

    private Node buildEstadisticasEstudio() {
        VBox box = new VBox(8);
        for (Estudio est : gd.getEstudios()) {
            VBox subPanel = new VBox(0); subPanel.setStyle("-fx-background-color:#1a2545; -fx-background-radius:6;");
            HBox subCab = new HBox(8); subCab.setAlignment(Pos.CENTER_LEFT); subCab.setPadding(new Insets(6, 10, 6, 10)); subCab.setStyle("-fx-cursor:hand;");
            Label sf = new Label("▶"); sf.getStyleClass().add("label-gold");
            Label st = new Label("🏢 " + est.getNombre()); st.getStyleClass().add("label-normal");
            subCab.getChildren().addAll(sf, st);
            VBox sc = new VBox(4); sc.setPadding(new Insets(4, 10, 8, 22)); sc.setVisible(false); sc.setManaged(false);

            Juego mv = gd.getJuegoMejorValoradoEstudio(est);
            Juego ms = gd.getJuegoMasVendidoEstudio(est);
            if (mv != null) { Label l = new Label("⭐ Mejor valorado: " + mv.getTitulo() + String.format("  (%.1f/10)", gd.getPuntuacionMediaJuego(mv.getIdJuego()))); l.getStyleClass().add("label-normal"); sc.getChildren().add(l); }
            if (ms != null) { Label l = new Label("🔥 Más vendido: " + ms.getTitulo() + "  (" + gd.getVentasJuego(ms) + " uds)"); l.getStyleClass().add("label-normal"); sc.getChildren().add(l); }

            subCab.setOnMouseClicked(e -> { boolean ab = sc.isVisible(); sf.setText(ab ? "▶" : "▼"); sc.setVisible(!ab); sc.setManaged(!ab); });
            subPanel.getChildren().addAll(subCab, sc); box.getChildren().add(subPanel);
        }
        return box;
    }

    private Node buildJuegosPorUsuario() {
        VBox box = new VBox(8);
        for (Usuario u : gd.getUsuarios()) {
            List<Juego> bib = gd.getBibliotecaUsuario(u.getIdUsuario());
            VBox subPanel = new VBox(0); subPanel.setStyle("-fx-background-color:#1a2545; -fx-background-radius:6;");
            HBox subCab = new HBox(8); subCab.setAlignment(Pos.CENTER_LEFT); subCab.setPadding(new Insets(6, 10, 6, 10)); subCab.setStyle("-fx-cursor:hand;");
            Label sf = new Label("▶"); sf.getStyleClass().add("label-gold");
            Label st = new Label("👤 " + u.getNombreCompleto() + "  (" + bib.size() + " juegos)"); st.getStyleClass().add("label-normal");
            subCab.getChildren().addAll(sf, st);
            VBox sc = new VBox(4); sc.setPadding(new Insets(4, 10, 8, 22)); sc.setVisible(false); sc.setManaged(false);
            if (bib.isEmpty()) sc.getChildren().add(new Label("Sin compras."));
            else for (Juego j : bib) { Label l = new Label("  • " + j.getTitulo() + " (" + j.getGenero() + ")"); l.getStyleClass().add("label-normal"); sc.getChildren().add(l); }
            subCab.setOnMouseClicked(e -> { boolean ab = sc.isVisible(); sf.setText(ab ? "▶" : "▼"); sc.setVisible(!ab); sc.setManaged(!ab); });
            subPanel.getChildren().addAll(subCab, sc); box.getChildren().add(subPanel);
        }
        return box;
    }

    private Node buildResenasPorUsuario() {
        VBox box = new VBox(8); boolean hay = false;
        for (Usuario u : gd.getUsuarios()) {
            List<Resena> lista = gd.getResenasPorUsuario(u); if (lista.isEmpty()) continue; hay = true;
            VBox subPanel = new VBox(0); subPanel.setStyle("-fx-background-color:#1a2545; -fx-background-radius:6;");
            HBox subCab = new HBox(8); subCab.setAlignment(Pos.CENTER_LEFT); subCab.setPadding(new Insets(6, 10, 6, 10)); subCab.setStyle("-fx-cursor:hand;");
            Label sf = new Label("▶"); sf.getStyleClass().add("label-gold");
            Label st = new Label("👤 " + u.getNombreCompleto() + "  (" + lista.size() + " reseñas)"); st.getStyleClass().add("label-normal");
            subCab.getChildren().addAll(sf, st);
            VBox sc = new VBox(4); sc.setPadding(new Insets(4, 10, 8, 22)); sc.setVisible(false); sc.setManaged(false);
            for (Resena r : lista) { Label l = new Label("  • " + r.getJuego().getTitulo() + "  ⭐" + r.getPuntuacion() + "/10  –  " + r.getComentario()); l.getStyleClass().add("label-normal"); l.setWrapText(true); sc.getChildren().add(l); }
            subCab.setOnMouseClicked(e -> { boolean ab = sc.isVisible(); sf.setText(ab ? "▶" : "▼"); sc.setVisible(!ab); sc.setManaged(!ab); });
            subPanel.getChildren().addAll(subCab, sc); box.getChildren().add(subPanel);
        }
        if (!hay) box.getChildren().add(new Label("No hay reseñas."));
        return box;
    }

    // Mantener estos métodos para compatibilidad con los botones del FXML existentes
    @FXML public void mostrarMejorValorados() { mostrarEstadisticas(); }
    @FXML public void mostrarMasVendidos()    { mostrarEstadisticas(); }

    // ─────────────────────────────────────────────────────────
    // AYUDA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarAyuda() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("❓ Sistema de Ayuda"); titulo.getStyleClass().add("label-title"); root.getChildren().add(titulo);
        String[][] secciones = {
            {"🔍 Buscar Juegos",       "Busca por nombre, género, director o estudio."},
            {"🎮 Catálogo",            "Muestra todos los juegos disponibles. Compra con tu saldo."},
            {"🛒 Historial de Compras","Consulta todas tus compras con fecha y coste total."},
            {"📚 Biblioteca",          "Lista los juegos que posees. Solo puedes reseñar juegos de tu biblioteca."},
            {"✍ Mis Reseñas",         "Escribe, edita o elimina tus reseñas. Puntúa del 1 al 10."},
            {"👤 Mi Perfil",           "Consulta y modifica tus datos personales."},
            {"📊 Estadísticas",        "Accede a rankings y estadísticas desplegables por categoría."}
        };
        for (String[] sec : secciones) {
            VBox card = new VBox(5); card.getStyleClass().add("card"); card.setPadding(new Insets(12));
            Label ls = new Label(sec[0]); ls.getStyleClass().add("label-section");
            Label ld = new Label(sec[1]); ld.getStyleClass().add("label-normal"); ld.setWrapText(true);
            card.getChildren().addAll(ls, ld); root.getChildren().add(card);
        }
        setContent(scroll(root));
    }

    // ─────────────────────────────────────────────────────────
    // CERRAR SESIÓN
    // ─────────────────────────────────────────────────────────
    @FXML public void cerrarSesion() {
        gd.cerrarSesion();
        try { App.setRoot("login"); } catch (IOException e) { e.printStackTrace(); }
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private Node buildCatalogo(List<Juego> lista) {
        VBox root = new VBox(12); root.setPadding(new Insets(10));
        if (lista.isEmpty()) { Label lbl = new Label("No se encontraron juegos."); lbl.getStyleClass().add("label-muted"); root.getChildren().add(lbl); }
        else for (Juego j : lista) root.getChildren().add(buildJuegoCard(j, true));
        return scroll(root);
    }

    private HBox buildJuegoCard(Juego j, boolean mostrarComprar) {
        HBox card = new HBox(15); card.getStyleClass().add("card"); card.setAlignment(Pos.CENTER_LEFT); card.setPadding(new Insets(12));
        VBox info = new VBox(4); HBox.setHgrow(info, Priority.ALWAYS);
        Label lt = new Label(j.getTitulo()); lt.getStyleClass().add("label-section");
        Label lm = new Label("🎮 " + j.getPlataforma() + "  |  📂 " + j.getGenero() + "  |  🎬 " + j.getDirector()); lm.getStyleClass().add("label-muted");
        double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
        Label lmed = new Label(media > 0 ? String.format("⭐ %.1f/10", media) : "Sin valoraciones"); lmed.getStyleClass().add("label-normal");
        String stxt = j.getStock() > 10 ? "✅ " + j.getStock() + " en stock" : j.getStock() > 0 ? "⚠ Solo " + j.getStock() + " en stock" : "❌ Sin stock";
        Label lstk = new Label(stxt); lstk.getStyleClass().add(j.getStock() > 10 ? "stock-ok" : j.getStock() > 0 ? "stock-low" : "stock-out");
        info.getChildren().addAll(lt, lm, lmed, lstk);

        VBox acciones = new VBox(6); acciones.setAlignment(Pos.CENTER);
        Label lprecio = new Label(String.format("%.2f€", j.getPrecio())); lprecio.getStyleClass().add("price-badge");
        Button btnRes = new Button("Ver reseñas"); btnRes.getStyleClass().add("btn-secondary");
        btnRes.setOnAction(e -> mostrarResenasDe(j));

        if (mostrarComprar) {
            boolean posee = gd.usuarioPoseeJuego(gd.getUsuarioActual().getIdUsuario(), j.getIdJuego());
            if (posee) {
                Label lp = new Label("✅ En biblioteca"); lp.getStyleClass().add("stock-ok");
                acciones.getChildren().addAll(lprecio, lp, btnRes);
            } else {
                Button btnC = new Button("🛒 Comprar"); btnC.getStyleClass().add("btn-primary");
                btnC.setDisable(j.getStock() <= 0);
                btnC.setOnAction(e -> comprarJuego(j));
                acciones.getChildren().addAll(lprecio, btnC, btnRes);
            }
        } else {
            Button btnEsc = new Button("✍ Reseñar"); btnEsc.getStyleClass().add("btn-gold");
            btnEsc.setOnAction(e -> mostrarFormularioResena(null));
            acciones.getChildren().addAll(lprecio, btnRes, btnEsc);
        }
        card.getChildren().addAll(info, acciones); return card;
    }

    private void comprarJuego(Juego j) {
        String resultado = gd.comprarJuego(gd.getUsuarioActual(), j, 1);
        if ("OK".equals(resultado)) {
            actualizarSaldo();
            DialogUtil.info("¡Compra realizada! " + j.getTitulo() + " añadido a tu biblioteca.");
            mostrarCatalogo();
        } else { DialogUtil.error(resultado); }
    }

    private void mostrarResenasDe(Juego j) {
        List<Resena> lista = gd.getResenasPorJuego(j);
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("Reseñas de: " + j.getTitulo()); titulo.getStyleClass().add("label-title");
        Button btnVolver = new Button("← Volver al catálogo"); btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> mostrarCatalogo());
        root.getChildren().addAll(titulo, btnVolver);
        if (lista.isEmpty()) { Label lbl = new Label("Este juego no tiene reseñas todavía."); lbl.getStyleClass().add("label-muted"); root.getChildren().add(lbl); }
        else for (Resena r : lista) root.getChildren().add(buildResenaCard(r, false));
        setContent(scroll(root));
    }

    private VBox buildResenaCard(Resena r, boolean conAcciones) {
        VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(12));
        HBox cabecera = new HBox(10);
        Label la = new Label("👤 " + r.getAutor().getNombreCompleto()); la.getStyleClass().add("label-section");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label lp = new Label("⭐ " + r.getPuntuacion() + "/10"); lp.getStyleClass().add("label-gold");
        Label lf = new Label(r.getFechaFormateada()); lf.getStyleClass().add("label-muted");
        cabecera.getChildren().addAll(la, sp, lp, lf);
        Label lj = new Label("🎮 " + r.getJuego().getTitulo()); lj.getStyleClass().add("label-muted");
        Label lc = new Label(r.getComentario()); lc.getStyleClass().add("label-normal"); lc.setWrapText(true);
        card.getChildren().addAll(cabecera, lj, lc);

        if (conAcciones && r.getAutor().getCorreo().equals(gd.getUsuarioActual().getCorreo())) {
            Button btnE = new Button("✏ Editar"); btnE.getStyleClass().add("btn-secondary");
            Button btnD = new Button("🗑 Eliminar"); btnD.getStyleClass().add("btn-danger");
            btnE.setOnAction(e -> mostrarFormularioResena(r));
            btnD.setOnAction(e -> { if (DialogUtil.confirmar("¿Eliminar esta reseña?")) { gd.eliminarResena(r.getIdResena()); mostrarMisResenas(); }});
            card.getChildren().add(new HBox(8, btnE, btnD));
        }
        return card;
    }

    private HBox parLabel(String etiqueta, String valor) {
        HBox row = new HBox(10);
        Label e = new Label(etiqueta); e.getStyleClass().add("label-muted"); e.setMinWidth(190);
        Label v = new Label(valor);    v.getStyleClass().add("label-normal");
        row.getChildren().addAll(e, v); return row;
    }

    private TextField tf(String v) { TextField tf = new TextField(v); tf.getStyleClass().add("text-field"); return tf; }
    private Label lbl(String t)   { Label l = new Label(t); l.getStyleClass().add("label-normal"); return l; }
    private ScrollPane scroll(Node n) { ScrollPane sp = new ScrollPane(n); sp.setFitToWidth(true); sp.getStyleClass().add("scroll-pane"); return sp; }

    private <T> void col(TableView<T> tabla, String nombre, java.util.function.Function<T,String> getter, double width) {
        TableColumn<T,String> col = new TableColumn<>(nombre);
        col.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getter.apply(c.getValue())));
        col.setPrefWidth(width); tabla.getColumns().add(col);
    }
}
