Gestión de Préstamos de Libros
Descripción del proyecto
Este proyecto tiene como objetivo la creación de una aplicación para la gestión de préstamos de libros en una biblioteca o centro de lectura. La aplicación permite la gestión de alumnos, libros y préstamos, con funcionalidades para altas, bajas, modificaciones, consultas y devoluciones. Además, ofrece la generación de informes en formato PDF, gráficos de análisis y una interfaz multilingüe.

Funcionalidades
1. Gestión de Alumnos
Altas : Registro de nuevos alumnos.
Modificaciones : Edición de datos de alumnos.
Consultas : Búsqueda y visualización de los datos de los alumnos registrados.
2. Gestión de libros
Altas : Registro de nuevos libros en el sistema.
Bajas : Eliminación de libros (marcando su estado como "baja" en lugar de eliminar completamente).
Modificaciones : Actualización de los detalles de los libros.
Consultas : Búsqueda y visualización de los libros registrados.
Estados de los libros :

Nuevo
Usado nuevo
Usado seminuevo
Usado estropeado
Restaurado
3. Gestión de Préstamos
Altas : Registro de nuevos préstamos de libros a alumnos.
Consultas : Ver los libros prestados y sus detalles.
Restricción : No es posible prestar un libro que ya esté prestado.

4. Gestión de devoluciones
Alta en Histórico de Préstamos : Registro de la devolución en el historial.
Modificación del Estado del Libro : Actualización del estado del libro cuando se devuelve.
Baja de la Tabla de Préstamos : El libro se elimina de los préstamos activos y pasa a la tabla de históricos de préstamos.
5. Consulta de Histórico de Préstamos
Consultas con múltiples filtros para consultar el historial de préstamos.
6. Multidioma
La aplicación soporta varios idiomas. El idioma se selecciona a través de un parámetro leído desde un archivo de configuración.
7. Generación de Informes
La aplicación genera informes automáticos en PDF y gráficos para facilitar la gestión de formato de los préstamos y libros:

Informe 1 : Al alta de un préstamo, se genera un PDF con los siguientes datos:

Alumno (nombre, apellidos, DNI)
Libro (código, título, autor, editor y estado)
Fecha de préstamo
Fecha límite de devolución (15 días después de la fecha de préstamo)
Informe 2 : Listado de libros, incluyendo un subinforme con los préstamos históricos de cada libro.

Informe 3 : Informe con Múltiples subinformes, cada uno representado por un gráfico (por ejemplo, gráfico de barras con la frecuencia de préstamos de cada libro).

Informe 4 : Informe de alumnos con cálculos adicionales, como:

Número de libros prestados por cada alumno.
Días de préstamo de cada libro.
8. Ayuda y asistencia
La aplicación incluye ayudas interactivas:
Información sobre herramientas : Información adicional sobre los elementos de la interfaz.
Marcadores de posición : Sugerencias de entrada en formularios.
Guía rápida en formato HTML : Manual de uso accesible desde el menú principal de la aplicación.
Requisitos del Sistema
Para ejecutar la aplicación, se requiere tener instalados los siguientes programas y herramientas:

Lenguaje de programación : Python / Java / C# (dependiendo de la implementación)
Base de datos : MySQL / PostgreSQL / SQLite (según elección)
Librerías de generación de PDF : FPDF / ReportLab (para los informes)
Librerías de gráficos : Matplotlib / Plotly (para los gráficos)
Soporte para Multidioma : Archivos de configuración en formato .jsono.yaml
Uso de la aplicación
Gestión de Alumnos : Accede a la sección de "Alumnos" desde el menú principal para añadir, editar o consultar alumnos.

Gestión de Libros : Ingrese a la sección "Libros" para agregar nuevos libros, editar o dar baja libros.

Gestión de Préstamos : En "Préstamos", podrás registrar nuevos préstamos y consultar los préstamos actuales.

Gestión de Devoluciones : Desde "Devoluciones", podrás consultar los préstamos activos y registrar las devoluciones.

Generación de Informes : En el menú de informes, podrás generar los distintos informes en formato PDF y gráficos.
