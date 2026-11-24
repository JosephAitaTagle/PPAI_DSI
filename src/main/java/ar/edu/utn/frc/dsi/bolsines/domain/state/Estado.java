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

    protected Estado() {}

    protected Estado(String nombre, String descripcion, String ambito) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ambito = ambito;
    }

    // Consultas polimórficas de estado y ámbito (Diagrama de Clases Rediseño v2.1)
    public boolean esEnviado() { return false; }
    public boolean esRecibido() { return false; }
    public boolean esRecibidoYAceptado() { return false; }
    public boolean esRecibidoYAceptadoParcial() { return false; }
    public boolean esRecibidoYRechazado() { return false; }
    public boolean esRecibidoYAceptada() { return false; }
    public boolean esNoRecibida() { return false; }
    public boolean esRecibidaYRechazada() { return false; }
    public boolean esParaRedirigir() { return false; }

    public boolean esAmbitoBolsin() { return "Bolsin".equalsIgnoreCase(this.ambito); }
    public boolean esAmbitoRemito() { return "Remito".equalsIgnoreCase(this.ambito); }
    public boolean esAmbitoDocumentacion() { return "Documentacion".equalsIgnoreCase(this.ambito); }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getAmbito() { return ambito; }
    public void setAmbito(String ambito) { this.ambito = ambito; }
}
