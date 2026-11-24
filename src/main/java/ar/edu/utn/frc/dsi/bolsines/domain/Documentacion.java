package ar.edu.utn.frc.dsi.bolsines.domain;

import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoDocumentacion;
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
    private EstadoDocumentacion estado;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "documentacion_id")
    private List<CambioEstadoDocumentacion> cambioEstadoDocumentacion = new ArrayList<>();

    protected Documentacion() {}

    // Constructor: inicializa el estado inicial de la documentación
    public Documentacion(String asunto, LocalDate fechaPase, Integer numero,
                         TipoDocumentacion tipoDocumentacion, LocalDateTime fechaInicio) {
        this.asunto = asunto;
        this.fechaPase = fechaPase;
        this.numero = numero;
        this.tipoDocumentacion = tipoDocumentacion;

        EstadoDocumentacion estadoInicial = new EnBolsinEnviadoDoc();
        this.estado = estadoInicial;
        this.cambioEstadoDocumentacion.add(new CambioEstadoDocumentacion(fechaInicio, estadoInicial));
    }

    // Equivalente a setEstado(estado: Estado)
    public void setEstado(EstadoDocumentacion nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void agregarCambioEstado(CambioEstadoDocumentacion cambioEstado) {
        this.cambioEstadoDocumentacion.add(cambioEstado);
    }

    // Delega en el estado activo según la Máquina de Estados de Documentación,
    // pasándole explícitamente la documentación y el arreglo de cambios de estado a iterar.
    public void aceptar(LocalDateTime fechaActual, Empleado nombre) {
        getEstadoActual().aceptar(this, fechaActual, nombre, this.cambioEstadoDocumentacion);
    }

    public void denegar(LocalDateTime fechaActual, Empleado nombre) {
        getEstadoActual().denegar(this, fechaActual, nombre, this.cambioEstadoDocumentacion);
    }

    public void rechazar(LocalDateTime fechaActual, Empleado nombre) {
        getEstadoActual().rechazar(this, fechaActual, nombre, this.cambioEstadoDocumentacion);
    }

    public void redirigir(LocalDateTime fechaActual, Empleado nombre) {
        getEstadoActual().redirigir(this, fechaActual, nombre, this.cambioEstadoDocumentacion);
    }

    public EstadoDocumentacion getEstadoActual() {
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
