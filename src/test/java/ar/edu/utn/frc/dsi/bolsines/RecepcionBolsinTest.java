package ar.edu.utn.frc.dsi.bolsines;

import ar.edu.utn.frc.dsi.bolsines.config.DataSeeder;
import ar.edu.utn.frc.dsi.bolsines.control.*;
import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RecepcionBolsinTest {
    static ConfigurableApplicationContext app;
    static Path carpeta;
    static String[] argumentos;
    GestorRecpBolsin gestor;
    BolsinRepository bolsines;
    NotificacionRecepcionRepository notificaciones;
    TransactionTemplate transaccion;

    @BeforeAll static void iniciar() throws Exception {
        carpeta = Files.createTempDirectory("bolsines-test-");
        argumentos = new String[]{"--spring.datasource.url=jdbc:sqlite:" + carpeta.resolve("test.db"),
                "--spring.jpa.hibernate.ddl-auto=update", "--spring.main.banner-mode=off", "--logging.level.root=ERROR"};
        app = arrancar();
    }
    static ConfigurableApplicationContext arrancar() {
        return new SpringApplicationBuilder(BolsinesApplication.class).web(WebApplicationType.NONE).run(argumentos);
    }
    @AfterAll static void cerrar() throws Exception {
        if (app != null) app.close();
        try (var archivos = Files.list(carpeta)) {
            for (Path archivo : archivos.toList()) Files.deleteIfExists(archivo);
        }
        Files.deleteIfExists(carpeta);
    }
    @BeforeEach void preparar() {
        app.getBean(DataSeeder.class).cargarDatosIniciales();
        gestor = app.getBean(GestorRecpBolsin.class);
        bolsines = app.getBean(BolsinRepository.class);
        notificaciones = app.getBean(NotificacionRecepcionRepository.class);
        transaccion = new TransactionTemplate(app.getBean(PlatformTransactionManager.class));
    }
    Long bolsin(int numero) {
        return bolsines.findAll().stream().filter(b -> b.getNumeroBolsin() == numero).findFirst().orElseThrow().getId();
    }
    List<Long> documentos(Long id) {
        return transaccion.execute(t -> bolsines.findById(id).orElseThrow().getRemitos().stream()
                .flatMap(r -> r.getDetalleRemito().stream()).map(d -> d.getDocumentacion().getId()).toList());
    }
    SolicitudRecepcion solicitud(Long id, OpcionRecepcion opcion, Long... docs) {
        return new SolicitudRecepcion(id, opcion, Set.of(docs));
    }
    String estadoDoc(Long id) {
        return transaccion.execute(t -> app.getBean(EntityManager.class).find(Documentacion.class, id).getEstadoActual().getNombre());
    }
    String estadoRemito(Long id) {
        return transaccion.execute(t -> bolsines.findById(id).orElseThrow().getRemitos().get(0).getEstado().getNombre());
    }
    void sigueEnviado(Long id) {
        assertEquals(Boolean.TRUE, transaccion.execute(t -> bolsines.findById(id).orElseThrow().sosEstadoEnviado()));
    }

    @Test void flujoPrincipalPersisteResponsablesYNotificacion() {
        Long id = bolsin(1042);
        var respuesta = gestor.tomarConfirmacion(solicitud(id, OpcionRecepcion.ACEPTAR));
        assertEquals("RecibidoEnCMDestino", respuesta.get("nuevoEstadoBolsin"));
        assertEquals("RecibidoYAceptado", estadoRemito(id));
        transaccion.executeWithoutResult(t -> {
            Bolsin b = bolsines.findById(id).orElseThrow();
            assertEquals(2, b.getCambioEstadoBolsin().size());
            var actual = b.getCambioEstadoBolsin().stream().filter(CambioEstadoBolsin::sosActual).findFirst().orElseThrow();
            assertNotNull(actual.getEmpleado());
            assertEquals(actual.getEstado().getId(), b.getEstadoActual().getId());
            for (var det : b.getRemitos().get(0).getDetalleRemito()) {
                var d = det.getDocumentacion();
                assertEquals("RecibidaYAceptada", d.getEstadoActual().getNombre());
                var ce = d.getCambioEstadoDocumentacion().stream().filter(CambioEstadoDocumentacion::sosActual).findFirst().orElseThrow();
                assertEquals(actual.getEmpleado().getId(), ce.getEmpleado().getId());
                assertEquals(ce.getEstado().getId(), d.getEstadoActual().getId());
                assertEquals(1, d.getCambioEstadoDocumentacion().stream().filter(CambioEstadoDocumentacion::sosActual).count());
            }
        });
        assertEquals(1, notificaciones.count());
        var aviso = notificaciones.findAll().get(0);
        assertEquals("PENDIENTE_ENVIO", aviso.getEstado());
        assertEquals("cm06@srt.gob.ar", aviso.getDestinatario());
        assertTrue(aviso.getCuerpo().contains("RecibidaYAceptada"));
    }

    @Test void faltanteSoloAfectaLoSeleccionadoYDejaRemitoParcial() {
        Long id = bolsin(1042); var docs = documentos(id);
        gestor.tomarConfirmacion(solicitud(id, OpcionRecepcion.DENEGAR, docs.get(0)));
        assertEquals("NoRecibida", estadoDoc(docs.get(0)));
        assertEquals("RecibidaYAceptada", estadoDoc(docs.get(1)));
        assertEquals("RecibidoYAceptadoParcial", estadoRemito(id));
        assertTrue(notificaciones.findAll().get(0).getCuerpo().contains("NoRecibida"));
    }

    @Test void rechazoNoRechazaDocumentosCorrectos() {
        Long id = bolsin(1042); var docs = documentos(id);
        gestor.tomarConfirmacion(solicitud(id, OpcionRecepcion.RECHAZAR, docs.get(0)));
        assertEquals("RecibidaYRechazada", estadoDoc(docs.get(0)));
        assertEquals("RecibidaYAceptada", estadoDoc(docs.get(1)));
    }

    @Test void redirigirSoloDocumentosSeleccionadosEnCMC() {
        Long id = bolsin(1042); var docs = documentos(id);
        gestor.tomarConfirmacion(solicitud(id, OpcionRecepcion.REDIRIGIR, docs.get(1)));
        assertEquals("RecibidaYAceptada", estadoDoc(docs.get(0)));
        assertEquals("ParaRedirigir", estadoDoc(docs.get(1)));
    }

    @Test void cmNoCentralNoPuedeRedirigirAunqueManipuleLaSolicitud() {
        Long id = bolsin(1042); Long doc = documentos(id).get(0);
        transaccion.executeWithoutResult(t -> app.getBean(EntityManager.class)
                .createQuery("UPDATE ComisionMedica c SET c.esCMC = false").executeUpdate());
        assertThrows(IllegalArgumentException.class, () -> gestor.tomarConfirmacion(solicitud(id, OpcionRecepcion.REDIRIGIR, doc)));
        sigueEnviado(id);
        assertEquals(0, notificaciones.count());
    }

    @Test void noPermiteSeleccionarNiConfirmarBolsinAjeno() {
        Long ajeno = bolsin(2099);
        assertThrows(IllegalArgumentException.class, () -> gestor.tomarSeleccionBolsin(ajeno));
        assertThrows(IllegalArgumentException.class, () -> gestor.tomarConfirmacion(solicitud(ajeno, OpcionRecepcion.ACEPTAR)));
        sigueEnviado(ajeno);
    }

    @Test void documentosAjenosNoProvocanCambios() {
        Long id = bolsin(1042), otro = bolsin(1043);
        assertThrows(IllegalArgumentException.class, () -> gestor.tomarConfirmacion(
                solicitud(id, OpcionRecepcion.DENEGAR, documentos(otro).get(0))));
        sigueEnviado(id);
        assertEquals(0, notificaciones.count());
    }

    @Test void alternativasExigenDocumentosYCompletaNoAdmiteIncidencias() {
        Long id = bolsin(1042);
        assertThrows(IllegalArgumentException.class, () -> gestor.tomarConfirmacion(solicitud(id, OpcionRecepcion.DENEGAR)));
        assertThrows(IllegalArgumentException.class, () -> gestor.tomarConfirmacion(solicitud(id, OpcionRecepcion.ACEPTAR, documentos(id).get(0))));
        sigueEnviado(id);
    }

    @Test void dosPestanasNoSobrescribenSuSeleccionYCancelarNoPersiste() {
        Long primero = bolsin(1042), segundo = bolsin(1043);
        gestor.tomarSeleccionBolsin(primero);
        gestor.tomarOpcRecepcion(solicitud(primero, OpcionRecepcion.ACEPTAR));
        gestor.tomarSeleccionBolsin(segundo);
        sigueEnviado(primero); sigueEnviado(segundo);
        assertEquals(0, notificaciones.count());
        gestor.tomarConfirmacion(solicitud(primero, OpcionRecepcion.ACEPTAR));
        sigueEnviado(segundo);
    }

    @Test void confirmacionRepetidaNoDuplicaHistorialNiAviso() {
        Long id = bolsin(1042); var solicitud = solicitud(id, OpcionRecepcion.ACEPTAR);
        gestor.tomarConfirmacion(solicitud);
        assertThrows(IllegalStateException.class, () -> gestor.tomarConfirmacion(solicitud));
        assertEquals(1, notificaciones.count());
    }

    @Test void falloDuranteCascadaRevierteBolsinRemitoYDocumentos() {
        Long id = bolsin(1042); var docs = documentos(id);
        transaccion.executeWithoutResult(t -> {
            var em = app.getBean(EntityManager.class);
            em.find(Documentacion.class, docs.get(1)).aceptar(java.time.LocalDateTime.now(), null);
        });
        assertThrows(UnsupportedOperationException.class, () -> gestor.tomarConfirmacion(solicitud(id, OpcionRecepcion.ACEPTAR)));
        sigueEnviado(id);
        assertEquals("EnBolsinEnviado", estadoDoc(docs.get(0)));
        assertEquals("EnBolsinEnviado", estadoRemito(id));
        assertEquals(0, notificaciones.count());
    }

    @Test void filtrosNoEncuentranPrecintosNiOrigenInexistentes() {
        assertEquals(List.of(), gestor.selOpcRegRecpBolsin("INEXISTENTE", null).get("bolsines"));
        assertEquals(List.of(), gestor.selOpcRegRecpBolsin(null, "INEXISTENTE").get("bolsines"));
    }

    @Test void recepcionSobreviveAlReinicio() {
        Long id = bolsin(1042);
        gestor.tomarConfirmacion(solicitud(id, OpcionRecepcion.ACEPTAR));
        app.close();
        app = arrancar();
        var tx = new TransactionTemplate(app.getBean(PlatformTransactionManager.class));
        tx.executeWithoutResult(t -> {
            Bolsin b = app.getBean(BolsinRepository.class).findById(id).orElseThrow();
            assertEquals("RecibidoEnCMDestino", b.getEstadoActual().getNombre());
            assertNotNull(b.getCambioEstadoBolsin().stream().filter(CambioEstadoBolsin::sosActual).findFirst().orElseThrow().getEmpleado());
        });
        assertEquals(1, app.getBean(NotificacionRecepcionRepository.class).count());
    }
    @Test void migraBaseAnteriorConEstadosCompartidosSinBorrarHistorial() throws Exception {
        Long pendiente = bolsin(1042), recibido = bolsin(1043), otro = bolsin(1044);
        var docs = documentos(pendiente);
        gestor.tomarConfirmacion(solicitud(recibido, OpcionRecepcion.ACEPTAR));
        app.close();
        try (var conexion = java.sql.DriverManager.getConnection("jdbc:sqlite:" + carpeta.resolve("test.db"));
             var sql = conexion.createStatement()) {
            // El catálogo anterior compartía un único estado entre múltiples contextos.
            for (String[] datos : List.of(
                    new String[]{"cambio_estado_bolsin", "BOLSIN_ENVIADO"},
                    new String[]{"cambio_estado_documentacion", "DOC_EN_BOLSIN_ENVIADO"},
                    new String[]{"remito", "REMITO_EN_BOLSIN_ENVIADO"})) {
                sql.executeUpdate("UPDATE " + datos[0] + " SET estado_id = (SELECT MIN(id) FROM estado WHERE tipo_estado = '"
                        + datos[1] + "') WHERE estado_id IN (SELECT id FROM estado WHERE tipo_estado = '" + datos[1] + "')");
            }
            // Simula el esquema previo, sin referencia directa al estado ni control de versión.
            for (String tabla : List.of("bolsin", "documentacion")) {
                sql.executeUpdate("ALTER TABLE " + tabla + " DROP COLUMN estado_id");
                sql.executeUpdate("ALTER TABLE " + tabla + " DROP COLUMN version");
            }
        }
        app = arrancar();
        gestor = app.getBean(GestorRecpBolsin.class);
        bolsines = app.getBean(BolsinRepository.class);
        transaccion = new TransactionTemplate(app.getBean(PlatformTransactionManager.class));
        assertEquals("RecibidoYAceptado", estadoRemito(recibido));
        assertEquals(1, app.getBean(NotificacionRecepcionRepository.class).count());
        gestor.tomarConfirmacion(solicitud(pendiente, OpcionRecepcion.DENEGAR, docs.get(0)));
        assertEquals("NoRecibida", estadoDoc(docs.get(0)));
        assertEquals("RecibidaYAceptada", estadoDoc(docs.get(1)));
        assertEquals("RecibidoYAceptadoParcial", estadoRemito(pendiente));
        sigueEnviado(otro);
        assertEquals(2, app.getBean(NotificacionRecepcionRepository.class).count());
    }

}
