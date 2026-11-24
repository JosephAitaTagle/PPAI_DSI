package ar.edu.utn.frc.dsi.bolsines.domain.state;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "estado")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_estado", discriminatorType = DiscriminatorType.STRING)
public abstract class EstadoRemito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    protected EstadoRemito() {}

    protected EstadoRemito(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Consultas polimórficas pertinentes al ciclo de vida del Remito
    public boolean esRecibidoYAceptado() { return false; }
    public boolean esRecibidoYAceptadoParcial() { return false; }
    public boolean esRecibidoYRechazado() { return false; }

    public void recibirYAceptar(Remito remito, LocalDateTime fecha, Empleado empleado,
                                 Map<Long, OpcionRecepcion> opciones) {
        throw new UnsupportedOperationException("Transición recibirYAceptar no permitida en estado: " + this.nombre);
    }

    public void recibirYAceptarParcialmente(Remito remito, LocalDateTime fecha, Empleado empleado,
                                             Map<Long, OpcionRecepcion> opciones) {
        throw new UnsupportedOperationException("Transición recibirYAceptarParcialmente no permitida en estado: " + this.nombre);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
