package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.Remito;
import ar.edu.utn.frc.dsi.bolsines.domain.state.remito.EstadoRemito;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("REMITO_EN_BOLSIN_SALIENTE")
public class EnBolsinSalienteRemito extends EstadoRemito {
    public EnBolsinSalienteRemito() { super("EnBolsinSaliente", "Remito asignado a bolsín saliente"); }
}
