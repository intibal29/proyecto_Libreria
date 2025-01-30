module org.intissar.proyecto2 {
    // Módulos requeridos por JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    // Módulos requeridos para funcionalidades adicionales
    requires java.desktop;
    requires java.sql;
    requires itextpdf;
    requires org.slf4j;
    requires jasperreports; // Módulo de SLF4J para logs
   // requires org.jfree.chart; // Módulo para gráficos con JFreeChart
    opens org.intissar.proyecto2 to javafx.fxml; // Esto permite que JavaFX acceda a los controladores

    // Exporta los paquetes necesarios para que puedan ser accedidos por otros módulos
    exports org.intissar.proyecto2;
    exports org.intissar.proyecto2.controller;

    // Abre paquetes a módulos específicos para funcionalidades de JavaFX
    opens org.intissar.proyecto2.controller to javafx.fxml;
    opens org.intissar.proyecto2.model to javafx.base;
}
