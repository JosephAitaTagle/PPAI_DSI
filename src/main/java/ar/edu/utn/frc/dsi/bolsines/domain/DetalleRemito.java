package ar.edu.utn.frc.dsi.bolsines.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DetalleRemito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String areaCMCDestino;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Documentacion documentacion;

    protected DetalleRemito() {}
    public DetalleRemito(String areaCMCDestino, Documentacion documentacion) {
        this.areaCMCDestino = areaCMCDestino;
        this.documentacion = documentacion;
    }

    public void procesarRecepcion(LocalDateTime fechaActual, Empleado nombre, OpcionRecepcion opcionRecepcion) {
        switch (opcionRecepcion) {
            case ACEPTAR   -> this.documentacion.aceptar(fechaActual, nombre);
            case DENEGAR   -> this.documentacion.denegar(fechaActual, nombre);
            case RECHAZAR  -> this.documentacion.rechazar(fechaActual, nombre);
            case REDIRIGIR -> this.documentacion.redirigir(fechaActual, nombre);
            default -> throw new IllegalArgumentException("Opción de recepción desconocida: " + opcionRecepcion);
        }
    }

    public String getAreaCMCDestino() { return areaCMCDestino; }
    public Documentacion getDocumentacion() { return documentacion; }
}
