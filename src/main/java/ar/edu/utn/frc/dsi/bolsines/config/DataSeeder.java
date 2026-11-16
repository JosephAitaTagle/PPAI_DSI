package ar.edu.utn.frc.dsi.bolsines.config;

import ar.edu.utn.frc.dsi.bolsines.domain.*;
import ar.edu.utn.frc.dsi.bolsines.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements ApplicationRunner {

    private final EstadoRepository estadoRepo;
    private final SesionRepository sesionRepo;
    private final EmpleadoRepository empleadoRepo;
    private final BolsinRepository bolsinRepo;

    @PersistenceContext
    private EntityManager em;

    public DataSeeder(EstadoRepository estadoRepo, SesionRepository sesionRepo,
                      EmpleadoRepository empleadoRepo, BolsinRepository bolsinRepo) {
        this.estadoRepo = estadoRepo;
        this.sesionRepo = sesionRepo;
        this.empleadoRepo = empleadoRepo;
        this.bolsinRepo = bolsinRepo;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Migración no destructiva desde la adaptación anterior (estado solo en el historial).
        em.createQuery("UPDATE Bolsin b SET b.version = 0 WHERE b.version IS NULL").executeUpdate();
        em.createQuery("UPDATE Documentacion d SET d.version = 0 WHERE d.version IS NULL").executeUpdate();
        em.createNativeQuery("UPDATE bolsin SET estado_id = (SELECT estado_id FROM cambio_estado_bolsin "
                + "WHERE bolsin_id = bolsin.id AND fecha_hora_fin IS NULL) WHERE estado_id IS NULL").executeUpdate();
        em.createNativeQuery("UPDATE documentacion SET estado_id = (SELECT estado_id FROM cambio_estado_documentacion "
                + "WHERE documentacion_id = documentacion.id AND fecha_hora_fin IS NULL) WHERE estado_id IS NULL").executeUpdate();
        if (bolsinRepo.count() == 0) {
            cargarDatosIniciales();
        }
    }

    @Transactional
    public void cargarDatosIniciales() {
        em.createQuery("DELETE FROM NotificacionRecepcion").executeUpdate();
        bolsinRepo.deleteAll();
        sesionRepo.deleteAll();
        empleadoRepo.deleteAll();
        em.flush();
        em.clear();
        em.createQuery("DELETE FROM Usuario").executeUpdate();
        em.createQuery("DELETE FROM ComisionMedica").executeUpdate();
        em.createQuery("DELETE FROM TipoDocumentacion").executeUpdate();
        estadoRepo.deleteAll();
        em.flush();
        em.clear();

        ComisionMedica cmCordoba = new ComisionMedica("CM N° 05 - Córdoba (CMC)", "CM05", "Bv. Chacabuco 450", "cm05@srt.gob.ar", "0351-4221100", true);
        ComisionMedica cmVillaMaria = new ComisionMedica("CM N° 06 - Villa María", "CM06", "San Martín 310", "cm06@srt.gob.ar", "0353-4520011", false);
        ComisionMedica cmRioCuarto = new ComisionMedica("CM N° 07 - Río Cuarto", "CM07", "Sobremonte 820", "cm07@srt.gob.ar", "0358-4641234", false);
        em.persist(cmCordoba);
        em.persist(cmVillaMaria);
        em.persist(cmRioCuarto);

        Usuario user = new Usuario("jaita", "1234");
        em.persist(user);
        Empleado emp = new Empleado("Aita Tagle", "José", "joseaita96@gmail.com", user, cmCordoba);
        empleadoRepo.save(emp);
        sesionRepo.save(new Sesion(LocalDateTime.now().minusHours(1), user));

        TipoDocumentacion tipoExp = new TipoDocumentacion("Expediente Médico", "Legajo médico laboral SRT");
        TipoDocumentacion tipoDict = new TipoDocumentacion("Dictamen Comisión", "Resolución médica firmada");
        em.persist(tipoExp);
        em.persist(tipoDict);

        LocalDateTime ayer = LocalDateTime.now().minusDays(1);

        // Los constructores de Bolsin, Remito y Documentacion instancian sus estados iniciales pasándose 'this'
        Bolsin b1 = new Bolsin("PRC-2026-8841", LocalDate.now().minusDays(1), 1042, 3.45, cmVillaMaria, cmCordoba, ayer);
        Remito r1 = new Remito(LocalDate.now().minusDays(1), 5011);
        r1.getDetalleRemito().add(new DetalleRemito("Mesa de Entradas",
                new Documentacion("Determinación de Incapacidad - Expte 4412/26", LocalDate.now().minusDays(2), 9001, tipoExp, ayer)));
        r1.getDetalleRemito().add(new DetalleRemito("Junta Médica",
                new Documentacion("Apelación Dictamen - Expte 4419/26", LocalDate.now().minusDays(2), 9002, tipoDict, ayer)));
        b1.getRemitos().add(r1);

        Bolsin b2 = new Bolsin("PRC-2026-8899", LocalDate.now().minusDays(1), 1043, 1.80, cmRioCuarto, cmCordoba, ayer);
        Remito r2 = new Remito(LocalDate.now().minusDays(1), 5015);
        r2.getDetalleRemito().add(new DetalleRemito("Auditoría Médica",
                new Documentacion("Revisión Homologación - Expte 4501/26", LocalDate.now().minusDays(1), 9010, tipoExp, ayer)));
        b2.getRemitos().add(r2);

        Bolsin b3 = new Bolsin("PRC-2026-8910", LocalDate.now(), 1044, 2.10, cmVillaMaria, cmCordoba, ayer);
        Remito r3 = new Remito(LocalDate.now(), 5020);
        r3.getDetalleRemito().add(new DetalleRemito("Archivo Central",
                new Documentacion("Historia Clínica Complementaria - Expte 4520/26", LocalDate.now(), 9022, tipoExp, ayer)));
        b3.getRemitos().add(r3);

        Bolsin b4 = new Bolsin("PRC-2026-9999", LocalDate.now(), 2099, 4.00, cmVillaMaria, cmRioCuarto, ayer);

        bolsinRepo.saveAll(List.of(b1, b2, b3, b4));
    }
}
