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
    public void aceptar(Documentacion documentacion, LocalDateTime fecha, Empleado empleado,
                         List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        transicionar(documentacion, fecha, empleado, cambiosEstadoDocumentacion, new RecibidaYAceptada());
    }

    // Flujo Alternativo 1: Documentación incompleta -> NoRecibida
    @Override
    public void denegar(Documentacion documentacion, LocalDateTime fecha, Empleado empleado,
                         List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        transicionar(documentacion, fecha, empleado, cambiosEstadoDocumentacion, new NoRecibida());
    }

    // Flujo Alternativo 2: No corresponde a CM destino -> RecibidaYRechazada
    @Override
    public void rechazar(Documentacion documentacion, LocalDateTime fecha, Empleado empleado,
                          List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        transicionar(documentacion, fecha, empleado, cambiosEstadoDocumentacion, new RecibidaYRechazada());
    }

    // Flujo Alternativo 3: CM destino es CMC -> ParaRedirigir
    @Override
    public void redirigir(Documentacion documentacion, LocalDateTime fecha, Empleado empleado,
                           List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion) {
        transicionar(documentacion, fecha, empleado, cambiosEstadoDocumentacion, new ParaRedirigir());
    }

    private void transicionar(Documentacion documentacion, LocalDateTime fecha, Empleado empleado,
                               List<CambioEstadoDocumentacion> cambiosEstadoDocumentacion, EstadoDocumentacion nuevoEstado) {
        obtenerCambioActual(cambiosEstadoDocumentacion).setFechaHoraFin(fecha);
        CambioEstadoDocumentacion nuevoCambio = new CambioEstadoDocumentacion(fecha, nuevoEstado, empleado);
        documentacion.setEstado(nuevoEstado);
        cambiosEstadoDocumentacion.add(nuevoCambio);
    }
}
