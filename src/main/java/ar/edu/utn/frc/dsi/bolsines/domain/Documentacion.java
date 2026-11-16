package ar.edu.utn.frc.dsi.bolsines.domain;

import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import ar.edu.utn.frc.dsi.bolsines.domain.state.doc.EnBolsinEnviadoDoc;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Documentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;
    private String asunto;
    private LocalDate fechaPase;
    private Integer numero;

    @ManyToOne(optional = false)
    private TipoDocumentacion tipoDocumentacion;

    // Referencia directa al estado activo (field state: State)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Estado estado;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "documentacion_id")
    private List<CambioEstadoDocumentacion> cambioEstadoDocumentacion = new ArrayList<>();

    protected Documentacion() {}

    // Constructor: inicializa el estado pasándose a sí mismo (this)
    public Documentacion(String asunto, LocalDate fechaPase, Integer numero,
                         TipoDocumentacion tipoDocumentacion, LocalDateTime fechaInicio) {
        this.asunto = asunto;
        this.fechaPase = fechaPase;
        this.numero = numero;
        this.tipoDocumentacion = tipoDocumentacion;

        Estado estadoInicial = new EnBolsinEnviadoDoc(this);
        this.estado = estadoInicial;
        this.cambioEstadoDocumentacion.add(new CambioEstadoDocumentacion(fechaInicio, estadoInicial));
    }

    @PostLoad
    private void reconstruirReferenciaInversa() {
        if (this.estado != null) {
            this.estado.vincularDocumentacion(this);
        }
    }

    // Equivalente a changeState(state: State) / setEstado(estado: Estado)
    public void setEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
        if (this.estado != null) {
            this.estado.vincularDocumentacion(this);
        }
    }

    public void changeState(Estado nuevoEstado) {
        setEstado(nuevoEstado);
    }

    // Delegación pura al estado activo según la Máquina de Estados de Documentación
    public void aceptar(LocalDateTime fechaActual, Empleado nombre) {
        Estado actual = getEstadoActual();
        actual.vincularDocumentacion(this);
        actual.aceptar(fechaActual, nombre);
    }

    public void denegar(LocalDateTime fechaActual, Empleado nombre) {
        Estado actual = getEstadoActual();
        actual.vincularDocumentacion(this);
        actual.denegar(fechaActual, nombre);
    }

    public void rechazar(LocalDateTime fechaActual, Empleado nombre) {
        Estado actual = getEstadoActual();
        actual.vincularDocumentacion(this);
        actual.rechazar(fechaActual, nombre);
    }

    public void redirigir(LocalDateTime fechaActual, Empleado nombre) {
        Estado actual = getEstadoActual();
        actual.vincularDocumentacion(this);
        actual.redirigir(fechaActual, nombre);
    }

    // Métodos de servicio del contexto invocados por los estados concretos
    public void cerrarCambioEstadoActual(LocalDateTime fechaActual) {
        this.cambioEstadoDocumentacion.stream()
                .filter(CambioEstadoDocumentacion::sosActual)
                .findFirst()
                .ifPresent(actual -> actual.setFechaHoraFin(fechaActual));
    }

    public void agregarCambioEstado(CambioEstadoDocumentacion cambioEstado) {
        this.cambioEstadoDocumentacion.add(cambioEstado);
    }

    public Estado getEstadoActual() {
        if (this.estado == null) throw new IllegalStateException("Falta el estado activo de la entidad");
        return this.estado;
    }

    public Long getId() { return id; }
    public String getAsunto() { return asunto; }
    public LocalDate getFechaPase() { return fechaPase; }
    public Integer getNumero() { return numero; }
    public TipoDocumentacion getTipoDoc() { return tipoDocumentacion; }
    public List<CambioEstadoDocumentacion> getCambioEstadoDocumentacion() { return cambioEstadoDocumentacion; }
}
