package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import ar.edu.utn.frc.dsi.bolsines.domain.state.doc.EstadoDocumentacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("DOC_EN_BOLSIN_ENVIADO")
public class EnBolsinEnviadoDoc extends EstadoDocumentacion {

    public EnBolsinEnviadoDoc() {
        super("EnBolsinEnviado", "Documentación en tránsito dentro de bolsín enviado");
    }

    // Las operaciones reciben explícitamente la documentación que transicionan.
    @Override
    public void aceptar(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                        CambioEstadoDocumentacion[] cambiosEstado) {
        cambiarEstadoDoc(cambiosEstado, fechaActual);
        RecibidaYAceptada nuevoEstado = crearEstadoDoc();
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        documentacion.setEstado(nuevoEstado);
        documentacion.agregarCambioEstado(nuevoCambio);
    }

    // Flujo Alternativo 1: Documentación incompleta -> NoRecibida
    @Override
    public void denegar(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                        CambioEstadoDocumentacion[] cambiosEstado) {
        cambiarEstadoDoc(cambiosEstado, fechaActual);
        NoRecibida nuevoEstado = new NoRecibida();
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        documentacion.setEstado(nuevoEstado);
        documentacion.agregarCambioEstado(nuevoCambio);
    }

    // Flujo Alternativo 2: No corresponde a CM destino -> RecibidaYRechazada
    @Override
    public void rechazar(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                         CambioEstadoDocumentacion[] cambiosEstado) {
        cambiarEstadoDoc(cambiosEstado, fechaActual);
        RecibidaYRechazada nuevoEstado = new RecibidaYRechazada();
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        documentacion.setEstado(nuevoEstado);
        documentacion.agregarCambioEstado(nuevoCambio);
    }

    // Flujo Alternativo 3: CM destino es CMC -> ParaRedirigir
    @Override
    public void redirigir(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                          CambioEstadoDocumentacion[] cambiosEstado) {
        cambiarEstadoDoc(cambiosEstado, fechaActual);
        ParaRedirigir nuevoEstado = new ParaRedirigir();
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        documentacion.setEstado(nuevoEstado);
        documentacion.agregarCambioEstado(nuevoCambio);
    }

    public void cambiarEstadoDoc(CambioEstadoDocumentacion[] cambiosEstado, LocalDateTime fechaActual) {
        java.util.Arrays.stream(cambiosEstado)
                .filter(CambioEstadoDocumentacion::sosActual)
                .findFirst()
                .ifPresent(actual -> actual.setFechaHoraFin(fechaActual));
    }

    public RecibidaYAceptada crearEstadoDoc() {
        return new RecibidaYAceptada();
    }

    public CambioEstadoDocumentacion crearCambioEstadoDocumentacion(LocalDateTime fechaActual, Estado estado, Empleado nombre) {
        return new CambioEstadoDocumentacion(fechaActual, estado, nombre);
    }
}
