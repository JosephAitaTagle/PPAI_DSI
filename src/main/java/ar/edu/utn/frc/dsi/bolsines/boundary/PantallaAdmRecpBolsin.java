package ar.edu.utn.frc.dsi.bolsines.boundary;

import ar.edu.utn.frc.dsi.bolsines.config.DataSeeder;
import ar.edu.utn.frc.dsi.bolsines.control.GestorRecpBolsin;
import ar.edu.utn.frc.dsi.bolsines.control.SolicitudRecepcion;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/cu28")
public class PantallaAdmRecpBolsin {
    private final GestorRecpBolsin gestor;
    private final DataSeeder dataSeeder;

    public PantallaAdmRecpBolsin(GestorRecpBolsin gestor, DataSeeder dataSeeder) {
        this.gestor = gestor;
        this.dataSeeder = dataSeeder;
    }

    @GetMapping("/iniciar")
    public Map<String, Object> selOpcRegRecpBolsin(
            @RequestParam(required = false) String precinto, @RequestParam(required = false) String origen) {
        return gestor.selOpcRegRecpBolsin(precinto, origen);
    }

    @PostMapping("/seleccionar-bolsin/{id}")
    public Map<String, Object> tomarSeleccionBolsin(@PathVariable Long id) {
        return gestor.tomarSeleccionBolsin(id);
    }

    @PostMapping("/seleccionar-opcion")
    public Map<String, Object> tomarOpcRecepcion(@RequestBody SolicitudRecepcion solicitud) {
        return gestor.tomarOpcRecepcion(solicitud);
    }

    @PostMapping("/confirmar")
    public Map<String, Object> tomarConfirmacion(@RequestBody SolicitudRecepcion solicitud) {
        return gestor.tomarConfirmacion(solicitud);
    }

    @PostMapping("/reset-demo")
    public Map<String, String> resetDemo() {
        dataSeeder.cargarDatosIniciales();
        return Map.of("mensaje", "Base de datos reiniciada con datos de prueba.");
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, UnsupportedOperationException.class})
    public ResponseEntity<?> errorDeOperacion(RuntimeException error) {
        return ResponseEntity.badRequest().body(Map.of("error", error.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> solicitudInvalida() {
        return ResponseEntity.badRequest().body(Map.of("error", "La solicitud de recepción no es válida"));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<?> recepcionConcurrente() {
        return ResponseEntity.status(409).body(Map.of("error", "El bolsín fue modificado. Actualice el listado."));
    }
}
