package org.intissar.proyecto2.controller;

import net.sf.jasperreports.engine.util.JRLoader;
import java.io.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.intissar.proyecto2.util.DBConnection;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Clase responsable de generar distintos informes relacionados con la gestión de préstamos y libros en la biblioteca.
 * Utiliza JasperReports para la generación de informes y iText para la exportación en PDF.
 */
public class InformesController {

    /**
     * Logger para registrar eventos y errores en la generación de informes.
     */
    private static final Logger logger = LoggerFactory.getLogger(InformesController.class);

    /**
     * Genera un informe sobre los alumnos y los préstamos que han realizado.
     * El informe se carga desde un archivo `.jasper` y se muestra en JasperViewer.
     * También se exporta automáticamente a un archivo PDF.
     */
    public static void generarReporteAlumnos() {
        try {
            // Cargar el archivo .jasper
            String reportPath = "/org/intissar/proyecto2/reports/informe_alumnos.jasper";
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(InformesController.class.getResourceAsStream(reportPath));

            // Obtener conexión a la base de datos
            Connection connection = DBConnection.getConnection();

            // Parámetros del informe
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("TITULO", "Informe de Alumnos y Préstamos");

            // Llenar el informe con los datos de la base de datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            // Mostrar el informe en una ventana
            JasperViewer.viewReport(jasperPrint, false);

            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, "Informe_Alumnos.pdf");

            logger.info("Informe de alumnos generado correctamente.");
        } catch (Exception e) {
            logger.error("Error al generar el informe de alumnos.", e);
        }
    }

    /**
     * Genera un informe sobre los libros disponibles en la biblioteca.
     * Los datos se obtienen de la base de datos y se muestran en un informe JasperReports.
     */
    public static void generarReporteLibros() {
        Connection conexion = null;
        try {
            // Obtener la conexión a la base de datos
            conexion = DBConnection.getConnection();

            // Definir la consulta SQL
            String sql = "SELECT * FROM libros";
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            // Crear un JRResultSetDataSource a partir del ResultSet
            JRResultSetDataSource dataSource = new JRResultSetDataSource(resultSet);

            // Parámetros del informe
            HashMap<String, Object> parametros = new HashMap<>();
            parametros.put("TITULO", "Informe de Libros");

            // Generar el informe
            JasperPrint jasperPrint = JasperFillManager.fillReport("ruta/a/tu/informe.jasper", parametros, dataSource);

            // Mostrar el informe
            JasperViewer.viewReport(jasperPrint, false);
            logger.info("Informe de libros generado correctamente.");

        } catch (Exception e) {
            logger.error("Error al generar el informe de libros.", e);
        } finally {
            if (conexion != null) {
                try {
                    conexion.close();
                    logger.info("Conexión cerrada correctamente.");
                } catch (Exception ex) {
                    logger.error("Error al cerrar la conexión.", ex);
                }
            }
        }
    }

    /**
     * Genera un informe con gráficos estadísticos sobre los préstamos de libros.
     * Utiliza un informe `.jasper` con gráficos de barras, pastel y líneas.
     */
    public static void generarInformeGrafico() {
        try {
            Connection connection = DBConnection.getConnection();

            // Cargar el informe con gráficos
            InputStream reportStream = InformesController.class.getResourceAsStream("/org/intissar/proyecto2/reports/informe_graficos.jasper");
            if (reportStream == null) {
                throw new RuntimeException("No se encontró el archivo 'informe_graficos.jasper'.");
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

            // Parámetros del informe
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("TITULO", "Informe Estadístico de Préstamos");

            // Llenar el informe con los datos de la base de datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            // Mostrar el informe
            JasperViewer.viewReport(jasperPrint, false);

            // Exportar a PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, "Informe_Graficos.pdf");

            logger.info("Informe gráfico generado correctamente.");
        } catch (Exception e) {
            logger.error("Error al generar el informe gráfico.", e);
        }
    }

    /**
     * Genera un informe en PDF con los detalles de un préstamo de libro.
     *
     * @param alumnoNombre  Nombre del alumno
     * @param alumnoDni     DNI del alumno
     * @param libroCodigo   Código del libro
     * @param libroTitulo   Título del libro
     * @param libroAutor    Autor del libro
     * @param libroEditor   Editorial del libro
     * @param libroEstado   Estado actual del libro
     * @param fechaPrestamo Fecha en la que se realizó el préstamo
     */
    public void generarInformePrestamo(String alumnoNombre, String alumnoDni, String libroCodigo, String libroTitulo,
                                       String libroAutor, String libroEditor, String libroEstado, Date fechaPrestamo) {
        Document document = new Document();
        PdfWriter writer = null;

        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream("Informe_Prestamo_" + alumnoDni + ".pdf"));
            document.open();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            // Añadir datos al PDF
            document.add(new Paragraph("Informe de Préstamo", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Alumno: " + alumnoNombre + " (DNI: " + alumnoDni + ")"));
            document.add(new Paragraph("Libro: " + libroTitulo + " (Código: " + libroCodigo + ", Autor: " + libroAutor +
                    ", Editor: " + libroEditor + ", Estado: " + libroEstado + ")"));
            document.add(new Paragraph("Fecha de Préstamo: " + sdf.format(fechaPrestamo)));

            // Calcular la fecha límite de devolución (15 días después)
            Date fechaLimite = new Date(fechaPrestamo.getTime() + (15L * 24 * 60 * 60 * 1000));
            document.add(new Paragraph("Fecha límite de devolución: " + sdf.format(fechaLimite)));

            logger.info("Informe de préstamo generado correctamente.");
        } catch (DocumentException | FileNotFoundException e) {
            logger.error("Error al generar el informe de préstamo.", e);
        } finally {
            if (writer != null) {
                writer.flush();
            }
            document.close();
        }
    }
}
