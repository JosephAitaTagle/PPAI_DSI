# PPAI 2026 — CU28: Registrar recepción de bolsín

Diseño de Sistemas de Información, UTN FRC. Grupo 8, curso 3K2. Versión 2.0, integrada con V3 de upstream/main. Entrega 2: implementación del rediseño con patrón State sobre Documentación, conforme al alcance del CU28 de la ERS v1.2.

Java 17+, Spring Boot 3.2.5, interfaz web HTML/JavaScript, SQLite y persistencia JPA/Hibernate. Se requiere Maven 3.6+.

## Ejecutar

```bash
mvn spring-boot:run
```

Abrir http://localhost:8080. Se utiliza una única sesión de demostración precargada para el empleado José Aita Tagle de la CMC Córdoba; no se implementa autenticación. La base `bolsines.db` conserva las recepciones entre reinicios. Las bases anteriores se adaptan al arrancar: se recupera el estado activo desde el historial; no hace falta borrar el archivo. El botón **Reiniciar BD Demo** elimina los registros de demostración y vuelve a cargar los datos iniciales.

## Demostrar el CU28 - (Apretar el Reiniciar BD por las dudas)

1. Buscar los bolsines enviados a la CM del empleado. Se puede filtrar por precinto o CM origen; los filtros sin coincidencias muestran el resultado vacío (A1/A2).
2. Seleccionar el bolsín 1042, que contiene dos documentos.
3. Para el flujo principal, elegir **El contenido del bolsín es igual al registrado**, continuar y confirmar. Se aceptan todos los documentos y remitos.
4. Reiniciar la demo para repetir con otra situación. Para faltantes (A3), elegir la opción y marcar solo el documento que no llegó: ese documento queda NoRecibida, el restante se acepta y el remito queda parcial.
5. Para rechazo (A4), marcar solo el documento que no corresponde a la CM. Para redirección (A5), marcar los documentos a distribuir internamente en la CMC.
6. Mostrar los estados finales y el empleado responsable. Volver o cancelar antes de confirmar no registra cambios (A6 y observación 1).

Los contextos guardan una referencia al estado activo; los estados concretos crean los estados destino y los cambios de historial con su responsable. Las referencias inversas se reconstruyen al cargar las entidades. Las bases anteriores se adaptan al iniciar sin borrar sus datos.

El patrón State conserva las referencias inversas y las fábricas de estados de V3. Cada contexto guarda su estado activo y mantiene la trazabilidad en sus cambios de estado; las recepciones nuevas registran el empleado responsable.

La selección se mantiene en cada pestaña. El servidor vuelve a validar la solicitud completa al confirmar y no conserva selecciones globales.

## Notificación al origen: integración pendiente

La recepción invoca `GestorNotificarRecepcion`, que guarda en la misma transacción una notificación con destinatario y situación de cada documento. **El envío por email del CU29 está pendiente de integrar con un servicio de correo.** La interfaz informa “pendiente de envío”; no se envían correos reales.

## Verificar

```bash
mvn test
```

Las pruebas utilizan una base SQLite temporal independiente de `bolsines.db`. Cubren los flujos de recepción, validaciones, responsables, atomicidad, selecciones independientes y persistencia tras reiniciar y migración del esquema anterior.

La adecuación al CU28, la validación de la máquina de estados y los diagramas de diseño se incluyen en este README. Los PDF aportados permanecen sin modificar; estos diagramas sirven de base para actualizar la entrega formal.

## Implementación de la persistencia

La aplicación usa Spring Data JPA con Hibernate y el driver SQLite. La configuración está en `src/main/resources/application.properties`: `jdbc:sqlite:bolsines.db` crea o abre el archivo en el directorio desde el que se inicia la aplicación, y `spring.jpa.hibernate.ddl-auto=update` actualiza el esquema al arrancar sin recrear la base. `spring.jpa.open-in-view=false` cierra el contexto de persistencia al terminar cada operación web; los servicios cargan y procesan los datos dentro de sus transacciones.

Las clases del dominio anotadas con `@Entity` representan las tablas. Bolsines, remitos, documentación, comisiones médicas, empleados, usuarios, sesiones, tipos de documentación, estados, cambios de estado y notificaciones se guardan en la misma base. Las asociaciones JPA relacionan el bolsín con sus comisiones, remitos e historial; cada remito contiene detalles, y cada detalle referencia una documentación. Los historiales de bolsín y documentación conservan inicio, fin y empleado responsable de cada transición. El fin nulo identifica el cambio vigente.

### Estado activo e historial

`Bolsin`, `Remito` y `Documentacion` guardan una referencia directa al objeto `Estado` activo. El historial mantiene además una fila `CambioEstadoBolsin` o `CambioEstadoDocumentacion` por transición, asociada al mismo estado y con sus fechas y responsable. Así, la referencia directa permite consultar y aplicar rápidamente el estado actual, mientras el historial aporta trazabilidad. Los tipos concretos de `Estado` se almacenan en una sola tabla (`estado`) mediante herencia `SINGLE_TABLE` y un discriminador; las referencias inversas a los contextos son transitorias y se reconstruyen al cargar (`@PostLoad`) o al cambiar el estado.

`Bolsin` y `Documentacion` tienen un campo `@Version` para control optimista de concurrencia. Las colecciones de remitos e historiales del bolsín, y el historial de documentación, usan cascada y eliminación de huérfanos para persistir sus elementos junto con el agregado.

### Confirmación transaccional

La consulta y selección de un bolsín no modifican los datos. Al confirmar, `GestorRecpBolsin.tomarConfirmacion` abre una transacción, vuelve a validar el bolsín, el destino, el estado y los documentos seleccionados, ejecuta las transiciones del patrón State y guarda el agregado. En esa misma transacción registra la notificación pendiente dirigida a la comisión de origen. Si la operación falla, los cambios del estado, el historial y la notificación se revierten juntos. La notificación queda en la base como `PENDIENTE_ENVIO`; todavía no se transmite por correo.

### Inicialización y compatibilidad

`DataSeeder` corre al iniciar dentro de una transacción. Si no hay bolsines, carga el conjunto de datos de demostración; si ya hay datos, los conserva. Antes de esa decisión adapta registros de esquemas anteriores: inicializa versiones nulas y completa la referencia al estado activo de bolsines y documentación usando el cambio de historial sin fecha de fin. Es una adaptación puntual del esquema/datos al inicio, no una migración versionada. **Reiniciar BD Demo** vuelve a cargar los datos iniciales y elimina los registros de demostración, incluidas las notificaciones.


## Adecuación del CU28 a la ERS v1.2 y la Entrega 2

Referencias aportadas: ERS v1.2 (reglas de negocio, página 6; CU28, páginas 11–12), consignas 2026 (Entrega 2, página 4) y diagrama de clases Rediseño v2.1. Este documento registra los cambios que deben trasladarse a los modelos de entrega; no modifica los PDF originales ni reemplaza la presentación formal con carátula.

## Alcance y decisiones

El grupo 8 implementa CU28 y aplica State a Documentación. Se conserva la jerarquía común `Estado` del diagrama, con estados concretos para Bolsín, Remito y Documentación. La integración de V3 (`upstream/main`, commit `da63ebc`) agrega una referencia directa al estado activo y una referencia inversa transitoria desde el estado al contexto. Las entidades delegan las operaciones al estado actual; los estados concretos crean los estados destino y los cambios de estado; el gestor coordina la transacción y valida el contexto de la recepción.

La elección se realiza para el bolsín y se identifican los documentos afectados. Los demás documentos se aceptan. Una recepción registra una de las situaciones enumeradas en el CU28; combinar varias situaciones diferentes en una misma recepción queda fuera de esta interfaz.

| Situación | Documentos afectados | Documentos restantes | Remito |
| --- | --- | --- | --- |
| Contenido completo | Todos recibidos y aceptados | No aplica | Recibido y aceptado |
| Faltantes | No recibidos | Recibidos y aceptados | Parcial si contiene faltantes; aceptado si no los contiene |
| No corresponden al destino | Recibidos y rechazados | Recibidos y aceptados | Parcial si contiene rechazados y aceptados; rechazado si **todos** sus documentos fueron rechazados |
| Redirección en CMC | Para redirigir | Recibidos y aceptados | Parcial si contiene documentos a redirigir; aceptado si no los contiene |

La ERS especifica que el remito queda parcial cuando el origen devuelve el documento faltante a Registrada o lo da de baja. Se adopta recepción parcial desde el registro del faltante, sin esperar esa gestión posterior, para reflejar de inmediato que el contenido no llegó completo. Se extiende el mismo criterio a cualquier incidencia individual sobre un documento del remito —rechazo (no corresponde al destino) o redirección (redistribución interna en la CMC)—: el remito queda recibido y aceptado por completo solo cuando todos sus documentos se aceptan sin observaciones; si alguno tiene un resultado distinto, el remito queda parcial. Caso particular: si **todos** los documentos de un remito se rechazan por no corresponder al destino, el remito no queda parcial sino en un estado propio, recibido y rechazado (`RecibidoYRechazadoRemito`), porque ningún documento fue efectivamente aceptado.

El bolsín pasa a RecibidoEnCMDestino en las cuatro situaciones. Las acciones posteriores del origen y la distribución interna de la CMC pertenecen a otros casos de uso: no se implementan sus operaciones completas aquí.

## Trazabilidad y persistencia

Los cambios de estado de Bolsín y Documentación incorporan una referencia `Empleado`. Los registros nuevos de recepción guardan obligatoriamente el empleado resuelto desde el contexto de la demo. Los registros iniciales o heredados pueden no tener responsable, y la interfaz lo informa.

Las escrituras del bolsín, documentos, remitos y notificación pendiente se ejecutan en la misma transacción. Una transición inválida revierte la recepción completa. `@Version` protege las entidades frente a actualizaciones sobre una versión anterior; SQLite tiene limitaciones para escrituras simultáneas. No se implementa un sistema de autenticación: se usa la única sesión activa precargada para la defensa.

`ddl-auto=update` conserva el historial al reiniciar. Para bases anteriores, Hibernate agrega las columnas faltantes sin borrar tablas; el arranque inicializa las versiones nulas y reconstruye la referencia directa al estado desde el cambio de estado actual. La columna de estado admite nulos durante esa adaptación; las operaciones de dominio exigen un estado activo. Las referencias inversas se restablecen al cargar y antes de delegar cada operación, para soportar las filas de estados compartidas por la versión anterior sin confundir documentos ni bolsines. Esto es una adaptación para el proyecto académico, no un sistema de migraciones para producción.

## Integración del CU29

`GestorNotificarRecepcion` prepara y persiste una notificación al email de la CM origen con el resultado de los remitos y cada documento. Estado: `PENDIENTE_ENVIO`. No cambia automáticamente documentos faltantes a Registrada o DeBaja: esas decisiones corresponden al origen.

**Falta integrar el transporte de correo para completar el envío exigido por el CU29.** La aplicación no informa que el correo fue enviado. No se configura ni se utiliza un servidor SMTP y las pruebas no envían mensajes reales.

## Cambios para trasladar al diagrama de clases

- Agregar la asociación desde ambos cambios de estado hacia `Empleado` (opcional para historia anterior, obligatoria en las recepciones nuevas).
- Agregar `SolicitudRecepcion(bolsinId: Long, opcion: OpcionRecepcion, documentosAfectados: Set<Long>)` y el enum de opciones.
- Quitar del gestor el estado mutable del flujo: la selección pertenece a cada pestaña y se envía en la solicitud. Cancelar descarta la selección sin escribir en el servidor.
- Las operaciones de recepción de Bolsín y Remito reciben `Map<Long, OpcionRecepcion>` para resolver las decisiones individuales.
- Agregar `esRecibidoYAceptadoParcial(): boolean` a Estado y su redefinición en el estado parcial del remito.
- Incorporar el atributo `estado: Estado` a Bolsín y Documentación, conservando los historiales. Los contextos exponen `setEstado` y `changeState` como en V3.
- Incorporar las referencias inversas transitorias de Estado a sus contextos, sus constructores y la revinculación al cargar desde JPA.
- Los estados concretos crean las instancias destino (`crearEstado`, `crearEstadoRem`, `crearEstadoDoc`) y los cambios de estado con el empleado responsable. Las asociaciones persisten esas instancias por cascada; ya no se consulta un catálogo global para transicionar.
- Agregar `GestorNotificarRecepcion`, `NotificacionRecepcion` y su repositorio como integración pendiente de transporte del CU29.

```mermaid
classDiagram
    Bolsin "1" *-- "1..*" CambioEstadoBolsin
    Bolsin "1" *-- "1..*" Remito
    Remito "1" *-- "1..*" DetalleRemito
    DetalleRemito --> Documentacion
    Documentacion "1" *-- "1..*" CambioEstadoDocumentacion
    CambioEstadoBolsin --> Estado
    CambioEstadoDocumentacion --> Estado
    CambioEstadoBolsin --> Empleado : responsable
    CambioEstadoDocumentacion --> Empleado : responsable
    Remito --> Estado : estado activo
    Bolsin --> Estado : estado activo
    Documentacion --> Estado : estado activo
    Estado ..> Bolsin : contexto transitorio
    Estado ..> Remito : contexto transitorio
    Estado ..> Documentacion : contexto transitorio
    Estado <|-- EnBolsinEnviadoDoc
    Estado <|-- RecibidaYAceptada
    Estado <|-- NoRecibida
    Estado <|-- RecibidaYRechazada
    Estado <|-- ParaRedirigir
    PantallaAdmRecpBolsin --> GestorRecpBolsin
    GestorRecpBolsin --> SolicitudRecepcion
    GestorRecpBolsin --> Bolsin
    GestorRecpBolsin --> GestorNotificarRecepcion
    GestorNotificarRecepcion --> NotificacionRecepcion
```

## Secuencia del escenario rediseñado

```mermaid
sequenceDiagram
    actor EB as Encargado de Bolsines
    participant UI as Interfaz web
    participant G as GestorRecpBolsin
    participant B as Bolsin
    participant BE as Enviado
    participant R as Remito / EnBolsinEnviadoRemito
    participant D as DetalleRemito
    participant DOC as Documentacion
    participant E as EnBolsinEnviadoDoc
    participant N as GestorNotificarRecepcion
    EB->>UI: Elegir situación y documentos afectados
    UI->>G: tomarOpcRecepcion(solicitud)
    G-->>UI: Resumen validado para confirmación
    EB->>UI: Confirmar
    UI->>G: tomarConfirmacion(solicitud)
    Note over G,N: Una transacción para recepción y notificación pendiente
    G->>G: Validar empleado, CM, estado, opción y pertenencia de documentos
    G->>B: recibirYAceptarBolsin(fecha, empleado, opciones)
    B->>BE: recibirYAceptarBolsin(...)
    BE->>B: cerrarCambioEstadoActual(fecha)
    BE->>BE: crearEstado y crearCambioEstadoBolsin con empleado
    BE->>R: recibir(fecha, empleado, opciones)
    loop Cada documento
        R->>D: procesarRecepcion(fecha, empleado, opción individual)
        D->>DOC: aceptar / denegar / rechazar / redirigir
        DOC->>E: Delegar operación
        E->>DOC: cerrarCambioEstadoActual(fecha)
    E->>E: Crear estado destino y cambio de estado con empleado
    E->>DOC: setEstado(destino) y agregarCambioEstado(cambio)
    end
    R->>R: Asignar estado aceptado o parcial según faltantes
    BE->>B: setEstado(destino) y agregarCambioEstado(cambio)
    G->>N: registrarNotificacion(bolsín, fecha)
    N-->>G: Notificación pendiente persistida
    G-->>UI: Resultado y responsables
```

## Validación y defensa

`mvn test` ejecuta pruebas con SQLite temporal: recepción completa; faltantes; rechazo; redirección; validación de CMC y CM destino; documentos ajenos; confirmación repetida; selecciones independientes; cancelación sin escritura; rollback de la cascada; búsquedas sin resultados conservación tras reiniciar y migración desde el esquema anterior con estados compartidos.

Para demostrar el patrón: seguir `Documentacion.aceptar` hasta `EnBolsinEnviadoDoc.aceptar` y la creación del historial. En una recepción con incidencias, comparar el documento seleccionado con el restante y mostrar el responsable persistido.

## Validación de la máquina de estados

## Clases e IDs

Las clases de estado del diagrama de Bolsín están presentes: `CreadoBolsin`, `CerradoBolsin`, `Enviado`, `RecibidoEnCMDestino` y `DeBajaBolsin`. Sus IDs son IDs de base de datos asignados al cargar la demo; se muestran en **Ver historial de todos los bolsines** junto al ID del Bolsín y del cambio de estado. No hay IDs numéricos permanentes para clases Java.

La máquina de estados de Remito también contiene `CreadoRemito`, `CanceladoRemito`, `EnBolsinSalienteRemito`, `EnBolsinEnviadoRemito`, `RecibidoYAceptadoRemito` y `RecibidoYAceptadoParcialRemito`.

## Transiciones del diagrama de Bolsín

| Transición | Resultado de la revisión |
| --- | --- |
| Inicio → Creado | El estado está modelado; la creación de bolsín inicializa en `Enviado`, no implementa esta secuencia de CU19. |
| Creado → Creado (modificar) | No implementada como operación de estado. |
| Creado → Cerrado | El estado existe; falta la operación de cierre. |
| Creado/Cerrado → DeBaja | El estado existe; falta la operación de baja. |
| Cerrado → Creado (reabrir) | Falta la operación de reapertura. |
| Enviado → RecibidoEnCMDestino | Implementada al confirmar la recepción CU28. |
| RecibidoEnCMDestino → final | El diagrama termina el ciclo; no se modela otra transición. |

La demo incorpora 15 bolsines adicionales con secuencias históricas válidas: Creado → Cerrado → Enviado (10), Creado → Cerrado → Enviado → RecibidoEnCMDestino (3) y Creado → Cerrado → DeBaja (2). En total son 48 cambios de estado nuevos, con IDs y marcas de tiempo consultables desde el historial. Los 15 casos apuntan a Río Cuarto para no interferir con los bolsines de CU28 precargados para Córdoba.

## Alcance comprobado

El patrón State está operativo para la transición de recepción CU28 y se persisten los estados activos e historial. Las otras operaciones del diagrama de Bolsín (generar, modificar, cerrar, reabrir y dar de baja) todavía no tienen métodos de transición ni endpoints; los registros de demo sirven para mostrar sus pasos históricos, no para ejecutar esas operaciones. Los estados de Remito y Documentación adicionales se usan para los resultados de recepción CU28, pero el ciclo completo de Remito del segundo diagrama tampoco está expuesto como CRUD independiente.
