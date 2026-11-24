package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoRemito;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REMITO_RECIBIDO_Y_ACEPTADO")
public class RecibidoYAceptadoRemito extends EstadoRemito {

    public RecibidoYAceptadoRemito() {
        super("RecibidoYAceptado", "Remito recibido y aceptado en destino");
    }

    @Override
    public boolean esRecibidoYAceptado() {
        return true;
    }
}
