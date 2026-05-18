package com.mycompany.proyectotiendajuegos.aaron.franco.clases;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton que gestiona todas las operaciones de datos mediante JDBC (MySQL).
 * Sustituye por completo la versión anterior basada en listas en memoria.
 */
public class GestorDatos {

    // ── Singleton ──────────────────────────────────────────
    private static GestorDatos instancia;

    public static GestorDatos getInstance() {
        if (instancia == null) instancia = new GestorDatos();
        return instancia;
    }

    // ── Sesión activa (solo en memoria) ───────────────────
    private Usuario       usuarioActual;
    private Administrador adminActual;

    private GestorDatos() { /* conexión lazy a través de DBConexion */ }

    private Connection con() {
        return DBConexion.getInstance().getConexion();
    }

    // ══════════════════════════════════════════════════════
    // SESIÓN
    // ══════════════════════════════════════════════════════
    public Usuario loginUsuario(String correo, String pass) {
        String sql = "SELECT * FROM usuario WHERE correo = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = mapUsuario(rs);
                if (u.verificarContrasena(pass)) {
                    usuarioActual = u;
                    return u;
                }
            }
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    public Administrador loginAdmin(String correo, String pass) {
        String sql = "SELECT * FROM administrador WHERE correo = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Administrador a = mapAdmin(rs);
                if (a.verificarContrasena(pass)) {
                    adminActual = a;
                    return a;
                }
            }
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    public void cerrarSesion() { usuarioActual = null; adminActual = null; }

    public Usuario       getUsuarioActual() { return usuarioActual; }
    public Administrador getAdminActual()   { return adminActual; }

    // ══════════════════════════════════════════════════════
    // USUARIOS
    // ══════════════════════════════════════════════════════
    public ArrayList<Usuario> getUsuarios() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY id_usuario";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapUsuario(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public boolean altaUsuario(String nombre, String apellidos, String correo,
                               String pass, double saldo, String idioma) {
        if (buscarUsuarioPorCorreo(correo) != null) return false;
        String sql = "INSERT INTO usuario (nombre,apellidos,correo,contrasena,saldo,idioma) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, nombre); ps.setString(2, apellidos);
            ps.setString(3, correo); ps.setString(4, pass);
            ps.setDouble(5, saldo);  ps.setString(6, idioma);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public boolean bajaUsuario(int id) {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM usuario WHERE id_usuario=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public boolean actualizarUsuario(Usuario u) {
        String sql = "UPDATE usuario SET nombre=?,apellidos=?,correo=?,contrasena=?,saldo=?,idioma=? WHERE id_usuario=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());    ps.setString(2, u.getApellidos());
            ps.setString(3, u.getCorreo());    ps.setString(4, u.getContrasena());
            ps.setDouble(5, u.getSaldo());     ps.setString(6, u.getIdioma());
            ps.setInt(7, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public Usuario buscarUsuarioPorCorreo(String correo) {
        String sql = "SELECT * FROM usuario WHERE correo=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUsuario(rs);
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    public Usuario buscarUsuarioPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id_usuario=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUsuario(rs);
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    /** Biblioteca: juegos que el usuario ha comprado. */
    public ArrayList<Juego> getBibliotecaUsuario(int idUsuario) {
        ArrayList<Juego> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT j.* FROM juego j "
                   + "JOIN compra c ON c.id_juego = j.id_juego "
                   + "WHERE c.id_usuario = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapJuego(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    /** Compras de un usuario. */
    public ArrayList<Compra> getComprasUsuario(int idUsuario) {
        ArrayList<Compra> lista = new ArrayList<>();
        String sql = "SELECT c.*, u.nombre u_nombre, u.apellidos u_ap, u.correo u_correo, "
                   + "u.contrasena u_pass, u.saldo u_saldo, u.idioma u_idioma, "
                   + "j.titulo j_titulo, j.genero j_gen, j.plataforma j_plat, "
                   + "j.precio j_precio, j.stock j_stock, j.director j_dir, j.id_estudio j_est "
                   + "FROM compra c "
                   + "JOIN usuario u ON u.id_usuario = c.id_usuario "
                   + "JOIN juego  j ON j.id_juego   = c.id_juego "
                   + "WHERE c.id_usuario = ? ORDER BY c.fecha DESC";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapCompraCompleta(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    // ══════════════════════════════════════════════════════
    // ADMINISTRADORES
    // ══════════════════════════════════════════════════════
    public ArrayList<Administrador> getAdministradores() {
        ArrayList<Administrador> lista = new ArrayList<>();
        String sql = "SELECT * FROM administrador ORDER BY id_admin";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapAdmin(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public boolean altaAdmin(String nombre, String apellidos, String correo, String pass) {
        String chk = "SELECT id_admin FROM administrador WHERE correo=?";
        try (PreparedStatement ps = con().prepareStatement(chk)) {
            ps.setString(1, correo);
            if (ps.executeQuery().next()) return false;
        } catch (SQLException e) { manejarError(e); return false; }

        String sql = "INSERT INTO administrador (nombre,apellidos,correo,contrasena) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, nombre); ps.setString(2, apellidos);
            ps.setString(3, correo); ps.setString(4, pass);
            ps.executeUpdate(); return true;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public boolean bajaAdmin(int id) {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM administrador WHERE id_admin=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public boolean actualizarAdmin(Administrador a) {
        String sql = "UPDATE administrador SET nombre=?,apellidos=?,correo=?,contrasena=? WHERE id_admin=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, a.getNombre()); ps.setString(2, a.getApellidos());
            ps.setString(3, a.getCorreo()); ps.setString(4, a.getContrasena());
            ps.setInt(5, a.getIdAdmin());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    // ══════════════════════════════════════════════════════
    // JUEGOS
    // ══════════════════════════════════════════════════════
    public ArrayList<Juego> getJuegos() {
        ArrayList<Juego> lista = new ArrayList<>();
        String sql = "SELECT * FROM juego ORDER BY titulo";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapJuego(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public Juego altaJuego(String titulo, String genero, String plataforma,
                           double precio, int stock, String director) {
        String sql = "INSERT INTO juego (titulo,genero,plataforma,precio,stock,director) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, titulo); ps.setString(2, genero);
            ps.setString(3, plataforma); ps.setDouble(4, precio);
            ps.setInt(5, stock); ps.setString(6, director);
            ps.executeUpdate();
            ResultSet gen = ps.getGeneratedKeys();
            if (gen.next()) return buscarJuegoPorId(gen.getInt(1));
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    public boolean bajaJuego(int id) {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM juego WHERE id_juego=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public boolean actualizarJuego(Juego j) {
        String sql = "UPDATE juego SET titulo=?,genero=?,plataforma=?,precio=?,stock=?,director=? WHERE id_juego=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1,j.getTitulo());    ps.setString(2,j.getGenero());
            ps.setString(3,j.getPlataforma()); ps.setDouble(4,j.getPrecio());
            ps.setInt(5,j.getStock());         ps.setString(6,j.getDirector());
            ps.setInt(7,j.getIdJuego());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public boolean asignarJuegoAEstudio(int idJuego, int idEstudio) {
        String sql = "UPDATE juego SET id_estudio=? WHERE id_juego=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idEstudio); ps.setInt(2, idJuego);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public Juego buscarJuegoPorId(int id) {
        String sql = "SELECT * FROM juego WHERE id_juego=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapJuego(rs);
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    public List<Juego> buscarJuegosPorNombre(String texto) {
        return buscarJuegosConFiltro("titulo", texto);
    }
    public List<Juego> buscarJuegosPorGenero(String texto) {
        return buscarJuegosConFiltro("genero", texto);
    }
    public List<Juego> buscarJuegosPorDirector(String texto) {
        return buscarJuegosConFiltro("director", texto);
    }

    private List<Juego> buscarJuegosConFiltro(String campo, String texto) {
        List<Juego> lista = new ArrayList<>();
        String sql = "SELECT * FROM juego WHERE " + campo + " LIKE ? ORDER BY titulo";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapJuego(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public List<Juego> buscarJuegosPorEstudio(String nombreEstudio) {
        List<Juego> lista = new ArrayList<>();
        String sql = "SELECT j.* FROM juego j "
                   + "JOIN estudio e ON e.id_estudio = j.id_estudio "
                   + "WHERE e.nombre LIKE ? ORDER BY j.titulo";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, "%" + nombreEstudio + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapJuego(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    /** Comprueba si un usuario ya tiene un juego (por compra). */
    public boolean usuarioPoseeJuego(int idUsuario, int idJuego) {
        String sql = "SELECT 1 FROM compra WHERE id_usuario=? AND id_juego=? LIMIT 1";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idUsuario); ps.setInt(2, idJuego);
            return ps.executeQuery().next();
        } catch (SQLException e) { manejarError(e); return false; }
    }

    // ══════════════════════════════════════════════════════
    // COMPRAS
    // ══════════════════════════════════════════════════════
    public String comprarJuego(Usuario u, Juego j, int cantidad) {
        if (j.getStock() < cantidad)                  return "Stock insuficiente.";
        if (u.getSaldo() < j.getPrecio() * cantidad)  return "Saldo insuficiente.";
        if (usuarioPoseeJuego(u.getIdUsuario(), j.getIdJuego())) return "Ya tienes este juego en tu biblioteca.";

        String sqlCompra  = "INSERT INTO compra (id_usuario,id_juego,cantidad,coste,fecha) VALUES (?,?,?,?,?)";
        String sqlStock   = "UPDATE juego  SET stock  = stock  - ? WHERE id_juego  = ?";
        String sqlSaldo   = "UPDATE usuario SET saldo  = saldo  - ? WHERE id_usuario = ?";
        try {
            con().setAutoCommit(false);
            try (PreparedStatement p1 = con().prepareStatement(sqlCompra);
                 PreparedStatement p2 = con().prepareStatement(sqlStock);
                 PreparedStatement p3 = con().prepareStatement(sqlSaldo)) {

                double coste = j.getPrecio() * cantidad;
                p1.setInt(1, u.getIdUsuario()); p1.setInt(2, j.getIdJuego());
                p1.setInt(3, cantidad);          p1.setDouble(4, coste);
                p1.setDate(5, Date.valueOf(LocalDate.now()));
                p1.executeUpdate();

                p2.setInt(1, cantidad); p2.setInt(2, j.getIdJuego());
                p2.executeUpdate();

                p3.setDouble(1, coste); p3.setInt(2, u.getIdUsuario());
                p3.executeUpdate();

                con().commit();
                // Actualizar el objeto en memoria de la sesión activa
                u.setSaldo(u.getSaldo() - coste);
                j.setStock(j.getStock() - cantidad);
                return "OK";
            } catch (SQLException ex) {
                con().rollback();
                throw ex;
            } finally {
                con().setAutoCommit(true);
            }
        } catch (SQLException e) { manejarError(e); return "Error al procesar la compra."; }
    }

    public ArrayList<Compra> getHistorialComprasGlobal() {
        ArrayList<Compra> lista = new ArrayList<>();
        String sql = "SELECT c.*, "
                   + "u.nombre u_nombre, u.apellidos u_ap, u.correo u_correo, "
                   + "u.contrasena u_pass, u.saldo u_saldo, u.idioma u_idioma, "
                   + "j.titulo j_titulo, j.genero j_gen, j.plataforma j_plat, "
                   + "j.precio j_precio, j.stock j_stock, j.director j_dir, j.id_estudio j_est "
                   + "FROM compra c "
                   + "JOIN usuario u ON u.id_usuario = c.id_usuario "
                   + "JOIN juego  j ON j.id_juego   = c.id_juego "
                   + "ORDER BY c.fecha DESC, c.cod_compra DESC";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapCompraCompleta(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    // ══════════════════════════════════════════════════════
    // RESEÑAS
    // ══════════════════════════════════════════════════════
    public ArrayList<Resena> getResenas() {
        return getResenasPorFiltro(null, null, null);
    }

    public List<Resena> getResenasPorUsuario(Usuario u) {
        return getResenasPorFiltro("r.id_usuario", String.valueOf(u.getIdUsuario()), null);
    }

    public List<Resena> getResenasPorJuego(Juego j) {
        return getResenasPorFiltro("r.id_juego", String.valueOf(j.getIdJuego()), null);
    }

    public List<Resena> getResenasPorIdioma(String idioma) {
        return getResenasPorFiltro(null, null, idioma);
    }

    private ArrayList<Resena> getResenasPorFiltro(String campo, String valor, String idioma) {
        ArrayList<Resena> lista = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT r.*, "
          + "u.nombre u_nombre, u.apellidos u_ap, u.correo u_correo, "
          + "u.contrasena u_pass, u.saldo u_saldo, u.idioma u_idioma, "
          + "j.id_juego j_id, j.titulo j_titulo, j.genero j_gen, j.plataforma j_plat, "
          + "j.precio j_precio, j.stock j_stock, j.director j_dir, j.id_estudio j_est "
          + "FROM resena r "
          + "JOIN usuario u ON u.id_usuario = r.id_usuario "
          + "JOIN juego  j ON j.id_juego   = r.id_juego WHERE 1=1");

        if (campo != null)  sb.append(" AND ").append(campo).append(" = ?");
        if (idioma != null) sb.append(" AND r.idioma = ?");
        sb.append(" ORDER BY r.fecha DESC, r.id_resena DESC");

        try (PreparedStatement ps = con().prepareStatement(sb.toString())) {
            int idx = 1;
            if (campo  != null) ps.setString(idx++, valor);
            if (idioma != null) ps.setString(idx, idioma);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapResenaCompleta(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public String anadirResena(Usuario autor, Juego juego, String comentario,
                               int puntuacion, String idioma) {
        if (!usuarioPoseeJuego(autor.getIdUsuario(), juego.getIdJuego()))
            return "Solo puedes reseñar juegos que posees.";

        String chk = "SELECT 1 FROM resena WHERE id_usuario=? AND id_juego=? LIMIT 1";
        try (PreparedStatement ps = con().prepareStatement(chk)) {
            ps.setInt(1, autor.getIdUsuario()); ps.setInt(2, juego.getIdJuego());
            if (ps.executeQuery().next()) return "Ya has escrito una reseña para este juego.";
        } catch (SQLException e) { manejarError(e); return "Error de base de datos."; }

        String sql = "INSERT INTO resena (id_usuario,id_juego,comentario,puntuacion,idioma,fecha) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, autor.getIdUsuario()); ps.setInt(2, juego.getIdJuego());
            ps.setString(3, comentario);        ps.setInt(4, Math.max(1, Math.min(10, puntuacion)));
            ps.setString(5, idioma);            ps.setDate(6, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            return "OK";
        } catch (SQLException e) { manejarError(e); return "Error al guardar la reseña."; }
    }

    public boolean actualizarResena(Resena r) {
        String sql = "UPDATE resena SET comentario=?,puntuacion=?,idioma=? WHERE id_resena=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, r.getComentario()); ps.setInt(2, r.getPuntuacion());
            ps.setString(3, r.getIdioma());     ps.setInt(4, r.getIdResena());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public boolean eliminarResena(int id) {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM resena WHERE id_resena=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    // ══════════════════════════════════════════════════════
    // ESTADÍSTICAS
    // ══════════════════════════════════════════════════════
    public List<Juego> getJuegosMejorValorados() {
        List<Juego> lista = new ArrayList<>();
        String sql = "SELECT j.*, AVG(r.puntuacion) AS media "
                   + "FROM juego j JOIN resena r ON r.id_juego = j.id_juego "
                   + "GROUP BY j.id_juego ORDER BY media DESC";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapJuego(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public List<Juego> getJuegosMasVendidos() {
        List<Juego> lista = new ArrayList<>();
        String sql = "SELECT j.*, COALESCE(SUM(c.cantidad),0) AS total_ventas "
                   + "FROM juego j LEFT JOIN compra c ON c.id_juego = j.id_juego "
                   + "GROUP BY j.id_juego ORDER BY total_ventas DESC";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapJuego(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public int getVentasJuego(Juego j) {
        String sql = "SELECT COALESCE(SUM(cantidad),0) FROM compra WHERE id_juego=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, j.getIdJuego());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { manejarError(e); }
        return 0;
    }

    public double getPuntuacionMediaJuego(int idJuego) {
        String sql = "SELECT AVG(puntuacion) FROM resena WHERE id_juego=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idJuego);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { manejarError(e); }
        return 0.0;
    }

    // ── Por estudio ────────────────────────────────────────
    public Juego getJuegoMejorValoradoEstudio(Estudio est) {
        String sql = "SELECT j.*, AVG(r.puntuacion) AS media "
                   + "FROM juego j JOIN resena r ON r.id_juego = j.id_juego "
                   + "WHERE j.id_estudio = ? "
                   + "GROUP BY j.id_juego ORDER BY media DESC LIMIT 1";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, est.getIdEstudio());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapJuego(rs);
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    public Juego getJuegoMasVendidoEstudio(Estudio est) {
        String sql = "SELECT j.*, COALESCE(SUM(c.cantidad),0) AS total_ventas "
                   + "FROM juego j LEFT JOIN compra c ON c.id_juego = j.id_juego "
                   + "WHERE j.id_estudio = ? "
                   + "GROUP BY j.id_juego ORDER BY total_ventas DESC LIMIT 1";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, est.getIdEstudio());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapJuego(rs);
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    // ── Por desarrollador ──────────────────────────────────
    public Juego getJuegoMejorValoradoDesarrollador(Desarrollador d) {
        String sql = "SELECT j.*, AVG(r.puntuacion) AS media "
                   + "FROM juego j "
                   + "JOIN desarrollador_juego dj ON dj.id_juego = j.id_juego "
                   + "JOIN resena r ON r.id_juego = j.id_juego "
                   + "WHERE dj.id_desarrollador = ? "
                   + "GROUP BY j.id_juego ORDER BY media DESC LIMIT 1";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, d.getIdDesarrollador());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapJuego(rs);
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    public Juego getJuegoMasVendidoDesarrollador(Desarrollador d) {
        String sql = "SELECT j.*, COALESCE(SUM(c.cantidad),0) AS total_ventas "
                   + "FROM juego j "
                   + "JOIN desarrollador_juego dj ON dj.id_juego = j.id_juego "
                   + "LEFT JOIN compra c ON c.id_juego = j.id_juego "
                   + "WHERE dj.id_desarrollador = ? "
                   + "GROUP BY j.id_juego ORDER BY total_ventas DESC LIMIT 1";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, d.getIdDesarrollador());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapJuego(rs);
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    // ══════════════════════════════════════════════════════
    // ESTUDIOS
    // ══════════════════════════════════════════════════════
    public ArrayList<Estudio> getEstudios() {
        ArrayList<Estudio> lista = new ArrayList<>();
        String sql = "SELECT * FROM estudio ORDER BY nombre";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Estudio e = mapEstudio(rs);
                e.getDesarrolladores().addAll(getDesarrolladoresDeEstudio(e));
                e.getJuegos().addAll(getJuegosDeEstudio(e));
                lista.add(e);
            }
        } catch (SQLException ex) { manejarError(ex); }
        return lista;
    }

    public Estudio altaEstudio(String nombre) {
        String sql = "INSERT INTO estudio (nombre) VALUES (?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
            ResultSet gen = ps.getGeneratedKeys();
            if (gen.next()) return buscarEstudioPorId(gen.getInt(1));
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    public boolean bajaEstudio(int id) {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM estudio WHERE id_estudio=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public boolean actualizarEstudio(Estudio e) {
        String sql = "UPDATE estudio SET nombre=? WHERE id_estudio=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1, e.getNombre()); ps.setInt(2, e.getIdEstudio());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { manejarError(ex); return false; }
    }

    public Estudio buscarEstudioPorId(int id) {
        String sql = "SELECT * FROM estudio WHERE id_estudio=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapEstudio(rs);
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    private List<Juego> getJuegosDeEstudio(Estudio e) {
        List<Juego> lista = new ArrayList<>();
        String sql = "SELECT * FROM juego WHERE id_estudio=? ORDER BY titulo";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, e.getIdEstudio());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapJuego(rs));
        } catch (SQLException ex) { manejarError(ex); }
        return lista;
    }

    // ══════════════════════════════════════════════════════
    // DESARROLLADORES
    // ══════════════════════════════════════════════════════
    public ArrayList<Desarrollador> getDesarrolladores() {
        ArrayList<Desarrollador> lista = new ArrayList<>();
        String sql = "SELECT * FROM desarrollador ORDER BY nombre";
        try (Statement st = con().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapDesarrollador(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public List<Desarrollador> getDesarrolladoresDeEstudio(Estudio est) {
        List<Desarrollador> lista = new ArrayList<>();
        String sql = "SELECT * FROM desarrollador WHERE id_estudio=? ORDER BY nombre";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, est.getIdEstudio());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapDesarrollador(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public Desarrollador altaDesarrollador(String nombre, String apellidos,
                                           int anos, String puesto, Estudio estudio) {
        String sql = "INSERT INTO desarrollador (nombre,apellidos,anos_experiencia,puesto_actual,id_estudio) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre); ps.setString(2, apellidos);
            ps.setInt(3, anos);      ps.setString(4, puesto);
            if (estudio != null) ps.setInt(5, estudio.getIdEstudio());
            else ps.setNull(5, Types.INTEGER);
            ps.executeUpdate();
            ResultSet gen = ps.getGeneratedKeys();
            if (gen.next()) return buscarDesarrolladorPorId(gen.getInt(1));
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    public boolean bajaDesarrollador(int id) {
        try (PreparedStatement ps = con().prepareStatement("DELETE FROM desarrollador WHERE id_desarrollador=?")) {
            ps.setInt(1, id); return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public boolean actualizarDesarrollador(Desarrollador d) {
        String sql = "UPDATE desarrollador SET nombre=?,apellidos=?,anos_experiencia=?,puesto_actual=? WHERE id_desarrollador=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setString(1,d.getNombre());  ps.setString(2,d.getApellidos());
            ps.setInt(3,d.getAnosExperiencia()); ps.setString(4,d.getPuestoActual());
            ps.setInt(5,d.getIdDesarrollador());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { manejarError(e); return false; }
    }

    public Desarrollador buscarDesarrolladorPorId(int id) {
        String sql = "SELECT * FROM desarrollador WHERE id_desarrollador=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapDesarrollador(rs);
        } catch (SQLException e) { manejarError(e); }
        return null;
    }

    /** Juegos de un desarrollador (tabla relacional). */
    public ArrayList<Juego> getJuegosDesarrollador(int idDesarrollador) {
        ArrayList<Juego> lista = new ArrayList<>();
        String sql = "SELECT j.* FROM juego j "
                   + "JOIN desarrollador_juego dj ON dj.id_juego = j.id_juego "
                   + "WHERE dj.id_desarrollador = ?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idDesarrollador);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapJuego(rs));
        } catch (SQLException e) { manejarError(e); }
        return lista;
    }

    public void asignarJuegoADesarrollador(int idDev, int idJuego) {
        String sql = "INSERT IGNORE INTO desarrollador_juego (id_desarrollador,id_juego) VALUES (?,?)";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idDev); ps.setInt(2, idJuego);
            ps.executeUpdate();
        } catch (SQLException e) { manejarError(e); }
    }

    public void quitarJuegoADesarrollador(int idDev, int idJuego) {
        String sql = "DELETE FROM desarrollador_juego WHERE id_desarrollador=? AND id_juego=?";
        try (PreparedStatement ps = con().prepareStatement(sql)) {
            ps.setInt(1, idDev); ps.setInt(2, idJuego);
            ps.executeUpdate();
        } catch (SQLException e) { manejarError(e); }
    }

    public void setJuegosDesarrollador(int idDev, List<Integer> idsJuegos) {
        String del = "DELETE FROM desarrollador_juego WHERE id_desarrollador=?";
        String ins = "INSERT IGNORE INTO desarrollador_juego (id_desarrollador,id_juego) VALUES (?,?)";
        try {
            con().setAutoCommit(false);
            try (PreparedStatement psDel = con().prepareStatement(del);
                 PreparedStatement psIns = con().prepareStatement(ins)) {
                psDel.setInt(1, idDev); psDel.executeUpdate();
                for (int idJ : idsJuegos) {
                    psIns.setInt(1, idDev); psIns.setInt(2, idJ);
                    psIns.executeUpdate();
                }
                con().commit();
            } catch (SQLException ex) { con().rollback(); throw ex; }
            finally { con().setAutoCommit(true); }
        } catch (SQLException e) { manejarError(e); }
    }

    // ══════════════════════════════════════════════════════
    // MAPPERS – ResultSet → Objetos
    // ══════════════════════════════════════════════════════
    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellidos(rs.getString("apellidos"));
        u.setCorreo(rs.getString("correo"));
        u.setContrasena(rs.getString("contrasena"));
        u.setSaldo(rs.getDouble("saldo"));
        u.setIdioma(rs.getString("idioma"));
        return u;
    }

    private Administrador mapAdmin(ResultSet rs) throws SQLException {
        Administrador a = new Administrador();
        a.setIdAdmin(rs.getInt("id_admin"));
        a.setNombre(rs.getString("nombre"));
        a.setApellidos(rs.getString("apellidos"));
        a.setCorreo(rs.getString("correo"));
        a.setContrasena(rs.getString("contrasena"));
        return a;
    }

    private Juego mapJuego(ResultSet rs) throws SQLException {
        Juego j = new Juego();
        j.setIdJuego(rs.getInt("id_juego"));
        j.setTitulo(rs.getString("titulo"));
        j.setGenero(rs.getString("genero"));
        j.setPlataforma(rs.getString("plataforma"));
        j.setPrecio(rs.getDouble("precio"));
        j.setStock(rs.getInt("stock"));
        j.setDirector(rs.getString("director"));
        return j;
    }

    private Estudio mapEstudio(ResultSet rs) throws SQLException {
        Estudio e = new Estudio();
        e.setIdEstudio(rs.getInt("id_estudio"));
        e.setNombre(rs.getString("nombre"));
        return e;
    }

    private Desarrollador mapDesarrollador(ResultSet rs) throws SQLException {
        Desarrollador d = new Desarrollador();
        d.setIdDesarrollador(rs.getInt("id_desarrollador"));
        d.setNombre(rs.getString("nombre"));
        d.setApellidos(rs.getString("apellidos"));
        d.setAnosExperiencia(rs.getInt("anos_experiencia"));
        d.setPuestoActual(rs.getString("puesto_actual"));
        return d;
    }

    private Compra mapCompraCompleta(ResultSet rs) throws SQLException {
        Compra c = new Compra();
        c.setCodCompra(rs.getInt("cod_compra"));
        c.setCantidad(rs.getInt("cantidad"));
        c.setCoste(rs.getDouble("coste"));
        c.setFecha(rs.getDate("fecha").toLocalDate());

        Usuario u = new Usuario();
        u.setNombre(rs.getString("u_nombre")); u.setApellidos(rs.getString("u_ap"));
        u.setCorreo(rs.getString("u_correo")); u.setContrasena(rs.getString("u_pass"));
        u.setSaldo(rs.getDouble("u_saldo"));   u.setIdioma(rs.getString("u_idioma"));
        c.setUsuario(u);

        Juego j = new Juego();
        j.setTitulo(rs.getString("j_titulo")); j.setGenero(rs.getString("j_gen"));
        j.setPlataforma(rs.getString("j_plat")); j.setPrecio(rs.getDouble("j_precio"));
        j.setStock(rs.getInt("j_stock"));      j.setDirector(rs.getString("j_dir"));
        c.setJuego(j);
        return c;
    }

    private Resena mapResenaCompleta(ResultSet rs) throws SQLException {
        Resena r = new Resena();
        r.setIdResena(rs.getInt("id_resena"));
        r.setComentario(rs.getString("comentario"));
        r.setPuntuacion(rs.getInt("puntuacion"));
        r.setIdioma(rs.getString("idioma"));
        r.setFecha(rs.getDate("fecha").toLocalDate());

        Usuario us = new Usuario();
        us.setNombre(rs.getString("u_nombre")); us.setApellidos(rs.getString("u_ap"));
        us.setCorreo(rs.getString("u_correo")); us.setContrasena(rs.getString("u_pass"));
        us.setSaldo(rs.getDouble("u_saldo"));   us.setIdioma(rs.getString("u_idioma"));
        r.setAutor(us);

        Juego ju = new Juego();
        ju.setIdJuego(rs.getInt("j_id"));
        ju.setTitulo(rs.getString("j_titulo")); ju.setGenero(rs.getString("j_gen"));
        ju.setPlataforma(rs.getString("j_plat")); ju.setPrecio(rs.getDouble("j_precio"));
        ju.setStock(rs.getInt("j_stock"));      ju.setDirector(rs.getString("j_dir"));
        r.setJuego(ju);
        return r;
    }

    // ══════════════════════════════════════════════════════
    // ERROR HANDLING
    // ══════════════════════════════════════════════════════
    private void manejarError(SQLException e) {
        System.err.println("[GestorDatos SQL] " + e.getMessage());
        e.printStackTrace();
    }
}
