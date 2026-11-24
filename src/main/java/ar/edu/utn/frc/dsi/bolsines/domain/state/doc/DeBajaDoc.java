package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import ar.edu.utn.frc.dsi.bolsines.domain.Documentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.state.doc.EstadoDocumentacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOC_DE_BAJA")
public class DeBajaDoc extends EstadoDocumentacion {
    public DeBajaDoc() { super("DeBaja", "Documentación dada de baja"); }
}
