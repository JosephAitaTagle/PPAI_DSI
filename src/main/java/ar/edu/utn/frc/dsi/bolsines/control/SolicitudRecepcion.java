package ar.edu.utn.frc.dsi.bolsines.control;

import ar.edu.utn.frc.dsi.bolsines.domain.OpcionRecepcion;
import java.util.Set;

/** Los datos pertenecen a esta operación, nunca al singleton del gestor. */
public record SolicitudRecepcion(Long bolsinId, OpcionRecepcion opcion, Set<Long> documentosAfectados) {}
