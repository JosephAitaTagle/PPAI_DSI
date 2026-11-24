package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import ar.edu.utn.frc.dsi.bolsines.domain.Documentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.state.doc.EstadoDocumentacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOC_NO_RECIBIDA")
public class NoRecibida extends EstadoDocumentacion {
    public NoRecibida() { super("NoRecibida", "Documentación faltante o incompleta al abrir el bolsín"); }
    @Override
    public boolean esNoRecibida() { return true; }
}
