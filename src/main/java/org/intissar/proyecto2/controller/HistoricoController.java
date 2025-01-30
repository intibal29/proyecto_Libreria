package org.intissar.proyecto2.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.intissar.proyecto2.dao.HistoricoPrestamoDAO;
import org.intissar.proyecto2.model.HistoricoPrestamo;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la gestión del historial de préstamos de libros.
 * Permite la consulta de préstamos históricos mediante filtros.
 */
public class HistoricoController {

    /**
     * Logger para registrar eventos y errores.
     */
    private static final Logger logger = LoggerFactory.getLogger(HistoricoController.class);

    @FXML private TextField dniAlumnoField;
    @FXML private TextField codigoLibroField;
    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    @FXML private TableView<HistoricoPrestamo> historicoTable;
    @FXML private TableColumn<HistoricoPrestamo, Integer> idPrestamoColumn;
    @FXML private TableColumn<HistoricoPrestamo, String> dniAlumnoColumn;
    @FXML private TableColumn<HistoricoPrestamo, Integer> codigoLibroColumn;
    @FXML private TableColumn<HistoricoPrestamo, LocalDate> fechaPrestamoColumn;
    @FXML private TableColumn<HistoricoPrestamo, LocalDate> fechaDevolucionColumn;

    private final HistoricoPrestamoDAO historicoDAO;

    /**
     * Constructor que inicializa el DAO para gestionar los préstamos históricos.
     * @throws SQLException si ocurre un error en la conexión con la base de datos.
     */
    public HistoricoController() throws SQLException {
        historicoDAO = new HistoricoPrestamoDAO();
    }

    /**
     * Metodo de inicialización del controlador.
     * Configura las columnas de la tabla y carga los datos del historial sin filtros.
     */
    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla con las propiedades del modelo
        idPrestamoColumn.setCellValueFactory(cellData -> cellData.getValue().idPrestamoProperty().asObject());
        dniAlumnoColumn.setCellValueFactory(cellData -> cellData.getValue().dniAlumnoProperty());
        codigoLibroColumn.setCellValueFactory(cellData -> cellData.getValue().codigoLibroProperty().asObject());
        fechaPrestamoColumn.setCellValueFactory(cellData -> cellData.getValue().fechaPrestamoProperty());
        fechaDevolucionColumn.setCellValueFactory(cellData -> cellData.getValue().fechaDevolucionProperty());

        // Cargar todos los préstamos sin filtros al inicio
        cargarHistorico(null, null, null, null);
    }

    /**
     * Carga el historial de préstamos con los filtros aplicados.
     *
     * @param dniAlumno    DNI del alumno (puede ser nulo para no filtrar por DNI).
     * @param codigoLibro  Código del libro (puede ser nulo para no filtrar por código).
     * @param fechaInicio  Fecha de inicio del rango de búsqueda (puede ser nula).
     * @param fechaFin     Fecha de fin del rango de búsqueda (puede ser nula).
     */
    private void cargarHistorico(String dniAlumno, Integer codigoLibro, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            List<HistoricoPrestamo> historico = historicoDAO.obtenerHistorico(dniAlumno, codigoLibro, fechaInicio, fechaFin);
            historicoTable.getItems().setAll(historico);
        } catch (SQLException e) {
            logger.error("Error al cargar el historial de préstamos:", e);
        }
    }

    /**
     * Aplica los filtros especificados por el usuario y carga los datos del historial.
     */
    @FXML
    private void filtrarHistorico() {
        // Obtener valores de los filtros
        String dniAlumno = dniAlumnoField.getText().trim().isEmpty() ? null : dniAlumnoField.getText().trim();
        Integer codigoLibro = null;

        if (!codigoLibroField.getText().trim().isEmpty()) {
            try {
                codigoLibro = Integer.parseInt(codigoLibroField.getText().trim());
            } catch (NumberFormatException e) {
                logger.error("El código del libro debe ser un número válido.", e);
                mostrarError("El código del libro debe ser un número válido.");
                return;
            }
        }

        LocalDate fechaInicio = fechaInicioPicker.getValue();
        LocalDate fechaFin = fechaFinPicker.getValue();

        // Cargar datos con los filtros aplicados
        cargarHistorico(dniAlumno, codigoLibro, fechaInicio, fechaFin);
    }

    /**
     * Muestra un mensaje de error en una ventana emergente.
     *
     * @param mensaje Mensaje de error a mostrar.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Entrada no válida");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Navega de vuelta al menú principal cargando el archivo FXML de inicio.
     *
     * @param actionEvent Evento de acción que desencadena la navegación.
     */
    public void irAmenu(ActionEvent actionEvent) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/intissar/proyecto2/view/inicio.fxml"));
            Parent root = loader.load();

            // Obtener la escena actual y el Stage
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Configurar la nueva escena con la vista de inicio
            stage.setScene(new Scene(root));
            stage.show();

            logger.info("Menú principal cargado correctamente.");

        } catch (IOException e) {
            logger.error("Error al cargar el menú principal:", e);
            mostrarError("No se pudo cargar el menú principal.");
        }
    }
}
