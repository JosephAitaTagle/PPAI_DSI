package ar.edu.utn.frc.dsi.bolsines.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;

    @ManyToOne(optional = false)
    private Usuario usuario;

    protected Sesion() {}
    public Sesion(LocalDateTime fechaHoraInicio, Usuario usuario) {
        this.fechaHoraInicio = fechaHoraInicio;
        this.usuario = usuario;
    }

    public Usuario getUsuario() { return usuario; }
    public boolean esActiva() { return fechaHoraFin == null; }
    public void finalizar(LocalDateTime fechaHoraFin) {
        if (!esActiva()) throw new IllegalStateException("La sesión ya está cerrada");
        this.fechaHoraFin = fechaHoraFin;
    }
}
