package org.intissar.proyecto2.dao;

import org.intissar.proyecto2.model.Libro;
import org.intissar.proyecto2.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Clase DAO (Data Access Object) para gestionar las operaciones sobre la tabla `Libro` en la base de datos.
 * Proporciona métodos para operaciones CRUD, búsquedas avanzadas y gestión del estado de libros.
 *
 * <p>Se gestiona la conexión a la base de datos a través de {@link DBConnection}.</p>
 *
 * <strong>Recomendación:</strong> Mantener buenas prácticas para cerrar conexiones y manejar excepciones.
 *
 * @version 1.0
 */
public class LibroDAO {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(LibroDAO.class.getName());

    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException Si ocurre un error al obtener la conexión.
     */
    public LibroDAO() throws SQLException {
        connection = DBConnection.getConnection();
    }
    /**
     * Obtiene una lista de todos los libros registrados, con opción de incluir los dados de baja.
     *
     * @param incluirBaja true si se desean incluir libros dados de baja; false en caso contrario.
     * @return Una lista de objetos {@link Libro}.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    // Obtener libros, con opción de incluir dados de baja
    public List<Libro> obtenerLibros(boolean incluirBaja) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String query = incluirBaja ? "SELECT Codigo, Titulo, Autor, Editorial, Estado, Baja FROM Libro"
                : "SELECT Codigo, Titulo, Autor, Editorial, Estado, Baja FROM Libro WHERE Baja = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                libros.add(new Libro(
                        rs.getInt("codigo"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editorial"),
                        rs.getString("estado"),
                        rs.getBoolean("baja")
                ));
            }
        }
        return libros;
    }
    /**
     * Busca libros por un criterio específico (título, autor o editorial).
     *
     * @param criterio Cadena de búsqueda.
     * @return Una lista de objetos {@link Libro} que coincidan con el criterio.
     */
    // Buscar libros por criterio
    public List<Libro> buscarLibrosPorCriterio(String criterio) {
        List<Libro> libros = new ArrayList<>();
        String query = "SELECT * FROM Libro WHERE Baja = 0 AND " +
                "(Titulo LIKE ? OR Autor LIKE ? OR Editorial LIKE ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String filtro = "%" + criterio + "%";
            stmt.setString(1, filtro);
            stmt.setString(2, filtro);
            stmt.setString(3, filtro);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar libros por criterio: " + criterio, e);
        }
        return libros;
    }
    /**
     * Inserta un nuevo libro en la base de datos.
     *
     * @param libro El objeto {@link Libro} con la información del libro a insertar.
     * @throws SQLException Si ocurre un error en la inserción.
     */
    // Insertar un nuevo libro
    public void insertarLibro(Libro libro) throws SQLException {
        String query = "INSERT INTO Libro (Titulo, Autor, Editorial, Estado, Baja) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setString(4, libro.getEstado());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Libro agregado correctamente.");
            } else {
                System.err.println("⚠ No se pudo insertar el libro.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("❌ Error al insertar el libro: " + e.getMessage());
        }
    }
    /**
     * Reactiva un libro dado de baja.
     *
     * @param codigo El código del libro a reactivar.
     * @throws SQLException Si ocurre un error durante la reactivación.
     */
    public void reactivarLibro(int codigo) throws SQLException {
        String query = "UPDATE Libro SET Baja = 0 WHERE codigo = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, codigo);
            stmt.executeUpdate();
        }
    }
    /**
     * Obtiene libros filtrados por un estado específico.
     *
     * @param estado El estado del libro (ej. "Disponible", "Prestado").
     * @return Lista de libros con el estado especificado.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    public List<Libro> obtenerLibrosPorEstado(String estado) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String query = "SELECT * FROM Libro WHERE estado = ? AND baja = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, estado);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearLibro(rs));
                }
            }
        }
        return libros;
    }
    /**
     * Elimina un libro de la base de datos basado en su código.
     *
     * @param codigo El código del libro a eliminar.
     * @throws SQLException Si ocurre un error durante la eliminación.
     */
    public boolean eliminarLibro(int codigo) throws SQLException {
        String query = "DELETE FROM Libro WHERE codigo = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, codigo);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info(" Libro eliminado correctamente. Código: {}");
                return true;
            } else {
                logger.warning("⚠ No se encontró el libro con código: {} para eliminar.");
                return false;
            }

        } catch (SQLException e) {
            logger.info(" Error al eliminar el libro con código: {}");
            throw new SQLException("Error al eliminar el libro con código: " + codigo, e);
        }
    }

    /**
     * Modifica los datos de un libro existente.
     *
     * @param libro El objeto {@link Libro} con los nuevos datos.
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    // Modificar un libro existente
    public void modificarLibro(Libro libro) throws SQLException {
        String query = "UPDATE Libro SET Titulo = ?, Autor = ?, Editorial = ?, Estado = ? WHERE Codigo = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setString(4, libro.getEstado());
            stmt.setInt(5, libro.getCodigo());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Libro modificado correctamente.");
            } else {
                System.err.println("⚠ No se pudo modificar el libro.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("❌ Error al modificar el libro: " + e.getMessage());
        }
    }
    /**
     * Verifica si un libro está disponible para préstamo.
     *
     * @param codigo El código del libro.
     * @return true si el libro está disponible; false en caso contrario.
     */
    // Verificar si un libro está disponible para préstamo
    public boolean estaDisponible(int codigo) {
        String query = "SELECT Baja FROM Libro WHERE codigo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return !rs.getBoolean("Baja");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar disponibilidad del libro con código: " + codigo, e);
        }
        return false;
    }
    /**
     * Actualiza el estado de un libro en la base de datos.
     *
     * @param codigoLibro  El código del libro a actualizar.
     * @param nuevoEstado  El nuevo estado que se asignará al libro.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public void actualizarEstadoLibro(int codigoLibro, String nuevoEstado) throws SQLException {
        String query = "UPDATE Libro SET Estado = ? WHERE Codigo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, codigoLibro);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar el estado del libro: " + codigoLibro, e);
            throw new SQLException("Error al actualizar el estado del libro: " + codigoLibro, e);
        }
    }

    /**
     * Marca un libro como dado de baja en el sistema.
     * Antes de darlo de baja, verifica si existe un préstamo activo para el libro
     * y lo registra en el historial de préstamos. Luego elimina el préstamo y marca el libro como dado de baja.
     *
     * @param codigo El código del libro a dar de baja.
     * @throws SQLException Si el libro no existe, si no tiene préstamo activo,
     *                      o si ocurre un error al realizar las operaciones en la base de datos.
     */
    public void marcarBaja(int codigo) throws SQLException {
        if (!existeLibro(codigo)) {
            throw new SQLException("El libro con código " + codigo + " no existe.");
        }

        // Obtener el DNI del alumno asociado al préstamo antes de hacer la baja
        String queryObtenerDni = "SELECT dni_alumno FROM Prestamo WHERE codigo_libro = ?";
        String dniAlumno = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtDni = conn.prepareStatement(queryObtenerDni)) {

            stmtDni.setInt(1, codigo);
            try (ResultSet rs = stmtDni.executeQuery()) {
                if (rs.next()) {
                    dniAlumno = rs.getString("dni_alumno");
                } else {
                    throw new SQLException("No se encontró un préstamo activo para este libro.");
                }
            }
        }

        // Insertar en el historial de préstamos
        String queryHistorico = "INSERT INTO Historico_prestamo (codigo_libro, fecha_prestamo, fecha_devolucion) " +
                "SELECT codigo FROM Libro WHERE codigo = ?";

        // Eliminar el préstamo activo
        String queryEliminarPrestamo = "DELETE FROM Prestamo WHERE codigo_libro = ?";

        // Marcar el libro como dado de baja
        String queryBaja = "UPDATE Libro SET Baja = 1 WHERE codigo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtHist = conn.prepareStatement(queryHistorico);
             PreparedStatement stmtEliminarPrestamo = conn.prepareStatement(queryEliminarPrestamo);
             PreparedStatement stmtBaja = conn.prepareStatement(queryBaja)) {

            // Insertar en el historial
            stmtHist.setString(1, dniAlumno);
            stmtHist.setInt(2, codigo);
            int filasHistorial = stmtHist.executeUpdate();

            if (filasHistorial == 0) {
                throw new SQLException("No se pudo mover el préstamo al historial.");
            }

            // Eliminar de la tabla de préstamos activos
            stmtEliminarPrestamo.setInt(1, codigo);
            stmtEliminarPrestamo.executeUpdate();

            // Marcar el libro como dado de baja
            stmtBaja.setInt(1, codigo);
            stmtBaja.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al dar de baja el libro con código: " + codigo, e);
            throw e;
        }
    }

    // Verificar existencia de un libro por su código
    public boolean existeLibro(int codigo) {
        String query = "SELECT COUNT(*) FROM Libro WHERE codigo = ?";

        try (Connection conn = DBConnection.getConnection();  // 🔹 Nueva conexión para cada consulta
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar existencia del libro con código: " + codigo, e);
        }
        return false;
    }
    /**
     * Mapear un {@link ResultSet} a un objeto {@link Libro}.
     *
     * @param rs El objeto ResultSet con los datos del libro.
     * @return Un objeto Libro.
     * @throws SQLException Si ocurre un error al acceder a los datos.
     */
    // Mapear ResultSet a objeto Libro
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("Codigo"),
                rs.getString("Titulo"),
                rs.getString("Autor"),
                rs.getString("Editorial"),
                rs.getString("Estado"),
                rs.getBoolean("Baja")
        );

    }
}
