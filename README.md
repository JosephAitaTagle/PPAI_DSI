# PPAI 2026 — CU28: Registrar recepción de bolsín

Diseño de Sistemas de Información, UTN FRC. Grupo 8, curso 3K2. Versión 2.0, integrada con V3 de upstream/main. Entrega 2: implementación del rediseño con patrón State sobre Documentación, conforme al alcance del CU28 de la ERS v1.2.

Java 17+, Spring Boot 3.2.5, interfaz web HTML/JavaScript, SQLite y persistencia JPA/Hibernate. Se requiere Maven 3.6+.

## Ejecutar

```bash
mvn spring-boot:run
```

Abrir http://localhost:8080. Se utiliza una única sesión de demostración precargada para el empleado José Aita Tagle de la CMC Córdoba; no se implementa autenticación. La base `bolsines.db` conserva las recepciones entre reinicios. Las bases anteriores se adaptan al arrancar: se recupera el estado activo desde el historial; no hace falta borrar el archivo. El botón **Reiniciar BD Demo** elimina los registros de demostración y vuelve a cargar los datos iniciales.

## Demostrar el CU28

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

Ver [adecuación al CU28 y cambios para los modelos](docs/adecuacion-cu28.md), con decisiones de interpretación, diagrama de relaciones y secuencia del escenario rediseñado. Los PDF aportados permanecen sin modificar; estos diagramas sirven de base para actualizar la entrega formal.
