package ar.edu.utn.frc.dsi.bolsines.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DetalleRemito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String areaCMCDestino;

    // auxiliar, no está en el diagrama: transporta la decisión individual (ACEPTAR/DENEGAR/RECHAZAR/REDIRIGIR)
    // hasta que la cascada de recepción llega a este detalle, sin pasarla como parámetro por toda la cadena.
    @Enumerated(EnumType.STRING)
    private OpcionRecepcion opcionRecepcion = OpcionRecepcion.ACEPTAR;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Documentacion documentacion;

    protected DetalleRemito() {}
    public DetalleRemito(String areaCMCDestino, Documentacion documentacion) {
        this.areaCMCDestino = areaCMCDestino;
        this.documentacion = documentacion;
    }

    public void aceptar(LocalDateTime fechaActual, Empleado nombre) {
        switch (opcionRecepcion) {
            case ACEPTAR   -> this.documentacion.aceptar(fechaActual, nombre);
            case DENEGAR   -> this.documentacion.denegar(fechaActual, nombre);
            case RECHAZAR  -> this.documentacion.rechazar(fechaActual, nombre);
            case REDIRIGIR -> this.documentacion.redirigir(fechaActual, nombre);
        }
    }

    public void setOpcionRecepcion(OpcionRecepcion opcionRecepcion) { this.opcionRecepcion = opcionRecepcion; }
    public OpcionRecepcion getOpcionRecepcion() { return opcionRecepcion; }
    public String getAreaCMCDestino() { return areaCMCDestino; }
    public Documentacion getDocumentacion() { return documentacion; }
}
