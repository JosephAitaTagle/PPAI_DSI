package ar.edu.utn.frc.dsi.bolsines.domain.state.bolsin;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import ar.edu.utn.frc.dsi.bolsines.domain.state.bolsin.EstadoBolsin;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

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

    // Secuencia 1.3.1 -> 1.3.1.9; el contexto llega explícitamente desde Bolsin.
    @Override
    public void recibirYAceptarBolsin(Bolsin bolsin, LocalDateTime fechaActual, Empleado nombre,
                                      Remito[] remitos, CambioEstadoBolsin[] cambiosEstado,
                                      java.util.Map<Long, OpcionRecepcion> opciones) {
        // 1.3.1.1 -> 1.3.1.2: Cierra CambioEstadoBolsin actual en el contexto
        cambiarEstadoBolsin(cambiosEstado, fechaActual);

        // 1.3.1.3 -> 1.3.1.4: crearEstado(): RecibidoEnCMDestino
        RecibidoEnCMDestino nuevoEstado = crearEstado();

        // 1.3.1.5 -> 1.3.1.6: crearCambioEstadoBolsin(fechaActual, estadoActual)
        CambioEstadoBolsin nuevoCambio = crearCambioEstadoBolsin(fechaActual, nuevoEstado, nombre);

        // 1.3.1.7: loop [Mientras haya remitos] -> recibir()
        for (Remito remito : remitos) {
            remito.recibirYAceptar(fechaActual, nombre, opciones);
        }

        // 1.3.1.8: setEstado(estado: RecibidoEnCMDestino)
        bolsin.setEstado(nuevoEstado);

        // 1.3.1.9: agregarCambioEstado(cambioEstado: CambioEstadoBolsin)
        bolsin.agregarCambioEstado(nuevoCambio);
    }

    public void cambiarEstadoBolsin(CambioEstadoBolsin[] cambiosEstado, LocalDateTime fechaActual) {
        java.util.Arrays.stream(cambiosEstado)
                .filter(CambioEstadoBolsin::sosActual)
                .findFirst()
                .ifPresent(actual -> actual.setFechaHoraFin(fechaActual));
    }

    public RecibidoEnCMDestino crearEstado() {
        return new RecibidoEnCMDestino();
    }

    public CambioEstadoBolsin crearCambioEstadoBolsin(LocalDateTime fechaActual, Estado estadoActual, Empleado empleado) {
        return new CambioEstadoBolsin(fechaActual, estadoActual, empleado);
    }
}
