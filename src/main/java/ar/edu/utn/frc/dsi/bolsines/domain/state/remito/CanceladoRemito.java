package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.Remito;
import ar.edu.utn.frc.dsi.bolsines.domain.state.remito.EstadoRemito;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REMITO_CANCELADO")
public class CanceladoRemito extends EstadoRemito {
    public CanceladoRemito() { super("Cancelado", "Remito cancelado"); }
}
