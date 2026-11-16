package ar.edu.utn.frc.dsi.bolsines.domain.state;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "estado")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_estado", discriminatorType = DiscriminatorType.STRING)
public abstract class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String ambito;

    // Referencias inversas protegidas hacia los 3 contextos (Bolsin, Remito, Documentacion)
    @Transient
    protected Bolsin bolsin;

    @Transient
    protected Remito remito;

    @Transient
    protected Documentacion documentacion;

    protected Estado() {}

    protected Estado(String nombre, String descripcion, String ambito) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ambito = ambito;
    }

    protected Estado(String nombre, String descripcion, String ambito, Bolsin bolsin) {
        this(nombre, descripcion, ambito);
        this.bolsin = bolsin;
    }

    protected Estado(String nombre, String descripcion, String ambito, Remito remito) {
        this(nombre, descripcion, ambito);
        this.remito = remito;
    }

    protected Estado(String nombre, String descripcion, String ambito, Documentacion documentacion) {
        this(nombre, descripcion, ambito);
        this.documentacion = documentacion;
    }

    // Vinculación de referencia inversa (usada en setEstado / changeState / @PostLoad)
    public void vincularBolsin(Bolsin bolsin) { this.bolsin = bolsin; }
    public void vincularRemito(Remito remito) { this.remito = remito; }
    public void vincularDocumentacion(Documentacion documentacion) { this.documentacion = documentacion; }

    // Consultas polimórficas de estado y ámbito (Diagrama de Clases Rediseño v2.1)
    public boolean esEnviado() { return false; }
    public boolean esRecibido() { return false; }
    public boolean esRecibidoYAceptado() { return false; }
    public boolean esRecibidoYAceptadoParcial() { return false; }
    public boolean esRecibidoYAceptada() { return false; }
    public boolean esNoRecibida() { return false; }
    public boolean esRecibidaYRechazada() { return false; }
    public boolean esParaRedirigir() { return false; }

    public boolean esAmbitoBolsin() { return "Bolsin".equalsIgnoreCase(this.ambito); }
    public boolean esAmbitoRemito() { return "Remito".equalsIgnoreCase(this.ambito); }
    public boolean esAmbitoDocumentacion() { return "Documentacion".equalsIgnoreCase(this.ambito); }

    // Operaciones del ciclo de vida del CU 28 (operan sobre la referencia inversa)
    public void recibirYAceptarBolsin(LocalDateTime fechaActual, Empleado nombre, java.util.Map<Long, OpcionRecepcion> opciones) {
        throw new UnsupportedOperationException("Transición recibirYAceptarBolsin no permitida en estado: " + this.nombre);
    }

    public void recibirYAceptar(LocalDateTime fechaActual, Empleado nombre, java.util.Map<Long, OpcionRecepcion> opciones) {
        throw new UnsupportedOperationException("Transición recibirYAceptar no permitida en estado: " + this.nombre);
    }

    public void aceptar(LocalDateTime fechaActual, Empleado nombre) {
        throw new UnsupportedOperationException("Transición aceptar no permitida en estado: " + this.nombre);
    }

    public void denegar(LocalDateTime fechaActual, Empleado nombre) {
        throw new UnsupportedOperationException("Transición denegar no permitida en estado: " + this.nombre);
    }

    public void rechazar(LocalDateTime fechaActual, Empleado nombre) {
        throw new UnsupportedOperationException("Transición rechazar no permitida en estado: " + this.nombre);
    }

    public void redirigir(LocalDateTime fechaActual, Empleado nombre) {
        throw new UnsupportedOperationException("Transición redirigir no permitida en estado: " + this.nombre);
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getAmbito() { return ambito; }
    public void setAmbito(String ambito) { this.ambito = ambito; }
}
