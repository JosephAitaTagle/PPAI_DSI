package ar.edu.utn.frc.dsi.bolsines.domain;

import ar.edu.utn.frc.dsi.bolsines.domain.state.remito.EstadoRemito;
import ar.edu.utn.frc.dsi.bolsines.domain.state.remito.EnBolsinEnviadoRemito;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Remito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private Integer numero;

    // Referencia directa al estado activo (field state: State)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    private EstadoRemito estado;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "remito_id")
    private List<DetalleRemito> detalleRemito = new ArrayList<>();

    protected Remito() {}

    // Constructor: inicializa el estado pasándose a sí mismo (this)
    public Remito(LocalDate fecha, Integer numero) {
        this.fecha = fecha;
        this.numero = numero;
        this.estado = new EnBolsinEnviadoRemito();
    }

    // Equivalente a changeState(state: State) / setEstado(estado: Estado)
    public void setEstado(EstadoRemito nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void changeState(EstadoRemito nuevoEstado) {
        setEstado(nuevoEstado);
    }

    // Delegación pura al estado activo
    public void recibirYAceptar(LocalDateTime fechaActual, Empleado nombre,
                                java.util.Map<Long, OpcionRecepcion> opciones) {
        boolean contenidoCompleto = this.detalleRemito.stream()
                .allMatch(detalle -> opciones.get(detalle.getDocumentacion().getId()) == OpcionRecepcion.ACEPTAR);
        DetalleRemito[] detalles = this.detalleRemito.toArray(DetalleRemito[]::new);
        if (contenidoCompleto) {
            this.estado.recibirYAceptar(this, fechaActual, nombre, detalles, opciones);
        } else {
            this.estado.recibirYAceptarParcialmente(this, fechaActual, nombre, detalles, opciones);
        }
    }

    public Long getId() { return id; }
    public Integer getNumero() { return numero; }
    public LocalDate getFecha() { return fecha; }
    public EstadoRemito getEstado() { return estado; }
    public List<DetalleRemito> getDetalleRemito() { return detalleRemito; }
}
