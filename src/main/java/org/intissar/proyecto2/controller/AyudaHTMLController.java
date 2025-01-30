package org.intissar.proyecto2.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.URL;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador para la ventana de ayuda en HTML.
 */
public class AyudaHTMLController implements Initializable {
    /**
     * Logger a usar
     */
    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    @FXML
    private WebView visor;

    private WebEngine webEngine;

    private static String archivoAyuda = "index.html"; // Archivo predeterminado

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        webEngine = visor.getEngine();
        cargarAyuda(archivoAyuda);
    }

    /**
     * Carga la ayuda en el visor WebView.
     *
     * @param archivo Nombre del archivo HTML a cargar.
     */
    public void cargarAyuda(String archivo) {
        URL url = getClass().getResource("/org/intissar/proyecto2/help/html/" + archivo);
        if (url != null) {
            webEngine.load(url.toExternalForm());
        } else {
            logger.error("ERROR: No se encontró el archivo de ayuda");
        }
    }

    /**
     * Metodo estático para cambiar el archivo de ayuda antes de abrir la ventana.
     *
     * @param archivo Nombre del archivo HTML.
     */
    public static void setArchivoAyuda(String archivo) {
        archivoAyuda = archivo;
    }
}
