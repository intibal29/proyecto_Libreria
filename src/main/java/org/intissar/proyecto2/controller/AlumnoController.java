package org.intissar.proyecto2.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.intissar.proyecto2.Main;
import org.intissar.proyecto2.dao.AlumnoDAO;
import org.intissar.proyecto2.model.Alumno;
import javafx.scene.Node;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.intissar.proyecto2.Main.cargarVista;

/**
 * Controlador para la gestión de alumnos en la aplicación.
 * Permite realizar operaciones como agregar, modificar, eliminar y buscar alumnos.
 * También gestiona la carga de datos, la búsqueda y el filtrado de alumnos.
 */
public class AlumnoController {
    /**
     * Logger para registrar eventos y errores.
     */
    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    @FXML
    private ComboBox<String> idiomaComboBox;
    private static final String SPANISH = "Español";
    private static final String ENGLISH = "English";
    @FXML
    private TextField dniField, nombreField, apellido1Field, apellido2Field, buscarField;

    @FXML
    private TableView<Alumno> alumnosTable;

    @FXML
    private TableColumn<Alumno, String> dniColumn, nombreColumn, apellido1Column, apellido2Column;

    @FXML
    private Button agregarButton, modificarButton, eliminarButton;
    private static Locale locale = new Locale("es"); // Idioma predeterminado (Español)
    private static ResourceBundle bundle;
    private ObservableList<Alumno> listaAlumnos;
    private final AlumnoDAO alumnoDAO;
    /**
     * Constructor que inicializa la lista de alumnos y el DAO.
     *
     * @throws SQLException Si ocurre un error al conectar con la base de datos.
     */
    public AlumnoController() throws SQLException {
        listaAlumnos = FXCollections.observableArrayList();
        alumnoDAO = new AlumnoDAO();
    }

    /**
     * Metodo de inicialización que configura la tabla, la búsqueda y los botones.
     * Se ejecuta automáticamente al cargar la vista.
     */
    @FXML
    public void initialize() {
        // Configuración de las columnas de la tabla
        dniColumn.setCellValueFactory(new PropertyValueFactory<>("dni"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellido1Column.setCellValueFactory(new PropertyValueFactory<>("apellido1"));
        apellido2Column.setCellValueFactory(new PropertyValueFactory<>("apellido2"));

        // Asignar la lista de alumnos a la tabla
        cargarAlumnos();

        // Configurar filtro de búsqueda
        FilteredList<Alumno> filteredList = new FilteredList<>(listaAlumnos, p -> true);
        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(alumno -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Mostrar todos si no hay criterio de búsqueda
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return alumno.getDni().toLowerCase().contains(lowerCaseFilter) ||
                        alumno.getNombre().toLowerCase().contains(lowerCaseFilter) ||
                        alumno.getApellido1().toLowerCase().contains(lowerCaseFilter) ||
                        alumno.getApellido2().toLowerCase().contains(lowerCaseFilter);
            });
        });
        alumnosTable.setItems(filteredList);

        // Deshabilitar botones hasta que se cumplan las condiciones
        agregarButton.disableProperty().bind(
                dniField.textProperty().isEmpty()
                        .or(nombreField.textProperty().isEmpty())
                        .or(apellido1Field.textProperty().isEmpty())
        );

        modificarButton.disableProperty().bind(
                alumnosTable.getSelectionModel().selectedItemProperty().isNull()
                        .or(dniField.textProperty().isEmpty())
                        .or(nombreField.textProperty().isEmpty())
                        .or(apellido1Field.textProperty().isEmpty())
        );

        eliminarButton.disableProperty().bind(
                alumnosTable.getSelectionModel().selectedItemProperty().isNull()
        );

        idiomaComboBox.setItems(FXCollections.observableArrayList(SPANISH, ENGLISH));
        idiomaComboBox.setOnAction(event -> cambiarIdioma());
    }
    /**
     * Configura el ComboBox de idioma y su evento de cambio.
     */
    @FXML
    private void cambiarIdioma() {
        String idiomaSeleccionado = idiomaComboBox.getSelectionModel().getSelectedItem();

        if (SPANISH.equals(idiomaSeleccionado)) {
            cambiarIdioma(new Locale("es"));
        } else if (ENGLISH.equals(idiomaSeleccionado)) {
            cambiarIdioma(new Locale("en"));
        }
    }
    /**
     * Asigna los textos de los botones e inputs desde el archivo de recursos (Bundle)
     * de acuerdo con el idioma seleccionado.
     *
     * Este metodo establece los textos de los botones de agregar, modificar y eliminar,
     * así como el placeholder del campo de búsqueda, utilizando las claves del archivo de idiomas.
     */
    @FXML
    private void asignarTextosDesdeBundle() {
        agregarButton.setText(bundle.getString("agregar"));
        modificarButton.setText(bundle.getString("modificar"));
        eliminarButton.setText(bundle.getString("eliminar"));
        buscarField.setPromptText(bundle.getString("buscar"));
    }
    /**
     * Cambia el idioma de la aplicación y actualiza los textos en la interfaz.
     *
     * @param nuevaLocale La nueva configuración regional (Locale) que define el idioma a utilizar.
     *
     * Se actualiza el `locale` con el nuevo idioma seleccionado, se carga el archivo de recursos correspondiente,
     * y se recarga la vista para aplicar los cambios visuales en la interfaz de usuario.
     */
    private void cambiarIdioma(Locale nuevaLocale) {
        locale = nuevaLocale;
        bundle = ResourceBundle.getBundle("org.intissar.proyecto2.languages.lang", locale);
        recargarVista();
        asignarTextosDesdeBundle();
    }
    /**
     * Recarga la vista actual para aplicar el idioma seleccionado.
     */
    private void recargarVista() {
        try {
            Node currentNode = alumnosTable.getScene().getRoot();
            Stage stage = (Stage) currentNode.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/intissar/proyecto2/view/alumno_view.fxml"), bundle);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(bundle.getString("titulo"));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Carga la lista de alumnos desde la base de datos y los muestra en la tabla.
     */
    private void cargarAlumnos() {
        try {
            listaAlumnos.setAll(alumnoDAO.obtenerAlumnos());
        } catch (Exception e) {
            logger.error("Error al cargar los libros", e);

            mostrarAlerta("Error", "No se pudieron cargar los alumnos", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    /**
     * Busca un alumno en la base de datos por su DNI y muestra sus datos en los campos de entrada.
     *
     * Se obtiene el valor ingresado en el campo de búsqueda. Si está vacío, se registra un error en el logger
     * y no se realiza ninguna búsqueda. Si el alumno es encontrado en la base de datos, sus datos se cargan
     * en los campos correspondientes. En caso contrario, se muestra una alerta informando que el alumno no fue encontrado.
     */
    @FXML
    private void buscarAlumno() {
        String dniBuscado = buscarField.getText();
        if (dniBuscado.isEmpty()) {
            logger.error("Campo vacio");
            return;
        }

        Optional<Alumno> alumnoEncontrado = alumnoDAO.buscarAlumnoPorDni(dniBuscado);
        if (alumnoEncontrado.isPresent()) {
            Alumno alumno = alumnoEncontrado.get();
            dniField.setText(alumno.getDni());
            nombreField.setText(alumno.getNombre());
            apellido1Field.setText(alumno.getApellido1());
            apellido2Field.setText(alumno.getApellido2());
        } else {
            mostrarAlerta("No encontrado", "Alumno no encontrado", "No se encontró un alumno con el DNI proporcionado.", Alert.AlertType.INFORMATION);
        }
    }

    /**
     * Abre la ventana de ayuda de la aplicación y muestra la sección correspondiente a los alumnos.
     * Se carga el archivo FXML de ayuda y se obtiene el controlador de la vista de ayuda.
     * Luego, se llama al metodo que carga el contenido específico sobre alumnos.
     * Si ocurre un error durante la carga, se registra en el logger y se muestra una alerta informativa.
     */
    @FXML
    private void abrirAyuda() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/intissar/proyecto2/view/AyudaHTML.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la ayuda
            AyudaHTMLController ayudaController = loader.getController();

            // Seleccionar directamente la sección "Alumno"
            ayudaController.cargarAyuda("alumno.html");

            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Ayuda - Alumno");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.error("Error Ayuda", e);
            mostrarAlerta("Error", "No se pudo abrir la ayuda", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Agrega un nuevo alumno a la base de datos y a la lista de la interfaz.
     *
     * Se obtiene la información ingresada en los campos de texto, se crea un objeto Alumno
     * y se registra en la base de datos a través del DAO. Luego, se añade a la lista observable
     * de la tabla y se limpian los campos de entrada.
     */
    @FXML
    private void agregarAlumno() {
        String dni = dniField.getText();
        String nombre = nombreField.getText();
        String apellido1 = apellido1Field.getText();
        String apellido2 = apellido2Field.getText();

        Alumno nuevoAlumno = new Alumno(dni, nombre, apellido1, apellido2);

        try {
            alumnoDAO.registrarAlumno(nuevoAlumno);
            listaAlumnos.add(nuevoAlumno);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo agregar el alumno", e.getMessage(), Alert.AlertType.ERROR);
        }

        limpiarCampos();
    }

    /**
     * Modifica los datos de un alumno seleccionado en la tabla.
     *
     * Si hay un alumno seleccionado, actualiza sus datos con la información de los campos de texto
     * y los guarda en la base de datos a través del DAO. Luego, refresca la tabla para mostrar los cambios.
     * Si no se selecciona un alumno, se muestra una alerta indicando la necesidad de una selección.
     */
    @FXML
    private void modificarAlumno() {
        Alumno seleccionado = alumnosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            String dni = dniField.getText();
            String nombre = nombreField.getText();
            String apellido1 = apellido1Field.getText();
            String apellido2 = apellido2Field.getText();

            seleccionado.setDni(dni);
            seleccionado.setNombre(nombre);
            seleccionado.setApellido1(apellido1);
            seleccionado.setApellido2(apellido2);

            try {
                alumnoDAO.modificarAlumno(seleccionado);
                alumnosTable.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo modificar el alumno", e.getMessage(), Alert.AlertType.ERROR);
            }

            limpiarCampos();
        } else {
            mostrarAlerta("Error", "Selección requerida", "Por favor selecciona un alumno para modificar.", Alert.AlertType.WARNING);
        }
    }

    /**
     * Genera un informe de los alumnos utilizando JasperReports.
     *
     * Se llama al metodo correspondiente en InformesController para generar y visualizar
     * el informe en formato PDF.
     */
    @FXML
    private void generarInforme() {
        InformesController.generarReporteAlumnos();
    }

    /**
     * Elimina un alumno seleccionado de la base de datos y de la lista en la interfaz.
     *
     * Si hay un alumno seleccionado, se muestra una alerta de confirmación antes de eliminarlo.
     * Si el usuario confirma la eliminación, se procede a eliminar el alumno en la base de datos
     * y se remueve de la lista observable de la tabla. Si no hay un alumno seleccionado, se muestra
     * una alerta indicando la necesidad de una selección.
     */
    @FXML
    private void eliminarAlumno() {
        Alumno seleccionado = alumnosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de eliminar este alumno?");
            confirm.setHeaderText("Confirmar eliminación");
            confirm.setTitle("Eliminar Alumno");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        alumnoDAO.eliminarAlumno(seleccionado.getDni());
                        listaAlumnos.remove(seleccionado);
                    } catch (Exception e) {
                        mostrarAlerta("Error", "No se pudo eliminar el alumno", e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            });
        } else {
            mostrarAlerta("Error", "Selección requerida", "Por favor selecciona un alumno para eliminar.", Alert.AlertType.WARNING);
        }
    }


    /**
     * Eliminar campos introducidos
     */
    private void limpiarCampos() {
        dniField.clear();
        nombreField.clear();
        apellido1Field.clear();
        apellido2Field.clear();
    }
    /**
     * Muestra una alerta emergente con un mensaje específico.
     *
     * @param titulo   Título de la alerta.
     * @param encabezado Encabezado de la alerta.
     */
    private void mostrarAlerta(String titulo, String encabezado, String contenido, Alert.AlertType tipoAlerta) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }


    @FXML
    public void irAmenu(ActionEvent actionEvent) {
        try {
            InputStream fxmlStream = getClass().getResourceAsStream("/org/intissar/proyecto2/view/inicio.fxml");
            if (fxmlStream == null) {
                throw new IOException("Archivo FXML no encontrado.");
            }
            Parent root = new FXMLLoader().load(fxmlStream);


            // Cargar el archivo FXML


            // Obtener el Stage actual desde el evento del botón
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Crear la nueva escena
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/intissar/proyecto2/styles/styles.css").toExternalForm());

            // Configurar la nueva escena en el Stage
            stage.setScene(scene);
            stage.setTitle("Menú Principal");
            stage.setResizable(false);
            stage.show();

            logger.info("✅ Menú principal cargado correctamente desde: {}");

        } catch (IOException e) {
            logger.error("❌ Error al cargar la vista FXML: ", e);
        }
    }

}
