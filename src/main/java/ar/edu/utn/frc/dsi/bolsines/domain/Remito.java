package ar.edu.utn.frc.dsi.bolsines.domain;

import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
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
    private Estado estado;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "remito_id")
    private List<DetalleRemito> detalleRemito = new ArrayList<>();

    protected Remito() {}

    // Constructor: inicializa el estado pasándose a sí mismo (this)
    public Remito(LocalDate fecha, Integer numero) {
        this.fecha = fecha;
        this.numero = numero;
        this.estado = new EnBolsinEnviadoRemito(this);
    }

    @PostLoad
    private void reconstruirReferenciaInversa() {
        if (this.estado != null) {
            this.estado.vincularRemito(this);
        }
    }

    // Equivalente a changeState(state: State) / setEstado(estado: Estado)
    public void setEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
        if (this.estado != null) {
            this.estado.vincularRemito(this);
        }
    }

    public void changeState(Estado nuevoEstado) {
        setEstado(nuevoEstado);
    }

    // Delegación pura al estado activo
    public void recibir(LocalDateTime fechaActual, Empleado nombre, java.util.Map<Long, OpcionRecepcion> opciones) {
        this.estado.vincularRemito(this);
        this.estado.recibirYAceptar(fechaActual, nombre, opciones);
    }

    public Integer getNumero() { return numero; }
    public LocalDate getFecha() { return fecha; }
    public Estado getEstado() { return estado; }
    public List<DetalleRemito> getDetalleRemito() { return detalleRemito; }
}
