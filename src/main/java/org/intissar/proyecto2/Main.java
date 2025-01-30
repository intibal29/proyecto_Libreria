package org.intissar.proyecto2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Clase principal de la aplicación JavaFX que gestiona la navegación entre vistas y el manejo de idiomas.
 * Proporciona métodos para cargar vistas y cambiar dinámicamente el idioma de la interfaz.
 *
 * <p>El idioma predeterminado es español, pero puede cambiarse a inglés mediante la interfaz gráfica.</p>
 *
 * <strong>Nota:</strong> Las vistas FXML y los archivos de recursos deben estar ubicados en los directorios
 * especificados para que se carguen correctamente.
 *
 * @author Intissar
 * @version 1.0
 */
public class Main extends Application {

    private static Stage primaryStage;
    private static Locale locale = new Locale("es"); // Idioma predeterminado (Español)
    private static ResourceBundle bundle;

    /**
     * Metodo principal de JavaFX que se ejecuta al iniciar la aplicación.
     * Establece el idioma inicial y carga la vista principal.
     *
     * @param stage la ventana principal de la aplicación.
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        cambiarIdioma(locale);
        cargarVista("/org/intissar/proyecto2/view/inicio.fxml");
    }

    /**
     * Cambia el idioma de la aplicación y recarga la vista principal.
     *
     * @param nuevaLocale Objeto Locale que define el nuevo idioma.
     */
    public static void cambiarIdioma(Locale nuevaLocale) {
        locale = nuevaLocale;
        bundle = ResourceBundle.getBundle("org.intissar.proyecto2.languages.lang", locale);
        recargarVista();
    }

    /**
     * Carga una vista FXML específica en la ventana principal.
     *
     * @param rutaFXML Ruta relativa del archivo FXML.
     */
    public static void cargarVista(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(rutaFXML), bundle);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/org/intissar/proyecto2/styles/styles.css")).toExternalForm());

            primaryStage.setTitle(bundle.getString("titulo"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recarga la vista principal para aplicar cambios en el idioma.
     */
    private static void recargarVista() {
        cargarVista("/org/intissar/proyecto2/view/inicio.fxml");
    }

    /**
     * Maneja la acción para mostrar la vista de alumnos.
     *
     * @param event Evento de acción de JavaFX.
     */
    public void handleAlumno(ActionEvent event) {
        cargarVista("/org/intissar/proyecto2/view/alumno_view.fxml");
    }

    /**
     * Maneja la acción para mostrar la vista del histórico.
     *
     * @param event Evento de acción de JavaFX.
     */
    public void handleHistorico(ActionEvent event) {
        cargarVista("/org/intissar/proyecto2/view/historico_view.fxml");
    }

    /**
     * Maneja la acción para mostrar la vista de libros.
     *
     * @param event Evento de acción de JavaFX.
     */
    public void handleLibro(ActionEvent event) {
        cargarVista("/org/intissar/proyecto2/view/libro_view.fxml");
    }

    /**
     * Maneja la acción para mostrar la vista de préstamos.
     *
     * @param event Evento de acción de JavaFX.
     */
    public void handlePrestamo(ActionEvent event) {
        cargarVista("/org/intissar/proyecto2/view/prestamo_view.fxml");
    }

    @FXML
    private ComboBox<String> idiomaComboBox;

    /**
     * Metodo de inicialización para configurar el ComboBox de selección de idiomas.
     */
    @FXML
    public void initialize() {
        idiomaComboBox.getItems().addAll("Español", "English");
        idiomaComboBox.setValue(locale.getLanguage().equals("en") ? "English" : "Español");
        idiomaComboBox.setOnAction(event -> cambiarIdiomaSeleccionado());
    }

    /**
     * Cambia el idioma según la selección del usuario en el ComboBox.
     */
    private void cambiarIdiomaSeleccionado() {
        String seleccion = idiomaComboBox.getValue();
        Locale nuevoIdioma = seleccion.equals("English") ? new Locale("en") : new Locale("es");
        cambiarIdioma(nuevoIdioma);
    }

    /**
     * Metodo principal de la aplicación.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
