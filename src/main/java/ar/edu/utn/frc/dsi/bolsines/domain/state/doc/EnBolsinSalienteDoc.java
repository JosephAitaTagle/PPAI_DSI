package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import ar.edu.utn.frc.dsi.bolsines.domain.Documentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.state.doc.EstadoDocumentacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOC_EN_BOLSIN_SALIENTE")
public class EnBolsinSalienteDoc extends EstadoDocumentacion {
    public EnBolsinSalienteDoc() { super("EnBolsinSaliente", "Documentación dentro de bolsín saliente"); }
}
