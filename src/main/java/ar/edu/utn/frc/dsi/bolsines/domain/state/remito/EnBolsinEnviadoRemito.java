package ar.edu.utn.frc.dsi.bolsines.domain.state.remito;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoRemito;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("REMITO_EN_BOLSIN_ENVIADO")
public class EnBolsinEnviadoRemito extends EstadoRemito {

    public EnBolsinEnviadoRemito() {
        super("EnBolsinEnviado", "Remito viajando dentro de un bolsín enviado");
    }

    // Secuencia 1.3.1.7.1 -> 1.3.1.7.1.5 operando sobre el remito y su detalle recibidos por parámetro
    @Override
    public void recibirYAceptar(Remito remito, LocalDateTime fechaActual, Empleado nombre, List<DetalleRemito> detalle) {
        cambiarEstadoDocumentacion(fechaActual, nombre, detalle);
        remito.setEstado(new RecibidoYAceptadoRemito());
    }

    @Override
    public void recibirYAceptarParcialmente(Remito remito, LocalDateTime fechaActual, Empleado nombre, List<DetalleRemito> detalle) {
        cambiarEstadoDocumentacion(fechaActual, nombre, detalle);
        // Caso particular: si todos los documentos quedaron rechazados, el remito no es parcial sino rechazado por completo.
        boolean todosRechazados = detalle.stream()
                .allMatch(d -> d.getDocumentacion().getEstadoActual().esRecibidaYRechazada());
        remito.setEstado(todosRechazados ? new RecibidoYRechazadoRemito() : new RecibidoYAceptadoParcialRemito());
    }

    public void cambiarEstadoDocumentacion(LocalDateTime fechaActual, Empleado nombre, List<DetalleRemito> detalle) {
        for (DetalleRemito d : detalle) {
            d.aceptar(fechaActual, nombre);
        }
    }
}
