package ar.edu.utn.frc.dsi.bolsines.domain.state.bolsin;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoBolsin;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("BOLSIN_ENVIADO")
public class Enviado extends EstadoBolsin {

    public Enviado() {
        super("Enviado", "Bolsín despachado hacia la Comisión Médica destino");
    }

    @Override
    public boolean esEnviado() {
        return true;
    }

    // Secuencia 1.3.1 -> 1.3.1.9 operando sobre el contexto recibido por parámetro
    @Override
    public void recibirYAceptarBolsin(Bolsin bolsin, LocalDateTime fechaActual, Empleado nombre,
                                       List<Remito> remito, List<CambioEstadoBolsin> cambioEstadoBolsin) {
        // 1.3.1.1 -> 1.3.1.2
        cambiarEstadoBolsin(fechaActual, cambioEstadoBolsin);

        // 1.3.1.3 -> 1.3.1.4
        RecibidoEnCMDestino nuevoEstado = crearEstado();

        // 1.3.1.5 -> 1.3.1.6
        CambioEstadoBolsin nuevoCambio = crearCambioEstadoBolsin(fechaActual, nuevoEstado, nombre);

        // 1.3.1.7: loop [Mientras haya remitos] -> recibirYAceptar()
        for (Remito r : remito) {
            r.recibirYAceptar(fechaActual, nombre);
        }

        // 1.3.1.8: setEstado(estado: RecibidoEnCMDestino)
        bolsin.setEstado(nuevoEstado);

        // 1.3.1.9: agregarCambioEstado(cambioEstado: CambioEstadoBolsin)
        bolsin.agregarCambioEstado(nuevoCambio);
    }

    public RecibidoEnCMDestino crearEstado() {
        return new RecibidoEnCMDestino();
    }

    public CambioEstadoBolsin crearCambioEstadoBolsin(LocalDateTime fechaActual, EstadoBolsin estado, Empleado empleado) {
        return new CambioEstadoBolsin(fechaActual, estado, empleado);
    }
}
