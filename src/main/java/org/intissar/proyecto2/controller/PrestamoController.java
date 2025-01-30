package org.intissar.proyecto2.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.intissar.proyecto2.dao.PrestamoDAO;
import org.intissar.proyecto2.model.Prestamo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.Node;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class PrestamoController {
    private static final Logger logger = LoggerFactory.getLogger(PrestamoController.class);

    @FXML private TextField dniAlumnoField;
    @FXML private TextField codigoLibroField;
    @FXML private DatePicker fechaPrestamoPicker;
    @FXML private TableView<Prestamo> prestamosTable;
    @FXML private TableColumn<Prestamo, Integer> idPrestamoColumn;
    @FXML private TableColumn<Prestamo, String> dniAlumnoColumn;
    @FXML private TableColumn<Prestamo, Integer> codigoLibroColumn;
    @FXML private TableColumn<Prestamo, LocalDate> fechaPrestamoColumn;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    private Prestamo prestamo; // Se agrega para validar su estado antes de generar informes

    public PrestamoController() throws SQLException {}

    @FXML
    public void initialize() {
        try {
            idPrestamoColumn.setCellValueFactory(cellData -> cellData.getValue().idPrestamoProperty().asObject());
            dniAlumnoColumn.setCellValueFactory(cellData -> cellData.getValue().dniAlumnoProperty());
            codigoLibroColumn.setCellValueFactory(cellData -> cellData.getValue().codigoLibroProperty().asObject());
            fechaPrestamoColumn.setCellValueFactory(cellData -> cellData.getValue().fechaPrestamoProperty());

            cargarPrestamosActivos();

        } catch (Exception e) {
            logger.error("Error en la inicialización del controlador de préstamos", e);
        }
    }
    @FXML
    private void cargarPrestamosActivos() {
        List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosActivos();
        prestamosTable.getItems().setAll(prestamos);
        logger.info("Préstamos activos cargados: {}", prestamos.size());
    }


    @FXML
    private void cargarPrestamosHistorico() {
        try {
            List<Prestamo> prestamosHistoricos = prestamoDAO.obtenerPrestamosHistorico();
            prestamosTable.getItems().setAll(prestamosHistoricos);
        } catch (Exception e) {
            logger.error("Error al cargar los Prestamos", e);
        }
    }


    @FXML
    private void registrarPrestamo() {
        try {
            if (dniAlumnoField.getText().isEmpty() || codigoLibroField.getText().isEmpty() || fechaPrestamoPicker.getValue() == null) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.WARNING);
                return;
            }

            int codigoLibro = Integer.parseInt(codigoLibroField.getText());
            String dniAlumno = dniAlumnoField.getText();
            LocalDate fechaPrestamo = fechaPrestamoPicker.getValue();

            if (prestamoDAO.esLibroPrestado(codigoLibro)) {
                mostrarAlerta("Error", "El libro ya está prestado.", Alert.AlertType.ERROR);
                return;
            }

            Prestamo nuevoPrestamo = new Prestamo(0, dniAlumno, codigoLibro, fechaPrestamo);
            prestamoDAO.registrarPrestamo(nuevoPrestamo);
            cargarPrestamosActivos();
            mostrarAlerta("Éxito", "Préstamo registrado correctamente.", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            logger.error("Error, el código del libro debe ser válido ", e);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo registrar el préstamo.", Alert.AlertType.ERROR);
        }
    }



    @FXML
    private void eliminarPrestamo() throws SQLException {
        Prestamo prestamoSeleccionado = prestamosTable.getSelectionModel().getSelectedItem();
        if (prestamoSeleccionado != null) {
            prestamoDAO.eliminarPrestamo(prestamoSeleccionado.getIdPrestamo());
            cargarPrestamosActivos();
        }
    }

    @FXML
    private void devolverPrestamo() {
        Prestamo prestamoSeleccionado = prestamosTable.getSelectionModel().getSelectedItem();
        if (prestamoSeleccionado != null) {
            LocalDate fechaDevolucion = LocalDate.now();
            try {
                // Registrar en el histórico
                prestamoDAO.marcarComoDevuelto(prestamoSeleccionado.getIdPrestamo(), java.sql.Date.valueOf(fechaDevolucion));

                // Actualizar la vista
                cargarPrestamosActivos();
                logger.info("devolucion exitosa !!");
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo procesar la devolución: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Selección Incorrecta", "Por favor, seleccione un préstamo para devolver.", Alert.AlertType.WARNING);
        }
    }


    @FXML
    private void abrirAyuda() {
        try {
            // Establecer el archivo de ayuda de préstamos
            AyudaHTMLController.setArchivoAyuda("prestamo.html");

            // Cargar la ventana de ayuda
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/intissar/proyecto2/view/AyudaHTML.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ayuda - Préstamos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.error("no se puede abrir la ayuda ", e);

        }
    }

    @FXML
    private void generarInforme(ActionEvent event) {
       /* if (prestamo == null) {
            logger.error("Error: No hay préstamo seleccionado para generar informe.");
            return;
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", prestamo.getId_prestamo());
        parameters.put("nombre", prestamo.getAlumno().getNombre());
        parameters.put("apellidos", prestamo.getAlumno().getApellido1() + " " + prestamo.getAlumno().getApellido2());
        parameters.put("dni", prestamo.getAlumno().getDni());
        parameters.put("titulo", prestamo.getLibro().getTitulo());
        parameters.put("codigo", prestamo.getLibro().getCodigo());
        parameters.put("autor", prestamo.getLibro().getAutor());
        parameters.put("editorial", prestamo.getLibro().getEditorial());
        parameters.put("estado", prestamo.getLibro().getEstado());
        parameters.put("fecha", prestamo.getFecha_prestamo());
        parameters.put("fecha_limite", prestamo.getFecha_prestamo().plusDays(15));

        try {
            JasperReport report = (JasperReport) JRLoader.loadObject(
                    getClass().getResource("/com/alesandro/biblioteca/reports/InformeAltaPrestamo.jasper")
            );
            JasperPrint jprint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
            JasperViewer.viewReport(jprint, false);
            logger.info("Informe de préstamo generado correctamente.");
        } catch (JRException e) {
            logger.error("Error al generar el informe de préstamo.", e);
        }*/
    }
    @FXML
    private void verHistorico() {
        // Cargar la vista de los préstamos históricos
        cargarPrestamosHistorico();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    /**
     * Metodo para volver al menú principal cargando el archivo FXML de inicio.
     *
     * @param actionEvent Evento de acción que desencadena la navegación.
     */
    public void irAmenu(ActionEvent actionEvent) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/intissar/proyecto2/view/inicio.fxml"));
            Parent root = loader.load();

            // Obtener la ventana (Stage) actual a través del evento
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Configurar la nueva escena con la vista de inicio
            stage.setScene(new Scene(root));
            stage.show();

            // Mensaje de confirmación en consola (opcional)
            System.out.println("✅ Menú principal cargado correctamente.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error al cargar el menú principal: " + e.getMessage());
        }
    }

}
