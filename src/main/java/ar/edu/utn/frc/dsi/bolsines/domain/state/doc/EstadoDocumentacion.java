package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import ar.edu.utn.frc.dsi.bolsines.domain.CambioEstadoDocumentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.Documentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.Empleado;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ESTADO_DOCUMENTACION")
public abstract class EstadoDocumentacion extends Estado {

    protected EstadoDocumentacion() {}

    protected EstadoDocumentacion(String nombre, String descripcion) {
        super(nombre, descripcion, "Documentacion");
    }

    public void aceptar(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                        CambioEstadoDocumentacion[] cambiosEstado) {
        throw new UnsupportedOperationException("Transición aceptar no permitida en estado: " + getNombre());
    }

    public void denegar(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                        CambioEstadoDocumentacion[] cambiosEstado) {
        throw new UnsupportedOperationException("Transición denegar no permitida en estado: " + getNombre());
    }

    public void rechazar(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                         CambioEstadoDocumentacion[] cambiosEstado) {
        throw new UnsupportedOperationException("Transición rechazar no permitida en estado: " + getNombre());
    }

    public void redirigir(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                          CambioEstadoDocumentacion[] cambiosEstado) {
        throw new UnsupportedOperationException("Transición redirigir no permitida en estado: " + getNombre());
    }
}
