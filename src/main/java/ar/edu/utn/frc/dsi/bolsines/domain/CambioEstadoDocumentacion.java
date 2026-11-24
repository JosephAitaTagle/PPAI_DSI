package ar.edu.utn.frc.dsi.bolsines.domain;

import java.time.LocalDateTime;

import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoDocumentacion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CambioEstadoDocumentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    private EstadoDocumentacion estado;

    @ManyToOne
    private Empleado empleado;

    protected CambioEstadoDocumentacion() {}
    public CambioEstadoDocumentacion(LocalDateTime fechaHoraInicio, EstadoDocumentacion estado) {
        this(fechaHoraInicio, estado, null);
    }

    public CambioEstadoDocumentacion(LocalDateTime fechaHoraInicio, EstadoDocumentacion estado, Empleado empleado) {
        this.empleado = empleado;
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = estado;
    }

    public boolean sosActual() { return this.fechaHoraFin == null; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
    public Empleado getEmpleado() { return empleado; }
    public EstadoDocumentacion getEstado() { return estado; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
}
