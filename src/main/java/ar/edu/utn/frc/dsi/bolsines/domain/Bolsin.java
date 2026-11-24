package ar.edu.utn.frc.dsi.bolsines.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoBolsin;
import ar.edu.utn.frc.dsi.bolsines.domain.state.bolsin.Enviado;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

@Entity
public class Bolsin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;
    private String numeroPrecinto;
    private LocalDate fecha;
    private Integer numeroBolsin;
    private Double peso;

    @ManyToOne(optional = false)
    private ComisionMedica origen;

    @ManyToOne(optional = false)
    private ComisionMedica destino;

    // Referencia directa al estado activo (field state: State)
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private EstadoBolsin estado;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bolsin_id")
    private List<Remito> remitos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bolsin_id")
    private List<CambioEstadoBolsin> cambioEstadoBolsin = new ArrayList<>();

    protected Bolsin() {}

    // Constructor: inicializa el estado pasándose a sí mismo (this.state = new Enviado(this))
    public Bolsin(String numeroPrecinto, LocalDate fecha, Integer numeroBolsin, Double peso,
                  ComisionMedica origen, ComisionMedica destino, LocalDateTime fechaInicio) {
        this.numeroPrecinto = numeroPrecinto;
        this.fecha = fecha;
        this.numeroBolsin = numeroBolsin;
        this.peso = peso;
        this.origen = origen;
        this.destino = destino;

        EstadoBolsin estadoInicial = new Enviado();
        this.estado = estadoInicial;
        this.cambioEstadoBolsin.add(new CambioEstadoBolsin(fechaInicio, estadoInicial));
    }

    // Equivalente a changeState(state: State) / setEstado(estado: Estado)
    public void setEstado(EstadoBolsin nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void changeState(EstadoBolsin nuevoEstado) {
        setEstado(nuevoEstado);
    }

    // Delega en el estado activo, pasándole explícitamente el contexto que necesita
    // (el propio bolsín, sus remitos y el arreglo de cambios de estado a iterar).
    public void recibirYAceptarBolsin(LocalDateTime fechaActual, Empleado empleado,
                                      java.util.Map<Long, OpcionRecepcion> opciones) {
        getEstadoActual().recibirYAceptarBolsin(this, fechaActual, empleado, this.remitos, this.cambioEstadoBolsin, opciones);
    }

    /** Carga una secuencia de estados de demostración manteniendo el estado activo y su historial alineados. */
    public void cargarHistorialDemo(List<EstadoBolsin> estados, List<LocalDateTime> fechas, Empleado empleado) {
        if (estados == null || fechas == null || estados.isEmpty() || estados.size() != fechas.size()) {
            throw new IllegalArgumentException("El historial de demostración requiere estados y fechas equivalentes");
        }
        this.cambioEstadoBolsin.clear();
        for (int i = 0; i < estados.size(); i++) {
            EstadoBolsin estado = estados.get(i);
            CambioEstadoBolsin cambio = new CambioEstadoBolsin(fechas.get(i), estado, empleado);
            if (i + 1 < fechas.size()) cambio.setFechaHoraFin(fechas.get(i + 1));
            this.cambioEstadoBolsin.add(cambio);
        }
        setEstado(estados.get(estados.size() - 1));
    }

    public boolean esTuCmDestino(ComisionMedica cm) {
        return this.destino != null && cm != null && this.destino.getId().equals(cm.getId());
    }

    public boolean sosEstadoEnviado() {
        return this.estado != null && this.estado.esEnviado();
    }

    public EstadoBolsin getEstadoActual() {
        if (this.estado == null) throw new IllegalStateException("Falta el estado activo de la entidad");
        return this.estado;
    }

    public Long getId() { return id; }
    public String getNumeroPrecinto() { return numeroPrecinto; }
    public LocalDate getFecha() { return fecha; }
    public Integer getNumeroBolsin() { return numeroBolsin; }
    public Double getPeso() { return peso; }
    public ComisionMedica getCMOrigen() { return origen; }
    public ComisionMedica getCMDestino() { return destino; }
    public List<Remito> getRemitos() { return remitos; }
    public List<CambioEstadoBolsin> getCambioEstadoBolsin() { return cambioEstadoBolsin; }
}
