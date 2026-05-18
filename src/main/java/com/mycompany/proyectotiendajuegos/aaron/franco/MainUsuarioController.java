package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.App;
import com.mycompany.proyectotiendajuegos.aaron.franco.datos.GestorDatos;
import com.mycompany.proyectotiendajuegos.aaron.franco.modelo.*;
import com.mycompany.proyectotiendajuegos.aaron.franco.util.DialogUtil;
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
    @FXML private Label lblBienvenida, lblSaldo;

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

    private void setContent(Node node) {
        contentPane.getChildren().setAll(node);
    }

    // ─────────────────────────────────────────────────────────
    // CATÁLOGO / BÚSQUEDA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarCatalogo() {
        setContent(buildCatalogo(gd.getJuegos()));
    }

    @FXML public void mostrarBuscarJuegos() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label titulo = new Label("🔍 Buscar Juegos");
        titulo.getStyleClass().add("label-title");

        HBox barra = new HBox(10);
        TextField txBusqueda = new TextField();
        txBusqueda.setPromptText("Nombre, género o director...");
        txBusqueda.setPrefWidth(300);
        txBusqueda.getStyleClass().add("text-field");

        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList("Nombre", "Género", "Director"));
        cbTipo.getSelectionModel().selectFirst();
        cbTipo.getStyleClass().add("combo-box");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("btn-primary");

        barra.getChildren().addAll(txBusqueda, cbTipo, btnBuscar);

        VBox resultados = new VBox(10);
        btnBuscar.setOnAction(e -> {
            String texto = txBusqueda.getText().trim();
            if (texto.isEmpty()) return;
            List<Juego> lista;
            switch (cbTipo.getValue()) {
                case "Género":   lista = gd.buscarJuegosPorGenero(texto); break;
                case "Director": lista = gd.buscarJuegosPorDirector(texto); break;
                default:         lista = gd.buscarJuegosPorNombre(texto);
            }
            resultados.getChildren().setAll(buildCatalogo(lista));
        });

        root.getChildren().addAll(titulo, barra, resultados);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    // ─────────────────────────────────────────────────────────
    // HISTORIAL DE COMPRAS
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarHistorialCompras() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label titulo = new Label("🛒 Historial de Compras");
        titulo.getStyleClass().add("label-title");

        TableView<Compra> tabla = new TableView<>();
        tabla.getStyleClass().add("table-view");
        tabla.setPrefHeight(400);

        TableColumn<Compra, String> colJuego = new TableColumn<>("Juego");
        colJuego.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getJuego().getTitulo()));
        colJuego.setPrefWidth(200);

        TableColumn<Compra, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getFechaFormateada()));
        colFecha.setPrefWidth(100);

        TableColumn<Compra, String> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getCantidad())));
        colCant.setPrefWidth(80);

        TableColumn<Compra, String> colCoste = new TableColumn<>("Coste");
        colCoste.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(String.format("%.2f€", c.getValue().getCoste())));
        colCoste.setPrefWidth(100);

        tabla.getColumns().addAll(colJuego, colFecha, colCant, colCoste);
        tabla.setItems(FXCollections.observableArrayList(gd.getUsuarioActual().getHistorialCompras()));

        double totalGastado = gd.getUsuarioActual().getHistorialCompras().stream()
                .mapToDouble(Compra::getCoste).sum();
        Label lblTotal = new Label(String.format("Total gastado: %.2f€", totalGastado));
        lblTotal.getStyleClass().add("label-gold");

        root.getChildren().addAll(titulo, tabla, lblTotal);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    // ─────────────────────────────────────────────────────────
    // BIBLIOTECA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarBiblioteca() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label titulo = new Label("📚 Mi Biblioteca");
        titulo.getStyleClass().add("label-title");

        List<Juego> biblioteca = gd.getUsuarioActual().getBiblioteca();
        if (biblioteca.isEmpty()) {
            root.getChildren().addAll(titulo, new Label("No tienes juegos en tu biblioteca todavía."));
        } else {
            root.getChildren().add(titulo);
            for (Juego j : biblioteca) {
                HBox card = buildJuegoCard(j, false);
                root.getChildren().add(card);
            }
        }

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    // ─────────────────────────────────────────────────────────
    // MIS RESEÑAS
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarMisResenas() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label titulo = new Label("✍ Mis Reseñas");
        titulo.getStyleClass().add("label-title");

        List<Resena> misResenas = gd.getResenasPorUsuario(gd.getUsuarioActual());

        if (misResenas.isEmpty()) {
            Label lbl = new Label("No has escrito ninguna reseña todavía.");
            lbl.getStyleClass().add("label-muted");
            root.getChildren().addAll(titulo, lbl);
        } else {
            root.getChildren().add(titulo);
            for (Resena r : misResenas) {
                VBox card = buildResenaCard(r, true);
                root.getChildren().add(card);
            }
        }

        // Botón para nueva reseña
        Button btnNueva = new Button("+ Nueva Reseña");
        btnNueva.getStyleClass().add("btn-primary");
        btnNueva.setOnAction(e -> mostrarFormularioResena(null));
        root.getChildren().add(btnNueva);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    private void mostrarFormularioResena(Resena resenaEditar) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(500);
        form.getStyleClass().add("card");

        Label titulo = new Label(resenaEditar == null ? "Nueva Reseña" : "Editar Reseña");
        titulo.getStyleClass().add("label-section");

        // Selector de juego (solo los que posee)
        ComboBox<Juego> cbJuego = new ComboBox<>(
                FXCollections.observableArrayList(gd.getUsuarioActual().getBiblioteca()));
        cbJuego.setPromptText("Selecciona un juego");
        cbJuego.getStyleClass().add("combo-box");
        cbJuego.setMaxWidth(Double.MAX_VALUE);

        TextArea txComentario = new TextArea();
        txComentario.setPromptText("Escribe tu reseña...");
        txComentario.setPrefRowCount(4);
        txComentario.getStyleClass().add("text-area");

        Spinner<Integer> spinPunt = new Spinner<>(1, 10, 8);
        spinPunt.getStyleClass().add("spinner");

        ComboBox<String> cbIdioma = new ComboBox<>(FXCollections.observableArrayList(
                "Español", "English", "Français", "Deutsch", "Português"));
        cbIdioma.getSelectionModel().select(gd.getUsuarioActual().getIdioma());
        cbIdioma.getStyleClass().add("combo-box");

        if (resenaEditar != null) {
            cbJuego.getSelectionModel().select(resenaEditar.getJuego());
            cbJuego.setDisable(true);
            txComentario.setText(resenaEditar.getComentario());
            spinPunt.getValueFactory().setValue(resenaEditar.getPuntuacion());
            cbIdioma.getSelectionModel().select(resenaEditar.getIdioma());
        }

        Label lblErr = new Label();
        lblErr.getStyleClass().add("label-accent");

        Button btnGuardar = new Button("Guardar");
        btnGuardar.getStyleClass().add("btn-primary");
        btnGuardar.setOnAction(e -> {
            if (cbJuego.getValue() == null) { lblErr.setText("Selecciona un juego."); return; }
            if (txComentario.getText().trim().isEmpty()) { lblErr.setText("Escribe un comentario."); return; }

            if (resenaEditar != null) {
                resenaEditar.setComentario(txComentario.getText().trim());
                resenaEditar.setPuntuacion(spinPunt.getValue());
                resenaEditar.setIdioma(cbIdioma.getValue());
                DialogUtil.info("Reseña actualizada correctamente.");
            } else {
                String resultado = gd.anadirResena(gd.getUsuarioActual(), cbJuego.getValue(),
                        txComentario.getText().trim(), spinPunt.getValue(), cbIdioma.getValue());
                if (!"OK".equals(resultado)) { lblErr.setText(resultado); return; }
                DialogUtil.info("Reseña publicada correctamente.");
            }
            mostrarMisResenas();
        });

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("btn-secondary");
        btnCancelar.setOnAction(e -> mostrarMisResenas());

        HBox botonesBox = new HBox(10, btnGuardar, btnCancelar);

        form.getChildren().addAll(
                titulo,
                new Label("Juego:"), cbJuego,
                new Label("Puntuación (1-10):"), spinPunt,
                new Label("Idioma:"), cbIdioma,
                new Label("Comentario:"), txComentario,
                lblErr, botonesBox
        );
        for (Node n : form.getChildren()) {
            if (n instanceof Label && !((Label)n).getStyleClass().contains("label-section"))
                ((Label)n).getStyleClass().add("label-normal");
        }

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    // ─────────────────────────────────────────────────────────
    // PERFIL
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarPerfil() {
        Usuario u = gd.getUsuarioActual();
        VBox root = new VBox(20);
        root.setPadding(new Insets(10));

        Label titulo = new Label("👤 Mi Perfil");
        titulo.getStyleClass().add("label-title");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setMaxWidth(450);

        card.getChildren().addAll(
                labelPar("ID:", String.valueOf(u.getIdUsuario())),
                labelPar("Nombre:", u.getNombreCompleto()),
                labelPar("Correo:", u.getCorreo()),
                labelPar("Idioma:", u.getIdioma()),
                labelPar("Saldo:", String.format("%.2f€", u.getSaldo())),
                labelPar("Juegos en biblioteca:", String.valueOf(u.getBiblioteca().size())),
                labelPar("Compras realizadas:", String.valueOf(u.getHistorialCompras().size())),
                labelPar("Reseñas escritas:", String.valueOf(gd.getResenasPorUsuario(u).size()))
        );

        // Editar perfil
        Label lblSecEd = new Label("Editar datos");
        lblSecEd.getStyleClass().add("label-section");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);

        TextField txNombre = new TextField(u.getNombre());
        TextField txApellidos = new TextField(u.getApellidos());
        TextField txCorreo = new TextField(u.getCorreo());
        PasswordField txPass = new PasswordField();
        txPass.setPromptText("Nueva contraseña (opcional)");

        for (TextField tf : new TextField[]{txNombre, txApellidos, txCorreo})
            tf.getStyleClass().add("text-field");
        txPass.getStyleClass().add("password-field");

        grid.addRow(0, lbl("Nombre:"), txNombre);
        grid.addRow(1, lbl("Apellidos:"), txApellidos);
        grid.addRow(2, lbl("Correo:"), txCorreo);
        grid.addRow(3, lbl("Contraseña:"), txPass);

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");

        Button btnGuardar = new Button("Guardar cambios");
        btnGuardar.getStyleClass().add("btn-primary");
        btnGuardar.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre no puede estar vacío."); return; }
            u.setNombre(txNombre.getText().trim());
            u.setApellidos(txApellidos.getText().trim());
            u.setCorreo(txCorreo.getText().trim());
            if (!txPass.getText().isEmpty()) u.setContrasena(txPass.getText());
            lblBienvenida.setText("Hola, " + u.getNombre() + "!");
            DialogUtil.info("Perfil actualizado.");
            mostrarPerfil();
        });

        root.getChildren().addAll(titulo, card, lblSecEd, grid, lblErr, btnGuardar);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    // ─────────────────────────────────────────────────────────
    // ESTADÍSTICAS
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarMejorValorados() {
        List<Juego> lista = gd.getJuegosMejorValorados();
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label titulo = new Label("⭐ Juegos Mejor Valorados");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        int pos = 1;
        for (Juego j : lista) {
            HBox row = new HBox(15);
            row.getStyleClass().add("card");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));

            Label lblPos = new Label("#" + pos++);
            lblPos.getStyleClass().add("label-gold");
            lblPos.setMinWidth(30);

            VBox info = new VBox(3);
            Label lblTitulo = new Label(j.getTitulo());
            lblTitulo.getStyleClass().add("label-section");
            Label lblMedia = new Label(String.format("⭐ %.1f/10  (%d reseñas)", j.getPuntuacionMedia(), j.getResenas().size()));
            lblMedia.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblTitulo, lblMedia);

            row.getChildren().addAll(lblPos, info);
            root.getChildren().add(row);
        }

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    @FXML public void mostrarMasVendidos() {
        List<Juego> lista = gd.getJuegosMasVendidos();
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label titulo = new Label("🔥 Juegos Más Vendidos");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        int pos = 1;
        for (Juego j : lista) {
            HBox row = new HBox(15);
            row.getStyleClass().add("card");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));

            Label lblPos = new Label("#" + pos++);
            lblPos.getStyleClass().add("label-gold");
            lblPos.setMinWidth(30);

            VBox info = new VBox(3);
            Label lblTitulo = new Label(j.getTitulo());
            lblTitulo.getStyleClass().add("label-section");
            int ventas = gd.getVentasJuego(j);
            Label lblVentas = new Label("🔥 " + ventas + " unidades vendidas");
            lblVentas.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblTitulo, lblVentas);

            row.getChildren().addAll(lblPos, info);
            root.getChildren().add(row);
        }

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    // ─────────────────────────────────────────────────────────
    // AYUDA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarAyuda() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label titulo = new Label("❓ Sistema de Ayuda");
        titulo.getStyleClass().add("label-title");

        String[] secciones = {
                "🔍 Buscar Juegos", "Usa el buscador para encontrar juegos por nombre, género o director. Selecciona el tipo de búsqueda y escribe el texto.",
                "🎮 Catálogo", "Muestra todos los juegos disponibles. Puedes comprar un juego directamente desde la tarjeta si tienes saldo suficiente.",
                "🛒 Historial de Compras", "Consulta todas tus compras anteriores con fecha, cantidad y coste total.",
                "📚 Biblioteca", "Lista de todos los juegos que posees. Solo puedes reseñar juegos de tu biblioteca.",
                "✍ Reseñas", "Escribe reseñas de los juegos que posees. Puntúa del 1 al 10 y añade un comentario.",
                "👤 Perfil", "Consulta y modifica tus datos personales: nombre, correo y contraseña.",
                "⭐ Mejor Valorados", "Ranking de juegos por puntuación media de reseñas.",
                "🔥 Más Vendidos", "Ranking de juegos por número total de unidades vendidas."
        };

        for (int i = 0; i < secciones.length; i += 2) {
            VBox card = new VBox(5);
            card.getStyleClass().add("card");
            card.setPadding(new Insets(12));

            Label lSec = new Label(secciones[i]);
            lSec.getStyleClass().add("label-section");
            Label lDesc = new Label(secciones[i + 1]);
            lDesc.getStyleClass().add("label-normal");
            lDesc.setWrapText(true);

            card.getChildren().addAll(lSec, lDesc);
            root.getChildren().add(card);
        }

        root.getChildren().add(0, titulo);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    // ─────────────────────────────────────────────────────────
    // CERRAR SESIÓN
    // ─────────────────────────────────────────────────────────
    @FXML public void cerrarSesion() {
        gd.cerrarSesion();
        try { App.setRoot("login"); }
        catch (IOException e) { e.printStackTrace(); }
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS UI
    // ─────────────────────────────────────────────────────────
    private Node buildCatalogo(List<Juego> lista) {
        VBox root = new VBox(12);
        root.setPadding(new Insets(10));

        if (lista.isEmpty()) {
            Label lbl = new Label("No se encontraron juegos.");
            lbl.getStyleClass().add("label-muted");
            root.getChildren().add(lbl);
        } else {
            for (Juego j : lista) {
                root.getChildren().add(buildJuegoCard(j, true));
            }
        }

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        return scroll;
    }

    private HBox buildJuegoCard(Juego j, boolean mostrarComprar) {
        HBox card = new HBox(15);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12));

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblTitulo = new Label(j.getTitulo());
        lblTitulo.getStyleClass().add("label-section");

        Label lblMeta = new Label("🎮 " + j.getPlataforma() + "  |  📂 " + j.getGenero()
                + "  |  🎬 " + j.getDirector());
        lblMeta.getStyleClass().add("label-muted");

        double media = j.getPuntuacionMedia();
        String estrellas = media > 0 ? String.format("⭐ %.1f/10", media) : "Sin valoraciones";
        Label lblMedia = new Label(estrellas);
        lblMedia.getStyleClass().add("label-normal");

        String stockTxt = j.getStock() > 10 ? "✅ " + j.getStock() + " en stock" :
                          j.getStock() > 0  ? "⚠ " + j.getStock() + " en stock" : "❌ Sin stock";
        Label lblStock = new Label(stockTxt);
        lblStock.getStyleClass().add(j.getStock() > 10 ? "stock-ok" : j.getStock() > 0 ? "stock-low" : "stock-out");

        info.getChildren().addAll(lblTitulo, lblMeta, lblMedia, lblStock);

        VBox acciones = new VBox(6);
        acciones.setAlignment(Pos.CENTER);

        Label lblPrecio = new Label(String.format("%.2f€", j.getPrecio()));
        lblPrecio.getStyleClass().add("price-badge");

        if (mostrarComprar) {
            if (gd.getUsuarioActual().poseeJuego(j)) {
                Label lblPoseido = new Label("✅ En biblioteca");
                lblPoseido.getStyleClass().add("stock-ok");
                Button btnResenas = new Button("Ver reseñas");
                btnResenas.getStyleClass().add("btn-secondary");
                btnResenas.setOnAction(e -> mostrarResenasDe(j));
                acciones.getChildren().addAll(lblPrecio, lblPoseido, btnResenas);
            } else {
                Button btnComprar = new Button("🛒 Comprar");
                btnComprar.getStyleClass().add("btn-primary");
                btnComprar.setDisable(j.getStock() <= 0);
                btnComprar.setOnAction(e -> comprarJuego(j));

                Button btnResenas = new Button("Ver reseñas");
                btnResenas.getStyleClass().add("btn-secondary");
                btnResenas.setOnAction(e -> mostrarResenasDe(j));
                acciones.getChildren().addAll(lblPrecio, btnComprar, btnResenas);
            }
        } else {
            Button btnResenas = new Button("Ver reseñas");
            btnResenas.getStyleClass().add("btn-secondary");
            btnResenas.setOnAction(e -> mostrarResenasDe(j));
            Button btnNuevaResena = new Button("✍ Escribir reseña");
            btnNuevaResena.getStyleClass().add("btn-gold");
            btnNuevaResena.setOnAction(e -> mostrarFormularioResena(null));
            acciones.getChildren().addAll(lblPrecio, btnResenas, btnNuevaResena);
        }

        card.getChildren().addAll(info, acciones);
        return card;
    }

    private void comprarJuego(Juego j) {
        String resultado = gd.comprarJuego(gd.getUsuarioActual(), j, 1);
        if ("OK".equals(resultado)) {
            actualizarSaldo();
            DialogUtil.info("¡Compra realizada! " + j.getTitulo() + " añadido a tu biblioteca.");
            mostrarCatalogo();
        } else {
            DialogUtil.error(resultado);
        }
    }

    private void mostrarResenasDe(Juego j) {
        List<Resena> lista = gd.getResenasPorJuego(j);
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label titulo = new Label("Reseñas de: " + j.getTitulo());
        titulo.getStyleClass().add("label-title");

        Button btnVolver = new Button("← Volver al catálogo");
        btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> mostrarCatalogo());

        root.getChildren().addAll(titulo, btnVolver);

        if (lista.isEmpty()) {
            Label lbl = new Label("Este juego no tiene reseñas todavía.");
            lbl.getStyleClass().add("label-muted");
            root.getChildren().add(lbl);
        } else {
            for (Resena r : lista) {
                root.getChildren().add(buildResenaCard(r, false));
            }
        }

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");
        setContent(scroll);
    }

    private VBox buildResenaCard(Resena r, boolean conAcciones) {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(12));

        HBox cabecera = new HBox(10);
        Label lblAutor = new Label("👤 " + r.getAutor().getNombreCompleto());
        lblAutor.getStyleClass().add("label-section");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label lblPunt = new Label("⭐ " + r.getPuntuacion() + "/10");
        lblPunt.getStyleClass().add("label-gold");
        Label lblFecha = new Label(r.getFechaFormateada());
        lblFecha.getStyleClass().add("label-muted");
        cabecera.getChildren().addAll(lblAutor, sp, lblPunt, lblFecha);

        Label lblJuego = new Label("🎮 " + r.getJuego().getTitulo());
        lblJuego.getStyleClass().add("label-muted");

        Label lblComentario = new Label(r.getComentario());
        lblComentario.getStyleClass().add("label-normal");
        lblComentario.setWrapText(true);

        card.getChildren().addAll(cabecera, lblJuego, lblComentario);

        if (conAcciones && r.getAutor() == gd.getUsuarioActual()) {
            HBox btns = new HBox(8);
            Button btnEditar = new Button("✏ Editar");
            btnEditar.getStyleClass().add("btn-secondary");
            btnEditar.setOnAction(e -> mostrarFormularioResena(r));

            Button btnEliminar = new Button("🗑 Eliminar");
            btnEliminar.getStyleClass().add("btn-danger");
            btnEliminar.setOnAction(e -> {
                if (DialogUtil.confirmar("¿Eliminar esta reseña?")) {
                    gd.eliminarResena(r.getIdResena());
                    mostrarMisResenas();
                }
            });
            btns.getChildren().addAll(btnEditar, btnEliminar);
            card.getChildren().add(btns);
        }

        return card;
    }

    private HBox labelPar(String etiqueta, String valor) {
        HBox row = new HBox(10);
        Label lEtiq = new Label(etiqueta);
        lEtiq.getStyleClass().add("label-muted");
        lEtiq.setMinWidth(170);
        Label lVal = new Label(valor);
        lVal.getStyleClass().add("label-normal");
        row.getChildren().addAll(lEtiq, lVal);
        return row;
    }

    private Label lbl(String txt) {
        Label l = new Label(txt);
        l.getStyleClass().add("label-normal");
        return l;
    }
}
