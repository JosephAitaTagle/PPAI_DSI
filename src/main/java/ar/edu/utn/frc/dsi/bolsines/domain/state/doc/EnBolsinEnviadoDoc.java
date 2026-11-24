package ar.edu.utn.frc.dsi.bolsines.domain.state.doc;

import java.time.LocalDateTime;
import java.util.List;

import ar.edu.utn.frc.dsi.bolsines.domain.CambioEstadoDocumentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.Documentacion;
import ar.edu.utn.frc.dsi.bolsines.domain.Empleado;
import ar.edu.utn.frc.dsi.bolsines.domain.state.EstadoDocumentacion;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DOC_EN_BOLSIN_ENVIADO")
public class EnBolsinEnviadoDoc extends EstadoDocumentacion {

    public EnBolsinEnviadoDoc() {
        super("EnBolsinEnviado", "Documentación en tránsito dentro de bolsín enviado");
    }

    // Flujo Principal (Secuencia 1.3.1.7.1.4.1 -> 5) operando sobre la documentación recibida por parámetro
    @Override
    public void aceptar(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                         List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        cambiarEstadoDoc(fechaActual, cambiosEstadoDocumentacion);
        RecibidaYAceptada nuevoEstado = crearEstadoDoc();
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        documentacion.setEstado(nuevoEstado);
        documentacion.agregarCambioEstado(nuevoCambio);
    }

    // Flujo Alternativo 1: Documentación incompleta -> NoRecibida
    @Override
    public void denegar(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                         List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        cambiarEstadoDoc(fechaActual, cambiosEstadoDocumentacion);
        NoRecibida nuevoEstado = new NoRecibida();
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        documentacion.setEstado(nuevoEstado);
        documentacion.agregarCambioEstado(nuevoCambio);
    }

    // Flujo Alternativo 2: No corresponde a CM destino -> RecibidaYRechazada
    @Override
    public void rechazar(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                          List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        cambiarEstadoDoc(fechaActual, cambiosEstadoDocumentacion);
        RecibidaYRechazada nuevoEstado = new RecibidaYRechazada();
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        documentacion.setEstado(nuevoEstado);
        documentacion.agregarCambioEstado(nuevoCambio);
    }

    // Flujo Alternativo 3: CM destino es CMC -> ParaRedirigir
    @Override
    public void redirigir(Documentacion documentacion, LocalDateTime fechaActual, Empleado nombre,
                           List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        cambiarEstadoDoc(fechaActual, cambiosEstadoDocumentacion);
        ParaRedirigir nuevoEstado = new ParaRedirigir();
        CambioEstadoDocumentacion nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, nombre);
        documentacion.setEstado(nuevoEstado);
        documentacion.agregarCambioEstado(nuevoCambio);
    }

    public RecibidaYAceptada crearEstadoDoc() {
        return new RecibidaYAceptada();
    }

    public CambioEstadoDocumentacion crearCambioEstadoDocumentacion(LocalDateTime fechaActual, EstadoDocumentacion estado, Empleado nombre) {
        return new CambioEstadoDocumentacion(fechaActual, estado, nombre);
    }
}
