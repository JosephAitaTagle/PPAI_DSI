package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import ar.edu.utn.frc.dsi.bolsines.domain.Documentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.state.doc.EstadoDocumentacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOC_RECIBIDA_Y_RECHAZADA")
public class RecibidaYRechazada extends EstadoDocumentacion {
    public RecibidaYRechazada() { super("RecibidaYRechazada", "Documentación rechazada por no corresponder a la CM destino"); }
    @Override
    public boolean esRecibidaYRechazada() { return true; }
}
