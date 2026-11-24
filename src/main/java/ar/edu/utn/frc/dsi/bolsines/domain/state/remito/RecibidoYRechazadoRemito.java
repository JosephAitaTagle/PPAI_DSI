package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.Remito;
import ar.edu.utn.frc.dsi.bolsines.domain.state.remito.EstadoRemito;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REMITO_RECIBIDO_Y_RECHAZADO")
public class RecibidoYRechazadoRemito extends EstadoRemito {

    public RecibidoYRechazadoRemito() {
        super("RecibidoYRechazado", "Remito recibido y rechazado por completo en destino");
    }

@Override
    public boolean esRecibidoYRechazado() {
        return true;
    }
}
