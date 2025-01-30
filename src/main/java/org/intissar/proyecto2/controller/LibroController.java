package org.intissar.proyecto2.controller;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.intissar.proyecto2.dao.LibroDAO;
import org.intissar.proyecto2.model.Libro;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.Node;
/**
 * Controlador para gestionar la vista de libros en la aplicación.
 * Permite realizar operaciones como agregar, modificar, eliminar y filtrar libros,
 * así como interactuar con la base de datos y generar informes.
 */
public class LibroController {
    /**
     * Logger para registrar eventos y errores en la clase.
     */
    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

        @FXML
        private TextField tituloField, autorField, editorialField;
        @FXML
        private ComboBox<String> estadoComboBox;
        @FXML
        private TableView<Libro> librosTable;
        @FXML
        private TableColumn<Libro, Integer> codigoColumn;
        @FXML
        private TableColumn<Libro, String> tituloColumn, autorColumn, estadoColumn;

        @FXML private Label tituloLabel, labelIdioma;

        @FXML private ComboBox<String>  idiomaComboBox;

        @FXML private Button agregarLibroButton, modificarLibroButton, marcarBajaLibroButton,
                eliminarLibroButton, verBajaButton, reactivarLibroButton, helpButton, informeButton;

        private Locale locale = new Locale("es"); // Idioma predeterminado (Español)
        private ResourceBundle bundle;

        private ObservableList<Libro> librosList;
        private final LibroDAO libroDAO;
    /**
     * Constructor de la clase `LibroController`.
     * @throws SQLException Si ocurre un error de conexión con la base de datos.
     */
        public LibroController() throws SQLException {
            libroDAO = new LibroDAO();
        }
    /**
     * Inicializa la vista configurando elementos UI y cargando la lista de libros.
     */
        @FXML
        public void initialize() {
            // 📌 Configurar idioma predeterminado

            // 📌 Configurar ComboBox de idioma
            idiomaComboBox.setItems(FXCollections.observableArrayList("Español", "English"));
            idiomaComboBox.setValue("Español");
            librosList = FXCollections.observableArrayList(); // 🔹 Se inicializa aquí para evitar NullPointerException
            librosTable.setItems(librosList); // 🔹 Se asigna a la tabla para evitar errores

            cargarLibros(); // 🔹 Se carga la lista de libros
            // Inicializar la lista observable
            librosList = FXCollections.observableArrayList();

            // Configurar ComboBox con los estados disponibles
            estadoComboBox.setItems(FXCollections.observableArrayList(
                    "Nuevo", "Usado nuevo", "Usado seminuevo", "Usado estropeado", "Restaurado"
            ));

            // Configurar columnas de la tabla
            codigoColumn.setCellValueFactory(cellData -> cellData.getValue().codigoProperty().asObject());
            tituloColumn.setCellValueFactory(cellData -> cellData.getValue().tituloProperty());
            autorColumn.setCellValueFactory(cellData -> cellData.getValue().autorProperty());
            estadoColumn.setCellValueFactory(cellData -> cellData.getValue().estadoProperty());
            // Asignar la lista observable a la tabla
            librosTable.setItems(librosList);
            idiomaComboBox.setItems(FXCollections.observableArrayList("Español", "English"));
            idiomaComboBox.setValue("Español");
            idiomaComboBox.setOnAction(event -> cambiarIdioma());

            // Cargar el idioma por defecto
            cargarLibros();

        }

    /**
     * Cambia el idioma de la interfaz basado en la selección del ComboBox.
     */
    @FXML
    private void cambiarIdioma() {
            String seleccion = idiomaComboBox.getValue();
            locale = seleccion.equals("English") ? new Locale("en") : new Locale("es");
            bundle = ResourceBundle.getBundle("i18n.messages", locale);

            actualizarInterfaz();
    }
    /**
     * Actualiza los textos de la interfaz según el idioma seleccionado.
     */
    private void actualizarInterfaz() {
        tituloLabel.setText(bundle.getString("titulo"));
        labelIdioma.setText(bundle.getString("idioma"));
        agregarLibroButton.setText(bundle.getString("agregar"));
        modificarLibroButton.setText(bundle.getString("modificar"));
        eliminarLibroButton.setText(bundle.getString("eliminar"));
    }



    /**
     * Agrega un nuevo libro a la base de datos si los campos son válidos.
     */

    @FXML
    private void agregarLibro() {
        try {
            if (validarCampos()) {
                Libro libro = new Libro();
                libro.setTitulo(tituloField.getText());
                libro.setAutor(autorField.getText());
                libro.setEditorial(editorialField.getText());
                libro.setEstado(estadoComboBox.getValue());

                libroDAO.insertarLibro(libro);
                mostrarAlerta("Éxito", "Libro agregado correctamente.", Alert.AlertType.INFORMATION);
                cargarLibros();
                limpiarCampos();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo agregar el libro.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private ComboBox<String> filtroEstadoComboBox;

    @FXML
    private void filtrarPorEstado() {
        String estadoSeleccionado = filtroEstadoComboBox.getValue();
        if (estadoSeleccionado == null || estadoSeleccionado.equals("Todos")) {
            cargarLibros();
            return;
        }

        try {
            List<Libro> librosFiltrados = libroDAO.obtenerLibrosPorEstado(estadoSeleccionado);
            librosList.clear();
            librosList.addAll(librosFiltrados);
            librosTable.refresh();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los libros filtrados.", Alert.AlertType.ERROR);
        }
    }
    /**
     * Elimina un libro seleccionado de la base de datos.
     */
    @FXML
    private void eliminarLibro() {
        try {
            Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
            if (libroSeleccionado != null) {
                libroDAO.eliminarLibro(libroSeleccionado.getCodigo());
                mostrarAlerta("Éxito", "Libro eliminado correctamente.", Alert.AlertType.INFORMATION);
                cargarLibros();
            } else {
                mostrarAlerta("Error", "Seleccione un libro para eliminar.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo eliminar el libro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void reactivarLibro() {
        try {
            Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
            if (libroSeleccionado != null) {
                libroDAO.reactivarLibro(libroSeleccionado.getCodigo());
                mostrarAlerta("Éxito", "Libro reactivado correctamente.", Alert.AlertType.INFORMATION);
                cargarLibros();
            } else {
                mostrarAlerta("Error", "Seleccione un libro para reactivarlo.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo reactivar el libro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarLibrosDadosDeBaja() {
        try {
            List<Libro> librosBaja = libroDAO.obtenerLibros(true);
            librosList.clear();
            librosList.addAll(librosBaja);
            librosTable.refresh();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los libros dados de baja.", Alert.AlertType.ERROR);
        }
    }
    /**
     * Modifica los datos de un libro seleccionado.
     */
    @FXML
    private void modificarLibro() {
        try {
            Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
            if (libroSeleccionado != null && validarCampos()) {
                libroSeleccionado.setTitulo(tituloField.getText());
                libroSeleccionado.setAutor(autorField.getText());
                libroSeleccionado.setEditorial(editorialField.getText());
                libroSeleccionado.setEstado(estadoComboBox.getValue());

                libroDAO.modificarLibro(libroSeleccionado);
                mostrarAlerta("Éxito", "Libro modificado correctamente.", Alert.AlertType.INFORMATION);
                cargarLibros();
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "Seleccione un libro para modificar.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo modificar el libro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void marcarBajaLibro() {
        try {
            Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
            if (libroSeleccionado != null) {
                libroDAO.marcarBaja(libroSeleccionado.getCodigo());
                mostrarAlerta("Éxito", "Libro marcado como baja.", Alert.AlertType.INFORMATION);
                cargarLibros();
            } else {
                mostrarAlerta("Error", "Seleccione un libro para marcarlo como baja.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo marcar el libro como baja.", Alert.AlertType.ERROR);
        }
    }
    /**
     * Carga la lista de libros desde la base de datos.
     */
    @FXML
    private void cargarLibros() {
        try {
            logger.info("Cargando libros desde la base de datos...");
            List<Libro> libros = libroDAO.obtenerLibros(false);
            System.out.println("📌 Libros obtenidos: " + libros.size());

            librosList.clear(); // ✅ Evitar NullPointerException asegurando que `librosList` está inicializado
            librosList.addAll(libros);
            librosTable.refresh();
            logger.info("Libros cargados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error al cargar los libros", e);
            mostrarAlerta("Error", "No se pudieron cargar los libros.", Alert.AlertType.ERROR);
        }
    }



    private void limpiarCampos() {
        tituloField.clear();
        autorField.clear();
        editorialField.clear();
        estadoComboBox.getSelectionModel().clearSelection();
    }
    @FXML
    /**
     * Abre la ventana de ayuda sobre libros.
     */
    private void abrirAyuda() {
        try {
            logger.info("Cargando ayuda ");
            // Establecer el archivo de ayuda que se quiere abrir
            AyudaHTMLController.setArchivoAyuda("libro.html");

            // Cargar la ventana de ayuda
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/intissar/proyecto2/view/AyudaHTML.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ayuda - Libros");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.info("error");

            mostrarAlerta("Error", "No se pudo abrir la ayuda", Alert.AlertType.ERROR);
        }
    }
    /**
     * Genera un informe de libros en la base de datos.
     */
    @FXML
    private void generarInforme() {
        InformesController.generarReporteLibros();
    }
    private boolean validarCampos() {
        if (tituloField.getText().isEmpty() || autorField.getText().isEmpty() || editorialField.getText().isEmpty() || estadoComboBox.getValue() == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    /**
     * Muestra una alerta en pantalla.
     * @param titulo Título de la alerta.
     * @param mensaje Mensaje a mostrar en la alerta.
     * @param tipo Tipo de alerta (Información, Advertencia o Error).
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void seleccionarLibro(MouseEvent event) {
        Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
        if (libroSeleccionado != null) {
            tituloField.setText(libroSeleccionado.getTitulo());
            autorField.setText(libroSeleccionado.getAutor());
            editorialField.setText(libroSeleccionado.getEditorial());
            estadoComboBox.setValue(libroSeleccionado.getEstado());
        }
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
            // Obtener la escena actual y el Stage
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
