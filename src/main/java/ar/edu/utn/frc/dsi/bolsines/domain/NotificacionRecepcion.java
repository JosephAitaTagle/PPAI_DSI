package ar.edu.utn.frc.dsi.bolsines.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Bandeja de salida del CU29: pendiente hasta integrar un transporte de correo. */
@Entity
public class NotificacionRecepcion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long bolsinId;
    private String destinatario;
    private String asunto;
    @Lob
    private String cuerpo;
    private LocalDateTime fechaCreacion;
    private String estado;

    protected NotificacionRecepcion() {}
    public NotificacionRecepcion(Bolsin bolsin, String cuerpo, LocalDateTime fecha) {
        this.bolsinId = bolsin.getId();
        this.destinatario = bolsin.getCMOrigen().getEmail();
        this.asunto = "Recepción de bolsín N° " + bolsin.getNumeroBolsin();
        this.cuerpo = cuerpo;
        this.fechaCreacion = fecha;
        this.estado = "PENDIENTE_ENVIO";
    }
    public Long getId() { return id; }
    public String getEstado() { return estado; }
    public String getDestinatario() { return destinatario; }
    public String getCuerpo() { return cuerpo; }
}
