package ar.edu.utn.frc.dsi.bolsines.control;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class GestorRecpBolsin {
    private final SesionRepository sesiones;
    private final EmpleadoRepository empleados;
    private final BolsinRepository bolsines;
    private final GestorNotificarRecepcion notificaciones;

    public GestorRecpBolsin(SesionRepository sesiones, EmpleadoRepository empleados,
                           BolsinRepository bolsines,
                           GestorNotificarRecepcion notificaciones) {
        this.sesiones = sesiones;
        this.empleados = empleados;
        this.bolsines = bolsines;
        this.notificaciones = notificaciones;
    }

    public Map<String, Object> selOpcRegRecpBolsin(String precinto, String origen) {
        Empleado empleado = buscarEmpleado();
        ComisionMedica cm = empleado.obtenerCM();
        List<Map<String, Object>> pendientes = bolsines.findAll().stream()
                .filter(b -> b.esTuCmDestino(cm) && b.sosEstadoEnviado())
                .filter(b -> contiene(b.getNumeroPrecinto(), precinto))
                .filter(b -> contiene(b.getCMOrigen().getNombre(), origen))
                .map(this::buscarDatosBolsin).toList();
        return Map.of("empleado", empleado.getNombreCompleto(), "nombreCM", cm.getNombre(),
                "esCMC", cm.isEsCMC(), "bolsines", pendientes);
    }

    private boolean contiene(String valor, String filtro) {
        return filtro == null || filtro.isBlank()
                || valor.toLowerCase(Locale.ROOT).contains(filtro.trim().toLowerCase(Locale.ROOT));
    }

    private Empleado buscarEmpleado() {
        // Contexto de demostración: una única sesión activa precargada, sin implementar login.
        List<Sesion> activas = sesiones.findAll().stream().filter(Sesion::esActiva).toList();
        if (activas.size() != 1) throw new IllegalStateException("Debe existir una única sesión activa de demostración");
        Usuario usuario = activas.get(0).getUsuario();
        return empleados.findAll().stream().filter(e -> e.esTuUsuario(usuario)).findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró el empleado de la sesión"));
    }

    private Bolsin buscarBolsin(Long id, Empleado empleado) {
        if (id == null) throw new IllegalArgumentException("Seleccione un bolsín");
        Bolsin bolsin = bolsines.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bolsín no encontrado"));
        if (!bolsin.esTuCmDestino(empleado.obtenerCM()))
            throw new IllegalArgumentException("El bolsín no corresponde a la CM del empleado");
        if (!bolsin.sosEstadoEnviado())
            throw new IllegalStateException("El bolsín ya no está en estado Enviado");
        return bolsin;
    }

    private Map<String, Object> buscarDatosBolsin(Bolsin b) {
        return Map.of("id", b.getId(), "numeroBolsin", b.getNumeroBolsin(),
                "numeroPrecinto", b.getNumeroPrecinto(), "fecha", b.getFecha().toString(),
                "peso", b.getPeso(), "cmOrigen", b.getCMOrigen().getNombre(),
                "estadoActual", b.getEstadoActual().getNombre());
    }

    public Map<String, Object> tomarSeleccionBolsin(Long id) {
        Empleado empleado = buscarEmpleado();
        Bolsin bolsin = buscarBolsin(id, empleado);
        List<Map<String, Object>> remitos = new ArrayList<>();
        for (Remito r : bolsin.getRemitos()) {
            List<Map<String, Object>> docs = r.getDetalleRemito().stream().map(det -> {
                Documentacion d = det.getDocumentacion();
                return Map.<String, Object>of("id", d.getId(), "numero", d.getNumero(),
                        "asunto", d.getAsunto(), "tipoDocumentacion", d.getTipoDoc().getNombre(),
                        "areaCMCDestino", det.getAreaCMCDestino(), "estadoActual", d.getEstadoActual().getNombre());
            }).toList();
            remitos.add(Map.of("numero", r.getNumero(), "fecha", r.getFecha().toString(),
                    "estadoActual", r.getEstado().getNombre(), "documentaciones", docs));
        }
        return Map.of("bolsin", buscarDatosBolsin(bolsin), "remitos", remitos,
                "esCMC", empleado.obtenerCM().isEsCMC());
    }

    private Map<Long, OpcionRecepcion> validarSolicitud(Bolsin bolsin, Empleado empleado, SolicitudRecepcion solicitud) {
        if (solicitud.opcion() == null) throw new IllegalArgumentException("Seleccione una opción de recepción");
        if (solicitud.opcion() == OpcionRecepcion.REDIRIGIR && !empleado.obtenerCM().isEsCMC())
            throw new IllegalArgumentException("Solo la CMC puede redirigir documentación a otra área");
        Set<Long> afectados = solicitud.documentosAfectados() == null ? Set.of() : solicitud.documentosAfectados();
        if (solicitud.opcion() == OpcionRecepcion.ACEPTAR && !afectados.isEmpty())
            throw new IllegalArgumentException("La recepción completa no admite documentos con incidencias");
        if (solicitud.opcion() != OpcionRecepcion.ACEPTAR && afectados.isEmpty())
            throw new IllegalArgumentException("Seleccione los documentos afectados por la situación indicada");
        if (bolsin.getRemitos().isEmpty()) throw new IllegalStateException("El bolsín no contiene remitos");
        Map<Long, OpcionRecepcion> opciones = new LinkedHashMap<>();
        for (Remito r : bolsin.getRemitos()) {
            if (r.getDetalleRemito().isEmpty()) throw new IllegalStateException("El remito no contiene documentación");
            for (DetalleRemito det : r.getDetalleRemito()) {
                Long id = det.getDocumentacion().getId();
                if (opciones.put(id, afectados.contains(id) ? solicitud.opcion() : OpcionRecepcion.ACEPTAR) != null)
                    throw new IllegalStateException("Hay documentación repetida en el bolsín");
            }
        }
        if (!opciones.keySet().containsAll(afectados))
            throw new IllegalArgumentException("Los documentos seleccionados no pertenecen al bolsín");
        return opciones;
    }

    public Map<String, Object> tomarOpcRecepcion(SolicitudRecepcion solicitud) {
        Empleado empleado = buscarEmpleado();
        Bolsin bolsin = buscarBolsin(solicitud.bolsinId(), empleado);
        Map<Long, OpcionRecepcion> opciones = validarSolicitud(bolsin, empleado, solicitud);
        long cantidad = opciones.values().stream().filter(o -> o != OpcionRecepcion.ACEPTAR).count();
        return Map.of("mensajeConfirmacion", "¿Confirma la recepción del bolsín N° " + bolsin.getNumeroBolsin()
                + " con la opción " + solicitud.opcion() + "? Documentos afectados: " + cantidad
                + ". Los restantes se registrarán como recibidos y aceptados.");
    }

    @Transactional
    public Map<String, Object> tomarConfirmacion(SolicitudRecepcion solicitud) {
        Empleado empleado = buscarEmpleado();
        Bolsin bolsin = buscarBolsin(solicitud.bolsinId(), empleado);
        Map<Long, OpcionRecepcion> opciones = validarSolicitud(bolsin, empleado, solicitud);
        LocalDateTime fecha = LocalDateTime.now();
        bolsin.recibirYAceptarBolsin(fecha, empleado, opciones);
        bolsines.saveAndFlush(bolsin);
        NotificacionRecepcion notificacion = notificaciones.registrarNotificacion(bolsin, fecha);
        return construirResumen(bolsin, fecha, notificacion);
    }

    private Map<String, Object> construirResumen(Bolsin b, LocalDateTime fecha, NotificacionRecepcion notificacion) {
        List<Map<String, Object>> historial = b.getCambioEstadoBolsin().stream().map(ce ->
                Map.<String, Object>of("estado", ce.getEstado().getNombre(), "inicio", ce.getFechaHoraInicio().toString(),
                        "fin", ce.getFechaHoraFin() == null ? "ACTUAL" : ce.getFechaHoraFin().toString(),
                        "responsable", ce.getEmpleado() == null ? "Sin registro previo" : ce.getEmpleado().getNombreCompleto())).toList();
        List<Map<String, Object>> docs = new ArrayList<>();
        for (Remito r : b.getRemitos()) for (DetalleRemito det : r.getDetalleRemito()) {
            Documentacion d = det.getDocumentacion();
            CambioEstadoDocumentacion actual = d.getCambioEstadoDocumentacion().stream()
                    .filter(CambioEstadoDocumentacion::sosActual).findFirst().orElseThrow();
            docs.add(Map.of("numeroDoc", d.getNumero(), "asunto", d.getAsunto(),
                    "nuevoEstado", d.getEstadoActual().getNombre(), "remitoEstado", r.getEstado().getNombre(),
                    "responsable", actual.getEmpleado().getNombreCompleto()));
        }
        return Map.of("mensaje", "Recepción del bolsín N° " + b.getNumeroBolsin() + " registrada correctamente.",
                "fechaHoraRegistro", fecha.toString(), "nuevoEstadoBolsin", b.getEstadoActual().getNombre(),
                "historialEstadosBolsin", historial, "documentacionesActualizadas", docs,
                "notificacion", Map.of("id", notificacion.getId(), "estado", notificacion.getEstado(),
                        "destinatario", notificacion.getDestinatario()));
    }
}
