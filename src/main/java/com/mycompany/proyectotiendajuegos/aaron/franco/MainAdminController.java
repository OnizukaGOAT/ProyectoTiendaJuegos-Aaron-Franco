package com.mycompany.proyectotiendajuegos.aaron.franco;

import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Juego;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Desarrollador;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Resena;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Administrador;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Usuario;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Compra;
import com.mycompany.proyectotiendajuegos.aaron.franco.clases.Estudio;
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

public class MainAdminController implements Initializable {

    @FXML private StackPane contentPane;
    @FXML private Label     lblAdminNombre;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblAdminNombre.setText("Admin: " + gd.getAdminActual().getNombreCompleto());
        gestionUsuarios();
    }

    private void setContent(Node node) { contentPane.getChildren().setAll(node); }

    // ══════════════════════════════════════════════════════
    // GESTIÓN USUARIOS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionUsuarios() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));

        Label titulo = new Label("👤 Gestión de Usuarios");
        titulo.getStyleClass().add("label-title");

        Button btnNuevo = new Button("+ Nuevo Usuario");
        btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> formUsuario(null));

        TableView<Usuario> tabla = buildTablaUsuarios();
        root.getChildren().addAll(titulo, btnNuevo, tabla);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    private TableView<Usuario> buildTablaUsuarios() {
        TableView<Usuario> tabla = new TableView<>(FXCollections.observableArrayList(gd.getUsuarios()));
        tabla.getStyleClass().add("table-view");
        tabla.setPrefHeight(350);

        col(tabla, "ID",       u -> String.valueOf(u.getIdUsuario()), 50);
        col(tabla, "Nombre",   u -> u.getNombreCompleto(), 180);
        col(tabla, "Correo",   u -> u.getCorreo(), 200);
        col(tabla, "Saldo",    u -> String.format("%.2f€", u.getSaldo()), 90);
        col(tabla, "Juegos",   u -> String.valueOf(u.getBiblioteca().size()), 70);

        TableColumn<Usuario, Void> acciones = new TableColumn<>("Acciones");
        acciones.setPrefWidth(200);
        acciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnVer  = new Button("🔍 Ver");
            final Button btnEdit = new Button("✏ Editar");
            final Button btnDel  = new Button("🗑 Baja");
            {
                btnVer.getStyleClass().add("btn-gold");
                btnEdit.getStyleClass().add("btn-secondary");
                btnDel.getStyleClass().add("btn-danger");
                btnVer.setOnAction(e -> consultarUsuario(getTableView().getItems().get(getIndex())));
                btnEdit.setOnAction(e -> formUsuario(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> {
                    Usuario u = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Dar de baja al usuario " + u.getNombreCompleto() + "?")) {
                        gd.bajaUsuario(u.getIdUsuario());
                        gestionUsuarios();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnVer, btnEdit, btnDel));
            }
        });
        tabla.getColumns().add(acciones);
        return tabla;
    }

    private void consultarUsuario(Usuario u) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        root.setMaxWidth(560);

        Label titulo = new Label("🔍 Detalle de Usuario");
        titulo.getStyleClass().add("label-title");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));
        card.getChildren().addAll(
            parLabel("ID:",                  String.valueOf(u.getIdUsuario())),
            parLabel("Nombre completo:",     u.getNombreCompleto()),
            parLabel("Correo:",              u.getCorreo()),
            parLabel("Idioma:",              u.getIdioma()),
            parLabel("Saldo:",               String.format("%.2f€", u.getSaldo())),
            parLabel("Juegos en biblioteca:",String.valueOf(u.getBiblioteca().size())),
            parLabel("Compras realizadas:",  String.valueOf(u.getHistorialCompras().size()))
        );

        // Biblioteca
        if (!u.getBiblioteca().isEmpty()) {
            Label lblBib = new Label("📚 Biblioteca:");
            lblBib.getStyleClass().add("label-section");
            card.getChildren().add(lblBib);
            for (Juego j : u.getBiblioteca()) {
                Label lbl = new Label("  • " + j.getTitulo() + " – " + j.getGenero());
                lbl.getStyleClass().add("label-normal");
                card.getChildren().add(lbl);
            }
        }

        // Reseñas del usuario
        List<Resena> resenas = gd.getResenasPorUsuario(u);
        if (!resenas.isEmpty()) {
            Label lblRes = new Label("✍ Reseñas (" + resenas.size() + "):");
            lblRes.getStyleClass().add("label-section");
            card.getChildren().add(lblRes);
            for (Resena r : resenas) {
                Label lbl = new Label("  • " + r.getJuego().getTitulo()
                        + "  ⭐" + r.getPuntuacion() + "/10 – " + r.getComentario());
                lbl.getStyleClass().add("label-normal");
                lbl.setWrapText(true);
                card.getChildren().add(lbl);
            }
        }

        Button btnVolver = new Button("← Volver");
        btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> gestionUsuarios());

        root.getChildren().addAll(titulo, card, btnVolver);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    private void formUsuario(Usuario editar) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(480);
        form.getStyleClass().add("card");

        Label titulo = new Label(editar == null ? "Nuevo Usuario" : "Editar Usuario");
        titulo.getStyleClass().add("label-section");

        TextField txNombre    = tf(editar != null ? editar.getNombre()    : "");
        TextField txApellidos = tf(editar != null ? editar.getApellidos() : "");
        TextField txCorreo    = tf(editar != null ? editar.getCorreo()    : "");
        PasswordField txPass  = new PasswordField();
        txPass.getStyleClass().add("password-field");
        txPass.setPromptText(editar != null ? "(sin cambios)" : "Contraseña");
        TextField txSaldo = tf(editar != null ? String.valueOf(editar.getSaldo()) : "50");
        ComboBox<String> cbIdioma = new ComboBox<>(FXCollections.observableArrayList(
                "Español", "English", "Français", "Deutsch", "Português", "Italiano"));
        cbIdioma.getSelectionModel().select(editar != null ? editar.getIdioma() : "Español");
        cbIdioma.getStyleClass().add("combo-box");

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");

        Button btnGuardar = new Button("Guardar"); btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancel  = new Button("Cancelar"); btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> gestionUsuarios());

        btnGuardar.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre es obligatorio."); return; }
            double saldo;
            try { saldo = Double.parseDouble(txSaldo.getText().replace(",", ".")); }
            catch (NumberFormatException ex) { lblErr.setText("Saldo inválido."); return; }

            if (editar == null) {
                if (txPass.getText().isEmpty()) { lblErr.setText("La contraseña es obligatoria."); return; }
                boolean ok = gd.altaUsuario(txNombre.getText().trim(), txApellidos.getText().trim(),
                        txCorreo.getText().trim(), txPass.getText(), saldo, cbIdioma.getValue());
                if (!ok) { lblErr.setText("El correo ya existe."); return; }
            } else {
                editar.setNombre(txNombre.getText().trim());
                editar.setApellidos(txApellidos.getText().trim());
                editar.setCorreo(txCorreo.getText().trim());
                editar.setSaldo(saldo);
                editar.setIdioma(cbIdioma.getValue());
                if (!txPass.getText().isEmpty()) editar.setContrasena(txPass.getText());
            }
            DialogUtil.info("Usuario guardado correctamente.");
            gestionUsuarios();
        });

        form.getChildren().addAll(titulo,
                lbl("Nombre:"), txNombre,
                lbl("Apellidos:"), txApellidos,
                lbl("Correo:"), txCorreo,
                lbl("Contraseña:"), txPass,
                lbl("Saldo (€):"), txSaldo,
                lbl("Idioma:"), cbIdioma,
                lblErr, new HBox(10, btnGuardar, btnCancel));
        setContent(new ScrollPane(form) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN ADMINISTRADORES
    // ══════════════════════════════════════════════════════
    @FXML public void gestionAdmins() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("🔑 Gestión de Administradores");
        titulo.getStyleClass().add("label-title");
        Button btnNuevo = new Button("+ Nuevo Admin");
        btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> formAdmin(null));

        TableView<Administrador> tabla = new TableView<>(FXCollections.observableArrayList(gd.getAdministradores()));
        tabla.getStyleClass().add("table-view");
        tabla.setPrefHeight(300);
        col(tabla, "ID",     a -> String.valueOf(a.getIdAdmin()), 50);
        col(tabla, "Nombre", a -> a.getNombreCompleto(), 200);
        col(tabla, "Correo", a -> a.getCorreo(), 220);

        TableColumn<Administrador, Void> acciones = new TableColumn<>("Acciones");
        acciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnEdit = new Button("✏ Editar");
            final Button btnDel  = new Button("🗑 Baja");
            {
                btnEdit.getStyleClass().add("btn-secondary");
                btnDel.getStyleClass().add("btn-danger");
                btnEdit.setOnAction(e -> formAdmin(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> {
                    Administrador a = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Dar de baja al admin " + a.getNombreCompleto() + "?")) {
                        gd.bajaAdmin(a.getIdAdmin());
                        gestionAdmins();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnEdit, btnDel));
            }
        });
        tabla.getColumns().add(acciones);
        root.getChildren().addAll(titulo, btnNuevo, tabla);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    private void formAdmin(Administrador editar) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(450);
        form.getStyleClass().add("card");

        Label titulo = new Label(editar == null ? "Nuevo Administrador" : "Editar Administrador");
        titulo.getStyleClass().add("label-section");
        TextField txNombre    = tf(editar != null ? editar.getNombre()    : "");
        TextField txApellidos = tf(editar != null ? editar.getApellidos() : "");
        TextField txCorreo    = tf(editar != null ? editar.getCorreo()    : "");
        PasswordField txPass  = new PasswordField();
        txPass.getStyleClass().add("password-field");
        txPass.setPromptText(editar != null ? "(sin cambios)" : "Contraseña");
        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");

        Button btnGuardar = new Button("Guardar"); btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancel  = new Button("Cancelar"); btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> gestionAdmins());

        btnGuardar.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre es obligatorio."); return; }
            if (editar == null) {
                if (txPass.getText().isEmpty()) { lblErr.setText("La contraseña es obligatoria."); return; }
                boolean ok = gd.altaAdmin(txNombre.getText().trim(), txApellidos.getText().trim(),
                        txCorreo.getText().trim(), txPass.getText());
                if (!ok) { lblErr.setText("El correo ya existe."); return; }
            } else {
                editar.setNombre(txNombre.getText().trim());
                editar.setApellidos(txApellidos.getText().trim());
                editar.setCorreo(txCorreo.getText().trim());
                if (!txPass.getText().isEmpty()) editar.setContrasena(txPass.getText());
            }
            DialogUtil.info("Administrador guardado."); gestionAdmins();
        });

        form.getChildren().addAll(titulo,
                lbl("Nombre:"), txNombre, lbl("Apellidos:"), txApellidos,
                lbl("Correo:"), txCorreo, lbl("Contraseña:"), txPass,
                lblErr, new HBox(10, btnGuardar, btnCancel));
        setContent(new ScrollPane(form) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN JUEGOS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionJuegos() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("🎮 Gestión de Juegos");
        titulo.getStyleClass().add("label-title");
        Button btnNuevo = new Button("+ Nuevo Juego");
        btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> formJuego(null));

        TableView<Juego> tabla = new TableView<>(FXCollections.observableArrayList(gd.getJuegos()));
        tabla.getStyleClass().add("table-view");
        tabla.setPrefHeight(350);
        col(tabla, "ID",         j -> String.valueOf(j.getIdJuego()), 50);
        col(tabla, "Título",     j -> j.getTitulo(), 180);
        col(tabla, "Género",     j -> j.getGenero(), 120);
        col(tabla, "Plataforma", j -> j.getPlataforma(), 130);
        col(tabla, "Precio",     j -> String.format("%.2f€", j.getPrecio()), 80);
        col(tabla, "Stock",      j -> String.valueOf(j.getStock()), 60);
        col(tabla, "Director",   j -> j.getDirector(), 150);

        TableColumn<Juego, Void> acciones = new TableColumn<>("Acciones");
        acciones.setPrefWidth(200);
        acciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnVer  = new Button("🔍");
            final Button btnEdit = new Button("✏");
            final Button btnDel  = new Button("🗑");
            {
                btnVer.getStyleClass().add("btn-gold");
                btnEdit.getStyleClass().add("btn-secondary");
                btnDel.getStyleClass().add("btn-danger");
                btnVer.setOnAction(e -> consultarJuego(getTableView().getItems().get(getIndex())));
                btnEdit.setOnAction(e -> formJuego(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> {
                    Juego j = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Eliminar '" + j.getTitulo() + "'?")) {
                        gd.bajaJuego(j.getIdJuego()); gestionJuegos();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(5, btnVer, btnEdit, btnDel));
            }
        });
        tabla.getColumns().add(acciones);
        root.getChildren().addAll(titulo, btnNuevo, tabla);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    private void consultarJuego(Juego j) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        root.setMaxWidth(560);

        Label titulo = new Label("🔍 Detalle del Juego");
        titulo.getStyleClass().add("label-title");

        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16));
        card.getChildren().addAll(
            parLabel("ID:",          String.valueOf(j.getIdJuego())),
            parLabel("Título:",      j.getTitulo()),
            parLabel("Género:",      j.getGenero()),
            parLabel("Plataforma:",  j.getPlataforma()),
            parLabel("Precio:",      String.format("%.2f€", j.getPrecio())),
            parLabel("Stock:",       String.valueOf(j.getStock())),
            parLabel("Director:",    j.getDirector()),
            parLabel("Valoración media:", j.getResenas().isEmpty() ? "Sin reseñas"
                                          : String.format("⭐ %.1f/10", j.getPuntuacionMedia())),
            parLabel("Ventas totales:",   String.valueOf(gd.getVentasJuego(j)))
        );

        // Reseñas
        if (!j.getResenas().isEmpty()) {
            Label lblResTitle = new Label("✍ Reseñas (" + j.getResenas().size() + "):");
            lblResTitle.getStyleClass().add("label-section");
            card.getChildren().add(lblResTitle);
            for (Resena r : j.getResenas()) {
                Label lbl = new Label("  • " + r.getAutor().getNombreCompleto()
                        + "  ⭐" + r.getPuntuacion() + " – " + r.getComentario());
                lbl.getStyleClass().add("label-normal");
                lbl.setWrapText(true);
                card.getChildren().add(lbl);
            }
        }

        Button btnVolver = new Button("← Volver");
        btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> gestionJuegos());

        root.getChildren().addAll(titulo, card, btnVolver);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    private void formJuego(Juego editar) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(500);
        form.getStyleClass().add("card");

        Label titulo = new Label(editar == null ? "Nuevo Juego" : "Editar Juego");
        titulo.getStyleClass().add("label-section");

        TextField txTitulo   = tf(editar != null ? editar.getTitulo()     : "");
        TextField txGenero   = tf(editar != null ? editar.getGenero()     : "");
        TextField txPlat     = tf(editar != null ? editar.getPlataforma() : "");
        TextField txPrecio   = tf(editar != null ? String.valueOf(editar.getPrecio()) : "");
        TextField txStock    = tf(editar != null ? String.valueOf(editar.getStock())  : "");
        TextField txDirector = tf(editar != null ? editar.getDirector()   : "");

        ComboBox<Estudio> cbEstudio = new ComboBox<>(FXCollections.observableArrayList(gd.getEstudios()));
        cbEstudio.setPromptText("Asignar a estudio (opcional)");
        cbEstudio.getStyleClass().add("combo-box");
        if (editar != null) {
            gd.getEstudios().stream()
                .filter(e -> e.getJuegos().contains(editar))
                .findFirst().ifPresent(cbEstudio.getSelectionModel()::select);
        }

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnGuardar = new Button("Guardar"); btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancel  = new Button("Cancelar"); btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> gestionJuegos());

        btnGuardar.setOnAction(e -> {
            if (txTitulo.getText().trim().isEmpty()) { lblErr.setText("El título es obligatorio."); return; }
            double precio; int stock;
            try { precio = Double.parseDouble(txPrecio.getText().replace(",", ".")); }
            catch (Exception ex) { lblErr.setText("Precio inválido."); return; }
            try { stock = Integer.parseInt(txStock.getText()); }
            catch (Exception ex) { lblErr.setText("Stock inválido."); return; }

            if (editar == null) {
                Juego j = gd.altaJuego(txTitulo.getText().trim(), txGenero.getText().trim(),
                        txPlat.getText().trim(), precio, stock, txDirector.getText().trim());
                if (cbEstudio.getValue() != null) cbEstudio.getValue().addJuego(j);
            } else {
                editar.setTitulo(txTitulo.getText().trim());
                editar.setGenero(txGenero.getText().trim());
                editar.setPlataforma(txPlat.getText().trim());
                editar.setPrecio(precio);
                editar.setStock(stock);
                editar.setDirector(txDirector.getText().trim());
                if (cbEstudio.getValue() != null) cbEstudio.getValue().addJuego(editar);
            }
            DialogUtil.info("Juego guardado correctamente."); gestionJuegos();
        });

        form.getChildren().addAll(titulo,
                lbl("Título:"), txTitulo, lbl("Género:"), txGenero,
                lbl("Plataforma:"), txPlat, lbl("Precio (€):"), txPrecio,
                lbl("Stock:"), txStock, lbl("Director:"), txDirector,
                lbl("Estudio:"), cbEstudio, lblErr, new HBox(10, btnGuardar, btnCancel));
        setContent(new ScrollPane(form) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN ESTUDIOS (con gestión de desarrolladores)
    // ══════════════════════════════════════════════════════
    @FXML public void gestionEstudios() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("🏢 Gestión de Estudios");
        titulo.getStyleClass().add("label-title");
        Button btnNuevo = new Button("+ Nuevo Estudio");
        btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> formEstudio(null));
        root.getChildren().addAll(titulo, btnNuevo);

        for (Estudio est : gd.getEstudios()) {
            VBox card = new VBox(8);
            card.getStyleClass().add("card");
            card.setPadding(new Insets(12));

            HBox cabecera = new HBox(10);
            Label lblNombre = new Label("🏢 " + est.getNombre());
            lblNombre.getStyleClass().add("label-section");
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Button btnEdit   = new Button("✏ Editar");    btnEdit.getStyleClass().add("btn-secondary");
            Button btnDel    = new Button("🗑 Baja");     btnDel.getStyleClass().add("btn-danger");
            Button btnAddDev = new Button("+ Dev");        btnAddDev.getStyleClass().add("btn-gold");
            btnEdit.setOnAction(e -> formEstudio(est));
            btnDel.setOnAction(e -> {
                if (DialogUtil.confirmar("¿Eliminar estudio '" + est.getNombre() + "'?")) {
                    gd.bajaEstudio(est.getIdEstudio()); gestionEstudios();
                }
            });
            btnAddDev.setOnAction(e -> formDesarrollador(null, est));
            cabecera.getChildren().addAll(lblNombre, sp, btnAddDev, btnEdit, btnDel);

            Label lblJuegos = new Label("Juegos (" + est.getJuegos().size() + "): "
                    + est.getJuegos().stream().map(Juego::getTitulo)
                        .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b));
            lblJuegos.getStyleClass().add("label-normal");
            lblJuegos.setWrapText(true);
            card.getChildren().addAll(cabecera, lblJuegos);

            if (!est.getDesarrolladores().isEmpty()) {
                Label lblDevsTit = new Label("👨‍💻 Desarrolladores notables:");
                lblDevsTit.getStyleClass().add("label-muted");
                card.getChildren().add(lblDevsTit);
                for (Desarrollador d : est.getDesarrolladores()) {
                    HBox rowDev = new HBox(8);
                    rowDev.setAlignment(Pos.CENTER_LEFT);
                    Label lblD = new Label("  • " + d.getNombreCompleto()
                            + " – " + d.getPuestoActual()
                            + "  (" + d.getAnosExperiencia() + " años exp.)");
                    lblD.getStyleClass().add("label-normal");
                    HBox.setHgrow(lblD, Priority.ALWAYS);
                    Button btnEditDev = new Button("✏"); btnEditDev.getStyleClass().add("btn-secondary");
                    Button btnDelDev  = new Button("🗑"); btnDelDev.getStyleClass().add("btn-danger");
                    btnEditDev.setOnAction(ev -> formDesarrollador(d, est));
                    btnDelDev.setOnAction(ev -> {
                        if (DialogUtil.confirmar("¿Dar de baja a " + d.getNombreCompleto() + "?")) {
                            gd.bajaDesarrollador(d.getIdDesarrollador()); gestionEstudios();
                        }
                    });
                    rowDev.getChildren().addAll(lblD, btnEditDev, btnDelDev);
                    card.getChildren().add(rowDev);
                }
            }
            root.getChildren().add(card);
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    private void formEstudio(Estudio editar) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(450);
        form.getStyleClass().add("card");

        Label titulo = new Label(editar == null ? "Nuevo Estudio" : "Editar Estudio");
        titulo.getStyleClass().add("label-section");
        TextField txNombre = tf(editar != null ? editar.getNombre() : "");
        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");

        Button btnGuardar = new Button("Guardar"); btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancel  = new Button("Cancelar"); btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> gestionEstudios());
        btnGuardar.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre es obligatorio."); return; }
            if (editar == null) gd.altaEstudio(txNombre.getText().trim());
            else editar.setNombre(txNombre.getText().trim());
            DialogUtil.info("Estudio guardado."); gestionEstudios();
        });

        form.getChildren().addAll(titulo, lbl("Nombre:"), txNombre, lblErr,
                new HBox(10, btnGuardar, btnCancel));
        setContent(new ScrollPane(form) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    private void formDesarrollador(Desarrollador editar, Estudio estudio) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(480);
        form.getStyleClass().add("card");

        Label titulo = new Label(editar == null ? "Nuevo Desarrollador" : "Editar Desarrollador");
        titulo.getStyleClass().add("label-section");

        TextField txNombre    = tf(editar != null ? editar.getNombre()    : "");
        TextField txApellidos = tf(editar != null ? editar.getApellidos() : "");
        TextField txPuesto    = tf(editar != null ? editar.getPuestoActual() : "");
        TextField txAnos      = tf(editar != null ? String.valueOf(editar.getAnosExperiencia()) : "");

        Label lblJuegosTitle = new Label("Juegos en los que ha trabajado:");
        lblJuegosTitle.getStyleClass().add("label-muted");
        VBox juegosBox = new VBox(4);
        java.util.List<CheckBox> checkBoxes = new java.util.ArrayList<>();
        for (Juego j : gd.getJuegos()) {
            CheckBox cb = new CheckBox(j.getTitulo());
            cb.setStyle("-fx-text-fill:#eaeaea;");
            if (editar != null) cb.setSelected(editar.getJuegosEnLosQueHaTrabajado().contains(j));
            checkBoxes.add(cb);
            juegosBox.getChildren().add(cb);
        }

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnGuardar = new Button("Guardar"); btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancel  = new Button("Cancelar"); btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> gestionEstudios());

        btnGuardar.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre es obligatorio."); return; }
            int anos = 0;
            try { anos = Integer.parseInt(txAnos.getText()); } catch (Exception ex) { /* ignora */ }

            if (editar == null) {
                Desarrollador d = gd.altaDesarrollador(txNombre.getText().trim(),
                        txApellidos.getText().trim(), anos, txPuesto.getText().trim(), estudio);
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) d.addJuego(gd.getJuegos().get(i));
                }
            } else {
                editar.setNombre(txNombre.getText().trim());
                editar.setApellidos(txApellidos.getText().trim());
                editar.setPuestoActual(txPuesto.getText().trim());
                editar.setAnosExperiencia(anos);
                editar.getJuegosEnLosQueHaTrabajado().clear();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) editar.addJuego(gd.getJuegos().get(i));
                }
            }
            DialogUtil.info("Desarrollador guardado."); gestionEstudios();
        });

        form.getChildren().addAll(titulo,
                lbl("Nombre:"), txNombre, lbl("Apellidos:"), txApellidos,
                lbl("Puesto:"), txPuesto, lbl("Años de experiencia:"), txAnos,
                lblJuegosTitle, juegosBox,
                lblErr, new HBox(10, btnGuardar, btnCancel));
        setContent(new ScrollPane(form) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN RESEÑAS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionResenas() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("✍ Gestión de Reseñas");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        HBox filtro = new HBox(10);
        TextField txFiltro = new TextField();
        txFiltro.setPromptText("Filtrar por juego o usuario...");
        txFiltro.getStyleClass().add("text-field");
        Button btnFiltrar = new Button("Filtrar");
        btnFiltrar.getStyleClass().add("btn-secondary");
        filtro.getChildren().addAll(txFiltro, btnFiltrar);
        root.getChildren().add(filtro);

        VBox listaResenas = new VBox(8);
        Runnable actualizar = () -> {
            listaResenas.getChildren().clear();
            String f = txFiltro.getText().toLowerCase();
            for (Resena r : gd.getResenas()) {
                if (!f.isEmpty()
                        && !r.getJuego().getTitulo().toLowerCase().contains(f)
                        && !r.getAutor().getNombreCompleto().toLowerCase().contains(f)) continue;

                HBox card = new HBox(10);
                card.getStyleClass().add("card");
                card.setAlignment(Pos.CENTER_LEFT);
                card.setPadding(new Insets(8));

                VBox info = new VBox(3); HBox.setHgrow(info, Priority.ALWAYS);
                Label lblT = new Label(r.getJuego().getTitulo() + " – ⭐" + r.getPuntuacion() + "/10");
                lblT.getStyleClass().add("label-section");
                Label lblA = new Label("por " + r.getAutor().getNombreCompleto()
                        + "  |  " + r.getFechaFormateada() + "  |  " + r.getIdioma());
                lblA.getStyleClass().add("label-muted");
                Label lblC = new Label(r.getComentario());
                lblC.getStyleClass().add("label-normal");
                lblC.setWrapText(true);
                info.getChildren().addAll(lblT, lblA, lblC);

                Button btnDel = new Button("🗑 Eliminar");
                btnDel.getStyleClass().add("btn-danger");
                btnDel.setOnAction(ev -> {
                    if (DialogUtil.confirmar("¿Eliminar esta reseña?")) {
                        gd.eliminarResena(r.getIdResena()); gestionResenas();
                    }
                });
                card.getChildren().addAll(info, btnDel);
                listaResenas.getChildren().add(card);
            }
            if (listaResenas.getChildren().isEmpty()) {
                listaResenas.getChildren().add(new Label("No hay reseñas."));
            }
        };
        btnFiltrar.setOnAction(e -> actualizar.run());
        actualizar.run();
        root.getChildren().add(listaResenas);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ══════════════════════════════════════════════════════
    // HISTORIAL DE COMPRAS GLOBAL
    // ══════════════════════════════════════════════════════
    @FXML public void verCompras() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("🛒 Historial de Compras Global");
        titulo.getStyleClass().add("label-title");

        TableView<Compra> tabla = new TableView<>(
                FXCollections.observableArrayList(gd.getHistorialComprasGlobal()));
        tabla.getStyleClass().add("table-view");
        tabla.setPrefHeight(400);
        col(tabla, "ID",       c -> String.valueOf(c.getCodCompra()), 50);
        col(tabla, "Usuario",  c -> c.getUsuario().getNombreCompleto(), 170);
        col(tabla, "Juego",    c -> c.getJuego().getTitulo(), 180);
        col(tabla, "Fecha",    c -> c.getFechaFormateada(), 100);
        col(tabla, "Cantidad", c -> String.valueOf(c.getCantidad()), 70);
        col(tabla, "Coste",    c -> String.format("%.2f€", c.getCoste()), 90);

        double total = gd.getHistorialComprasGlobal().stream().mapToDouble(Compra::getCoste).sum();
        Label lblTotal = new Label(String.format("Ingresos totales: %.2f€", total));
        lblTotal.getStyleClass().add("label-gold");

        root.getChildren().addAll(titulo, tabla, lblTotal);
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ══════════════════════════════════════════════════════
    // ESTADÍSTICAS
    // ══════════════════════════════════════════════════════
    @FXML public void estadVentas() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("📊 Ventas por Juego");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        int maxVentas = gd.getJuegos().stream().mapToInt(gd::getVentasJuego).max().orElse(1);

        for (Juego j : gd.getJuegos()) {
            int ventas = gd.getVentasJuego(j);
            HBox row = new HBox(15);
            row.getStyleClass().add("card");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8));

            VBox info = new VBox(3); HBox.setHgrow(info, Priority.ALWAYS);
            Label lblT = new Label(j.getTitulo()); lblT.getStyleClass().add("label-section");
            Label lblV = new Label("Ventas: " + ventas + "  |  Ingresos: "
                    + String.format("%.2f€", ventas * j.getPrecio()));
            lblV.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblV);

            double pct = maxVentas > 0 ? (double) ventas / maxVentas : 0;
            Region fill = new Region();
            fill.setStyle("-fx-background-color:#e94560; -fx-background-radius:4;");
            fill.setPrefWidth(220 * pct);
            fill.setPrefHeight(12);
            HBox barra = new HBox(fill);
            barra.setMinWidth(220);
            barra.setStyle("-fx-background-color:#16213e; -fx-background-radius:4;");
            barra.setAlignment(Pos.CENTER_LEFT);

            row.getChildren().addAll(info, barra);
            root.getChildren().add(row);
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    @FXML public void estadMejorVal() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("⭐ Juegos Mejor Valorados");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);
        int pos = 1;
        for (Juego j : gd.getJuegosMejorValorados()) {
            HBox row = new HBox(15);
            row.getStyleClass().add("card");
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));
            Label lblPos = new Label("#" + pos++); lblPos.getStyleClass().add("label-gold"); lblPos.setMinWidth(30);
            VBox info = new VBox(3);
            Label lblT = new Label(j.getTitulo()); lblT.getStyleClass().add("label-section");
            Label lblM = new Label(String.format("⭐ %.1f/10  (%d reseñas)  |  Director: %s",
                    j.getPuntuacionMedia(), j.getResenas().size(), j.getDirector()));
            lblM.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblM);
            row.getChildren().addAll(lblPos, info);
            root.getChildren().add(row);
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    @FXML public void estadMasVend() {
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
            int ventas = gd.getVentasJuego(j);
            Label lblV = new Label("🔥 " + ventas + " uds  |  "
                    + String.format("%.2f€ ingresos", ventas * j.getPrecio()));
            lblV.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblV);
            row.getChildren().addAll(lblPos, info);
            root.getChildren().add(row);
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    @FXML public void estadResIdioma() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("💬 Reseñas por Idioma");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        String[] idiomas = {"Español", "English", "Français", "Deutsch", "Português", "Italiano"};
        boolean hayAlguna = false;
        for (String idioma : idiomas) {
            List<Resena> lista = gd.getResenasPorIdioma(idioma);
            if (lista.isEmpty()) continue;
            hayAlguna = true;
            VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(10));
            Label lblI = new Label("🌐 " + idioma + "  (" + lista.size() + " reseñas)");
            lblI.getStyleClass().add("label-section");
            card.getChildren().add(lblI);
            for (Resena r : lista) {
                Label lbl = new Label("• " + r.getJuego().getTitulo() + " – "
                        + r.getAutor().getNombreCompleto() + "  ⭐" + r.getPuntuacion());
                lbl.getStyleClass().add("label-normal");
                card.getChildren().add(lbl);
            }
            root.getChildren().add(card);
        }
        if (!hayAlguna) root.getChildren().add(new Label("No hay reseñas registradas."));
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ── Reseñas por Juego (nueva consulta del PDF sección 3.2.2) ──
    @FXML public void estadResenasPorJuego() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("🎮 Reseñas por Juego");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        boolean hayAlguna = false;
        for (Juego j : gd.getJuegos()) {
            List<Resena> lista = gd.getResenasPorJuego(j);
            if (lista.isEmpty()) continue;
            hayAlguna = true;

            VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(10));
            Label lblJ = new Label("🎮 " + j.getTitulo()
                    + String.format("  ⭐ %.1f/10  (%d reseñas)", j.getPuntuacionMedia(), lista.size()));
            lblJ.getStyleClass().add("label-section");
            card.getChildren().add(lblJ);
            for (Resena r : lista) {
                Label lbl = new Label("  • " + r.getAutor().getNombreCompleto()
                        + "  ⭐" + r.getPuntuacion() + "/10  [" + r.getIdioma() + "]  – " + r.getComentario());
                lbl.getStyleClass().add("label-normal");
                lbl.setWrapText(true);
                card.getChildren().add(lbl);
            }
            root.getChildren().add(card);
        }
        if (!hayAlguna) root.getChildren().add(new Label("No hay reseñas registradas."));
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    @FXML public void estadEstudio() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("🏢 Estadísticas por Estudio");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        for (Estudio est : gd.getEstudios()) {
            VBox card = new VBox(8); card.getStyleClass().add("card"); card.setPadding(new Insets(12));
            Label lblEst = new Label("🏢 " + est.getNombre()); lblEst.getStyleClass().add("label-section");
            card.getChildren().add(lblEst);

            Juego mejorVal   = gd.getJuegoMejorValoradoEstudio(est);
            Juego masVendido = gd.getJuegoMasVendidoEstudio(est);

            if (mejorVal != null) {
                Label lbl = new Label("⭐ Mejor valorado: " + mejorVal.getTitulo()
                        + String.format("  (%.1f/10)", mejorVal.getPuntuacionMedia()));
                lbl.getStyleClass().add("label-normal");
                card.getChildren().add(lbl);
            }
            if (masVendido != null) {
                Label lbl = new Label("🔥 Más vendido: " + masVendido.getTitulo()
                        + "  (" + gd.getVentasJuego(masVendido) + " uds)");
                lbl.getStyleClass().add("label-normal");
                card.getChildren().add(lbl);
            }

            if (!est.getDesarrolladores().isEmpty()) {
                Label lblDevsTit = new Label("👨‍💻 Desarrolladores notables:");
                lblDevsTit.getStyleClass().add("label-muted");
                card.getChildren().add(lblDevsTit);
                for (Desarrollador d : est.getDesarrolladores()) {
                    Juego mejorDev = gd.getJuegoMejorValoradoDesarrollador(d);
                    Juego masVDev  = gd.getJuegoMasVendidoDesarrollador(d);
                    StringBuilder sb = new StringBuilder("  • " + d.getNombreCompleto()
                            + " (" + d.getPuestoActual() + ")");
                    if (mejorDev != null) sb.append("  |  ⭐ ").append(mejorDev.getTitulo());
                    if (masVDev  != null) sb.append("  |  🔥 ").append(masVDev.getTitulo());
                    Label lblD = new Label(sb.toString()); lblD.getStyleClass().add("label-muted");
                    card.getChildren().add(lblD);
                }
            }
            root.getChildren().add(card);
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ── Consulta: juegos comprados por usuario ─────────────
    @FXML public void estadJuegosPorUsuario() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("🛒 Juegos Comprados por Usuario");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        for (Usuario u : gd.getUsuarios()) {
            VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(10));
            Label lblU = new Label("👤 " + u.getNombreCompleto() + "  <" + u.getCorreo() + ">");
            lblU.getStyleClass().add("label-section");
            card.getChildren().add(lblU);
            if (u.getBiblioteca().isEmpty()) {
                card.getChildren().add(new Label("  Sin compras."));
            } else {
                for (Juego j : u.getBiblioteca()) {
                    Label lbl = new Label("  • " + j.getTitulo() + " (" + j.getGenero() + ")");
                    lbl.getStyleClass().add("label-normal");
                    card.getChildren().add(lbl);
                }
            }
            root.getChildren().add(card);
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ── Consulta: reseñas por usuario ─────────────────────
    @FXML public void estadResenasPorUsuario() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));
        Label titulo = new Label("✍ Reseñas por Usuario");
        titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);

        for (Usuario u : gd.getUsuarios()) {
            List<Resena> lista = gd.getResenasPorUsuario(u);
            if (lista.isEmpty()) continue;
            VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(10));
            Label lblU = new Label("👤 " + u.getNombreCompleto() + "  (" + lista.size() + " reseñas)");
            lblU.getStyleClass().add("label-section");
            card.getChildren().add(lblU);
            for (Resena r : lista) {
                Label lbl = new Label("  • " + r.getJuego().getTitulo()
                        + "  ⭐" + r.getPuntuacion() + "/10  –  " + r.getComentario());
                lbl.getStyleClass().add("label-normal");
                lbl.setWrapText(true);
                card.getChildren().add(lbl);
            }
            root.getChildren().add(card);
        }
        setContent(new ScrollPane(root) {{ setFitToWidth(true); getStyleClass().add("scroll-pane"); }});
    }

    // ══════════════════════════════════════════════════════
    // CERRAR SESIÓN
    // ══════════════════════════════════════════════════════
    @FXML public void cerrarSesion() {
        gd.cerrarSesion();
        try { App.setRoot("login"); } catch (IOException e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════
    @FunctionalInterface interface Getter<T> { String get(T item); }

    private <T> void col(TableView<T> tabla, String nombre, Getter<T> getter, double width) {
        TableColumn<T, String> col = new TableColumn<>(nombre);
        col.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getter.get(c.getValue())));
        col.setPrefWidth(width);
        tabla.getColumns().add(col);
    }

    private TextField tf(String valor) {
        TextField tf = new TextField(valor);
        tf.getStyleClass().add("text-field");
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private Label lbl(String txt) {
        Label l = new Label(txt); l.getStyleClass().add("label-normal"); return l;
    }

    private HBox parLabel(String etiqueta, String valor) {
        HBox row = new HBox(10);
        Label lEtiq = new Label(etiqueta); lEtiq.getStyleClass().add("label-muted"); lEtiq.setMinWidth(180);
        Label lVal  = new Label(valor);    lVal.getStyleClass().add("label-normal");
        row.getChildren().addAll(lEtiq, lVal);
        return row;
    }
}
