package ar.edu.utn.frc.dsi.bolsines.domain.state;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "estado")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_estado", discriminatorType = DiscriminatorType.STRING)
public abstract class EstadoBolsin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    protected EstadoBolsin() {}

    protected EstadoBolsin(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Consultas polimórficas pertinentes al ciclo de vida del Bolsín
    public boolean esEnviado() { return false; }
    public boolean esRecibido() { return false; }

    // Transición del CU 28: recibir y aceptar el bolsín en la Comisión Médica destino.
    // Recibe explícitamente el contexto (bolsín, remitos y su historial de cambios de estado)
    // en lugar de depender de una referencia inversa guardada en el propio estado.
    public void recibirYAceptarBolsin(Bolsin bolsin, LocalDateTime fecha, Empleado empleado,
                                       List<Remito> remitos, List<CambioEstadoBolsin> cambiosEstadoBolsin,
                                       Map<Long, OpcionRecepcion> opciones) {
        throw new UnsupportedOperationException("Transición recibirYAceptarBolsin no permitida en estado: " + this.nombre);
    }

    // Itera el historial recibido por parámetro para ubicar el cambio de estado vigente.
    protected CambioEstadoBolsin obtenerCambioActual(List<CambioEstadoBolsin> cambiosEstadoBolsin) {
        return cambiosEstadoBolsin.stream()
                .filter(CambioEstadoBolsin::sosActual)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay un cambio de estado de bolsín vigente"));
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
