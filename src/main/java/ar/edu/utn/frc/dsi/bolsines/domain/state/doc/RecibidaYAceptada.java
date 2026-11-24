package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import ar.edu.utn.frc.dsi.bolsines.domain.Documentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.state.doc.EstadoDocumentacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOC_RECIBIDA_Y_ACEPTADA")
public class RecibidaYAceptada extends EstadoDocumentacion {
    public RecibidaYAceptada() { super("RecibidaYAceptada", "Documentación completa recibida y aceptada en destino"); }
    @Override
    public boolean esRecibidoYAceptada() { return true; }
}
