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
        // Crear el estado de recepción del remito según sus documentos.
        boolean hayFaltantes = this.remito.getDetalleRemito().stream()
                .anyMatch(d -> opciones.get(d.getDocumentacion().getId()) == OpcionRecepcion.DENEGAR);
        Estado nuevoEstadoRem = hayFaltantes
                ? new RecibidoYAceptadoParcialRemito(this.remito) : crearEstadoRem();

        // 1.3.1.7.1.3: cambiarEstadoDocumentacion(fechaActual: DateTime)
        cambiarEstadoDocumentacion(fechaActual, nombre, opciones);

        // Mantener la recepción parcial cuando hay documentación faltante.
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
