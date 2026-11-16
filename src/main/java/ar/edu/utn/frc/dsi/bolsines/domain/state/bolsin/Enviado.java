package ar.edu.utn.frc.dsi.bolsines.domain.state.bolsin;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("BOLSIN_ENVIADO")
public class Enviado extends Estado {

    public Enviado() {
        super("Enviado", "Bolsín despachado hacia la Comisión Médica destino", "Bolsin");
    }

    public Enviado(Bolsin bolsin) {
        super("Enviado", "Bolsín despachado hacia la Comisión Médica destino", "Bolsin", bolsin);
    }

    @Override
    public boolean esEnviado() {
        return true;
    }

    // Secuencia 1.3.1 -> 1.3.1.9 sobre la referencia inversa this.bolsin
    @Override
    public void recibirYAceptarBolsin(LocalDateTime fechaActual, Empleado nombre, java.util.Map<Long, OpcionRecepcion> opciones) {
        // 1.3.1.1 -> 1.3.1.2: Cierra CambioEstadoBolsin actual en el contexto
        cambiarEstadoBolsin(fechaActual, this);

        // 1.3.1.3 -> 1.3.1.4: crearEstado(): RecibidoEnCMDestino
        RecibidoEnCMDestino nuevoEstado = crearEstado();

        // 1.3.1.5 -> 1.3.1.6: crearCambioEstadoBolsin(fechaActual, estadoActual)
        CambioEstadoBolsin nuevoCambio = crearCambioEstadoBolsin(fechaActual, nuevoEstado, nombre);

        // 1.3.1.7: loop [Mientras haya remitos] -> recibir()
        for (Remito remito : this.bolsin.getRemitos()) {
            remito.recibir(fechaActual, nombre, opciones);
        }

        // 1.3.1.8: setEstado(estado: RecibidoEnCMDestino)
        this.bolsin.setEstado(nuevoEstado);

        // 1.3.1.9: agregarCambioEstado(cambioEstado: CambioEstadoBolsin)
        this.bolsin.agregarCambioEstado(nuevoCambio);
    }

    public void cambiarEstadoBolsin(LocalDateTime fechaActual, Estado estado) {
        this.bolsin.cerrarCambioEstadoActual(fechaActual);
    }

    public RecibidoEnCMDestino crearEstado() {
        return new RecibidoEnCMDestino(this.bolsin);
    }

    public CambioEstadoBolsin crearCambioEstadoBolsin(LocalDateTime fechaActual, Estado estadoActual, Empleado empleado) {
        return new CambioEstadoBolsin(fechaActual, estadoActual, empleado);
    }
}
