package com.mycompany.proyectotiendajuegos.aaron.franco.clases;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton que gestiona todas las colecciones de datos y la lógica de negocio
 * de la aplicación GamePyramid.
 */
public class GestorDatos {

    // ── Instancia única (Singleton) ────────────────────────
    private static GestorDatos instancia;

    public static GestorDatos getInstance() {
        if (instancia == null) instancia = new GestorDatos();
        return instancia;
    }

    // ── Colecciones principales ────────────────────────────
    private final ArrayList<Usuario>       usuarios         = new ArrayList<>();
    private final ArrayList<Administrador> administradores  = new ArrayList<>();
    private final ArrayList<Juego>         juegos           = new ArrayList<>();
    private final ArrayList<Resena>        resenas          = new ArrayList<>();
    private final ArrayList<Compra>        historialComprasGlobal = new ArrayList<>();
    private final ArrayList<Estudio>       estudios         = new ArrayList<>();
    private final ArrayList<Desarrollador> desarrolladores  = new ArrayList<>();

    // ── Sesión activa ──────────────────────────────────────
    private Usuario        usuarioActual;
    private Administrador  adminActual;

    // ══════════════════════════════════════════════════════
    // CONSTRUCTOR – carga datos de demo
    // ══════════════════════════════════════════════════════
    private GestorDatos() {
        cargarDatosDemo();
    }

    private void cargarDatosDemo() {
        // ── Administradores ────────────────────────────────
        Administrador a1 = new Administrador("Admin", "Principal", "admin@gamepyramid.com", "admin123");
        administradores.add(a1);

        // ── Estudios ───────────────────────────────────────
        Estudio e1 = new Estudio("Rockstar Games");
        Estudio e2 = new Estudio("CD Projekt Red");
        Estudio e3 = new Estudio("Nintendo EPD");
        estudios.add(e1);
        estudios.add(e2);
        estudios.add(e3);

        // ── Desarrolladores notables ───────────────────────
        Desarrollador d1 = new Desarrollador("Dan", "Houser", 20, "Director Creativo");
        Desarrollador d2 = new Desarrollador("Sam", "Houser", 20, "Productor");
        Desarrollador d3 = new Desarrollador("Adam", "Badowski", 18, "Director de Juego");
        Desarrollador d4 = new Desarrollador("Shigeru", "Miyamoto", 40, "Productor");
        desarrolladores.add(d1); desarrolladores.add(d2);
        desarrolladores.add(d3); desarrolladores.add(d4);

        e1.addDesarrollador(d1); e1.addDesarrollador(d2);
        e2.addDesarrollador(d3);
        e3.addDesarrollador(d4);

        // ── Juegos ─────────────────────────────────────────
        Juego j1 = new Juego("Grand Theft Auto V",    "Acción",     "PC / PS5 / Xbox", 29.99, 50, "Dan Houser");
        Juego j2 = new Juego("Red Dead Redemption 2", "Aventura",   "PC / PS4 / Xbox", 39.99, 30, "Dan Houser");
        Juego j3 = new Juego("The Witcher 3",         "RPG",        "PC / PS5 / Xbox", 19.99, 45, "Adam Badowski");
        Juego j4 = new Juego("Cyberpunk 2077",        "RPG",        "PC / PS5 / Xbox", 34.99, 40, "Adam Badowski");
        Juego j5 = new Juego("The Legend of Zelda: BotW", "Aventura", "Switch",         59.99, 20, "Shigeru Miyamoto");
        Juego j6 = new Juego("Mario Kart 8 Deluxe",  "Carreras",   "Switch",          49.99, 35, "Shigeru Miyamoto");
        juegos.add(j1); juegos.add(j2); juegos.add(j3);
        juegos.add(j4); juegos.add(j5); juegos.add(j6);

        e1.addJuego(j1); e1.addJuego(j2);
        e2.addJuego(j3); e2.addJuego(j4);
        e3.addJuego(j5); e3.addJuego(j6);

        d1.addJuego(j1); d1.addJuego(j2);
        d2.addJuego(j1); d2.addJuego(j2);
        d3.addJuego(j3); d3.addJuego(j4);
        d4.addJuego(j5); d4.addJuego(j6);

        // ── Usuarios demo ──────────────────────────────────
        Usuario u1 = new Usuario("Carlos",  "García",   "carlos@email.com",  "pass123", 200.0, "Español");
        Usuario u2 = new Usuario("Ana",     "Martínez", "ana@email.com",     "pass123", 150.0, "Español");
        Usuario u3 = new Usuario("John",    "Smith",    "john@email.com",    "pass123", 300.0, "English");
        usuarios.add(u1); usuarios.add(u2); usuarios.add(u3);

        // ── Compras demo ───────────────────────────────────
        realizarCompraDemo(u1, j1, 1);
        realizarCompraDemo(u1, j3, 1);
        realizarCompraDemo(u2, j5, 1);
        realizarCompraDemo(u3, j1, 1);
        realizarCompraDemo(u3, j4, 1);

        // ── Reseñas demo ───────────────────────────────────
        addResenaDemo(u1, j1, "Increíble juego, horas y horas de entretenimiento.", 9, "Español");
        addResenaDemo(u1, j3, "El mejor RPG que he jugado nunca.", 10, "Español");
        addResenaDemo(u2, j5, "Una obra maestra de Nintendo.", 10, "Español");
        addResenaDemo(u3, j1, "Fantastic open world experience!", 8, "English");
        addResenaDemo(u3, j4, "Buggy at launch but now great.", 7, "English");
    }

    private void realizarCompraDemo(Usuario u, Juego j, int cantidad) {
        Compra c = new Compra(u, j, cantidad);
        u.addCompra(c);
        j.setStock(j.getStock() - cantidad);
        historialComprasGlobal.add(c);
    }

    private void addResenaDemo(Usuario autor, Juego juego, String comentario, int puntuacion, String idioma) {
        Resena r = new Resena(autor, juego, comentario, puntuacion, idioma);
        resenas.add(r);
        juego.addResena(r);
    }

    // ══════════════════════════════════════════════════════
    // SESIÓN
    // ══════════════════════════════════════════════════════
    public Usuario loginUsuario(String correo, String pass) {
        for (Usuario u : usuarios) {
            if (u.getCorreo().equalsIgnoreCase(correo) && u.verificarContrasena(pass)) {
                usuarioActual = u;
                return u;
            }
        }
        return null;
    }

    public Administrador loginAdmin(String correo, String pass) {
        for (Administrador a : administradores) {
            if (a.getCorreo().equalsIgnoreCase(correo) && a.verificarContrasena(pass)) {
                adminActual = a;
                return a;
            }
        }
        return null;
    }

    public void cerrarSesion() {
        usuarioActual = null;
        adminActual   = null;
    }

    public Usuario       getUsuarioActual() { return usuarioActual; }
    public Administrador getAdminActual()   { return adminActual; }

    // ══════════════════════════════════════════════════════
    // ALTA / BAJA / MODIFICACIÓN – USUARIOS
    // ══════════════════════════════════════════════════════
    public boolean altaUsuario(String nombre, String apellidos, String correo,
                               String pass, double saldo, String idioma) {
        if (buscarUsuarioPorCorreo(correo) != null) return false;
        usuarios.add(new Usuario(nombre, apellidos, correo, pass, saldo, idioma));
        return true;
    }

    public boolean bajaUsuario(int id) {
        return usuarios.removeIf(u -> u.getIdUsuario() == id);
    }

    public Usuario buscarUsuarioPorCorreo(String correo) {
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst().orElse(null);
    }

    public Usuario buscarUsuarioPorId(int id) {
        return usuarios.stream().filter(u -> u.getIdUsuario() == id).findFirst().orElse(null);
    }

    public ArrayList<Usuario> getUsuarios() { return usuarios; }

    // ══════════════════════════════════════════════════════
    // ALTA / BAJA – ADMINISTRADORES
    // ══════════════════════════════════════════════════════
    public boolean altaAdmin(String nombre, String apellidos, String correo, String pass) {
        boolean existe = administradores.stream()
                .anyMatch(a -> a.getCorreo().equalsIgnoreCase(correo));
        if (existe) return false;
        administradores.add(new Administrador(nombre, apellidos, correo, pass));
        return true;
    }

    public boolean bajaAdmin(int id) {
        return administradores.removeIf(a -> a.getIdAdmin() == id);
    }

    public ArrayList<Administrador> getAdministradores() { return administradores; }

    // ══════════════════════════════════════════════════════
    // ALTA / BAJA / MODIFICACIÓN – JUEGOS
    // ══════════════════════════════════════════════════════
    public Juego altaJuego(String titulo, String genero, String plataforma,
                           double precio, int stock, String director) {
        Juego j = new Juego(titulo, genero, plataforma, precio, stock, director);
        juegos.add(j);
        return j;
    }

    public boolean bajaJuego(int id) {
        Juego j = buscarJuegoPorId(id);
        if (j == null) return false;
        // Eliminar también de estudios
        estudios.forEach(e -> e.removeJuego(id));
        // Eliminar sus reseñas
        resenas.removeIf(r -> r.getJuego().getIdJuego() == id);
        return juegos.removeIf(jj -> jj.getIdJuego() == id);
    }

    public Juego buscarJuegoPorId(int id) {
        return juegos.stream().filter(j -> j.getIdJuego() == id).findFirst().orElse(null);
    }

    public List<Juego> buscarJuegosPorNombre(String texto) {
        String t = texto.toLowerCase();
        return juegos.stream()
                .filter(j -> j.getTitulo().toLowerCase().contains(t))
                .collect(Collectors.toList());
    }

    public List<Juego> buscarJuegosPorGenero(String genero) {
        String t = genero.toLowerCase();
        return juegos.stream()
                .filter(j -> j.getGenero().toLowerCase().contains(t))
                .collect(Collectors.toList());
    }

    public List<Juego> buscarJuegosPorDirector(String director) {
        String t = director.toLowerCase();
        return juegos.stream()
                .filter(j -> j.getDirector().toLowerCase().contains(t))
                .collect(Collectors.toList());
    }

    public List<Juego> buscarJuegosPorEstudio(String estudio) {
        String t = estudio.toLowerCase();
        return estudios.stream()
                .filter(e -> e.getNombre().toLowerCase().contains(t))
                .flatMap(e -> e.getJuegos().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public ArrayList<Juego> getJuegos() { return juegos; }

    // ══════════════════════════════════════════════════════
    // COMPRA DE JUEGOS
    // ══════════════════════════════════════════════════════
    public String comprarJuego(Usuario u, Juego j, int cantidad) {
        if (j.getStock() < cantidad)             return "Stock insuficiente.";
        if (u.getSaldo() < j.getPrecio() * cantidad) return "Saldo insuficiente.";
        if (u.poseeJuego(j))                      return "Ya tienes este juego en tu biblioteca.";

        u.setSaldo(u.getSaldo() - j.getPrecio() * cantidad);
        j.setStock(j.getStock() - cantidad);

        Compra c = new Compra(u, j, cantidad);
        u.addCompra(c);
        historialComprasGlobal.add(c);
        return "OK";
    }

    public ArrayList<Compra> getHistorialComprasGlobal() { return historialComprasGlobal; }

    // ══════════════════════════════════════════════════════
    // RESEÑAS
    // ══════════════════════════════════════════════════════
    public String anadirResena(Usuario autor, Juego juego, String comentario,
                               int puntuacion, String idioma) {
        if (!autor.poseeJuego(juego))
            return "Solo puedes reseñar juegos que posees.";
        boolean yaReseñado = resenas.stream()
                .anyMatch(r -> r.getAutor() == autor && r.getJuego() == juego);
        if (yaReseñado)
            return "Ya has escrito una reseña para este juego.";

        Resena r = new Resena(autor, juego, comentario, puntuacion, idioma);
        resenas.add(r);
        juego.addResena(r);
        return "OK";
    }

    public boolean eliminarResena(int id) {
        Resena r = resenas.stream().filter(rr -> rr.getIdResena() == id).findFirst().orElse(null);
        if (r == null) return false;
        r.getJuego().removeResena(id);
        return resenas.removeIf(rr -> rr.getIdResena() == id);
    }

    public List<Resena> getResenasPorUsuario(Usuario u) {
        return resenas.stream().filter(r -> r.getAutor() == u).collect(Collectors.toList());
    }

    public List<Resena> getResenasPorJuego(Juego j) {
        return resenas.stream().filter(r -> r.getJuego() == j).collect(Collectors.toList());
    }

    public List<Resena> getResenasPorIdioma(String idioma) {
        return resenas.stream()
                .filter(r -> idioma.equalsIgnoreCase(r.getIdioma()))
                .collect(Collectors.toList());
    }

    public ArrayList<Resena> getResenas() { return resenas; }

    // ══════════════════════════════════════════════════════
    // ESTADÍSTICAS
    // ══════════════════════════════════════════════════════
    public List<Juego> getJuegosMejorValorados() {
        return juegos.stream()
                .filter(j -> !j.getResenas().isEmpty())
                .sorted(Comparator.comparingDouble(Juego::getPuntuacionMedia).reversed())
                .collect(Collectors.toList());
    }

    public List<Juego> getJuegosMasVendidos() {
        return juegos.stream()
                .sorted(Comparator.comparingInt(this::getVentasJuego).reversed())
                .collect(Collectors.toList());
    }

    public int getVentasJuego(Juego j) {
        return historialComprasGlobal.stream()
                .filter(c -> c.getJuego() == j)
                .mapToInt(Compra::getCantidad)
                .sum();
    }

    // ── Estadísticas por estudio ───────────────────────────
    public Juego getJuegoMejorValoradoEstudio(Estudio est) {
        return est.getJuegos().stream()
                .filter(j -> !j.getResenas().isEmpty())
                .max(Comparator.comparingDouble(Juego::getPuntuacionMedia))
                .orElse(null);
    }

    public Juego getJuegoMasVendidoEstudio(Estudio est) {
        return est.getJuegos().stream()
                .max(Comparator.comparingInt(this::getVentasJuego))
                .orElse(null);
    }

    // ── Estadísticas por desarrollador ────────────────────
    public Juego getJuegoMejorValoradoDesarrollador(Desarrollador d) {
        return d.getJuegosEnLosQueHaTrabajado().stream()
                .filter(j -> !j.getResenas().isEmpty())
                .max(Comparator.comparingDouble(Juego::getPuntuacionMedia))
                .orElse(null);
    }

    public Juego getJuegoMasVendidoDesarrollador(Desarrollador d) {
        return d.getJuegosEnLosQueHaTrabajado().stream()
                .max(Comparator.comparingInt(this::getVentasJuego))
                .orElse(null);
    }

    // ══════════════════════════════════════════════════════
    // ESTUDIOS Y DESARROLLADORES
    // ══════════════════════════════════════════════════════
    public Estudio altaEstudio(String nombre) {
        Estudio e = new Estudio(nombre);
        estudios.add(e);
        return e;
    }

    public boolean bajaEstudio(int id) {
        return estudios.removeIf(e -> e.getIdEstudio() == id);
    }

    public Estudio buscarEstudioPorId(int id) {
        return estudios.stream().filter(e -> e.getIdEstudio() == id).findFirst().orElse(null);
    }

    public ArrayList<Estudio> getEstudios() { return estudios; }

    public Desarrollador altaDesarrollador(String nombre, String apellidos,
                                           int anos, String puesto, Estudio estudio) {
        Desarrollador d = new Desarrollador(nombre, apellidos, anos, puesto);
        desarrolladores.add(d);
        if (estudio != null) estudio.addDesarrollador(d);
        return d;
    }

    public boolean bajaDesarrollador(int id) {
        Desarrollador d = buscarDesarrolladorPorId(id);
        if (d == null) return false;
        estudios.forEach(e -> e.removeDesarrollador(id));
        return desarrolladores.removeIf(dd -> dd.getIdDesarrollador() == id);
    }

    public Desarrollador buscarDesarrolladorPorId(int id) {
        return desarrolladores.stream()
                .filter(d -> d.getIdDesarrollador() == id)
                .findFirst().orElse(null);
    }

    public ArrayList<Desarrollador> getDesarrolladores() { return desarrolladores; }

    /** Devuelve los desarrolladores que pertenecen a un estudio concreto. */
    public List<Desarrollador> getDesarrolladoresDeEstudio(Estudio est) {
        return est.getDesarrolladores();
    }
}
