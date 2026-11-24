package ar.edu.utn.frc.dsi.bolsines.domain.state;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "estado")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_estado", discriminatorType = DiscriminatorType.STRING)
public abstract class EstadoDocumentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    protected EstadoDocumentacion() {}

    protected EstadoDocumentacion(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Consultas polimórficas pertinentes al ciclo de vida de la Documentación
    public boolean esRecibidoYAceptada() { return false; }
    public boolean esNoRecibida() { return false; }
    public boolean esRecibidaYRechazada() { return false; }
    public boolean esParaRedirigir() { return false; }

    public void aceptar(Documentacion documentacion, LocalDateTime fecha, Empleado empleado,
                         List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        throw new UnsupportedOperationException("Transición aceptar no permitida en estado: " + this.nombre);
    }

    public void denegar(Documentacion documentacion, LocalDateTime fecha, Empleado empleado,
                         List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        throw new UnsupportedOperationException("Transición denegar no permitida en estado: " + this.nombre);
    }

    public void rechazar(Documentacion documentacion, LocalDateTime fecha, Empleado empleado,
                          List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        throw new UnsupportedOperationException("Transición rechazar no permitida en estado: " + this.nombre);
    }

    public void redirigir(Documentacion documentacion, LocalDateTime fecha, Empleado empleado,
                           List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        throw new UnsupportedOperationException("Transición redirigir no permitida en estado: " + this.nombre);
    }

    // Ubica el cambio de estado vigente iterando el historial recibido por parámetro y lo cierra.
    protected void cambiarEstadoDoc(LocalDateTime fechaActual, List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        cambiosEstadoDocumentacion.stream()
                .filter(CambioEstadoDocumentacion::sosActual)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay un cambio de estado de documentación vigente"))
                .setFechaHoraFin(fechaActual);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
