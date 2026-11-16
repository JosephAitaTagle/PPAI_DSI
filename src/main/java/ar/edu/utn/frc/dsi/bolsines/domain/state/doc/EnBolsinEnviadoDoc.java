package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.domain.state.Estado;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("DOC_EN_BOLSIN_ENVIADO")
public class EnBolsinEnviadoDoc extends Estado {

    public EnBolsinEnviadoDoc() {
        super("EnBolsinEnviado", "Documentación en tránsito dentro de bolsín enviado", "Documentacion");
    }

    public EnBolsinEnviadoDoc(Documentacion documentacion) {
        super("EnBolsinEnviado", "Documentación en tránsito dentro de bolsín enviado", "Documentacion", documentacion);
    }

    // Flujo Principal (Secuencia 1.3.1.7.1.4.1 -> 5 sobre referencia inversa this.documentacion)
    @Override
    public void aceptar(LocalDateTime fechaActual, Empleado nombre) {
        cambiarEstadoDoc(fechaActual, this);
        RecibidaYAceptada nuevoEstado = crearEstadoDoc();
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        this.documentacion.setEstado(nuevoEstado);
        this.documentacion.agregarCambioEstado(nuevoCambio);
    }

    // Flujo Alternativo 1: Documentación incompleta -> NoRecibida
    @Override
    public void denegar(LocalDateTime fechaActual, Empleado nombre) {
        cambiarEstadoDoc(fechaActual, this);
        NoRecibida nuevoEstado = new NoRecibida(this.documentacion);
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        this.documentacion.setEstado(nuevoEstado);
        this.documentacion.agregarCambioEstado(nuevoCambio);
    }

    // Flujo Alternativo 2: No corresponde a CM destino -> RecibidaYRechazada
    @Override
    public void rechazar(LocalDateTime fechaActual, Empleado nombre) {
        cambiarEstadoDoc(fechaActual, this);
        RecibidaYRechazada nuevoEstado = new RecibidaYRechazada(this.documentacion);
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        this.documentacion.setEstado(nuevoEstado);
        this.documentacion.agregarCambioEstado(nuevoCambio);
    }

    // Flujo Alternativo 3: CM destino es CMC -> ParaRedirigir
    @Override
    public void redirigir(LocalDateTime fechaActual, Empleado nombre) {
        cambiarEstadoDoc(fechaActual, this);
        ParaRedirigir nuevoEstado = new ParaRedirigir(this.documentacion);
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        this.documentacion.setEstado(nuevoEstado);
        this.documentacion.agregarCambioEstado(nuevoCambio);
    }

    public void cambiarEstadoDoc(LocalDateTime fechaActual, Estado estado) {
        this.documentacion.cerrarCambioEstadoActual(fechaActual);
    }

    public RecibidaYAceptada crearEstadoDoc() {
        return new RecibidaYAceptada(this.documentacion);
    }

    public CambioEstadoDocumentacion crearCambioEstadoDocumentacion(LocalDateTime fechaActual, Estado estado, Empleado nombre) {
        return new CambioEstadoDocumentacion(fechaActual, estado, nombre);
    }
}
