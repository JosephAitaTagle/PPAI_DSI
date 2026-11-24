package ar.edu.utn.frc.dsi.bolsines.domain;

import java.time.LocalDateTime;

import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoBolsin;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CambioEstadoBolsin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    private EstadoBolsin estado;

    @ManyToOne
    private Empleado empleado;

    protected CambioEstadoBolsin() {}
    public CambioEstadoBolsin(LocalDateTime fechaHoraInicio, EstadoBolsin estado) {
        this(fechaHoraInicio, estado, null);
    }

    public CambioEstadoBolsin(LocalDateTime fechaHoraInicio, EstadoBolsin estado, Empleado empleado) {
        this.empleado = empleado;
        this.fechaHoraInicio = fechaHoraInicio;
        this.estado = estado;
    }

    public boolean sosActual() {
        return this.fechaHoraFin == null;
    }

    public boolean sosEnviado() {
        return this.estado != null && this.estado.esEnviado();
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public Empleado getEmpleado() { return empleado; }
    public Long getId() { return id; }
    public EstadoBolsin getEstado() { return estado; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
}
