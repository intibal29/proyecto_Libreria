package org.intissar.proyecto2.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class HistoricoPrestamo {
    private final IntegerProperty idPrestamo;
    private final StringProperty dniAlumno;
    private final IntegerProperty codigoLibro;
    private final ObjectProperty<LocalDate> fechaPrestamo;
    private final ObjectProperty<LocalDate> fechaDevolucion;

    public HistoricoPrestamo(int idPrestamo, String dniAlumno, int codigoLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        this.idPrestamo = new SimpleIntegerProperty(idPrestamo);
        this.dniAlumno = new SimpleStringProperty(dniAlumno);
        this.codigoLibro = new SimpleIntegerProperty(codigoLibro);
        this.fechaPrestamo = new SimpleObjectProperty<>(fechaPrestamo);
        this.fechaDevolucion = new SimpleObjectProperty<>(fechaDevolucion);
    }

    public int getIdPrestamo() { return idPrestamo.get(); }
    public String getDniAlumno() { return dniAlumno.get(); }
    public int getCodigoLibro() { return codigoLibro.get(); }
    public LocalDate getFechaPrestamo() { return fechaPrestamo.get(); }
    public LocalDate getFechaDevolucion() { return fechaDevolucion.get(); }

    public IntegerProperty idPrestamoProperty() { return idPrestamo; }
    public StringProperty dniAlumnoProperty() { return dniAlumno; }
    public IntegerProperty codigoLibroProperty() { return codigoLibro; }
    public ObjectProperty<LocalDate> fechaPrestamoProperty() { return fechaPrestamo; }
    public ObjectProperty<LocalDate> fechaDevolucionProperty() { return fechaDevolucion; }

    @Override
    public String toString() {
        return "HistoricoPrestamo{" +
                "idPrestamo=" + idPrestamo.get() +
                ", dniAlumno='" + dniAlumno.get() + '\'' +
                ", codigoLibro=" + codigoLibro.get() +
                ", fechaPrestamo=" + fechaPrestamo.get() +
                ", fechaDevolucion=" + fechaDevolucion.get() +
                '}';
    }
}
