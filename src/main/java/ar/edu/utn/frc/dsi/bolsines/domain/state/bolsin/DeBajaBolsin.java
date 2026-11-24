package ar.edu.utn.frc.dsi.bolsines.domain.state.bolsin;

import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoBolsin;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BOLSIN_DE_BAJA")
public class DeBajaBolsin extends EstadoBolsin {
    public DeBajaBolsin() { super("DeBaja", "Bolsín dado de baja"); }
}
