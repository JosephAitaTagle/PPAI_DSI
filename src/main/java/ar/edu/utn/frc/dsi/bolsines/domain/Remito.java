package ar.edu.utn.frc.dsi.bolsines.domain;

import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoRemito;
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

    // Constructor: inicializa el estado inicial del remito
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

    // Delega en el estado activo, pasándole explícitamente el remito como contexto
    public void recibir(LocalDateTime fechaActual, Empleado nombre, java.util.Map<Long, OpcionRecepcion> opciones) {
        boolean contenidoCompleto = this.detalleRemito.stream()
                .allMatch(detalle -> opciones.get(detalle.getDocumentacion().getId()) == OpcionRecepcion.ACEPTAR);
        if (contenidoCompleto) {
            this.estado.recibirYAceptar(this, fechaActual, nombre, opciones);
        } else {
            this.estado.recibirYAceptarParcialmente(this, fechaActual, nombre, opciones);
        }
    }

    public Long getId() { return id; }
    public Integer getNumero() { return numero; }
    public LocalDate getFecha() { return fecha; }
    public EstadoRemito getEstado() { return estado; }
    public List<DetalleRemito> getDetalleRemito() { return detalleRemito; }
}
