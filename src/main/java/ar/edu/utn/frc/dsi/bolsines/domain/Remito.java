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

    // Equivalente a setEstado(estado: Estado)
    public void setEstado(EstadoRemito nuevoEstado) {
        this.estado = nuevoEstado;
    }

    // Decide, según la opción ya registrada en cada detalle, si el remito se recibió completo o no,
    // y delega en el estado activo pasándole el remito y su detalle.
    public void recibirYAceptar(LocalDateTime fechaActual, Empleado nombre) {
        boolean contenidoCompleto = this.detalleRemito.stream()
                .allMatch(detalle -> detalle.getOpcionRecepcion() == OpcionRecepcion.ACEPTAR);
        if (contenidoCompleto) {
            this.estado.recibirYAceptar(this, fechaActual, nombre, this.detalleRemito);
        } else {
            this.estado.recibirYAceptarParcialmente(this, fechaActual, nombre, this.detalleRemito);
        }
    }

    public Long getId() { return id; }
    public Integer getNumero() { return numero; }
    public LocalDate getFecha() { return fecha; }
    public EstadoRemito getEstado() { return estado; }
    public List<DetalleRemito> getDetalleRemito() { return detalleRemito; }
}
