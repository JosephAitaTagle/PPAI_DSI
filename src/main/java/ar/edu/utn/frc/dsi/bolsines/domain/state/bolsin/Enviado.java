package ar.edu.utn.frc.dsi.bolsines.domain.state.bolsin;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoBolsin;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    public void recibirYAceptarBolsin(Bolsin bolsin, LocalDateTime fecha, Empleado empleado,
                                       List<Remito> remitos, List<CambioEstadoBolsin> cambiosEstadoBolsin,
                                       Map<Long, OpcionRecepcion> opciones) {
        // 1.3.1.1 -> 1.3.1.2: cierra el CambioEstadoBolsin vigente, ubicado iterando el arreglo recibido
        obtenerCambioActual(cambiosEstadoBolsin).setFechaHoraFin(fecha);

        // 1.3.1.3 -> 1.3.1.4: crearEstado(): RecibidoEnCMDestino
        RecibidoEnCMDestino nuevoEstado = new RecibidoEnCMDestino();

        // 1.3.1.5 -> 1.3.1.6: crearCambioEstadoBolsin(fecha, nuevoEstado)
        CambioEstadoBolsin nuevoCambio = new CambioEstadoBolsin(fecha, nuevoEstado, empleado);

        // 1.3.1.7: loop [Mientras haya remitos] -> recibir()
        for (Remito remito : remitos) {
            remito.recibir(fecha, empleado, opciones);
        }

        // 1.3.1.8: setEstado(estado: RecibidoEnCMDestino)
        bolsin.setEstado(nuevoEstado);

        // 1.3.1.9: agregarCambioEstado(cambioEstado: CambioEstadoBolsin)
        cambiosEstadoBolsin.add(nuevoCambio);
    }
}
