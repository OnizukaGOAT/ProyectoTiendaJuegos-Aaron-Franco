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

        TableView<Usuario> tabla = new TableView<>(FXCollections.observableArrayList(gd.getUsuarios()));
        tabla.getStyleClass().add("table-view");
        tabla.setPrefHeight(350);
        col(tabla, "ID",     u -> String.valueOf(u.getIdUsuario()), 50);
        col(tabla, "Nombre", u -> u.getNombreCompleto(), 180);
        col(tabla, "Correo", u -> u.getCorreo(), 200);
        col(tabla, "Saldo",  u -> String.format("%.2f€", u.getSaldo()), 90);

        TableColumn<Usuario, Void> acciones = new TableColumn<>("Acciones");
        acciones.setPrefWidth(220);
        acciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnVer  = new Button("🔍 Ver");
            final Button btnEdit = new Button("✏ Editar");
            final Button btnDel  = new Button("🗑 Baja");
            {
                btnVer.getStyleClass().add("btn-gold");
                btnEdit.getStyleClass().add("btn-secondary");
                btnDel.getStyleClass().add("btn-danger");
                btnVer.setOnAction(e  -> consultarUsuario(getTableView().getItems().get(getIndex())));
                btnEdit.setOnAction(e -> formUsuario(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e  -> {
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
        root.getChildren().addAll(titulo, btnNuevo, tabla);
        setContent(scroll(root));
    }

    private void consultarUsuario(Usuario u) {
        List<Juego>  biblioteca = gd.getBibliotecaUsuario(u.getIdUsuario());
        List<Compra> compras    = gd.getComprasUsuario(u.getIdUsuario());
        List<Resena> resenas    = gd.getResenasPorUsuario(u);

        VBox root = new VBox(15); root.setPadding(new Insets(10)); root.setMaxWidth(560);
        Label titulo = new Label("🔍 Detalle de Usuario"); titulo.getStyleClass().add("label-title");

        VBox card = new VBox(10); card.getStyleClass().add("card"); card.setPadding(new Insets(16));
        card.getChildren().addAll(
            parLabel("ID:",                   String.valueOf(u.getIdUsuario())),
            parLabel("Nombre completo:",      u.getNombreCompleto()),
            parLabel("Correo:",               u.getCorreo()),
            parLabel("Idioma:",               u.getIdioma()),
            parLabel("Saldo:",                String.format("%.2f€", u.getSaldo())),
            parLabel("Juegos en biblioteca:", String.valueOf(biblioteca.size())),
            parLabel("Compras realizadas:",   String.valueOf(compras.size()))
        );

        if (!biblioteca.isEmpty()) {
            Label lbl = new Label("📚 Biblioteca:"); lbl.getStyleClass().add("label-section");
            card.getChildren().add(lbl);
            for (Juego j : biblioteca) {
                Label l = new Label("  • " + j.getTitulo() + " – " + j.getGenero());
                l.getStyleClass().add("label-normal");
                card.getChildren().add(l);
            }
        }
        if (!resenas.isEmpty()) {
            Label lbl = new Label("✍ Reseñas (" + resenas.size() + "):"); lbl.getStyleClass().add("label-section");
            card.getChildren().add(lbl);
            for (Resena r : resenas) {
                Label l = new Label("  • " + r.getJuego().getTitulo() + "  ⭐" + r.getPuntuacion() + "/10 – " + r.getComentario());
                l.getStyleClass().add("label-normal"); l.setWrapText(true);
                card.getChildren().add(l);
            }
        }

        Button btnVolver = new Button("← Volver"); btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> gestionUsuarios());
        root.getChildren().addAll(titulo, card, btnVolver);
        setContent(scroll(root));
    }

    private void formUsuario(Usuario editar) {
        VBox form = new VBox(12); form.setPadding(new Insets(20)); form.setMaxWidth(480); form.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Usuario" : "Editar Usuario"); titulo.getStyleClass().add("label-section");

        TextField txNombre    = tf(editar != null ? editar.getNombre()    : "");
        TextField txApellidos = tf(editar != null ? editar.getApellidos() : "");
        TextField txCorreo    = tf(editar != null ? editar.getCorreo()    : "");
        PasswordField txPass  = new PasswordField(); txPass.getStyleClass().add("password-field");
        txPass.setPromptText(editar != null ? "(sin cambios)" : "Contraseña");
        TextField txSaldo = tf(editar != null ? String.valueOf(editar.getSaldo()) : "50");
        ComboBox<String> cbIdioma = new ComboBox<>(FXCollections.observableArrayList(
                "Español","English","Français","Deutsch","Português","Italiano"));
        cbIdioma.getSelectionModel().select(editar != null ? editar.getIdioma() : "Español");
        cbIdioma.getStyleClass().add("combo-box");

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnG = new Button("Guardar"); btnG.getStyleClass().add("btn-primary");
        Button btnC = new Button("Cancelar"); btnC.getStyleClass().add("btn-secondary");
        btnC.setOnAction(e -> gestionUsuarios());

        btnG.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre es obligatorio."); return; }
            double saldo;
            try { saldo = Double.parseDouble(txSaldo.getText().replace(",",".")); }
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
                gd.actualizarUsuario(editar);
            }
            DialogUtil.info("Usuario guardado correctamente.");
            gestionUsuarios();
        });

        form.getChildren().addAll(titulo,
                lbl("Nombre:"), txNombre, lbl("Apellidos:"), txApellidos,
                lbl("Correo:"), txCorreo, lbl("Contraseña:"), txPass,
                lbl("Saldo (€):"), txSaldo, lbl("Idioma:"), cbIdioma,
                lblErr, new HBox(10, btnG, btnC));
        setContent(scroll(form));
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN ADMINISTRADORES
    // ══════════════════════════════════════════════════════
    @FXML public void gestionAdmins() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🔑 Gestión de Administradores"); titulo.getStyleClass().add("label-title");
        Button btnNuevo = new Button("+ Nuevo Admin"); btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> formAdmin(null));

        TableView<Administrador> tabla = new TableView<>(FXCollections.observableArrayList(gd.getAdministradores()));
        tabla.getStyleClass().add("table-view"); tabla.setPrefHeight(300);
        col(tabla, "ID",     a -> String.valueOf(a.getIdAdmin()), 50);
        col(tabla, "Nombre", a -> a.getNombreCompleto(), 200);
        col(tabla, "Correo", a -> a.getCorreo(), 220);

        TableColumn<Administrador, Void> acciones = new TableColumn<>("Acciones");
        acciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnEdit = new Button("✏ Editar"); final Button btnDel = new Button("🗑 Baja");
            { btnEdit.getStyleClass().add("btn-secondary"); btnDel.getStyleClass().add("btn-danger");
              btnEdit.setOnAction(e -> formAdmin(getTableView().getItems().get(getIndex())));
              btnDel.setOnAction(e -> {
                  Administrador a = getTableView().getItems().get(getIndex());
                  if (DialogUtil.confirmar("¿Dar de baja al admin " + a.getNombreCompleto() + "?")) {
                      gd.bajaAdmin(a.getIdAdmin()); gestionAdmins(); }
              }); }
            @Override protected void updateItem(Void i, boolean empty) {
                super.updateItem(i, empty); setGraphic(empty ? null : new HBox(5, btnEdit, btnDel)); }
        });
        tabla.getColumns().add(acciones);
        root.getChildren().addAll(titulo, btnNuevo, tabla);
        setContent(scroll(root));
    }

    private void formAdmin(Administrador editar) {
        VBox form = new VBox(12); form.setPadding(new Insets(20)); form.setMaxWidth(450); form.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Administrador" : "Editar Administrador"); titulo.getStyleClass().add("label-section");
        TextField txNombre = tf(editar != null ? editar.getNombre() : "");
        TextField txApellidos = tf(editar != null ? editar.getApellidos() : "");
        TextField txCorreo = tf(editar != null ? editar.getCorreo() : "");
        PasswordField txPass = new PasswordField(); txPass.getStyleClass().add("password-field");
        txPass.setPromptText(editar != null ? "(sin cambios)" : "Contraseña");
        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnG = new Button("Guardar"); btnG.getStyleClass().add("btn-primary");
        Button btnC = new Button("Cancelar"); btnC.getStyleClass().add("btn-secondary");
        btnC.setOnAction(e -> gestionAdmins());
        btnG.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre es obligatorio."); return; }
            if (editar == null) {
                if (txPass.getText().isEmpty()) { lblErr.setText("La contraseña es obligatoria."); return; }
                boolean ok = gd.altaAdmin(txNombre.getText().trim(), txApellidos.getText().trim(),
                        txCorreo.getText().trim(), txPass.getText());
                if (!ok) { lblErr.setText("El correo ya existe."); return; }
            } else {
                editar.setNombre(txNombre.getText().trim()); editar.setApellidos(txApellidos.getText().trim());
                editar.setCorreo(txCorreo.getText().trim());
                if (!txPass.getText().isEmpty()) editar.setContrasena(txPass.getText());
                gd.actualizarAdmin(editar);
            }
            DialogUtil.info("Administrador guardado."); gestionAdmins();
        });
        form.getChildren().addAll(titulo, lbl("Nombre:"), txNombre, lbl("Apellidos:"), txApellidos,
                lbl("Correo:"), txCorreo, lbl("Contraseña:"), txPass, lblErr, new HBox(10, btnG, btnC));
        setContent(scroll(form));
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN JUEGOS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionJuegos() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🎮 Gestión de Juegos"); titulo.getStyleClass().add("label-title");
        Button btnNuevo = new Button("+ Nuevo Juego"); btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> formJuego(null));

        TableView<Juego> tabla = new TableView<>(FXCollections.observableArrayList(gd.getJuegos()));
        tabla.getStyleClass().add("table-view"); tabla.setPrefHeight(350);
        col(tabla, "ID",         j -> String.valueOf(j.getIdJuego()), 50);
        col(tabla, "Título",     j -> j.getTitulo(), 180);
        col(tabla, "Género",     j -> j.getGenero(), 110);
        col(tabla, "Plataforma", j -> j.getPlataforma(), 130);
        col(tabla, "Precio",     j -> String.format("%.2f€", j.getPrecio()), 75);
        col(tabla, "Stock",      j -> String.valueOf(j.getStock()), 55);
        col(tabla, "Director",   j -> j.getDirector(), 140);

        TableColumn<Juego, Void> acciones = new TableColumn<>("Acciones");
        acciones.setPrefWidth(170);
        acciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnVer = new Button("🔍"); final Button btnEdit = new Button("✏"); final Button btnDel = new Button("🗑");
            { btnVer.getStyleClass().add("btn-gold"); btnEdit.getStyleClass().add("btn-secondary"); btnDel.getStyleClass().add("btn-danger");
              btnVer.setOnAction(e  -> consultarJuego(getTableView().getItems().get(getIndex())));
              btnEdit.setOnAction(e -> formJuego(getTableView().getItems().get(getIndex())));
              btnDel.setOnAction(e  -> { Juego j = getTableView().getItems().get(getIndex());
                  if (DialogUtil.confirmar("¿Eliminar '" + j.getTitulo() + "'?")) { gd.bajaJuego(j.getIdJuego()); gestionJuegos(); } }); }
            @Override protected void updateItem(Void i, boolean empty) {
                super.updateItem(i, empty); setGraphic(empty ? null : new HBox(4, btnVer, btnEdit, btnDel)); }
        });
        tabla.getColumns().add(acciones);
        root.getChildren().addAll(titulo, btnNuevo, tabla);
        setContent(scroll(root));
    }

    private void consultarJuego(Juego j) {
        List<Resena> resenas = gd.getResenasPorJuego(j);
        double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
        int ventas   = gd.getVentasJuego(j);

        VBox root = new VBox(15); root.setPadding(new Insets(10)); root.setMaxWidth(560);
        Label titulo = new Label("🔍 Detalle del Juego"); titulo.getStyleClass().add("label-title");
        VBox card = new VBox(10); card.getStyleClass().add("card"); card.setPadding(new Insets(16));
        card.getChildren().addAll(
            parLabel("ID:",          String.valueOf(j.getIdJuego())),
            parLabel("Título:",      j.getTitulo()),
            parLabel("Género:",      j.getGenero()),
            parLabel("Plataforma:",  j.getPlataforma()),
            parLabel("Precio:",      String.format("%.2f€", j.getPrecio())),
            parLabel("Stock:",       String.valueOf(j.getStock())),
            parLabel("Director:",    j.getDirector()),
            parLabel("Valoración media:", resenas.isEmpty() ? "Sin reseñas" : String.format("⭐ %.1f/10", media)),
            parLabel("Ventas totales:",   String.valueOf(ventas))
        );
        if (!resenas.isEmpty()) {
            Label lbl = new Label("✍ Reseñas (" + resenas.size() + "):"); lbl.getStyleClass().add("label-section");
            card.getChildren().add(lbl);
            for (Resena r : resenas) {
                Label l = new Label("  • " + r.getAutor().getNombreCompleto() + "  ⭐" + r.getPuntuacion() + " – " + r.getComentario());
                l.getStyleClass().add("label-normal"); l.setWrapText(true);
                card.getChildren().add(l);
            }
        }
        Button btnVolver = new Button("← Volver"); btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> gestionJuegos());
        root.getChildren().addAll(titulo, card, btnVolver);
        setContent(scroll(root));
    }

    private void formJuego(Juego editar) {
        VBox form = new VBox(12); form.setPadding(new Insets(20)); form.setMaxWidth(500); form.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Juego" : "Editar Juego"); titulo.getStyleClass().add("label-section");

        TextField txTitulo   = tf(editar != null ? editar.getTitulo()     : "");
        TextField txGenero   = tf(editar != null ? editar.getGenero()     : "");
        TextField txPlat     = tf(editar != null ? editar.getPlataforma() : "");
        TextField txPrecio   = tf(editar != null ? String.valueOf(editar.getPrecio()) : "");
        TextField txStock    = tf(editar != null ? String.valueOf(editar.getStock())  : "");
        TextField txDirector = tf(editar != null ? editar.getDirector()   : "");

        ComboBox<Estudio> cbEstudio = new ComboBox<>(FXCollections.observableArrayList(gd.getEstudios()));
        cbEstudio.setPromptText("Asignar a estudio (opcional)"); cbEstudio.getStyleClass().add("combo-box");

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnG = new Button("Guardar"); btnG.getStyleClass().add("btn-primary");
        Button btnC = new Button("Cancelar"); btnC.getStyleClass().add("btn-secondary");
        btnC.setOnAction(e -> gestionJuegos());

        btnG.setOnAction(e -> {
            if (txTitulo.getText().trim().isEmpty()) { lblErr.setText("El título es obligatorio."); return; }
            double precio; int stock;
            try { precio = Double.parseDouble(txPrecio.getText().replace(",",".")); } catch (Exception ex) { lblErr.setText("Precio inválido."); return; }
            try { stock  = Integer.parseInt(txStock.getText()); }                   catch (Exception ex) { lblErr.setText("Stock inválido.");  return; }

            if (editar == null) {
                Juego j = gd.altaJuego(txTitulo.getText().trim(), txGenero.getText().trim(),
                        txPlat.getText().trim(), precio, stock, txDirector.getText().trim());
                if (j != null && cbEstudio.getValue() != null)
                    gd.asignarJuegoAEstudio(j.getIdJuego(), cbEstudio.getValue().getIdEstudio());
            } else {
                editar.setTitulo(txTitulo.getText().trim()); editar.setGenero(txGenero.getText().trim());
                editar.setPlataforma(txPlat.getText().trim()); editar.setPrecio(precio);
                editar.setStock(stock); editar.setDirector(txDirector.getText().trim());
                gd.actualizarJuego(editar);
                if (cbEstudio.getValue() != null)
                    gd.asignarJuegoAEstudio(editar.getIdJuego(), cbEstudio.getValue().getIdEstudio());
            }
            DialogUtil.info("Juego guardado correctamente."); gestionJuegos();
        });

        form.getChildren().addAll(titulo,
                lbl("Título:"), txTitulo, lbl("Género:"), txGenero,
                lbl("Plataforma:"), txPlat, lbl("Precio (€):"), txPrecio,
                lbl("Stock:"), txStock, lbl("Director:"), txDirector,
                lbl("Estudio:"), cbEstudio, lblErr, new HBox(10, btnG, btnC));
        setContent(scroll(form));
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN ESTUDIOS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionEstudios() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🏢 Gestión de Estudios"); titulo.getStyleClass().add("label-title");
        Button btnNuevo = new Button("+ Nuevo Estudio"); btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> formEstudio(null));
        root.getChildren().addAll(titulo, btnNuevo);

        for (Estudio est : gd.getEstudios()) {
            VBox card = new VBox(8); card.getStyleClass().add("card"); card.setPadding(new Insets(12));
            HBox cabecera = new HBox(10);
            Label lblNombre = new Label("🏢 " + est.getNombre()); lblNombre.getStyleClass().add("label-section");
            Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
            Button btnEdit = new Button("✏ Editar"); btnEdit.getStyleClass().add("btn-secondary");
            Button btnDel  = new Button("🗑 Baja");  btnDel.getStyleClass().add("btn-danger");
            Button btnAddDev = new Button("+ Dev");  btnAddDev.getStyleClass().add("btn-gold");
            btnEdit.setOnAction(e -> formEstudio(est));
            btnDel.setOnAction(e -> { if (DialogUtil.confirmar("¿Eliminar estudio '" + est.getNombre() + "'?")) { gd.bajaEstudio(est.getIdEstudio()); gestionEstudios(); }});
            btnAddDev.setOnAction(e -> formDesarrollador(null, est));
            cabecera.getChildren().addAll(lblNombre, sp, btnAddDev, btnEdit, btnDel);

            String nombresJuegos = est.getJuegos().stream().map(Juego::getTitulo).reduce("", (a,b) -> a.isEmpty() ? b : a + ", " + b);
            Label lblJuegos = new Label("Juegos (" + est.getJuegos().size() + "): " + nombresJuegos);
            lblJuegos.getStyleClass().add("label-normal"); lblJuegos.setWrapText(true);
            card.getChildren().addAll(cabecera, lblJuegos);

            if (!est.getDesarrolladores().isEmpty()) {
                Label lblDevTit = new Label("👨‍💻 Desarrolladores:"); lblDevTit.getStyleClass().add("label-muted");
                card.getChildren().add(lblDevTit);
                for (Desarrollador d : est.getDesarrolladores()) {
                    HBox row = new HBox(8); row.setAlignment(Pos.CENTER_LEFT);
                    Label lblD = new Label("  • " + d.getNombreCompleto() + " – " + d.getPuestoActual() + "  (" + d.getAnosExperiencia() + " años)");
                    lblD.getStyleClass().add("label-normal"); HBox.setHgrow(lblD, Priority.ALWAYS);
                    Button be = new Button("✏"); be.getStyleClass().add("btn-secondary");
                    Button bd = new Button("🗑"); bd.getStyleClass().add("btn-danger");
                    be.setOnAction(ev -> formDesarrollador(d, est));
                    bd.setOnAction(ev -> { if (DialogUtil.confirmar("¿Dar de baja a " + d.getNombreCompleto() + "?")) { gd.bajaDesarrollador(d.getIdDesarrollador()); gestionEstudios(); }});
                    row.getChildren().addAll(lblD, be, bd);
                    card.getChildren().add(row);
                }
            }
            root.getChildren().add(card);
        }
        setContent(scroll(root));
    }

    private void formEstudio(Estudio editar) {
        VBox form = new VBox(12); form.setPadding(new Insets(20)); form.setMaxWidth(450); form.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Estudio" : "Editar Estudio"); titulo.getStyleClass().add("label-section");
        TextField txNombre = tf(editar != null ? editar.getNombre() : "");
        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnG = new Button("Guardar"); btnG.getStyleClass().add("btn-primary");
        Button btnC = new Button("Cancelar"); btnC.getStyleClass().add("btn-secondary");
        btnC.setOnAction(e -> gestionEstudios());
        btnG.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre es obligatorio."); return; }
            if (editar == null) gd.altaEstudio(txNombre.getText().trim());
            else { editar.setNombre(txNombre.getText().trim()); gd.actualizarEstudio(editar); }
            DialogUtil.info("Estudio guardado."); gestionEstudios();
        });
        form.getChildren().addAll(titulo, lbl("Nombre:"), txNombre, lblErr, new HBox(10, btnG, btnC));
        setContent(scroll(form));
    }

    private void formDesarrollador(Desarrollador editar, Estudio estudio) {
        VBox form = new VBox(12); form.setPadding(new Insets(20)); form.setMaxWidth(480); form.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Desarrollador" : "Editar Desarrollador"); titulo.getStyleClass().add("label-section");

        TextField txNombre    = tf(editar != null ? editar.getNombre()       : "");
        TextField txApellidos = tf(editar != null ? editar.getApellidos()    : "");
        TextField txPuesto    = tf(editar != null ? editar.getPuestoActual() : "");
        TextField txAnos      = tf(editar != null ? String.valueOf(editar.getAnosExperiencia()) : "");

        List<Juego> todosJuegos = gd.getJuegos();
        List<Juego> juegosActuales = editar != null ? gd.getJuegosDesarrollador(editar.getIdDesarrollador()) : List.of();
        VBox juegosBox = new VBox(4);
        java.util.List<CheckBox> cbs = new java.util.ArrayList<>();
        for (Juego j : todosJuegos) {
            CheckBox cb = new CheckBox(j.getTitulo()); cb.setStyle("-fx-text-fill:#eaeaea;");
            cb.setSelected(juegosActuales.stream().anyMatch(jj -> jj.getIdJuego() == j.getIdJuego()));
            cbs.add(cb); juegosBox.getChildren().add(cb);
        }

        Label lblErr = new Label(); lblErr.getStyleClass().add("label-accent");
        Button btnG = new Button("Guardar"); btnG.getStyleClass().add("btn-primary");
        Button btnC = new Button("Cancelar"); btnC.getStyleClass().add("btn-secondary");
        btnC.setOnAction(e -> gestionEstudios());

        btnG.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblErr.setText("El nombre es obligatorio."); return; }
            int anos = 0; try { anos = Integer.parseInt(txAnos.getText()); } catch (Exception ex) {}

            java.util.List<Integer> idsJuegos = new java.util.ArrayList<>();
            for (int i = 0; i < cbs.size(); i++) if (cbs.get(i).isSelected()) idsJuegos.add(todosJuegos.get(i).getIdJuego());

            if (editar == null) {
                Desarrollador d = gd.altaDesarrollador(txNombre.getText().trim(), txApellidos.getText().trim(), anos, txPuesto.getText().trim(), estudio);
                if (d != null) gd.setJuegosDesarrollador(d.getIdDesarrollador(), idsJuegos);
            } else {
                editar.setNombre(txNombre.getText().trim()); editar.setApellidos(txApellidos.getText().trim());
                editar.setPuestoActual(txPuesto.getText().trim()); editar.setAnosExperiencia(anos);
                gd.actualizarDesarrollador(editar);
                gd.setJuegosDesarrollador(editar.getIdDesarrollador(), idsJuegos);
            }
            DialogUtil.info("Desarrollador guardado."); gestionEstudios();
        });

        form.getChildren().addAll(titulo,
                lbl("Nombre:"), txNombre, lbl("Apellidos:"), txApellidos,
                lbl("Puesto:"), txPuesto, lbl("Años de experiencia:"), txAnos,
                new Label("Juegos en los que ha trabajado:") {{ getStyleClass().add("label-muted"); }},
                juegosBox, lblErr, new HBox(10, btnG, btnC));
        setContent(scroll(form));
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN RESEÑAS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionResenas() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("✍ Gestión de Reseñas"); titulo.getStyleClass().add("label-title");
        HBox filtro = new HBox(10);
        TextField txFiltro = new TextField(); txFiltro.setPromptText("Filtrar por juego o usuario..."); txFiltro.getStyleClass().add("text-field");
        Button btnFiltrar = new Button("Filtrar"); btnFiltrar.getStyleClass().add("btn-secondary");
        filtro.getChildren().addAll(txFiltro, btnFiltrar);
        VBox lista = new VBox(8);

        Runnable actualizar = () -> {
            lista.getChildren().clear();
            String f = txFiltro.getText().toLowerCase();
            for (Resena r : gd.getResenas()) {
                if (!f.isEmpty() && !r.getJuego().getTitulo().toLowerCase().contains(f)
                        && !r.getAutor().getNombreCompleto().toLowerCase().contains(f)) continue;
                HBox card = new HBox(10); card.getStyleClass().add("card"); card.setAlignment(Pos.CENTER_LEFT); card.setPadding(new Insets(8));
                VBox info = new VBox(3); HBox.setHgrow(info, Priority.ALWAYS);
                Label lt = new Label(r.getJuego().getTitulo() + " – ⭐" + r.getPuntuacion() + "/10"); lt.getStyleClass().add("label-section");
                Label la = new Label("por " + r.getAutor().getNombreCompleto() + "  |  " + r.getFechaFormateada() + "  |  " + r.getIdioma()); la.getStyleClass().add("label-muted");
                Label lc = new Label(r.getComentario()); lc.getStyleClass().add("label-normal"); lc.setWrapText(true);
                info.getChildren().addAll(lt, la, lc);
                Button btnDel = new Button("🗑 Eliminar"); btnDel.getStyleClass().add("btn-danger");
                btnDel.setOnAction(ev -> { if (DialogUtil.confirmar("¿Eliminar esta reseña?")) { gd.eliminarResena(r.getIdResena()); gestionResenas(); }});
                card.getChildren().addAll(info, btnDel);
                lista.getChildren().add(card);
            }
            if (lista.getChildren().isEmpty()) lista.getChildren().add(new Label("No hay reseñas."));
        };
        btnFiltrar.setOnAction(e -> actualizar.run());
        actualizar.run();
        root.getChildren().addAll(titulo, filtro, lista);
        setContent(scroll(root));
    }

    // ══════════════════════════════════════════════════════
    // HISTORIAL DE COMPRAS GLOBAL
    // ══════════════════════════════════════════════════════
    @FXML public void verCompras() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🛒 Historial de Compras Global"); titulo.getStyleClass().add("label-title");
        TableView<Compra> tabla = new TableView<>(FXCollections.observableArrayList(gd.getHistorialComprasGlobal()));
        tabla.getStyleClass().add("table-view"); tabla.setPrefHeight(400);
        col(tabla, "ID",       c -> String.valueOf(c.getCodCompra()), 50);
        col(tabla, "Usuario",  c -> c.getUsuario().getNombreCompleto(), 170);
        col(tabla, "Juego",    c -> c.getJuego().getTitulo(), 180);
        col(tabla, "Fecha",    c -> c.getFechaFormateada(), 100);
        col(tabla, "Cantidad", c -> String.valueOf(c.getCantidad()), 70);
        col(tabla, "Coste",    c -> String.format("%.2f€", c.getCoste()), 90);
        double total = gd.getHistorialComprasGlobal().stream().mapToDouble(Compra::getCoste).sum();
        Label lblTotal = new Label(String.format("Ingresos totales: %.2f€", total)); lblTotal.getStyleClass().add("label-gold");
        root.getChildren().addAll(titulo, tabla, lblTotal);
        setContent(scroll(root));
    }

    // ══════════════════════════════════════════════════════
    // ESTADÍSTICAS
    // ══════════════════════════════════════════════════════
    @FXML public void estadVentas() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("📊 Ventas por Juego"); titulo.getStyleClass().add("label-title");
        root.getChildren().add(titulo);
        List<Juego> juegos = gd.getJuegos();
        int max = juegos.stream().mapToInt(gd::getVentasJuego).max().orElse(1);
        for (Juego j : juegos) {
            int ventas = gd.getVentasJuego(j);
            HBox row = new HBox(15); row.getStyleClass().add("card"); row.setAlignment(Pos.CENTER_LEFT); row.setPadding(new Insets(8));
            VBox info = new VBox(3); HBox.setHgrow(info, Priority.ALWAYS);
            Label lt = new Label(j.getTitulo()); lt.getStyleClass().add("label-section");
            Label lv = new Label("Ventas: " + ventas + "  |  Ingresos: " + String.format("%.2f€", ventas * j.getPrecio())); lv.getStyleClass().add("label-normal");
            info.getChildren().addAll(lt, lv);
            Region fill = new Region(); fill.setStyle("-fx-background-color:#e94560; -fx-background-radius:4;");
            fill.setPrefWidth(220 * (max > 0 ? (double) ventas / max : 0)); fill.setPrefHeight(12);
            HBox barra = new HBox(fill); barra.setMinWidth(220); barra.setStyle("-fx-background-color:#16213e; -fx-background-radius:4;"); barra.setAlignment(Pos.CENTER_LEFT);
            row.getChildren().addAll(info, barra); root.getChildren().add(row);
        }
        setContent(scroll(root));
    }

    @FXML public void estadMejorVal() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("⭐ Juegos Mejor Valorados"); titulo.getStyleClass().add("label-title"); root.getChildren().add(titulo);
        int pos = 1;
        for (Juego j : gd.getJuegosMejorValorados()) {
            double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
            int nRes = gd.getResenasPorJuego(j).size();
            HBox row = new HBox(15); row.getStyleClass().add("card"); row.setAlignment(Pos.CENTER_LEFT); row.setPadding(new Insets(10));
            Label lp = new Label("#" + pos++); lp.getStyleClass().add("label-gold"); lp.setMinWidth(30);
            VBox info = new VBox(3);
            Label lt = new Label(j.getTitulo()); lt.getStyleClass().add("label-section");
            Label lm = new Label(String.format("⭐ %.1f/10  (%d reseñas)  |  Director: %s", media, nRes, j.getDirector())); lm.getStyleClass().add("label-normal");
            info.getChildren().addAll(lt, lm); row.getChildren().addAll(lp, info); root.getChildren().add(row);
        }
        setContent(scroll(root));
    }

    @FXML public void estadMasVend() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🔥 Juegos Más Vendidos"); titulo.getStyleClass().add("label-title"); root.getChildren().add(titulo);
        int pos = 1;
        for (Juego j : gd.getJuegosMasVendidos()) {
            int ventas = gd.getVentasJuego(j);
            HBox row = new HBox(15); row.getStyleClass().add("card"); row.setAlignment(Pos.CENTER_LEFT); row.setPadding(new Insets(10));
            Label lp = new Label("#" + pos++); lp.getStyleClass().add("label-gold"); lp.setMinWidth(30);
            VBox info = new VBox(3);
            Label lt = new Label(j.getTitulo()); lt.getStyleClass().add("label-section");
            Label lv = new Label("🔥 " + ventas + " uds  |  " + String.format("%.2f€ ingresos", ventas * j.getPrecio())); lv.getStyleClass().add("label-normal");
            info.getChildren().addAll(lt, lv); row.getChildren().addAll(lp, info); root.getChildren().add(row);
        }
        setContent(scroll(root));
    }

    @FXML public void estadResIdioma() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("💬 Reseñas por Idioma"); titulo.getStyleClass().add("label-title"); root.getChildren().add(titulo);
        String[] idiomas = {"Español","English","Français","Deutsch","Português","Italiano"};
        boolean hay = false;
        for (String idioma : idiomas) {
            List<Resena> lista = gd.getResenasPorIdioma(idioma); if (lista.isEmpty()) continue; hay = true;
            VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(10));
            Label li = new Label("🌐 " + idioma + "  (" + lista.size() + " reseñas)"); li.getStyleClass().add("label-section");
            card.getChildren().add(li);
            for (Resena r : lista) {
                Label l = new Label("• " + r.getJuego().getTitulo() + " – " + r.getAutor().getNombreCompleto() + "  ⭐" + r.getPuntuacion());
                l.getStyleClass().add("label-normal"); card.getChildren().add(l);
            }
            root.getChildren().add(card);
        }
        if (!hay) root.getChildren().add(new Label("No hay reseñas registradas."));
        setContent(scroll(root));
    }

    @FXML public void estadResenasPorJuego() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🎮 Reseñas por Juego"); titulo.getStyleClass().add("label-title"); root.getChildren().add(titulo);
        boolean hay = false;
        for (Juego j : gd.getJuegos()) {
            List<Resena> lista = gd.getResenasPorJuego(j); if (lista.isEmpty()) continue; hay = true;
            double media = gd.getPuntuacionMediaJuego(j.getIdJuego());
            VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(10));
            Label lj = new Label("🎮 " + j.getTitulo() + String.format("  ⭐ %.1f/10  (%d reseñas)", media, lista.size())); lj.getStyleClass().add("label-section");
            card.getChildren().add(lj);
            for (Resena r : lista) {
                Label l = new Label("  • " + r.getAutor().getNombreCompleto() + "  ⭐" + r.getPuntuacion() + "/10  [" + r.getIdioma() + "]  – " + r.getComentario());
                l.getStyleClass().add("label-normal"); l.setWrapText(true); card.getChildren().add(l);
            }
            root.getChildren().add(card);
        }
        if (!hay) root.getChildren().add(new Label("No hay reseñas registradas."));
        setContent(scroll(root));
    }

    @FXML public void estadEstudio() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🏢 Estadísticas por Estudio"); titulo.getStyleClass().add("label-title"); root.getChildren().add(titulo);
        for (Estudio est : gd.getEstudios()) {
            VBox card = new VBox(8); card.getStyleClass().add("card"); card.setPadding(new Insets(12));
            Label le = new Label("🏢 " + est.getNombre()); le.getStyleClass().add("label-section"); card.getChildren().add(le);
            Juego mv = gd.getJuegoMejorValoradoEstudio(est);
            Juego ms = gd.getJuegoMasVendidoEstudio(est);
            if (mv != null) { Label l = new Label("⭐ Mejor valorado: " + mv.getTitulo() + String.format("  (%.1f/10)", gd.getPuntuacionMediaJuego(mv.getIdJuego()))); l.getStyleClass().add("label-normal"); card.getChildren().add(l); }
            if (ms != null) { Label l = new Label("🔥 Más vendido: " + ms.getTitulo() + "  (" + gd.getVentasJuego(ms) + " uds)"); l.getStyleClass().add("label-normal"); card.getChildren().add(l); }
            if (!est.getDesarrolladores().isEmpty()) {
                Label ld = new Label("👨‍💻 Desarrolladores:"); ld.getStyleClass().add("label-muted"); card.getChildren().add(ld);
                for (Desarrollador d : est.getDesarrolladores()) {
                    Juego jmv = gd.getJuegoMejorValoradoDesarrollador(d);
                    Juego jms = gd.getJuegoMasVendidoDesarrollador(d);
                    StringBuilder sb = new StringBuilder("  • " + d.getNombreCompleto() + " (" + d.getPuestoActual() + ")");
                    if (jmv != null) sb.append("  |  ⭐ ").append(jmv.getTitulo());
                    if (jms != null) sb.append("  |  🔥 ").append(jms.getTitulo());
                    Label l = new Label(sb.toString()); l.getStyleClass().add("label-muted"); card.getChildren().add(l);
                }
            }
            root.getChildren().add(card);
        }
        setContent(scroll(root));
    }

    @FXML public void estadJuegosPorUsuario() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("🛒 Juegos Comprados por Usuario"); titulo.getStyleClass().add("label-title"); root.getChildren().add(titulo);
        for (Usuario u : gd.getUsuarios()) {
            List<Juego> bib = gd.getBibliotecaUsuario(u.getIdUsuario());
            VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(10));
            Label lu = new Label("👤 " + u.getNombreCompleto() + "  <" + u.getCorreo() + ">"); lu.getStyleClass().add("label-section"); card.getChildren().add(lu);
            if (bib.isEmpty()) { card.getChildren().add(new Label("  Sin compras.")); }
            else { for (Juego j : bib) { Label l = new Label("  • " + j.getTitulo() + " (" + j.getGenero() + ")"); l.getStyleClass().add("label-normal"); card.getChildren().add(l); } }
            root.getChildren().add(card);
        }
        setContent(scroll(root));
    }

    @FXML public void estadResenasPorUsuario() {
        VBox root = new VBox(15); root.setPadding(new Insets(10));
        Label titulo = new Label("✍ Reseñas por Usuario"); titulo.getStyleClass().add("label-title"); root.getChildren().add(titulo);
        for (Usuario u : gd.getUsuarios()) {
            List<Resena> lista = gd.getResenasPorUsuario(u); if (lista.isEmpty()) continue;
            VBox card = new VBox(6); card.getStyleClass().add("card"); card.setPadding(new Insets(10));
            Label lu = new Label("👤 " + u.getNombreCompleto() + "  (" + lista.size() + " reseñas)"); lu.getStyleClass().add("label-section"); card.getChildren().add(lu);
            for (Resena r : lista) {
                Label l = new Label("  • " + r.getJuego().getTitulo() + "  ⭐" + r.getPuntuacion() + "/10  –  " + r.getComentario());
                l.getStyleClass().add("label-normal"); l.setWrapText(true); card.getChildren().add(l);
            }
            root.getChildren().add(card);
        }
        setContent(scroll(root));
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

    private TextField tf(String v) { TextField tf = new TextField(v); tf.getStyleClass().add("text-field"); tf.setMaxWidth(Double.MAX_VALUE); return tf; }
    private Label lbl(String t)   { Label l = new Label(t); l.getStyleClass().add("label-normal"); return l; }
    private ScrollPane scroll(javafx.scene.Node n) { ScrollPane sp = new ScrollPane(n); sp.setFitToWidth(true); sp.getStyleClass().add("scroll-pane"); return sp; }

    private HBox parLabel(String etiqueta, String valor) {
        HBox row = new HBox(10);
        Label e = new Label(etiqueta); e.getStyleClass().add("label-muted"); e.setMinWidth(190);
        Label v = new Label(valor);    v.getStyleClass().add("label-normal");
        row.getChildren().addAll(e, v); return row;
    }
}
