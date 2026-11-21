package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("REMITO_EN_BOLSIN_ENVIADO")
public class EnBolsinEnviadoRemito extends Estado {

    public EnBolsinEnviadoRemito() {
        super("EnBolsinEnviado", "Remito viajando dentro de un bolsín enviado", "Remito");
    }

    public EnBolsinEnviadoRemito(Remito remito) {
        super("EnBolsinEnviado", "Remito viajando dentro de un bolsín enviado", "Remito", remito);
    }

    // Secuencia 1.3.1.7.1 -> 1.3.1.7.1.5 sobre la referencia inversa this.remito
    @Override
    public void recibirYAceptar(LocalDateTime fechaActual, Empleado nombre, java.util.Map<Long, OpcionRecepcion> opciones) {
        procesarRecepcion(fechaActual, nombre, opciones, crearEstadoRem());
    }

    @Override
    public void recibirYAceptarParcialmente(LocalDateTime fechaActual, Empleado nombre,
                                             java.util.Map<Long, OpcionRecepcion> opciones) {
        // Se conserva el estado de rechazo total existente; otras incidencias dejan el remito parcial.
        java.util.List<OpcionRecepcion> resultados = this.remito.getDetalleRemito().stream()
                .map(d -> opciones.get(d.getDocumentacion().getId())).toList();
        Estado nuevoEstadoRem = resultados.stream().allMatch(o -> o == OpcionRecepcion.RECHAZAR)
                ? new RecibidoYRechazadoRemito(this.remito)
                : new RecibidoYAceptadoParcialRemito(this.remito);
        procesarRecepcion(fechaActual, nombre, opciones, nuevoEstadoRem);
    }

    private void procesarRecepcion(LocalDateTime fechaActual, Empleado nombre,
                                   java.util.Map<Long, OpcionRecepcion> opciones, Estado nuevoEstadoRem) {
        cambiarEstadoDocumentacion(fechaActual, nombre, opciones);
        this.remito.setEstado(nuevoEstadoRem);
    }

    public RecibidoYAceptadoRemito crearEstadoRem() {
        return new RecibidoYAceptadoRemito(this.remito);
    }

    public void cambiarEstadoDocumentacion(LocalDateTime fechaActual, Empleado nombre, java.util.Map<Long, OpcionRecepcion> opciones) {
        for (DetalleRemito detalle : this.remito.getDetalleRemito()) {
            detalle.procesarRecepcion(fechaActual, nombre, opciones.get(detalle.getDocumentacion().getId()));
        }
    }
}
