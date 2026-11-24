package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.DetalleRemito;
import ar.edu.utn.frc.dsi.bolsines.domain.Empleado;
import ar.edu.utn.frc.dsi.bolsines.domain.OpcionRecepcion;
import ar.edu.utn.frc.dsi.bolsines.domain.Remito;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@DiscriminatorValue("ESTADO_REMITO")
public abstract class EstadoRemito extends Estado {

    protected EstadoRemito() {}

    protected EstadoRemito(String nombre, String descripcion) {
        super(nombre, descripcion, "Remito");
    }

    public void recibirYAceptar(Remito remito, LocalDateTime fechaActual, Empleado nombre,
                                DetalleRemito[] detalles, Map<Long, OpcionRecepcion> opciones) {
        throw new UnsupportedOperationException("Transición recibirYAceptar no permitida en estado: " + getNombre());
    }

    public void recibirYAceptarParcialmente(Remito remito, LocalDateTime fechaActual, Empleado nombre,
                                             DetalleRemito[] detalles, Map<Long, OpcionRecepcion> opciones) {
        throw new UnsupportedOperationException("Transición recibirYAceptarParcialmente no permitida en estado: " + getNombre());
    }
}
