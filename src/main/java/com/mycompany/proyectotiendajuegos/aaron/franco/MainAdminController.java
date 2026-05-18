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
import java.util.function.Supplier;

public class MainAdminController implements Initializable {

    @FXML private StackPane contentPane;
    @FXML private Label     lblAdminNombre;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblAdminNombre.setText("Admin: " + gd.getAdminActual().getNombreCompleto());
        gestionUsuarios();
    }

    private void setContent(Node nodo) { contentPane.getChildren().setAll(nodo); }

    // ══════════════════════════════════════════════════════
    // GESTIÓN USUARIOS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionUsuarios() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
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

        TableColumn<Usuario, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(220);
        colAcciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnVer   = new Button("🔍 Ver");
            final Button btnEditar = new Button("✏ Editar");
            final Button btnBaja  = new Button("🗑 Baja");
            {
                btnVer.getStyleClass().add("btn-gold");
                btnEditar.getStyleClass().add("btn-secondary");
                btnBaja.getStyleClass().add("btn-danger");
                btnVer.setOnAction(e    -> consultarUsuario(getTableView().getItems().get(getIndex())));
                btnEditar.setOnAction(e -> formUsuario(getTableView().getItems().get(getIndex())));
                btnBaja.setOnAction(e   -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Dar de baja al usuario " + usuario.getNombreCompleto() + "?")) {
                        gd.bajaUsuario(usuario.getIdUsuario());
                        gestionUsuarios();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : new HBox(5, btnVer, btnEditar, btnBaja));
            }
        });
        tabla.getColumns().add(colAcciones);
        raiz.getChildren().addAll(titulo, btnNuevo, tabla);
        setContent(scroll(raiz));
    }

    private void consultarUsuario(Usuario usuario) {
        List<Juego>  biblioteca = gd.getBibliotecaUsuario(usuario.getIdUsuario());
        List<Compra> compras    = gd.getComprasUsuario(usuario.getIdUsuario());
        List<Resena> resenas    = gd.getResenasPorUsuario(usuario);

        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        raiz.setMaxWidth(560);
        Label titulo = new Label("🔍 Detalle de Usuario");
        titulo.getStyleClass().add("label-title");

        VBox tarjeta = new VBox(10);
        tarjeta.getStyleClass().add("card");
        tarjeta.setPadding(new Insets(16));
        tarjeta.getChildren().addAll(
            parLabel("ID:",                   String.valueOf(usuario.getIdUsuario())),
            parLabel("Nombre completo:",      usuario.getNombreCompleto()),
            parLabel("Correo:",               usuario.getCorreo()),
            parLabel("Idioma:",               usuario.getIdioma()),
            parLabel("Saldo:",                String.format("%.2f€", usuario.getSaldo())),
            parLabel("Juegos en biblioteca:", String.valueOf(biblioteca.size())),
            parLabel("Compras realizadas:",   String.valueOf(compras.size()))
        );

        if (!biblioteca.isEmpty()) {
            Label lblBib = new Label("📚 Biblioteca:");
            lblBib.getStyleClass().add("label-section");
            tarjeta.getChildren().add(lblBib);
            for (Juego juego : biblioteca) {
                Label l = new Label("  • " + juego.getTitulo() + " – " + juego.getGenero());
                l.getStyleClass().add("label-normal");
                tarjeta.getChildren().add(l);
            }
        }
        if (!resenas.isEmpty()) {
            Label lblRes = new Label("✍ Reseñas (" + resenas.size() + "):");
            lblRes.getStyleClass().add("label-section");
            tarjeta.getChildren().add(lblRes);
            for (Resena resena : resenas) {
                Label l = new Label("  • " + resena.getJuego().getTitulo()
                        + "  ⭐" + resena.getPuntuacion() + "/10 – " + resena.getComentario());
                l.getStyleClass().add("label-normal");
                l.setWrapText(true);
                tarjeta.getChildren().add(l);
            }
        }

        Button btnVolver = new Button("← Volver");
        btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> gestionUsuarios());
        raiz.getChildren().addAll(titulo, tarjeta, btnVolver);
        setContent(scroll(raiz));
    }

    private void formUsuario(Usuario editar) {
        VBox formulario = new VBox(12);
        formulario.setPadding(new Insets(20));
        formulario.setMaxWidth(480);
        formulario.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Usuario" : "Editar Usuario");
        titulo.getStyleClass().add("label-section");

        TextField txNombre    = tf(editar != null ? editar.getNombre()    : "");
        TextField txApellidos = tf(editar != null ? editar.getApellidos() : "");
        TextField txCorreo    = tf(editar != null ? editar.getCorreo()    : "");
        PasswordField txContrasena = new PasswordField();
        txContrasena.getStyleClass().add("password-field");
        txContrasena.setPromptText(editar != null ? "(sin cambios)" : "Contraseña");
        TextField txSaldo = tf(editar != null ? String.valueOf(editar.getSaldo()) : "50");
        ComboBox<String> cbIdioma = new ComboBox<>(FXCollections.observableArrayList(
                "Español","English","Français","Deutsch","Português","Italiano"));
        cbIdioma.getSelectionModel().select(editar != null ? editar.getIdioma() : "Español");
        cbIdioma.getStyleClass().add("combo-box");

        Label lblError = new Label();
        lblError.getStyleClass().add("label-accent");
        Button btnGuardar  = new Button("Guardar");  btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancelar = new Button("Cancelar"); btnCancelar.getStyleClass().add("btn-secondary");
        btnCancelar.setOnAction(e -> gestionUsuarios());

        btnGuardar.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblError.setText("El nombre es obligatorio."); return; }
            double saldo;
            try { saldo = Double.parseDouble(txSaldo.getText().replace(",",".")); }
            catch (NumberFormatException ex) { lblError.setText("Saldo inválido."); return; }

            if (editar == null) {
                if (txContrasena.getText().isEmpty()) { lblError.setText("La contraseña es obligatoria."); return; }
                boolean exito = gd.altaUsuario(txNombre.getText().trim(), txApellidos.getText().trim(),
                        txCorreo.getText().trim(), txContrasena.getText(), saldo, cbIdioma.getValue());
                if (!exito) { lblError.setText("El correo ya existe."); return; }
            } else {
                editar.setNombre(txNombre.getText().trim());
                editar.setApellidos(txApellidos.getText().trim());
                editar.setCorreo(txCorreo.getText().trim());
                editar.setSaldo(saldo);
                editar.setIdioma(cbIdioma.getValue());
                if (!txContrasena.getText().isEmpty()) editar.setContrasena(txContrasena.getText());
                gd.actualizarUsuario(editar);
            }
            DialogUtil.info("Usuario guardado correctamente.");
            gestionUsuarios();
        });

        formulario.getChildren().addAll(titulo,
                lbl("Nombre:"), txNombre, lbl("Apellidos:"), txApellidos,
                lbl("Correo:"), txCorreo, lbl("Contraseña:"), txContrasena,
                lbl("Saldo (€):"), txSaldo, lbl("Idioma:"), cbIdioma,
                lblError, new HBox(10, btnGuardar, btnCancelar));
        setContent(scroll(formulario));
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN ADMINISTRADORES
    // ══════════════════════════════════════════════════════
    @FXML public void gestionAdmins() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
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

        TableColumn<Administrador, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnEditar = new Button("✏ Editar");
            final Button btnBaja   = new Button("🗑 Baja");
            {
                btnEditar.getStyleClass().add("btn-secondary");
                btnBaja.getStyleClass().add("btn-danger");
                btnEditar.setOnAction(e -> formAdmin(getTableView().getItems().get(getIndex())));
                btnBaja.setOnAction(e -> {
                    Administrador admin = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Dar de baja al admin " + admin.getNombreCompleto() + "?")) {
                        gd.bajaAdmin(admin.getIdAdmin());
                        gestionAdmins();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : new HBox(5, btnEditar, btnBaja));
            }
        });
        tabla.getColumns().add(colAcciones);
        raiz.getChildren().addAll(titulo, btnNuevo, tabla);
        setContent(scroll(raiz));
    }

    private void formAdmin(Administrador editar) {
        VBox formulario = new VBox(12);
        formulario.setPadding(new Insets(20));
        formulario.setMaxWidth(450);
        formulario.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Administrador" : "Editar Administrador");
        titulo.getStyleClass().add("label-section");
        TextField txNombre    = tf(editar != null ? editar.getNombre()    : "");
        TextField txApellidos = tf(editar != null ? editar.getApellidos() : "");
        TextField txCorreo    = tf(editar != null ? editar.getCorreo()    : "");
        PasswordField txContrasena = new PasswordField();
        txContrasena.getStyleClass().add("password-field");
        txContrasena.setPromptText(editar != null ? "(sin cambios)" : "Contraseña");
        Label lblError = new Label();
        lblError.getStyleClass().add("label-accent");
        Button btnGuardar  = new Button("Guardar");  btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancelar = new Button("Cancelar"); btnCancelar.getStyleClass().add("btn-secondary");
        btnCancelar.setOnAction(e -> gestionAdmins());
        btnGuardar.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblError.setText("El nombre es obligatorio."); return; }
            if (editar == null) {
                if (txContrasena.getText().isEmpty()) { lblError.setText("La contraseña es obligatoria."); return; }
                boolean exito = gd.altaAdmin(txNombre.getText().trim(), txApellidos.getText().trim(),
                        txCorreo.getText().trim(), txContrasena.getText());
                if (!exito) { lblError.setText("El correo ya existe."); return; }
            } else {
                editar.setNombre(txNombre.getText().trim());
                editar.setApellidos(txApellidos.getText().trim());
                editar.setCorreo(txCorreo.getText().trim());
                if (!txContrasena.getText().isEmpty()) editar.setContrasena(txContrasena.getText());
                gd.actualizarAdmin(editar);
            }
            DialogUtil.info("Administrador guardado.");
            gestionAdmins();
        });
        formulario.getChildren().addAll(titulo,
                lbl("Nombre:"), txNombre, lbl("Apellidos:"), txApellidos,
                lbl("Correo:"), txCorreo, lbl("Contraseña:"), txContrasena,
                lblError, new HBox(10, btnGuardar, btnCancelar));
        setContent(scroll(formulario));
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN JUEGOS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionJuegos() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
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
        col(tabla, "Género",     j -> j.getGenero(), 110);
        col(tabla, "Plataforma", j -> j.getPlataforma(), 130);
        col(tabla, "Precio",     j -> String.format("%.2f€", j.getPrecio()), 75);
        col(tabla, "Stock",      j -> String.valueOf(j.getStock()), 55);
        col(tabla, "Director",   j -> j.getDirector(), 140);

        TableColumn<Juego, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(170);
        colAcciones.setCellFactory(tc -> new TableCell<>() {
            final Button btnVer    = new Button("🔍");
            final Button btnEditar = new Button("✏");
            final Button btnBaja   = new Button("🗑");
            {
                btnVer.getStyleClass().add("btn-gold");
                btnEditar.getStyleClass().add("btn-secondary");
                btnBaja.getStyleClass().add("btn-danger");
                btnVer.setOnAction(e    -> consultarJuego(getTableView().getItems().get(getIndex())));
                btnEditar.setOnAction(e -> formJuego(getTableView().getItems().get(getIndex())));
                btnBaja.setOnAction(e   -> {
                    Juego juego = getTableView().getItems().get(getIndex());
                    if (DialogUtil.confirmar("¿Eliminar '" + juego.getTitulo() + "'?")) {
                        gd.bajaJuego(juego.getIdJuego());
                        gestionJuegos();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : new HBox(4, btnVer, btnEditar, btnBaja));
            }
        });
        tabla.getColumns().add(colAcciones);
        raiz.getChildren().addAll(titulo, btnNuevo, tabla);
        setContent(scroll(raiz));
    }

    private void consultarJuego(Juego juego) {
        List<Resena> resenas = gd.getResenasPorJuego(juego);
        double media          = gd.getPuntuacionMediaJuego(juego.getIdJuego());
        int ventas            = gd.getVentasJuego(juego);

        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        raiz.setMaxWidth(560);
        Label titulo = new Label("🔍 Detalle del Juego");
        titulo.getStyleClass().add("label-title");
        VBox tarjeta = new VBox(10);
        tarjeta.getStyleClass().add("card");
        tarjeta.setPadding(new Insets(16));
        tarjeta.getChildren().addAll(
            parLabel("ID:",           String.valueOf(juego.getIdJuego())),
            parLabel("Título:",       juego.getTitulo()),
            parLabel("Género:",       juego.getGenero()),
            parLabel("Plataforma:",   juego.getPlataforma()),
            parLabel("Precio:",       String.format("%.2f€", juego.getPrecio())),
            parLabel("Stock:",        String.valueOf(juego.getStock())),
            parLabel("Director:",     juego.getDirector()),
            parLabel("Valoración media:", resenas.isEmpty() ? "Sin reseñas"
                                          : String.format("⭐ %.1f/10", media)),
            parLabel("Ventas totales:",   String.valueOf(ventas))
        );
        if (!resenas.isEmpty()) {
            Label lblRes = new Label("✍ Reseñas (" + resenas.size() + "):");
            lblRes.getStyleClass().add("label-section");
            tarjeta.getChildren().add(lblRes);
            for (Resena resena : resenas) {
                Label l = new Label("  • " + resena.getAutor().getNombreCompleto()
                        + "  ⭐" + resena.getPuntuacion() + " – " + resena.getComentario());
                l.getStyleClass().add("label-normal");
                l.setWrapText(true);
                tarjeta.getChildren().add(l);
            }
        }
        Button btnVolver = new Button("← Volver");
        btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> gestionJuegos());
        raiz.getChildren().addAll(titulo, tarjeta, btnVolver);
        setContent(scroll(raiz));
    }

    private void formJuego(Juego editar) {
        VBox formulario = new VBox(12);
        formulario.setPadding(new Insets(20));
        formulario.setMaxWidth(500);
        formulario.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Juego" : "Editar Juego");
        titulo.getStyleClass().add("label-section");

        TextField txTitulo    = tf(editar != null ? editar.getTitulo()     : "");
        TextField txGenero    = tf(editar != null ? editar.getGenero()     : "");
        TextField txPlataforma = tf(editar != null ? editar.getPlataforma() : "");
        TextField txPrecio    = tf(editar != null ? String.valueOf(editar.getPrecio()) : "");
        TextField txStock     = tf(editar != null ? String.valueOf(editar.getStock())  : "");
        TextField txDirector  = tf(editar != null ? editar.getDirector()   : "");

        ComboBox<Estudio> cbEstudio = new ComboBox<>(FXCollections.observableArrayList(gd.getEstudios()));
        cbEstudio.setPromptText("Asignar a estudio (opcional)");
        cbEstudio.getStyleClass().add("combo-box");

        Label lblError = new Label();
        lblError.getStyleClass().add("label-accent");
        Button btnGuardar  = new Button("Guardar");  btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancelar = new Button("Cancelar"); btnCancelar.getStyleClass().add("btn-secondary");
        btnCancelar.setOnAction(e -> gestionJuegos());

        btnGuardar.setOnAction(e -> {
            if (txTitulo.getText().trim().isEmpty()) { lblError.setText("El título es obligatorio."); return; }
            double precio;
            int stock;
            try { precio = Double.parseDouble(txPrecio.getText().replace(",",".")); }
            catch (Exception ex) { lblError.setText("Precio inválido."); return; }
            try { stock = Integer.parseInt(txStock.getText()); }
            catch (Exception ex) { lblError.setText("Stock inválido."); return; }

            if (editar == null) {
                Juego nuevoJuego = gd.altaJuego(txTitulo.getText().trim(), txGenero.getText().trim(),
                        txPlataforma.getText().trim(), precio, stock, txDirector.getText().trim());
                if (nuevoJuego != null && cbEstudio.getValue() != null)
                    gd.asignarJuegoAEstudio(nuevoJuego.getIdJuego(), cbEstudio.getValue().getIdEstudio());
            } else {
                editar.setTitulo(txTitulo.getText().trim());
                editar.setGenero(txGenero.getText().trim());
                editar.setPlataforma(txPlataforma.getText().trim());
                editar.setPrecio(precio);
                editar.setStock(stock);
                editar.setDirector(txDirector.getText().trim());
                gd.actualizarJuego(editar);
                if (cbEstudio.getValue() != null)
                    gd.asignarJuegoAEstudio(editar.getIdJuego(), cbEstudio.getValue().getIdEstudio());
            }
            DialogUtil.info("Juego guardado correctamente.");
            gestionJuegos();
        });

        formulario.getChildren().addAll(titulo,
                lbl("Título:"), txTitulo, lbl("Género:"), txGenero,
                lbl("Plataforma:"), txPlataforma, lbl("Precio (€):"), txPrecio,
                lbl("Stock:"), txStock, lbl("Director:"), txDirector,
                lbl("Estudio:"), cbEstudio, lblError, new HBox(10, btnGuardar, btnCancelar));
        setContent(scroll(formulario));
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN ESTUDIOS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionEstudios() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("🏢 Gestión de Estudios");
        titulo.getStyleClass().add("label-title");
        Button btnNuevo = new Button("+ Nuevo Estudio");
        btnNuevo.getStyleClass().add("btn-primary");
        btnNuevo.setOnAction(e -> formEstudio(null));
        raiz.getChildren().addAll(titulo, btnNuevo);

        for (Estudio estudio : gd.getEstudios()) {
            VBox tarjeta = new VBox(8);
            tarjeta.getStyleClass().add("card");
            tarjeta.setPadding(new Insets(12));
            HBox cabecera = new HBox(10);
            Label lblNombre = new Label("🏢 " + estudio.getNombre());
            lblNombre.getStyleClass().add("label-section");
            Region separador = new Region();
            HBox.setHgrow(separador, Priority.ALWAYS);
            Button btnAnadirDev = new Button("+ Dev");  btnAnadirDev.getStyleClass().add("btn-gold");
            Button btnEditar    = new Button("✏ Editar"); btnEditar.getStyleClass().add("btn-secondary");
            Button btnBaja      = new Button("🗑 Baja");  btnBaja.getStyleClass().add("btn-danger");
            btnAnadirDev.setOnAction(e -> formDesarrollador(null, estudio));
            btnEditar.setOnAction(e -> formEstudio(estudio));
            btnBaja.setOnAction(e -> {
                if (DialogUtil.confirmar("¿Eliminar estudio '" + estudio.getNombre() + "'?")) {
                    gd.bajaEstudio(estudio.getIdEstudio());
                    gestionEstudios();
                }
            });
            cabecera.getChildren().addAll(lblNombre, separador, btnAnadirDev, btnEditar, btnBaja);

            String nombresJuegos = estudio.getJuegos().stream().map(Juego::getTitulo)
                    .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
            Label lblJuegos = new Label("Juegos (" + estudio.getJuegos().size() + "): " + nombresJuegos);
            lblJuegos.getStyleClass().add("label-normal");
            lblJuegos.setWrapText(true);
            tarjeta.getChildren().addAll(cabecera, lblJuegos);

            if (!estudio.getDesarrolladores().isEmpty()) {
                Label lblDevTit = new Label("👨‍💻 Desarrolladores:");
                lblDevTit.getStyleClass().add("label-muted");
                tarjeta.getChildren().add(lblDevTit);
                for (Desarrollador dev : estudio.getDesarrolladores()) {
                    HBox filadev = new HBox(8);
                    filadev.setAlignment(Pos.CENTER_LEFT);
                    Label lblDev = new Label("  • " + dev.getNombreCompleto()
                            + " – " + dev.getPuestoActual()
                            + "  (" + dev.getAnosExperiencia() + " años)");
                    lblDev.getStyleClass().add("label-normal");
                    HBox.setHgrow(lblDev, Priority.ALWAYS);
                    Button btnEditarDev = new Button("✏"); btnEditarDev.getStyleClass().add("btn-secondary");
                    Button btnBajaDev   = new Button("🗑"); btnBajaDev.getStyleClass().add("btn-danger");
                    btnEditarDev.setOnAction(ev -> formDesarrollador(dev, estudio));
                    btnBajaDev.setOnAction(ev -> {
                        if (DialogUtil.confirmar("¿Dar de baja a " + dev.getNombreCompleto() + "?")) {
                            gd.bajaDesarrollador(dev.getIdDesarrollador());
                            gestionEstudios();
                        }
                    });
                    filadev.getChildren().addAll(lblDev, btnEditarDev, btnBajaDev);
                    tarjeta.getChildren().add(filadev);
                }
            }
            raiz.getChildren().add(tarjeta);
        }
        setContent(scroll(raiz));
    }

    private void formEstudio(Estudio editar) {
        VBox formulario = new VBox(12);
        formulario.setPadding(new Insets(20));
        formulario.setMaxWidth(450);
        formulario.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Estudio" : "Editar Estudio");
        titulo.getStyleClass().add("label-section");
        TextField txNombre = tf(editar != null ? editar.getNombre() : "");
        Label lblError = new Label();
        lblError.getStyleClass().add("label-accent");
        Button btnGuardar  = new Button("Guardar");  btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancelar = new Button("Cancelar"); btnCancelar.getStyleClass().add("btn-secondary");
        btnCancelar.setOnAction(e -> gestionEstudios());
        btnGuardar.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblError.setText("El nombre es obligatorio."); return; }
            if (editar == null) gd.altaEstudio(txNombre.getText().trim());
            else { editar.setNombre(txNombre.getText().trim()); gd.actualizarEstudio(editar); }
            DialogUtil.info("Estudio guardado.");
            gestionEstudios();
        });
        formulario.getChildren().addAll(titulo, lbl("Nombre:"), txNombre, lblError,
                new HBox(10, btnGuardar, btnCancelar));
        setContent(scroll(formulario));
    }

    private void formDesarrollador(Desarrollador editar, Estudio estudio) {
        VBox formulario = new VBox(12);
        formulario.setPadding(new Insets(20));
        formulario.setMaxWidth(480);
        formulario.getStyleClass().add("card");
        Label titulo = new Label(editar == null ? "Nuevo Desarrollador" : "Editar Desarrollador");
        titulo.getStyleClass().add("label-section");

        TextField txNombre    = tf(editar != null ? editar.getNombre()          : "");
        TextField txApellidos = tf(editar != null ? editar.getApellidos()       : "");
        TextField txPuesto    = tf(editar != null ? editar.getPuestoActual()    : "");
        TextField txAnos      = tf(editar != null ? String.valueOf(editar.getAnosExperiencia()) : "");

        List<Juego> todosJuegos       = gd.getJuegos();
        List<Juego> juegosDelDev      = editar != null
                ? gd.getJuegosDesarrollador(editar.getIdDesarrollador())
                : List.of();
        VBox contenedorCheckboxes = new VBox(4);
        java.util.List<CheckBox> checkboxes = new java.util.ArrayList<>();
        for (Juego juego : todosJuegos) {
            CheckBox cb = new CheckBox(juego.getTitulo());
            cb.setStyle("-fx-text-fill:#eaeaea;");
            cb.setSelected(juegosDelDev.stream().anyMatch(jj -> jj.getIdJuego() == juego.getIdJuego()));
            checkboxes.add(cb);
            contenedorCheckboxes.getChildren().add(cb);
        }

        Label lblError = new Label();
        lblError.getStyleClass().add("label-accent");
        Button btnGuardar  = new Button("Guardar");  btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancelar = new Button("Cancelar"); btnCancelar.getStyleClass().add("btn-secondary");
        btnCancelar.setOnAction(e -> gestionEstudios());

        btnGuardar.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) { lblError.setText("El nombre es obligatorio."); return; }
            int anosExp = 0;
            try { anosExp = Integer.parseInt(txAnos.getText()); } catch (Exception ex) { /* ignora */ }

            java.util.List<Integer> idsJuegosSeleccionados = new java.util.ArrayList<>();
            for (int i = 0; i < checkboxes.size(); i++) {
                if (checkboxes.get(i).isSelected()) idsJuegosSeleccionados.add(todosJuegos.get(i).getIdJuego());
            }

            if (editar == null) {
                Desarrollador nuevoDev = gd.altaDesarrollador(txNombre.getText().trim(),
                        txApellidos.getText().trim(), anosExp, txPuesto.getText().trim(), estudio);
                if (nuevoDev != null) gd.setJuegosDesarrollador(nuevoDev.getIdDesarrollador(), idsJuegosSeleccionados);
            } else {
                editar.setNombre(txNombre.getText().trim());
                editar.setApellidos(txApellidos.getText().trim());
                editar.setPuestoActual(txPuesto.getText().trim());
                editar.setAnosExperiencia(anosExp);
                gd.actualizarDesarrollador(editar);
                gd.setJuegosDesarrollador(editar.getIdDesarrollador(), idsJuegosSeleccionados);
            }
            DialogUtil.info("Desarrollador guardado.");
            gestionEstudios();
        });

        formulario.getChildren().addAll(titulo,
                lbl("Nombre:"), txNombre, lbl("Apellidos:"), txApellidos,
                lbl("Puesto:"), txPuesto, lbl("Años de experiencia:"), txAnos,
                new Label("Juegos en los que ha trabajado:") {{ getStyleClass().add("label-muted"); }},
                contenedorCheckboxes, lblError, new HBox(10, btnGuardar, btnCancelar));
        setContent(scroll(formulario));
    }

    // ══════════════════════════════════════════════════════
    // GESTIÓN RESEÑAS
    // ══════════════════════════════════════════════════════
    @FXML public void gestionResenas() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("✍ Gestión de Reseñas");
        titulo.getStyleClass().add("label-title");
        HBox barraFiltro = new HBox(10);
        TextField txFiltro = new TextField();
        txFiltro.setPromptText("Filtrar por juego o usuario...");
        txFiltro.getStyleClass().add("text-field");
        Button btnFiltrar = new Button("Filtrar");
        btnFiltrar.getStyleClass().add("btn-secondary");
        barraFiltro.getChildren().addAll(txFiltro, btnFiltrar);
        VBox listaResenas = new VBox(8);

        Runnable actualizarLista = () -> {
            listaResenas.getChildren().clear();
            String filtro = txFiltro.getText().toLowerCase();
            for (Resena resena : gd.getResenas()) {
                if (!filtro.isEmpty()
                        && !resena.getJuego().getTitulo().toLowerCase().contains(filtro)
                        && !resena.getAutor().getNombreCompleto().toLowerCase().contains(filtro)) continue;
                HBox tarjeta = new HBox(10);
                tarjeta.getStyleClass().add("card");
                tarjeta.setAlignment(Pos.CENTER_LEFT);
                tarjeta.setPadding(new Insets(8));
                VBox info = new VBox(3);
                HBox.setHgrow(info, Priority.ALWAYS);
                Label lblTitulo = new Label(resena.getJuego().getTitulo() + " – ⭐" + resena.getPuntuacion() + "/10");
                lblTitulo.getStyleClass().add("label-section");
                Label lblAutor = new Label("por " + resena.getAutor().getNombreCompleto()
                        + "  |  " + resena.getFechaFormateada() + "  |  " + resena.getIdioma());
                lblAutor.getStyleClass().add("label-muted");
                Label lblComentario = new Label(resena.getComentario());
                lblComentario.getStyleClass().add("label-normal");
                lblComentario.setWrapText(true);
                info.getChildren().addAll(lblTitulo, lblAutor, lblComentario);
                Button btnEliminar = new Button("🗑 Eliminar");
                btnEliminar.getStyleClass().add("btn-danger");
                btnEliminar.setOnAction(ev -> {
                    if (DialogUtil.confirmar("¿Eliminar esta reseña?")) {
                        gd.eliminarResena(resena.getIdResena());
                        gestionResenas();
                    }
                });
                tarjeta.getChildren().addAll(info, btnEliminar);
                listaResenas.getChildren().add(tarjeta);
            }
            if (listaResenas.getChildren().isEmpty())
                listaResenas.getChildren().add(new Label("No hay reseñas."));
        };
        btnFiltrar.setOnAction(e -> actualizarLista.run());
        actualizarLista.run();
        raiz.getChildren().addAll(titulo, barraFiltro, listaResenas);
        setContent(scroll(raiz));
    }

    // ══════════════════════════════════════════════════════
    // HISTORIAL DE COMPRAS GLOBAL
    // ══════════════════════════════════════════════════════
    @FXML public void verCompras() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("🛒 Historial de Compras Global");
        titulo.getStyleClass().add("label-title");
        TableView<Compra> tabla = new TableView<>(FXCollections.observableArrayList(gd.getHistorialComprasGlobal()));
        tabla.getStyleClass().add("table-view");
        tabla.setPrefHeight(400);
        col(tabla, "ID",       c -> String.valueOf(c.getCodCompra()), 50);
        col(tabla, "Usuario",  c -> c.getUsuario().getNombreCompleto(), 170);
        col(tabla, "Juego",    c -> c.getJuego().getTitulo(), 180);
        col(tabla, "Fecha",    c -> c.getFechaFormateada(), 100);
        col(tabla, "Cantidad", c -> String.valueOf(c.getCantidad()), 70);
        col(tabla, "Coste",    c -> String.format("%.2f€", c.getCoste()), 90);
        double totalIngresos = gd.getHistorialComprasGlobal().stream().mapToDouble(Compra::getCoste).sum();
        Label lblTotal = new Label(String.format("Ingresos totales: %.2f€", totalIngresos));
        lblTotal.getStyleClass().add("label-gold");
        raiz.getChildren().addAll(titulo, tabla, lblTotal);
        setContent(scroll(raiz));
    }

    // ══════════════════════════════════════════════════════
    // ESTADÍSTICAS – acordeón desplegable
    // ══════════════════════════════════════════════════════
    @FXML public void mostrarEstadisticasAdmin() {
        VBox raiz = new VBox(12);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("📊 Estadísticas y Consultas");
        titulo.getStyleClass().add("label-title");
        raiz.getChildren().add(titulo);

        raiz.getChildren().add(acordeon("📊 Ventas por Juego",            this::buildVentasPorJuego));
        raiz.getChildren().add(acordeon("⭐ Juegos Mejor Valorados",       this::buildMejorValorados));
        raiz.getChildren().add(acordeon("🔥 Juegos Más Vendidos",          this::buildMasVendidos));
        raiz.getChildren().add(acordeon("💬 Reseñas por Idioma",           this::buildResenasPorIdioma));
        raiz.getChildren().add(acordeon("🎮 Reseñas por Juego",            this::buildResenasPorJuego));
        raiz.getChildren().add(acordeon("🏢 Estadísticas por Estudio",     this::buildEstadisticasPorEstudio));
        raiz.getChildren().add(acordeon("🛒 Juegos Comprados por Usuario", this::buildJuegosPorUsuario));
        raiz.getChildren().add(acordeon("✍ Reseñas por Usuario",           this::buildResenasPorUsuario));

        setContent(scroll(raiz));
    }

    /** Crea un panel acordeón: cabecera clicable que expande/colapsa el contenido. */
    private VBox acordeon(String etiqueta, Supplier<Node> constructorContenido) {
        VBox panel = new VBox(0);
        panel.getStyleClass().add("card");

        HBox cabecera = new HBox(10);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(new Insets(10, 14, 10, 14));
        cabecera.setStyle("-fx-cursor: hand;");
        Label indicadorFlecha = new Label("▶"); indicadorFlecha.getStyleClass().add("label-gold");
        Label lblEtiqueta = new Label(etiqueta); lblEtiqueta.getStyleClass().add("label-section");
        cabecera.getChildren().addAll(indicadorFlecha, lblEtiqueta);

        VBox contenedor = new VBox();
        contenedor.setPadding(new Insets(0, 14, 10, 14));
        contenedor.setVisible(false);
        contenedor.setManaged(false);

        cabecera.setOnMouseClicked(e -> {
            boolean estaAbierto = contenedor.isVisible();
            if (!estaAbierto) {
                if (contenedor.getChildren().isEmpty())
                    contenedor.getChildren().add(constructorContenido.get());
                indicadorFlecha.setText("▼");
                contenedor.setVisible(true);
                contenedor.setManaged(true);
            } else {
                indicadorFlecha.setText("▶");
                contenedor.setVisible(false);
                contenedor.setManaged(false);
            }
        });

        panel.getChildren().addAll(cabecera, contenedor);
        return panel;
    }

    private Node buildVentasPorJuego() {
        VBox caja = new VBox(8);
        List<Juego> juegos = gd.getJuegos();
        int ventasMaximas = juegos.stream().mapToInt(gd::getVentasJuego).max().orElse(1);
        for (Juego juego : juegos) {
            int ventas = gd.getVentasJuego(juego);
            HBox fila = new HBox(12); fila.setAlignment(Pos.CENTER_LEFT);
            VBox info = new VBox(2); HBox.setHgrow(info, Priority.ALWAYS);
            Label lblT = new Label(juego.getTitulo()); lblT.getStyleClass().add("label-section");
            Label lblV = new Label("Ventas: " + ventas + "  |  Ingresos: "
                    + String.format("%.2f€", ventas * juego.getPrecio()));
            lblV.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblV);
            double porcentaje = ventasMaximas > 0 ? (double) ventas / ventasMaximas : 0;
            Region relleno = new Region();
            relleno.setStyle("-fx-background-color:#e94560; -fx-background-radius:4;");
            relleno.setPrefWidth(180 * porcentaje);
            relleno.setPrefHeight(10);
            HBox barra = new HBox(relleno);
            barra.setMinWidth(180);
            barra.setStyle("-fx-background-color:#16213e; -fx-background-radius:4;");
            barra.setAlignment(Pos.CENTER_LEFT);
            fila.getChildren().addAll(info, barra);
            caja.getChildren().add(fila);
        }
        return caja;
    }

    private Node buildMejorValorados() {
        VBox caja = new VBox(8); int pos = 1;
        for (Juego juego : gd.getJuegosMejorValorados()) {
            double media  = gd.getPuntuacionMediaJuego(juego.getIdJuego());
            int nResenas  = gd.getResenasPorJuego(juego).size();
            HBox fila = new HBox(10); fila.setAlignment(Pos.CENTER_LEFT);
            Label lblPos = new Label("#" + pos++); lblPos.getStyleClass().add("label-gold"); lblPos.setMinWidth(28);
            VBox info = new VBox(2);
            Label lblT = new Label(juego.getTitulo()); lblT.getStyleClass().add("label-section");
            Label lblM = new Label(String.format("⭐ %.1f/10  (%d reseñas)  |  Director: %s",
                    media, nResenas, juego.getDirector()));
            lblM.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblM); fila.getChildren().addAll(lblPos, info);
            caja.getChildren().add(fila);
        }
        if (caja.getChildren().isEmpty()) caja.getChildren().add(new Label("Sin valoraciones todavía."));
        return caja;
    }

    private Node buildMasVendidos() {
        VBox caja = new VBox(8); int pos = 1;
        for (Juego juego : gd.getJuegosMasVendidos()) {
            int ventas = gd.getVentasJuego(juego);
            HBox fila = new HBox(10); fila.setAlignment(Pos.CENTER_LEFT);
            Label lblPos = new Label("#" + pos++); lblPos.getStyleClass().add("label-gold"); lblPos.setMinWidth(28);
            VBox info = new VBox(2);
            Label lblT = new Label(juego.getTitulo()); lblT.getStyleClass().add("label-section");
            Label lblV = new Label("🔥 " + ventas + " uds  |  "
                    + String.format("%.2f€ ingresos", ventas * juego.getPrecio()));
            lblV.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblV); fila.getChildren().addAll(lblPos, info);
            caja.getChildren().add(fila);
        }
        return caja;
    }

    private Node buildResenasPorIdioma() {
        VBox caja = new VBox(6);
        String[] idiomas = {"Español","English","Français","Deutsch","Português","Italiano"};
        boolean hayAlguna = false;
        for (String idioma : idiomas) {
            List<Resena> lista = gd.getResenasPorIdioma(idioma);
            if (lista.isEmpty()) continue;
            hayAlguna = true;
            Label lblI = new Label("🌐 " + idioma + "  (" + lista.size() + " reseñas)");
            lblI.getStyleClass().add("label-section");
            caja.getChildren().add(lblI);
            for (Resena resena : lista) {
                Label l = new Label("  • " + resena.getJuego().getTitulo() + " – "
                        + resena.getAutor().getNombreCompleto() + "  ⭐" + resena.getPuntuacion());
                l.getStyleClass().add("label-normal");
                caja.getChildren().add(l);
            }
        }
        if (!hayAlguna) caja.getChildren().add(new Label("No hay reseñas registradas."));
        return caja;
    }

    private Node buildResenasPorJuego() {
        VBox caja = new VBox(6); boolean hayAlguna = false;
        for (Juego juego : gd.getJuegos()) {
            List<Resena> lista = gd.getResenasPorJuego(juego);
            if (lista.isEmpty()) continue;
            hayAlguna = true;
            double media = gd.getPuntuacionMediaJuego(juego.getIdJuego());
            Label lblJ = new Label("🎮 " + juego.getTitulo()
                    + String.format("  ⭐ %.1f/10  (%d reseñas)", media, lista.size()));
            lblJ.getStyleClass().add("label-section");
            caja.getChildren().add(lblJ);
            for (Resena resena : lista) {
                Label l = new Label("  • " + resena.getAutor().getNombreCompleto()
                        + "  ⭐" + resena.getPuntuacion() + "/10  ["
                        + resena.getIdioma() + "]  – " + resena.getComentario());
                l.getStyleClass().add("label-normal"); l.setWrapText(true);
                caja.getChildren().add(l);
            }
        }
        if (!hayAlguna) caja.getChildren().add(new Label("No hay reseñas registradas."));
        return caja;
    }

    private Node buildEstadisticasPorEstudio() {
        VBox caja = new VBox(8);
        for (Estudio estudio : gd.getEstudios()) {
            VBox bloque = new VBox(4);
            bloque.setStyle("-fx-background-color:#1a2545; -fx-background-radius:6; -fx-padding:10;");
            Label lblE = new Label("🏢 " + estudio.getNombre()); lblE.getStyleClass().add("label-section");
            bloque.getChildren().add(lblE);
            Juego mejorValorado  = gd.getJuegoMejorValoradoEstudio(estudio);
            Juego masVendido     = gd.getJuegoMasVendidoEstudio(estudio);
            if (mejorValorado != null) {
                Label l = new Label("⭐ Mejor valorado: " + mejorValorado.getTitulo()
                        + String.format("  (%.1f/10)", gd.getPuntuacionMediaJuego(mejorValorado.getIdJuego())));
                l.getStyleClass().add("label-normal"); bloque.getChildren().add(l);
            }
            if (masVendido != null) {
                Label l = new Label("🔥 Más vendido: " + masVendido.getTitulo()
                        + "  (" + gd.getVentasJuego(masVendido) + " uds)");
                l.getStyleClass().add("label-normal"); bloque.getChildren().add(l);
            }
            if (!estudio.getDesarrolladores().isEmpty()) {
                Label lblDev = new Label("👨‍💻 Desarrolladores:"); lblDev.getStyleClass().add("label-muted");
                bloque.getChildren().add(lblDev);
                for (Desarrollador dev : estudio.getDesarrolladores()) {
                    Juego jMejorVal  = gd.getJuegoMejorValoradoDesarrollador(dev);
                    Juego jMasVend   = gd.getJuegoMasVendidoDesarrollador(dev);
                    StringBuilder sb = new StringBuilder("  • " + dev.getNombreCompleto()
                            + " (" + dev.getPuestoActual() + ")");
                    if (jMejorVal != null) sb.append("  |  ⭐ ").append(jMejorVal.getTitulo());
                    if (jMasVend  != null) sb.append("  |  🔥 ").append(jMasVend.getTitulo());
                    Label l = new Label(sb.toString()); l.getStyleClass().add("label-muted");
                    bloque.getChildren().add(l);
                }
            }
            caja.getChildren().add(bloque);
        }
        return caja;
    }

    private Node buildJuegosPorUsuario() {
        VBox caja = new VBox(6);
        for (Usuario usuario : gd.getUsuarios()) {
            List<Juego> biblioteca = gd.getBibliotecaUsuario(usuario.getIdUsuario());
            Label lblU = new Label("👤 " + usuario.getNombreCompleto()
                    + "  <" + usuario.getCorreo() + ">  (" + biblioteca.size() + " juegos)");
            lblU.getStyleClass().add("label-section");
            caja.getChildren().add(lblU);
            if (biblioteca.isEmpty()) {
                caja.getChildren().add(new Label("  Sin compras."));
            } else {
                for (Juego juego : biblioteca) {
                    Label l = new Label("  • " + juego.getTitulo() + " (" + juego.getGenero() + ")");
                    l.getStyleClass().add("label-normal");
                    caja.getChildren().add(l);
                }
            }
        }
        return caja;
    }

    private Node buildResenasPorUsuario() {
        VBox caja = new VBox(6); boolean hayAlguna = false;
        for (Usuario usuario : gd.getUsuarios()) {
            List<Resena> lista = gd.getResenasPorUsuario(usuario);
            if (lista.isEmpty()) continue;
            hayAlguna = true;
            Label lblU = new Label("👤 " + usuario.getNombreCompleto()
                    + "  (" + lista.size() + " reseñas)");
            lblU.getStyleClass().add("label-section");
            caja.getChildren().add(lblU);
            for (Resena resena : lista) {
                Label l = new Label("  • " + resena.getJuego().getTitulo()
                        + "  ⭐" + resena.getPuntuacion() + "/10  –  " + resena.getComentario());
                l.getStyleClass().add("label-normal"); l.setWrapText(true);
                caja.getChildren().add(l);
            }
        }
        if (!hayAlguna) caja.getChildren().add(new Label("No hay reseñas."));
        return caja;
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

    private <T> void col(TableView<T> tabla, String nombreColumna, Getter<T> getter, double ancho) {
        TableColumn<T, String> columna = new TableColumn<>(nombreColumna);
        columna.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getter.get(c.getValue())));
        columna.setPrefWidth(ancho);
        tabla.getColumns().add(columna);
    }

    private TextField tf(String valorInicial) {
        TextField campo = new TextField(valorInicial);
        campo.getStyleClass().add("text-field");
        campo.setMaxWidth(Double.MAX_VALUE);
        return campo;
    }

    private Label lbl(String texto) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("label-normal");
        return etiqueta;
    }

    private ScrollPane scroll(Node nodo) {
        ScrollPane sp = new ScrollPane(nodo);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        return sp;
    }

    private HBox parLabel(String etiqueta, String valor) {
        HBox fila = new HBox(10);
        Label lblEtiqueta = new Label(etiqueta);
        lblEtiqueta.getStyleClass().add("label-muted");
        lblEtiqueta.setMinWidth(190);
        Label lblValor = new Label(valor);
        lblValor.getStyleClass().add("label-normal");
        fila.getChildren().addAll(lblEtiqueta, lblValor);
        return fila;
    }
}
