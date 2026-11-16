package ar.edu.utn.frc.dsi.bolsines.control;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.repository.NotificacionRecepcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class GestorNotificarRecepcion {
    private final NotificacionRecepcionRepository repository;
    public GestorNotificarRecepcion(NotificacionRecepcionRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public NotificacionRecepcion registrarNotificacion(Bolsin bolsin, LocalDateTime fecha) {
        StringBuilder cuerpo = new StringBuilder("Bolsín recibido en ")
                .append(bolsin.getCMDestino().getNombre()).append(".\n");
        for (Remito remito : bolsin.getRemitos()) {
            cuerpo.append("Remito ").append(remito.getNumero()).append(": ")
                    .append(remito.getEstado().getNombre()).append("\n");
            for (DetalleRemito detalle : remito.getDetalleRemito()) {
                Documentacion doc = detalle.getDocumentacion();
                cuerpo.append("Documento ").append(doc.getNumero()).append(": ")
                        .append(doc.getEstadoActual().getNombre()).append("\n");
            }
        }
        return repository.save(new NotificacionRecepcion(bolsin, cuerpo.toString(), fecha));
    }
}
