package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.Remito;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REMITO_RECIBIDO_Y_RECHAZADO")
public class RecibidoYRechazadoRemito extends Estado {

    public RecibidoYRechazadoRemito() {
        super("RecibidoYRechazado", "Remito recibido y rechazado por completo en destino", "Remito");
    }

    public RecibidoYRechazadoRemito(Remito remito) {
        super("RecibidoYRechazado", "Remito recibido y rechazado por completo en destino", "Remito", remito);
    }

    @Override
    public boolean esRecibidoYRechazado() {
        return true;
    }
}
