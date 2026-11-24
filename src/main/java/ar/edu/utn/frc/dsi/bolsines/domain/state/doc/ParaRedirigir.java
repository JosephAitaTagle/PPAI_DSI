package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import ar.edu.utn.frc.dsi.bolsines.domain.Documentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.state.doc.EstadoDocumentacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOC_PARA_REDIRIGIR")
public class ParaRedirigir extends EstadoDocumentacion {
    public ParaRedirigir() { super("ParaRedirigir", "Documentación recibida en CMC para redistribución interna"); }
    @Override
    public boolean esParaRedirigir() { return true; }
}
