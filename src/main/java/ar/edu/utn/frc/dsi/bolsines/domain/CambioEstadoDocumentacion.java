package ar.edu.utn.frc.dsi.bolsines.domain;

import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CambioEstadoDocumentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    private Estado estado;

    @ManyToOne
    private Empleado empleado;

    protected CambioEstadoDocumentacion() {}
    public CambioEstadoDocumentacion(LocalDateTime fechaHoraInicio, Estado estado) {
        this(fechaHoraInicio, estado, null);
    }

    public CambioEstadoDocumentacion(LocalDateTime fechaHoraInicio, Estado estado, Empleado empleado) {
        this.empleado = empleado;
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = estado;
    }

    public boolean sosActual() { return this.fechaHoraFin == null; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
    public Empleado getEmpleado() { return empleado; }
    public Estado getEstado() { return estado; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
}
