package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoRemito;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@DiscriminatorValue("REMITO_EN_BOLSIN_ENVIADO")
public class EnBolsinEnviadoRemito extends EstadoRemito {

    public EnBolsinEnviadoRemito() {
        super("EnBolsinEnviado", "Remito viajando dentro de un bolsín enviado");
    }

    // Secuencia 1.3.1.7.1 -> 1.3.1.7.1.5 operando sobre el remito recibido por parámetro
    @Override
    public void recibirYAceptar(Remito remito, LocalDateTime fecha, Empleado empleado,
                                 Map<Long, OpcionRecepcion> opciones) {
        procesarRecepcion(remito, fecha, empleado, opciones, new RecibidoYAceptadoRemito());
    }

    @Override
    public void recibirYAceptarParcialmente(Remito remito, LocalDateTime fecha, Empleado empleado,
                                             Map<Long, OpcionRecepcion> opciones) {
        // Se conserva el estado de rechazo total existente; otras incidencias dejan el remito parcial.
        List<OpcionRecepcion> resultados = remito.getDetalleRemito().stream()
                .map(d -> opciones.get(d.getDocumentacion().getId())).toList();
        EstadoRemito nuevoEstadoRem = resultados.stream().allMatch(o -> o == OpcionRecepcion.RECHAZAR)
                ? new RecibidoYRechazadoRemito()
                : new RecibidoYAceptadoParcialRemito();
        procesarRecepcion(remito, fecha, empleado, opciones, nuevoEstadoRem);
    }

    private void procesarRecepcion(Remito remito, LocalDateTime fecha, Empleado empleado,
                                    Map<Long, OpcionRecepcion> opciones, EstadoRemito nuevoEstadoRem) {
        for (DetalleRemito detalle : remito.getDetalleRemito()) {
            detalle.procesarRecepcion(fecha, empleado, opciones.get(detalle.getDocumentacion().getId()));
        }
        remito.setEstado(nuevoEstadoRem);
    }
}
