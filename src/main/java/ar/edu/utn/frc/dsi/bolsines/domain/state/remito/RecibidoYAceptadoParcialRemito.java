package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.Remito;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REMITO_RECIBIDO_PARCIAL")
public class RecibidoYAceptadoParcialRemito extends Estado {
    public RecibidoYAceptadoParcialRemito() { super("RecibidoYAceptadoParcial", "Remito recibido con observaciones", "Remito"); }
    public RecibidoYAceptadoParcialRemito(Remito remito) { super("RecibidoYAceptadoParcial", "Remito recibido con observaciones", "Remito", remito); }
    @Override
    public boolean esRecibidoYAceptadoParcial() { return true; }
}
