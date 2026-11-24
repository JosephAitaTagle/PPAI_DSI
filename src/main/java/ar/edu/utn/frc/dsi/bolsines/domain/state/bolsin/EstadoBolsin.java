package ar.edu.utn.frc.dsi.bolsines.domain.state.bolsin;

import ar.edu.utn.frc.dsi.bolsines.domain.Bolsin;
import ar.edu.utn.frc.dsi.bolsines.domain.CambioEstadoBolsin;
import ar.edu.utn.frc.dsi.bolsines.domain.Empleado;
import ar.edu.utn.frc.dsi.bolsines.domain.OpcionRecepcion;
import ar.edu.utn.frc.dsi.bolsines.domain.Remito;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@DiscriminatorValue("ESTADO_BOLSIN")
public abstract class EstadoBolsin extends Estado {

    protected EstadoBolsin() {}

    protected EstadoBolsin(String nombre, String descripcion) {
        super(nombre, descripcion, "Bolsin");
    }

    public void recibirYAceptarBolsin(Bolsin bolsin, LocalDateTime fechaActual, Empleado nombre,
                                      Remito[] remitos, CambioEstadoBolsin[] cambiosEstado,
                                      Map<Long, OpcionRecepcion> opciones) {
        throw new UnsupportedOperationException("Transición recibirYAceptarBolsin no permitida en estado: " + getNombre());
    }
}
