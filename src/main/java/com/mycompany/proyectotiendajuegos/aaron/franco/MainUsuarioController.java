package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Juego;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Resena;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Usuario;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Compra;
import com.mycompany.proyectotiendajuegos.aaron.franco.App;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.GestorDatos;
import com.mycompany.proyectotiendajuegos.aaron.franco.DialogUtil;
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
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("🔍 Buscar Juegos");
        titulo.getStyleClass().add("label-title");

        HBox barra = new HBox(10);
        TextField txBusqueda = new TextField();
        txBusqueda.setPromptText("Nombre, género, director o estudio...");
        txBusqueda.setPrefWidth(280);
        txBusqueda.getStyleClass().add("text-field");
        ComboBox<String> cbTipo = new ComboBox<>(FXCollections.observableArrayList(
                "Nombre", "Género", "Director", "Estudio"));
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
                case "Estudio":  lista = gd.buscarJuegosPorEstudio(texto); break;
                default:         lista = gd.buscarJuegosPorNombre(texto);
            }
            resultados.getChildren().setAll(buildCatalogo(lista));
        });
        // Enter también busca
        txBusqueda.setOnAction(e -> btnBuscar.fire());

        root.getChildren().addAll(titulo, barra, resultados);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
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
        tabla.setPrefHeight(380);

        col(tabla, "Juego",    c -> c.getJuego().getTitulo(),        200);
        col(tabla, "Fecha",    c -> c.getFechaFormateada(),           100);
        col(tabla, "Cantidad", c -> String.valueOf(c.getCantidad()),   80);
        col(tabla, "Coste",    c -> String.format("%.2f€", c.getCoste()), 100);

        tabla.setItems(FXCollections.observableArrayList(gd.getUsuarioActual().getHistorialCompras()));

        double totalGastado = gd.getUsuarioActual().getHistorialCompras()
                .stream().mapToDouble(Compra::getCoste).sum();
        Label lblTotal = new Label(String.format("Total gastado: %.2f€", totalGastado));
        lblTotal.getStyleClass().add("label-gold");

        root.getChildren().addAll(titulo, tabla, lblTotal);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
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
            Label lbl = new Label("No tienes juegos en tu biblioteca todavía.");
            lbl.getStyleClass().add("label-muted");
            root.getChildren().addAll(titulo, lbl);
        } else {
            root.getChildren().add(titulo);
            for (Juego j : biblioteca) root.getChildren().add(buildJuegoCard(j, false));
        }

        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
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
        Button btnNueva = new Button("+ Nueva Reseña");
        btnNueva.getStyleClass().add("btn-primary");
        btnNueva.setOnAction(e -> mostrarFormularioResena(null));
        root.getChildren().addAll(titulo, btnNueva);

        if (misResenas.isEmpty()) {
            Label lbl = new Label("Aún no has escrito ninguna reseña.");
            lbl.getStyleClass().add("label-muted");
            root.getChildren().add(lbl);
        } else {
            for (Resena r : misResenas) root.getChildren().add(buildResenaCard(r, true));
        }

        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    private void mostrarFormularioResena(Resena resenaEditar) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(520);
        form.getStyleClass().add("card");

        Label titulo = new Label(resenaEditar == null ? "Nueva Reseña" : "Editar Reseña");
        titulo.getStyleClass().add("label-section");

        ComboBox<Juego> cbJuego = new ComboBox<>(
                FXCollections.observableArrayList(gd.getUsuarioActual().getBiblioteca()));
        cbJuego.setPromptText("Selecciona un juego de tu biblioteca");
        cbJuego.getStyleClass().add("combo-box");
        cbJuego.setMaxWidth(Double.MAX_VALUE);

        Spinner<Integer> spinPunt = new Spinner<>(1, 10, 8);
        spinPunt.getStyleClass().add("spinner");

        ComboBox<String> cbIdioma = new ComboBox<>(FXCollections.observableArrayList(
                "Español", "English", "Français", "Deutsch", "Português", "Italiano"));
        cbIdioma.getSelectionModel().select(gd.getUsuarioActual().getIdioma());
        cbIdioma.getStyleClass().add("combo-box");

        TextArea txComentario = new TextArea();
        txComentario.setPromptText("Escribe tu reseña aquí...");
        txComentario.setPrefRowCount(4);
        txComentario.getStyleClass().add("text-area");
        txComentario.setWrapText(true);

        if (resenaEditar != null) {
            cbJuego.getSelectionModel().select(resenaEditar.getJuego());
            cbJuego.setDisable(true);
            spinPunt.getValueFactory().setValue(resenaEditar.getPuntuacion());
            cbIdioma.getSelectionModel().select(resenaEditar.getIdioma());
            txComentario.setText(resenaEditar.getComentario());
        }

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");

        Button btnGuardar  = new Button("Guardar");  btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancelar = new Button("Cancelar"); btnCancelar.getStyleClass().add("btn-secondary");
        btnCancelar.setOnAction(e -> mostrarMisResenas());

        btnGuardar.setOnAction(e -> {
            if (cbJuego.getValue() == null)           { lblErr.setText("Selecciona un juego."); return; }
            if (txComentario.getText().trim().isEmpty()) { lblErr.setText("Escribe un comentario."); return; }

            if (resenaEditar != null) {
                resenaEditar.setComentario(txComentario.getText().trim());
                resenaEditar.setPuntuacion(spinPunt.getValue());
                resenaEditar.setIdioma(cbIdioma.getValue());
                DialogUtil.info("Reseña actualizada.");
            } else {
                String resultado = gd.anadirResena(gd.getUsuarioActual(), cbJuego.getValue(),
                        txComentario.getText().trim(), spinPunt.getValue(), cbIdioma.getValue());
                if (!"OK".equals(resultado)) { lblErr.setText(resultado); return; }
                DialogUtil.info("Reseña publicada.");
            }
            mostrarMisResenas();
        });

        form.getChildren().addAll(titulo,
                lbl("Juego:"), cbJuego,
                lbl("Puntuación (1-10):"), spinPunt,
                lbl("Idioma:"), cbIdioma,
                lbl("Comentario:"), txComentario,
                lblErr, new HBox(10, btnGuardar, btnCancelar));
        setContent(new ScrollPane(form) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
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
        card.setMaxWidth(460);
        card.getChildren().addAll(
                parLabel("ID:",                  String.valueOf(u.getIdUsuario())),
                parLabel("Nombre:",              u.getNombreCompleto()),
                parLabel("Correo:",              u.getCorreo()),
                parLabel("Idioma:",              u.getIdioma()),
                parLabel("Saldo:",               String.format("%.2f€", u.getSaldo())),
                parLabel("Juegos en biblioteca:", String.valueOf(u.getBiblioteca().size())),
                parLabel("Compras realizadas:",  String.valueOf(u.getHistorialCompras().size())),
                parLabel("Reseñas escritas:",    String.valueOf(gd.getResenasPorUsuario(u).size()))
        );

        Label lblEdit = new Label("✏ Editar datos"); lblEdit.getStyleClass().add("label-section");

        GridPane grid = new GridPane(); grid.setHgap(10); grid.setVgap(8);
        TextField txNombre    = tf(u.getNombre());
        TextField txApellidos = tf(u.getApellidos());
        TextField txCorreo    = tf(u.getCorreo());
        PasswordField txPass  = new PasswordField();
        txPass.setPromptText("Nueva contraseña (opcional)");
        txPass.getStyleClass().add("password-field");
        grid.addRow(0, lbl("Nombre:"), txNombre);
        grid.addRow(1, lbl("Apellidos:"), txApellidos);
        grid.addRow(2, lbl("Correo:"), txCorreo);
        grid.addRow(3, lbl("Contraseña:"), txPass);

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnGuardar = new Button("Guardar cambios"); btnGuardar.getStyleClass().add("btn-primary");
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

        root.getChildren().addAll(titulo, card, lblEdit, grid, lblErr, btnGuardar);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ─────────────────────────────────────────────────────────
    // ESTADÍSTICAS PÚBLICAS
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarMejorValorados() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("⭐ Juegos Mejor Valorados");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        List<Juego> lista = gd.getJuegosMejorValorados();
        if (lista.isEmpty()) {
            root.getChildren().add(new Label("Aún no hay reseñas."));
        } else {
            int pos = 1;
            for (Juego j : lista) {
                HBox row = new HBox(15);
                row.getStyleClass().add("card");
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                Label lblPos = new Label("#" + pos++); lblPos.getStyleClass().add("label-gold"); lblPos.setMinWidth(30);
                VBox info = new VBox(3);
                Label lblT = new Label(j.getTitulo()); lblT.getStyleClass().add("label-section");
                Label lblM = new Label(String.format("⭐ %.1f/10  (%d reseñas)", j.getPuntuacionMedia(), j.getResenas().size()));
                lblM.getStyleClass().add("label-normal");
                info.getChildren().addAll(lblT, lblM);
                row.getChildren().addAll(lblPos, info);
                root.getChildren().add(row);
            }
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    @FXML public void mostrarMasVendidos() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("🔥 Juegos Más Vendidos");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        int pos = 1;
        for (Juego j : gd.getJuegosMasVendidos()) {
            HBox row = new HBox(15);
            row.getStyleClass().add("card");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));
            Label lblPos = new Label("#" + pos++); lblPos.getStyleClass().add("label-gold"); lblPos.setMinWidth(30);
            VBox info = new VBox(3);
            Label lblT = new Label(j.getTitulo()); lblT.getStyleClass().add("label-section");
            Label lblV = new Label("🔥 " + gd.getVentasJuego(j) + " unidades vendidas");
            lblV.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblV);
            row.getChildren().addAll(lblPos, info);
            root.getChildren().add(row);
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ─────────────────────────────────────────────────────────
    // AYUDA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarAyuda() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("❓ Sistema de Ayuda");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        String[][] secciones = {
            {"🔍 Buscar Juegos",      "Busca por nombre, género, director o estudio. Escribe el texto y selecciona el tipo de búsqueda."},
            {"🎮 Catálogo",            "Muestra todos los juegos disponibles. Si tienes saldo, puedes comprar directamente."},
            {"🛒 Historial de Compras","Consulta todas tus compras con fecha, cantidad y coste total acumulado."},
            {"📚 Biblioteca",          "Lista todos los juegos que posees. Solo puedes reseñar juegos de tu biblioteca."},
            {"✍ Mis Reseñas",         "Escribe, edita o elimina tus reseñas. Puntúa del 1 al 10 y añade un comentario."},
            {"👤 Mi Perfil",           "Consulta y modifica tus datos: nombre, apellidos, correo y contraseña."},
            {"⭐ Mejor Valorados",     "Ranking de juegos por puntuación media de todas las reseñas."},
            {"🔥 Más Vendidos",        "Ranking de juegos ordenados por número total de unidades vendidas."}
        };

        for (String[] sec : secciones) {
            VBox card = new VBox(5);
            card.getStyleClass().add("card");
            card.setPadding(new Insets(12));
            Label lSec  = new Label(sec[0]); lSec.getStyleClass().add("label-section");
            Label lDesc = new Label(sec[1]); lDesc.getStyleClass().add("label-normal"); lDesc.setWrapText(true);
            card.getChildren().addAll(lSec, lDesc);
            root.getChildren().add(card);
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
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
        VBox root = new VBox(12);
        root.setPadding(new Insets(10));
        if (lista.isEmpty()) {
            Label lbl = new Label("No se encontraron juegos.");
            lbl.getStyleClass().add("label-muted");
            root.getChildren().add(lbl);
        } else {
            for (Juego j : lista) root.getChildren().add(buildJuegoCard(j, true));
        }
        return new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }};
    }

    private HBox buildJuegoCard(Juego j, boolean mostrarComprar) {
        HBox card = new HBox(15);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12));

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblTitulo = new Label(j.getTitulo()); lblTitulo.getStyleClass().add("label-section");
        Label lblMeta   = new Label("🎮 " + j.getPlataforma() + "  |  📂 " + j.getGenero()
                + "  |  🎬 " + j.getDirector());
        lblMeta.getStyleClass().add("label-muted");
        double media = j.getPuntuacionMedia();
        Label lblMedia = new Label(media > 0 ? String.format("⭐ %.1f/10", media) : "Sin valoraciones");
        lblMedia.getStyleClass().add("label-normal");
        String stockTxt = j.getStock() > 10 ? "✅ " + j.getStock() + " en stock"
                        : j.getStock() > 0  ? "⚠ Solo " + j.getStock() + " en stock"
                        : "❌ Sin stock";
        Label lblStock = new Label(stockTxt);
        lblStock.getStyleClass().add(j.getStock() > 10 ? "stock-ok"
                                    : j.getStock() > 0 ? "stock-low" : "stock-out");
        info.getChildren().addAll(lblTitulo, lblMeta, lblMedia, lblStock);

        VBox acciones = new VBox(6);
        acciones.setAlignment(Pos.CENTER);
        Label lblPrecio = new Label(String.format("%.2f€", j.getPrecio()));
        lblPrecio.getStyleClass().add("price-badge");

        Button btnResenas = new Button("Ver reseñas"); btnResenas.getStyleClass().add("btn-secondary");
        btnResenas.setOnAction(e -> mostrarResenasDe(j));

        if (mostrarComprar) {
            if (gd.getUsuarioActual().poseeJuego(j)) {
                Label lblPoseido = new Label("✅ En biblioteca"); lblPoseido.getStyleClass().add("stock-ok");
                acciones.getChildren().addAll(lblPrecio, lblPoseido, btnResenas);
            } else {
                Button btnComprar = new Button("🛒 Comprar"); btnComprar.getStyleClass().add("btn-primary");
                btnComprar.setDisable(j.getStock() <= 0);
                btnComprar.setOnAction(e -> comprarJuego(j));
                acciones.getChildren().addAll(lblPrecio, btnComprar, btnResenas);
            }
        } else {
            Button btnEscribir = new Button("✍ Reseñar"); btnEscribir.getStyleClass().add("btn-gold");
            btnEscribir.setOnAction(e -> mostrarFormularioResena(null));
            acciones.getChildren().addAll(lblPrecio, btnResenas, btnEscribir);
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
            for (Resena r : lista) root.getChildren().add(buildResenaCard(r, false));
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    private VBox buildResenaCard(Resena r, boolean conAcciones) {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(12));

        HBox cabecera = new HBox(10);
        Label lblAutor = new Label("👤 " + r.getAutor().getNombreCompleto());
        lblAutor.getStyleClass().add("label-section");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label lblPunt  = new Label("⭐ " + r.getPuntuacion() + "/10"); lblPunt.getStyleClass().add("label-gold");
        Label lblFecha = new Label(r.getFechaFormateada()); lblFecha.getStyleClass().add("label-muted");
        cabecera.getChildren().addAll(lblAutor, sp, lblPunt, lblFecha);

        Label lblJuego = new Label("🎮 " + r.getJuego().getTitulo()); lblJuego.getStyleClass().add("label-muted");
        Label lblComent = new Label(r.getComentario()); lblComent.getStyleClass().add("label-normal"); lblComent.setWrapText(true);
        card.getChildren().addAll(cabecera, lblJuego, lblComent);

        if (conAcciones && r.getAutor() == gd.getUsuarioActual()) {
            Button btnEditar   = new Button("✏ Editar");   btnEditar.getStyleClass().add("btn-secondary");
            Button btnEliminar = new Button("🗑 Eliminar"); btnEliminar.getStyleClass().add("btn-danger");
            btnEditar.setOnAction(e -> mostrarFormularioResena(r));
            btnEliminar.setOnAction(e -> {
                if (DialogUtil.confirmar("¿Eliminar esta reseña?")) {
                    gd.eliminarResena(r.getIdResena());
                    mostrarMisResenas();
                }
            });
            card.getChildren().add(new HBox(8, btnEditar, btnEliminar));
        }
        return card;
    }

    private HBox parLabel(String etiqueta, String valor) {
        HBox row = new HBox(10);
        Label lEtiq = new Label(etiqueta); lEtiq.getStyleClass().add("label-muted"); lEtiq.setMinWidth(180);
        Label lVal  = new Label(valor);    lVal.getStyleClass().add("label-normal");
        row.getChildren().addAll(lEtiq, lVal);
        return row;
    }

    private TextField tf(String valor) {
        TextField tf = new TextField(valor); tf.getStyleClass().add("text-field"); return tf;
    }

    private Label lbl(String txt) {
        Label l = new Label(txt); l.getStyleClass().add("label-normal"); return l;
    }

    // helper alias
    private <T> void col(TableView<T> tabla, String nombre,
                          java.util.function.Function<T, String> getter, double width) {
        TableColumn<T, String> col = new TableColumn<>(nombre);
        col.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getter.apply(c.getValue())));
        col.setPrefWidth(width);
        tabla.getColumns().add(col);
    }
}
