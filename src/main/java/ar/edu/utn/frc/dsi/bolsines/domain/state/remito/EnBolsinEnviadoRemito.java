package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.remito.EstadoRemito;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("REMITO_EN_BOLSIN_ENVIADO")
public class EnBolsinEnviadoRemito extends EstadoRemito {

    public EnBolsinEnviadoRemito() {
        super("EnBolsinEnviado", "Remito viajando dentro de un bolsín enviado");
    }

    // Secuencia 1.3.1.7.1; el remito se recibe como argumento desde el contexto.
    @Override
    public void recibirYAceptar(Remito remito, LocalDateTime fechaActual, Empleado nombre,
                                DetalleRemito[] detalles, java.util.Map<Long, OpcionRecepcion> opciones) {
        procesarRecepcion(remito, fechaActual, nombre, detalles, opciones, crearEstadoRem());
    }

    @Override
    public void recibirYAceptarParcialmente(Remito remito, LocalDateTime fechaActual, Empleado nombre,
                                             DetalleRemito[] detalles,
                                             java.util.Map<Long, OpcionRecepcion> opciones) {
        // Se conserva el estado de rechazo total existente; otras incidencias dejan el remito parcial.
        java.util.List<OpcionRecepcion> resultados = java.util.Arrays.stream(detalles)
                .map(d -> opciones.get(d.getDocumentacion().getId())).toList();
        EstadoRemito nuevoEstadoRem = resultados.stream().allMatch(o -> o == OpcionRecepcion.RECHAZAR)
                ? new RecibidoYRechazadoRemito()
                : new RecibidoYAceptadoParcialRemito();
        procesarRecepcion(remito, fechaActual, nombre, detalles, opciones, nuevoEstadoRem);
    }

    private void procesarRecepcion(Remito remito, LocalDateTime fechaActual, Empleado nombre,
                                   DetalleRemito[] detalles,
                                   java.util.Map<Long, OpcionRecepcion> opciones, EstadoRemito nuevoEstadoRem) {
        cambiarEstadoDocumentacion(remito, detalles, fechaActual, nombre, opciones);
        remito.setEstado(nuevoEstadoRem);
    }

    public RecibidoYAceptadoRemito crearEstadoRem() {
        return new RecibidoYAceptadoRemito();
    }

    public void cambiarEstadoDocumentacion(Remito remito, DetalleRemito[] detalles, LocalDateTime fechaActual, Empleado nombre, java.util.Map<Long, OpcionRecepcion> opciones) {
        for (DetalleRemito detalle : detalles) {
            detalle.procesarRecepcion(fechaActual, nombre, opciones.get(detalle.getDocumentacion().getId()));
        }
    }
}
