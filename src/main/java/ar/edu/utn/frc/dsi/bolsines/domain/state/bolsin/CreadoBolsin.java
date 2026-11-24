package ar.edu.utn.frc.dsi.bolsines.domain.state.bolsin;

import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoBolsin;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BOLSIN_CREADO")
public class CreadoBolsin extends EstadoBolsin {
    public CreadoBolsin() { super("Creado", "Bolsín creado en origen"); }
}
