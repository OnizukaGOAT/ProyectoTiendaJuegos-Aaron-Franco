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

public class MainUsuarioController implements Initializable {

    @FXML private StackPane contentPane;
    @FXML private Label     lblBienvenida, lblSaldo;

    private final GestorDatos gd = GestorDatos.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Usuario usuario = gd.getUsuarioActual();
        lblBienvenida.setText("Hola, " + usuario.getNombre() + "!");
        actualizarSaldo();
        mostrarCatalogo();
    }

    private void actualizarSaldo() {
        lblSaldo.setText(String.format("💰 %.2f€", gd.getUsuarioActual().getSaldo()));
    }

    private void setContent(Node nodo) { contentPane.getChildren().setAll(nodo); }

    // ─────────────────────────────────────────────────────────
    // CATÁLOGO / BÚSQUEDA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarCatalogo() {
        setContent(buildCatalogo(gd.getJuegos()));
    }

    @FXML public void mostrarBuscarJuegos() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("🔍 Buscar Juegos");
        titulo.getStyleClass().add("label-title");

        HBox barrasBusqueda = new HBox(10);
        TextField txBusqueda = new TextField();
        txBusqueda.setPromptText("Nombre, género, director o estudio...");
        txBusqueda.setPrefWidth(280);
        txBusqueda.getStyleClass().add("text-field");
        ComboBox<String> cbTipoBusqueda = new ComboBox<>(FXCollections.observableArrayList(
                "Nombre","Género","Director","Estudio"));
        cbTipoBusqueda.getSelectionModel().selectFirst();
        cbTipoBusqueda.getStyleClass().add("combo-box");
        Button btnBuscar = new Button("Buscar");
        btnBuscar.getStyleClass().add("btn-primary");
        barrasBusqueda.getChildren().addAll(txBusqueda, cbTipoBusqueda, btnBuscar);

        VBox contenedorResultados = new VBox(10);
        btnBuscar.setOnAction(e -> {
            String texto = txBusqueda.getText().trim();
            if (texto.isEmpty()) return;
            List<Juego> resultados;
            switch (cbTipoBusqueda.getValue()) {
                case "Género":   resultados = gd.buscarJuegosPorGenero(texto);   break;
                case "Director": resultados = gd.buscarJuegosPorDirector(texto); break;
                case "Estudio":  resultados = gd.buscarJuegosPorEstudio(texto);  break;
                default:         resultados = gd.buscarJuegosPorNombre(texto);
            }
            contenedorResultados.getChildren().setAll(buildCatalogo(resultados));
        });
        txBusqueda.setOnAction(e -> btnBuscar.fire());
        raiz.getChildren().addAll(titulo, barrasBusqueda, contenedorResultados);
        setContent(scroll(raiz));
    }

    // ─────────────────────────────────────────────────────────
    // HISTORIAL DE COMPRAS
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarHistorialCompras() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("🛒 Historial de Compras");
        titulo.getStyleClass().add("label-title");

        List<Compra> compras = gd.getComprasUsuario(gd.getUsuarioActual().getIdUsuario());
        TableView<Compra> tabla = new TableView<>(FXCollections.observableArrayList(compras));
        tabla.getStyleClass().add("table-view");
        tabla.setPrefHeight(380);
        col(tabla, "Juego",    c -> c.getJuego().getTitulo(),            200);
        col(tabla, "Fecha",    c -> c.getFechaFormateada(),               100);
        col(tabla, "Cantidad", c -> String.valueOf(c.getCantidad()),       80);
        col(tabla, "Coste",    c -> String.format("%.2f€", c.getCoste()), 100);

        double totalGastado = compras.stream().mapToDouble(Compra::getCoste).sum();
        Label lblTotal = new Label(String.format("Total gastado: %.2f€", totalGastado));
        lblTotal.getStyleClass().add("label-gold");
        raiz.getChildren().addAll(titulo, tabla, lblTotal);
        setContent(scroll(raiz));
    }

    // ─────────────────────────────────────────────────────────
    // BIBLIOTECA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarBiblioteca() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("📚 Mi Biblioteca");
        titulo.getStyleClass().add("label-title");
        List<Juego> biblioteca = gd.getBibliotecaUsuario(gd.getUsuarioActual().getIdUsuario());
        if (biblioteca.isEmpty()) {
            Label lbl = new Label("No tienes juegos en tu biblioteca todavía.");
            lbl.getStyleClass().add("label-muted");
            raiz.getChildren().addAll(titulo, lbl);
        } else {
            raiz.getChildren().add(titulo);
            for (Juego juego : biblioteca) raiz.getChildren().add(buildTarjetaJuego(juego, false));
        }
        setContent(scroll(raiz));
    }

    // ─────────────────────────────────────────────────────────
    // MIS RESEÑAS
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarMisResenas() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("✍ Mis Reseñas");
        titulo.getStyleClass().add("label-title");
        List<Resena> misResenas = gd.getResenasPorUsuario(gd.getUsuarioActual());
        Button btnNuevaResena = new Button("+ Nueva Reseña");
        btnNuevaResena.getStyleClass().add("btn-primary");
        btnNuevaResena.setOnAction(e -> mostrarFormularioResena(null));
        raiz.getChildren().addAll(titulo, btnNuevaResena);
        if (misResenas.isEmpty()) {
            Label lbl = new Label("Aún no has escrito ninguna reseña.");
            lbl.getStyleClass().add("label-muted");
            raiz.getChildren().add(lbl);
        } else {
            for (Resena resena : misResenas) raiz.getChildren().add(buildTarjetaResena(resena, true));
        }
        setContent(scroll(raiz));
    }

    private void mostrarFormularioResena(Resena resenaEditar) {
        VBox formulario = new VBox(12);
        formulario.setPadding(new Insets(20));
        formulario.setMaxWidth(520);
        formulario.getStyleClass().add("card");
        Label titulo = new Label(resenaEditar == null ? "Nueva Reseña" : "Editar Reseña");
        titulo.getStyleClass().add("label-section");

        List<Juego> bibliotecaUsuario = gd.getBibliotecaUsuario(gd.getUsuarioActual().getIdUsuario());
        ComboBox<Juego> cbJuego = new ComboBox<>(FXCollections.observableArrayList(bibliotecaUsuario));
        cbJuego.setPromptText("Selecciona un juego de tu biblioteca");
        cbJuego.getStyleClass().add("combo-box");
        cbJuego.setMaxWidth(Double.MAX_VALUE);

        Spinner<Integer> spinPuntuacion = new Spinner<>(1, 10, 8);
        spinPuntuacion.getStyleClass().add("spinner");
        ComboBox<String> cbIdioma = new ComboBox<>(FXCollections.observableArrayList(
                "Español","English","Français","Deutsch","Português","Italiano"));
        cbIdioma.getSelectionModel().select(gd.getUsuarioActual().getIdioma());
        cbIdioma.getStyleClass().add("combo-box");
        TextArea txComentario = new TextArea();
        txComentario.setPromptText("Escribe tu reseña aquí...");
        txComentario.setPrefRowCount(4);
        txComentario.getStyleClass().add("text-area");
        txComentario.setWrapText(true);

        if (resenaEditar != null) {
            bibliotecaUsuario.stream()
                    .filter(j -> j.getIdJuego() == resenaEditar.getJuego().getIdJuego())
                    .findFirst().ifPresent(cbJuego.getSelectionModel()::select);
            cbJuego.setDisable(true);
            spinPuntuacion.getValueFactory().setValue(resenaEditar.getPuntuacion());
            cbIdioma.getSelectionModel().select(resenaEditar.getIdioma());
            txComentario.setText(resenaEditar.getComentario());
        }

        Label lblError = new Label();
        lblError.getStyleClass().add("label-accent");
        Button btnGuardar  = new Button("Guardar");  btnGuardar.getStyleClass().add("btn-primary");
        Button btnCancelar = new Button("Cancelar"); btnCancelar.getStyleClass().add("btn-secondary");
        btnCancelar.setOnAction(e -> mostrarMisResenas());

        btnGuardar.setOnAction(e -> {
            if (cbJuego.getValue() == null)             { lblError.setText("Selecciona un juego."); return; }
            if (txComentario.getText().trim().isEmpty()) { lblError.setText("Escribe un comentario."); return; }
            if (resenaEditar != null) {
                resenaEditar.setComentario(txComentario.getText().trim());
                resenaEditar.setPuntuacion(spinPuntuacion.getValue());
                resenaEditar.setIdioma(cbIdioma.getValue());
                gd.actualizarResena(resenaEditar);
                DialogUtil.info("Reseña actualizada.");
            } else {
                String resultado = gd.anadirResena(gd.getUsuarioActual(), cbJuego.getValue(),
                        txComentario.getText().trim(), spinPuntuacion.getValue(), cbIdioma.getValue());
                if (!"OK".equals(resultado)) { lblError.setText(resultado); return; }
                DialogUtil.info("Reseña publicada.");
            }
            mostrarMisResenas();
        });

        formulario.getChildren().addAll(titulo,
                lbl("Juego:"), cbJuego,
                lbl("Puntuación (1-10):"), spinPuntuacion,
                lbl("Idioma:"), cbIdioma,
                lbl("Comentario:"), txComentario,
                lblError, new HBox(10, btnGuardar, btnCancelar));
        setContent(scroll(formulario));
    }

    // ─────────────────────────────────────────────────────────
    // PERFIL (con recarga de saldo)
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarPerfil() {
        Usuario usuario = gd.getUsuarioActual();
        VBox raiz = new VBox(20);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("👤 Mi Perfil");
        titulo.getStyleClass().add("label-title");

        // ── Tarjeta de datos actuales ──────────────────────
        VBox tarjetaDatos = new VBox(10);
        tarjetaDatos.getStyleClass().add("card");
        tarjetaDatos.setMaxWidth(500);
        tarjetaDatos.getChildren().addAll(
                parLabel("ID:",                  String.valueOf(usuario.getIdUsuario())),
                parLabel("Nombre:",              usuario.getNombreCompleto()),
                parLabel("Correo:",              usuario.getCorreo()),
                parLabel("Idioma:",              usuario.getIdioma()),
                parLabel("Saldo actual:",        String.format("%.2f€", usuario.getSaldo())),
                parLabel("Juegos en biblioteca:", String.valueOf(gd.getBibliotecaUsuario(usuario.getIdUsuario()).size())),
                parLabel("Compras realizadas:",  String.valueOf(gd.getComprasUsuario(usuario.getIdUsuario()).size())),
                parLabel("Reseñas escritas:",    String.valueOf(gd.getResenasPorUsuario(usuario).size()))
        );

        // ── Sección: añadir saldo ──────────────────────────
        Label lblTituloSaldo = new Label("💰 Añadir Saldo");
        lblTituloSaldo.getStyleClass().add("label-section");

        VBox tarjetaSaldo = new VBox(12);
        tarjetaSaldo.getStyleClass().add("card");
        tarjetaSaldo.setMaxWidth(500);
        tarjetaSaldo.setPadding(new Insets(16));

        Label lblSaldoActual = new Label(String.format("Saldo actual: %.2f€", usuario.getSaldo()));
        lblSaldoActual.getStyleClass().add("label-gold");

        // Botones de cantidad rápida
        Label lblCantidadRapida = new Label("Cantidad a añadir:");
        lblCantidadRapida.getStyleClass().add("label-normal");

        HBox botonesRapidos = new HBox(10);
        botonesRapidos.setAlignment(Pos.CENTER_LEFT);
        TextField txCantidadPersonalizada = new TextField();
        txCantidadPersonalizada.setPromptText("Cantidad personalizada (€)");
        txCantidadPersonalizada.getStyleClass().add("text-field");
        txCantidadPersonalizada.setPrefWidth(200);

        Label lblError = new Label();
        lblError.getStyleClass().add("label-accent");

        // Acción común de recarga
        java.util.function.Consumer<Double> accionRecargar = cantidad -> {
            if (cantidad <= 0) { lblError.setText("La cantidad debe ser mayor que 0."); return; }
            if (cantidad > 500) { lblError.setText("No puedes añadir más de 500€ de una vez."); return; }
            lblError.setText("");
            usuario.setSaldo(usuario.getSaldo() + cantidad);
            gd.actualizarUsuario(usuario);
            actualizarSaldo();
            DialogUtil.info(String.format("✅ Se han añadido %.2f€ a tu cuenta.\nNuevo saldo: %.2f€",
                    cantidad, usuario.getSaldo()));
            mostrarPerfil(); // refresca para mostrar el nuevo saldo
        };

        for (int importe : new int[]{5, 10, 20, 50}) {
            Button btn = new Button("+" + importe + "€");
            btn.getStyleClass().add("btn-secondary");
            final double cantidad = importe;
            btn.setOnAction(e -> accionRecargar.accept(cantidad));
            botonesRapidos.getChildren().add(btn);
        }

        Button btnPersonalizado = new Button("Añadir cantidad");
        btnPersonalizado.getStyleClass().add("btn-primary");
        btnPersonalizado.setOnAction(e -> {
            try {
                double cantidad = Double.parseDouble(txCantidadPersonalizada.getText().replace(",","."));
                accionRecargar.accept(cantidad);
            } catch (NumberFormatException ex) {
                lblError.setText("Introduce una cantidad válida.");
            }
        });

        tarjetaSaldo.getChildren().addAll(
                lblSaldoActual,
                lblCantidadRapida,
                botonesRapidos,
                lbl("O introduce una cantidad personalizada:"),
                new HBox(10, txCantidadPersonalizada, btnPersonalizado),
                lblError
        );

        // ── Sección: editar datos personales ──────────────
        Label lblTituloEditar = new Label("✏ Editar Datos Personales");
        lblTituloEditar.getStyleClass().add("label-section");

        VBox tarjetaEdicion = new VBox(10);
        tarjetaEdicion.getStyleClass().add("card");
        tarjetaEdicion.setMaxWidth(500);
        tarjetaEdicion.setPadding(new Insets(16));

        GridPane cuadricula = new GridPane();
        cuadricula.setHgap(10);
        cuadricula.setVgap(8);
        TextField txNombre    = tf(usuario.getNombre());
        TextField txApellidos = tf(usuario.getApellidos());
        TextField txCorreo    = tf(usuario.getCorreo());
        PasswordField txContrasena = new PasswordField();
        txContrasena.setPromptText("Nueva contraseña (opcional)");
        txContrasena.getStyleClass().add("password-field");
        ComboBox<String> cbIdioma = new ComboBox<>(FXCollections.observableArrayList(
                "Español","English","Français","Deutsch","Português","Italiano"));
        cbIdioma.getSelectionModel().select(usuario.getIdioma());
        cbIdioma.getStyleClass().add("combo-box");

        cuadricula.addRow(0, lbl("Nombre:"), txNombre);
        cuadricula.addRow(1, lbl("Apellidos:"), txApellidos);
        cuadricula.addRow(2, lbl("Correo:"), txCorreo);
        cuadricula.addRow(3, lbl("Contraseña:"), txContrasena);
        cuadricula.addRow(4, lbl("Idioma:"), cbIdioma);

        Label lblErrorEdicion = new Label();
        lblErrorEdicion.getStyleClass().add("label-accent");
        Button btnGuardarPerfil = new Button("Guardar cambios");
        btnGuardarPerfil.getStyleClass().add("btn-primary");
        btnGuardarPerfil.setOnAction(e -> {
            if (txNombre.getText().trim().isEmpty()) {
                lblErrorEdicion.setText("El nombre no puede estar vacío.");
                return;
            }
            usuario.setNombre(txNombre.getText().trim());
            usuario.setApellidos(txApellidos.getText().trim());
            usuario.setCorreo(txCorreo.getText().trim());
            usuario.setIdioma(cbIdioma.getValue());
            if (!txContrasena.getText().isEmpty()) usuario.setContrasena(txContrasena.getText());
            gd.actualizarUsuario(usuario);
            lblBienvenida.setText("Hola, " + usuario.getNombre() + "!");
            DialogUtil.info("Perfil actualizado correctamente.");
            mostrarPerfil();
        });

        tarjetaEdicion.getChildren().addAll(cuadricula, lblErrorEdicion, btnGuardarPerfil);

        raiz.getChildren().addAll(titulo, tarjetaDatos, lblTituloSaldo, tarjetaSaldo,
                lblTituloEditar, tarjetaEdicion);
        setContent(scroll(raiz));
    }

    // ─────────────────────────────────────────────────────────
    // ESTADÍSTICAS – acordeón desplegable
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarEstadisticas() {
        VBox raiz = new VBox(12);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("📊 Estadísticas");
        titulo.getStyleClass().add("label-title");
        raiz.getChildren().add(titulo);

        raiz.getChildren().add(acordeon("⭐ Juegos Mejor Valorados",  this::buildMejorValorados));
        raiz.getChildren().add(acordeon("🔥 Juegos Más Vendidos",     this::buildMasVendidos));
        raiz.getChildren().add(acordeon("💬 Reseñas por Idioma",      this::buildResenasPorIdioma));
        raiz.getChildren().add(acordeon("🎮 Reseñas por Juego",       this::buildResenasPorJuego));
        raiz.getChildren().add(acordeon("🏢 Estadísticas por Estudio",this::buildEstadisticasEstudio));
        raiz.getChildren().add(acordeon("🛒 Juegos por Usuario",      this::buildJuegosPorUsuario));
        raiz.getChildren().add(acordeon("✍ Reseñas por Usuario",      this::buildResenasPorUsuario));

        setContent(scroll(raiz));
    }

    // Compatibilidad con los botones del FXML antiguo (redirigen a estadísticas)
    @FXML public void mostrarMejorValorados() { mostrarEstadisticas(); }
    @FXML public void mostrarMasVendidos()    { mostrarEstadisticas(); }

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

    private Node buildMejorValorados() {
        VBox caja = new VBox(8);
        List<Juego> lista = gd.getJuegosMejorValorados();
        if (lista.isEmpty()) { caja.getChildren().add(new Label("Sin valoraciones todavía.")); return caja; }
        int pos = 1;
        for (Juego juego : lista) {
            double media = gd.getPuntuacionMediaJuego(juego.getIdJuego());
            int nResenas = gd.getResenasPorJuego(juego).size();
            HBox fila = new HBox(10); fila.setAlignment(Pos.CENTER_LEFT);
            Label lblPos = new Label("#" + pos++); lblPos.getStyleClass().add("label-gold"); lblPos.setMinWidth(28);
            VBox info = new VBox(2);
            Label lblT = new Label(juego.getTitulo()); lblT.getStyleClass().add("label-section");
            Label lblM = new Label(String.format("⭐ %.1f/10  (%d reseñas)", media, nResenas));
            lblM.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblM); fila.getChildren().addAll(lblPos, info);
            caja.getChildren().add(fila);
        }
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
            Label lblV = new Label("🔥 " + ventas + " unidades vendidas"); lblV.getStyleClass().add("label-normal");
            info.getChildren().addAll(lblT, lblV); fila.getChildren().addAll(lblPos, info);
            caja.getChildren().add(fila);
        }
        return caja;
    }

    private Node buildResenasPorIdioma() {
        VBox caja = new VBox(8);
        String[] idiomas = {"Español","English","Français","Deutsch","Português","Italiano"};
        boolean hayAlguna = false;
        for (String idioma : idiomas) {
            List<Resena> lista = gd.getResenasPorIdioma(idioma);
            if (lista.isEmpty()) continue;
            hayAlguna = true;
            VBox subPanel = subAcordeon("🌐 " + idioma + "  (" + lista.size() + " reseñas)", () -> {
                VBox contenido = new VBox(4);
                for (Resena resena : lista) {
                    Label l = new Label("• " + resena.getJuego().getTitulo() + " – "
                            + resena.getAutor().getNombreCompleto() + "  ⭐" + resena.getPuntuacion());
                    l.getStyleClass().add("label-normal");
                    contenido.getChildren().add(l);
                }
                return contenido;
            });
            caja.getChildren().add(subPanel);
        }
        if (!hayAlguna) caja.getChildren().add(new Label("No hay reseñas."));
        return caja;
    }

    private Node buildResenasPorJuego() {
        VBox caja = new VBox(8); boolean hayAlguna = false;
        for (Juego juego : gd.getJuegos()) {
            List<Resena> lista = gd.getResenasPorJuego(juego);
            if (lista.isEmpty()) continue;
            hayAlguna = true;
            double media = gd.getPuntuacionMediaJuego(juego.getIdJuego());
            VBox subPanel = subAcordeon("🎮 " + juego.getTitulo()
                    + String.format("  ⭐ %.1f/10  (%d reseñas)", media, lista.size()), () -> {
                VBox contenido = new VBox(4);
                for (Resena resena : lista) {
                    Label l = new Label("  • " + resena.getAutor().getNombreCompleto()
                            + "  ⭐" + resena.getPuntuacion() + "/10  ["
                            + resena.getIdioma() + "]  – " + resena.getComentario());
                    l.getStyleClass().add("label-normal"); l.setWrapText(true);
                    contenido.getChildren().add(l);
                }
                return contenido;
            });
            caja.getChildren().add(subPanel);
        }
        if (!hayAlguna) caja.getChildren().add(new Label("No hay reseñas."));
        return caja;
    }

    private Node buildEstadisticasEstudio() {
        VBox caja = new VBox(8);
        for (Estudio estudio : gd.getEstudios()) {
            VBox subPanel = subAcordeon("🏢 " + estudio.getNombre(), () -> {
                VBox contenido = new VBox(4);
                Juego mejorValorado = gd.getJuegoMejorValoradoEstudio(estudio);
                Juego masVendido    = gd.getJuegoMasVendidoEstudio(estudio);
                if (mejorValorado != null) {
                    Label l = new Label("⭐ Mejor valorado: " + mejorValorado.getTitulo()
                            + String.format("  (%.1f/10)", gd.getPuntuacionMediaJuego(mejorValorado.getIdJuego())));
                    l.getStyleClass().add("label-normal"); contenido.getChildren().add(l);
                }
                if (masVendido != null) {
                    Label l = new Label("🔥 Más vendido: " + masVendido.getTitulo()
                            + "  (" + gd.getVentasJuego(masVendido) + " uds)");
                    l.getStyleClass().add("label-normal"); contenido.getChildren().add(l);
                }
                return contenido;
            });
            caja.getChildren().add(subPanel);
        }
        return caja;
    }

    private Node buildJuegosPorUsuario() {
        VBox caja = new VBox(8);
        for (Usuario usuario : gd.getUsuarios()) {
            List<Juego> biblioteca = gd.getBibliotecaUsuario(usuario.getIdUsuario());
            VBox subPanel = subAcordeon("👤 " + usuario.getNombreCompleto()
                    + "  (" + biblioteca.size() + " juegos)", () -> {
                VBox contenido = new VBox(4);
                if (biblioteca.isEmpty()) contenido.getChildren().add(new Label("Sin compras."));
                else for (Juego juego : biblioteca) {
                    Label l = new Label("  • " + juego.getTitulo() + " (" + juego.getGenero() + ")");
                    l.getStyleClass().add("label-normal"); contenido.getChildren().add(l);
                }
                return contenido;
            });
            caja.getChildren().add(subPanel);
        }
        return caja;
    }

    private Node buildResenasPorUsuario() {
        VBox caja = new VBox(8); boolean hayAlguna = false;
        for (Usuario usuario : gd.getUsuarios()) {
            List<Resena> lista = gd.getResenasPorUsuario(usuario);
            if (lista.isEmpty()) continue;
            hayAlguna = true;
            VBox subPanel = subAcordeon("👤 " + usuario.getNombreCompleto()
                    + "  (" + lista.size() + " reseñas)", () -> {
                VBox contenido = new VBox(4);
                for (Resena resena : lista) {
                    Label l = new Label("  • " + resena.getJuego().getTitulo()
                            + "  ⭐" + resena.getPuntuacion() + "/10  –  " + resena.getComentario());
                    l.getStyleClass().add("label-normal"); l.setWrapText(true); contenido.getChildren().add(l);
                }
                return contenido;
            });
            caja.getChildren().add(subPanel);
        }
        if (!hayAlguna) caja.getChildren().add(new Label("No hay reseñas."));
        return caja;
    }

    /** Sub-acordeón de estilo más compacto para anidar dentro de secciones. */
    private VBox subAcordeon(String etiqueta, Supplier<Node> constructorContenido) {
        VBox panel = new VBox(0);
        panel.setStyle("-fx-background-color:#1a2545; -fx-background-radius:6;");
        HBox cabecera = new HBox(8);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(new Insets(6, 10, 6, 10));
        cabecera.setStyle("-fx-cursor:hand;");
        Label indicadorFlecha = new Label("▶"); indicadorFlecha.getStyleClass().add("label-gold");
        Label lblEtiqueta = new Label(etiqueta); lblEtiqueta.getStyleClass().add("label-normal");
        cabecera.getChildren().addAll(indicadorFlecha, lblEtiqueta);
        VBox contenedor = new VBox(4);
        contenedor.setPadding(new Insets(4, 10, 8, 22));
        contenedor.setVisible(false);
        contenedor.setManaged(false);
        cabecera.setOnMouseClicked(e -> {
            boolean estaAbierto = contenedor.isVisible();
            if (!estaAbierto && contenedor.getChildren().isEmpty())
                contenedor.getChildren().add(constructorContenido.get());
            indicadorFlecha.setText(estaAbierto ? "▶" : "▼");
            contenedor.setVisible(!estaAbierto);
            contenedor.setManaged(!estaAbierto);
        });
        panel.getChildren().addAll(cabecera, contenedor);
        return panel;
    }

    // ─────────────────────────────────────────────────────────
    // AYUDA
    // ─────────────────────────────────────────────────────────
    @FXML public void mostrarAyuda() {
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("❓ Sistema de Ayuda");
        titulo.getStyleClass().add("label-title");
        raiz.getChildren().add(titulo);
        String[][] secciones = {
            {"🔍 Buscar Juegos",       "Busca por nombre, género, director o estudio."},
            {"🎮 Catálogo",            "Muestra todos los juegos disponibles. Compra con tu saldo."},
            {"🛒 Historial de Compras","Consulta todas tus compras con fecha y coste total."},
            {"📚 Biblioteca",          "Lista los juegos que posees. Solo puedes reseñar juegos de tu biblioteca."},
            {"✍ Mis Reseñas",         "Escribe, edita o elimina tus reseñas. Puntúa del 1 al 10."},
            {"👤 Mi Perfil",           "Consulta y modifica tus datos. Desde aquí también puedes añadir saldo a tu cuenta."},
            {"📊 Estadísticas",        "Accede a rankings y estadísticas desplegables: mejor valorados, más vendidos, reseñas por idioma, por juego, por estudio y por usuario."}
        };
        for (String[] seccion : secciones) {
            VBox tarjeta = new VBox(5);
            tarjeta.getStyleClass().add("card");
            tarjeta.setPadding(new Insets(12));
            Label lblSeccion = new Label(seccion[0]); lblSeccion.getStyleClass().add("label-section");
            Label lblDesc    = new Label(seccion[1]); lblDesc.getStyleClass().add("label-normal"); lblDesc.setWrapText(true);
            tarjeta.getChildren().addAll(lblSeccion, lblDesc);
            raiz.getChildren().add(tarjeta);
        }
        setContent(scroll(raiz));
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
    private Node buildCatalogo(List<Juego> listaJuegos) {
        VBox raiz = new VBox(12);
        raiz.setPadding(new Insets(10));
        if (listaJuegos.isEmpty()) {
            Label lbl = new Label("No se encontraron juegos.");
            lbl.getStyleClass().add("label-muted");
            raiz.getChildren().add(lbl);
        } else {
            for (Juego juego : listaJuegos) raiz.getChildren().add(buildTarjetaJuego(juego, true));
        }
        return scroll(raiz);
    }

    private HBox buildTarjetaJuego(Juego juego, boolean mostrarOpcionCompra) {
        HBox tarjeta = new HBox(15);
        tarjeta.getStyleClass().add("card");
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setPadding(new Insets(12));

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label lblTitulo = new Label(juego.getTitulo()); lblTitulo.getStyleClass().add("label-section");
        Label lblMeta   = new Label("🎮 " + juego.getPlataforma()
                + "  |  📂 " + juego.getGenero()
                + "  |  🎬 " + juego.getDirector());
        lblMeta.getStyleClass().add("label-muted");
        double media = gd.getPuntuacionMediaJuego(juego.getIdJuego());
        Label lblMedia = new Label(media > 0 ? String.format("⭐ %.1f/10", media) : "Sin valoraciones");
        lblMedia.getStyleClass().add("label-normal");
        String textoStock = juego.getStock() > 10 ? "✅ " + juego.getStock() + " en stock"
                          : juego.getStock() > 0  ? "⚠ Solo " + juego.getStock() + " en stock"
                          : "❌ Sin stock";
        Label lblStock = new Label(textoStock);
        lblStock.getStyleClass().add(juego.getStock() > 10 ? "stock-ok"
                : juego.getStock() > 0 ? "stock-low" : "stock-out");
        info.getChildren().addAll(lblTitulo, lblMeta, lblMedia, lblStock);

        VBox panelAcciones = new VBox(6);
        panelAcciones.setAlignment(Pos.CENTER);
        Label lblPrecio = new Label(String.format("%.2f€", juego.getPrecio()));
        lblPrecio.getStyleClass().add("price-badge");
        Button btnVerResenas = new Button("Ver reseñas");
        btnVerResenas.getStyleClass().add("btn-secondary");
        btnVerResenas.setOnAction(e -> mostrarResenasDe(juego));

        if (mostrarOpcionCompra) {
            boolean loTieneElUsuario = gd.usuarioPoseeJuego(
                    gd.getUsuarioActual().getIdUsuario(), juego.getIdJuego());
            if (loTieneElUsuario) {
                Label lblEnBiblioteca = new Label("✅ En biblioteca");
                lblEnBiblioteca.getStyleClass().add("stock-ok");
                panelAcciones.getChildren().addAll(lblPrecio, lblEnBiblioteca, btnVerResenas);
            } else {
                Button btnComprar = new Button("🛒 Comprar");
                btnComprar.getStyleClass().add("btn-primary");
                btnComprar.setDisable(juego.getStock() <= 0);
                btnComprar.setOnAction(e -> ejecutarCompra(juego));
                panelAcciones.getChildren().addAll(lblPrecio, btnComprar, btnVerResenas);
            }
        } else {
            Button btnResenar = new Button("✍ Reseñar");
            btnResenar.getStyleClass().add("btn-gold");
            btnResenar.setOnAction(e -> mostrarFormularioResena(null));
            panelAcciones.getChildren().addAll(lblPrecio, btnVerResenas, btnResenar);
        }
        tarjeta.getChildren().addAll(info, panelAcciones);
        return tarjeta;
    }

    private void ejecutarCompra(Juego juego) {
        String resultado = gd.comprarJuego(gd.getUsuarioActual(), juego, 1);
        if ("OK".equals(resultado)) {
            actualizarSaldo();
            DialogUtil.info("¡Compra realizada! " + juego.getTitulo() + " añadido a tu biblioteca.");
            mostrarCatalogo();
        } else {
            DialogUtil.error(resultado);
        }
    }

    private void mostrarResenasDe(Juego juego) {
        List<Resena> resenas = gd.getResenasPorJuego(juego);
        VBox raiz = new VBox(15);
        raiz.setPadding(new Insets(10));
        Label titulo = new Label("Reseñas de: " + juego.getTitulo());
        titulo.getStyleClass().add("label-title");
        Button btnVolver = new Button("← Volver al catálogo");
        btnVolver.getStyleClass().add("btn-secondary");
        btnVolver.setOnAction(e -> mostrarCatalogo());
        raiz.getChildren().addAll(titulo, btnVolver);
        if (resenas.isEmpty()) {
            Label lbl = new Label("Este juego no tiene reseñas todavía.");
            lbl.getStyleClass().add("label-muted");
            raiz.getChildren().add(lbl);
        } else {
            for (Resena resena : resenas) raiz.getChildren().add(buildTarjetaResena(resena, false));
        }
        setContent(scroll(raiz));
    }

    private VBox buildTarjetaResena(Resena resena, boolean conBotonesEdicion) {
        VBox tarjeta = new VBox(6);
        tarjeta.getStyleClass().add("card");
        tarjeta.setPadding(new Insets(12));
        HBox cabecera = new HBox(10);
        Label lblAutor = new Label("👤 " + resena.getAutor().getNombreCompleto());
        lblAutor.getStyleClass().add("label-section");
        Region separador = new Region();
        HBox.setHgrow(separador, Priority.ALWAYS);
        Label lblPuntuacion = new Label("⭐ " + resena.getPuntuacion() + "/10");
        lblPuntuacion.getStyleClass().add("label-gold");
        Label lblFecha = new Label(resena.getFechaFormateada());
        lblFecha.getStyleClass().add("label-muted");
        cabecera.getChildren().addAll(lblAutor, separador, lblPuntuacion, lblFecha);
        Label lblJuego = new Label("🎮 " + resena.getJuego().getTitulo());
        lblJuego.getStyleClass().add("label-muted");
        Label lblComentario = new Label(resena.getComentario());
        lblComentario.getStyleClass().add("label-normal");
        lblComentario.setWrapText(true);
        tarjeta.getChildren().addAll(cabecera, lblJuego, lblComentario);

        if (conBotonesEdicion && resena.getAutor().getCorreo().equals(gd.getUsuarioActual().getCorreo())) {
            Button btnEditar   = new Button("✏ Editar");   btnEditar.getStyleClass().add("btn-secondary");
            Button btnEliminar = new Button("🗑 Eliminar"); btnEliminar.getStyleClass().add("btn-danger");
            btnEditar.setOnAction(e -> mostrarFormularioResena(resena));
            btnEliminar.setOnAction(e -> {
                if (DialogUtil.confirmar("¿Eliminar esta reseña?")) {
                    gd.eliminarResena(resena.getIdResena());
                    mostrarMisResenas();
                }
            });
            tarjeta.getChildren().add(new HBox(8, btnEditar, btnEliminar));
        }
        return tarjeta;
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

    private TextField tf(String valorInicial) {
        TextField campo = new TextField(valorInicial);
        campo.getStyleClass().add("text-field");
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

    private <T> void col(TableView<T> tabla, String nombreColumna,
                         java.util.function.Function<T, String> getter, double ancho) {
        TableColumn<T, String> columna = new TableColumn<>(nombreColumna);
        columna.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(getter.apply(c.getValue())));
        columna.setPrefWidth(ancho);
        tabla.getColumns().add(columna);
    }
}
