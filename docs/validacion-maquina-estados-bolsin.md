# Validación de la máquina de estados

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
