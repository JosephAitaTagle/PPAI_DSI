package ar.edu.utn.frc.dsi.bolsines.domain.state;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    public void recibirYAceptarBolsin(Bolsin bolsin, LocalDateTime fechaActual, Empleado nombre,
                                       List<Remito> remito, List<CambioEstadoBolsin> cambioEstadoBolsin) {
        throw new UnsupportedOperationException("Transición recibirYAceptarBolsin no permitida en estado: " + this.nombre);
    }

    // Ubica el cambio de estado vigente iterando el historial recibido por parámetro y lo cierra.
    protected void cambiarEstadoBolsin(LocalDateTime fechaActual, List<CambioEstadoBolsin> cambioEstadoBolsin) {
        cambioEstadoBolsin.stream()
                .filter(CambioEstadoBolsin::sosActual)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay un cambio de estado de bolsín vigente"))
                .setFechaHoraFin(fechaActual);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
