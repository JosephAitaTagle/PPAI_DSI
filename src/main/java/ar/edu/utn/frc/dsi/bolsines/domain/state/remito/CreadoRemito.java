package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoRemito;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REMITO_CREADO")
public class CreadoRemito extends EstadoRemito {
    public CreadoRemito() { super("Creado", "Remito generado"); }
}
