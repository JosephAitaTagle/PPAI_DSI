# Diagrama de secuencia — CU Registrar Recepción de Bolsín (tramo de confirmación)

Especificación extraída del diagrama de secuencia (Visual Paradigm).
Complementa `modelo-clases-recepcion-bolsin.md`. **Si hay conflicto, este archivo manda sobre las firmas.**
Cubre solo el tramo desde `tomarConfirmacion()` hasta la actualización de estados (bolsín → remitos → documentación).

## Convenciones

- Implementar el código para que la secuencia de llamadas sea **exactamente** la de la sección 2.
- `*` en un mensaje = se invoca iterando sobre una colección.
- `this` = el objeto contexto se pasa a su estado (patrón State).
- "self" = el objeto se llama a sí mismo.
- `new` = creación del objeto en esa línea.

---

## 1. Participantes (lifelines)

| Instancia | Clase | Rol |
|---|---|---|
| `:PantallaAdmRecpBolsin` | `PantallaAdmRecpBolsin` «boundary» | Pantalla |
| `:GestorRecpBolsin` | `GestorRecpBolsin` «control» | Gestor del CU |
| `Sel:Bolsin` | `Bolsin` | Bolsín seleccionado |
| `:Enviado` | `estados.bolsin.Enviado` | Estado actual del bolsín |
| `:CambioEstadoBolsin` | `CambioEstadoBolsin` | Colección de cambios (iteración) |
| `Actual:CambioEstadoBolsin` | `CambioEstadoBolsin` | Cambio vigente |
| `nuevo:RecibidoEnCMDestino` | `estados.bolsin.RecibidoEnCMDestino` | Estado nuevo (creado) |
| `nuevo:CambioEstadoBolsin` | `CambioEstadoBolsin` | Cambio nuevo (creado) |
| `:Remito` | `Remito` | Remito del bolsín |
| `:EnBolsinEnviado` (remito) | `estados.remito.EnBolsinEnviado` | Estado actual del remito |
| `nuevo:RecibidoYAceptado` | `estados.remito.RecibidoYAceptado` | Estado nuevo (creado) |
| `:DetalleRemito` | `DetalleRemito` | Detalle del remito |
| `:Documentacion` | `Documentacion` | Documentación del detalle |
| `:EnBolsinEnviado` (doc) | `estados.documentacion.EnBolsinEnviado` | Estado actual de la doc |
| `:CambioEstadoDocumentacion` | `CambioEstadoDocumentacion` | Colección de cambios (iteración) |
| `Actual:CambioEstadoDocumentacion` | `CambioEstadoDocumentacion` | Cambio vigente |
| `nuevo:RecibidaYAceptada` | `estados.documentacion.RecibidaYAceptada` | Estado nuevo (creado) |
| `nuevo:CambioEstadoDocumentacion` | `CambioEstadoDocumentacion` | Cambio nuevo (creado) |

> El boundary se llama `PantallaAdmRecpBolsin`. Usar este nombre (resuelve el nombre truncado del diagrama de clases).

---

## 2. Secuencia de mensajes

### Inicio (Pantalla → Gestor)

| Nº | Emisor → Receptor | Mensaje |
|---|---|---|
| 1 | Pantalla → Gestor | `tomarConfirmacion()` |
| 1.1 | Gestor → self | `obtenerFechaYHoraActual(): DateTime` |
| 1.2 | Gestor → self | `actualizarEstados()` |
| 1.3 | Gestor → `Sel:Bolsin` | `recibirYAceptarBolsin(fechaActual: DateTime, nombre: Empleado)` |

### Bolsín (Bolsin → Enviado)

| Nº | Emisor → Receptor | Mensaje |
|---|---|---|
| 1.3.1 | Bolsin → `:Enviado` | `recibirYAceptarBolsin(this: Bolsin, fechaActual: DateTime, nombre: Empleado, remito: Remito[], cambioEstado: CambioEstadoBolsin[])` |
| 1.3.1.1 | Enviado → self | `cambiarEstadoBolsin(fechaActual: DateTime)` |
| 1.3.1.2 | Enviado → `:CambioEstadoBolsin` | `*sosActual(): boolean` |
| 1.3.1.3 | Enviado → `Actual:CambioEstadoBolsin` | `setFechaHoraFin(fechaActual: DateTime)` |
| 1.3.1.4 | Enviado → self | `crearEstado(): RecibidoEnCMDestino` |
| 1.3.1.5 | Enviado → `nuevo:RecibidoEnCMDestino` | `new(): RecibidoEnCMDestino` |
| 1.3.1.6 | Enviado → self | `crearCambioEstadoBolsin(fechaActual: DateTime, estado: EstadoBolsin)` |
| 1.3.1.7 | Enviado → `nuevo:CambioEstadoBolsin` | `new(fechaActual: DateTime, estado: EstadoBolsin)` |

### `loop [Mientras haya remitos]` — sobre `remito: Remito[]`

| Nº | Emisor → Receptor | Mensaje |
|---|---|---|
| 1.3.1.8 | Enviado → `:Remito` | `recibirYAceptar(fechaActual: DateTime, nombre: Empleado)` |
| 1.3.1.8.1 | Remito → `:EnBolsinEnviado` | `recibirYAceptar(this: Remito, fechaActual: DateTime, nombre: Empleado, detalle: DetalleRemito[])` |
| 1.3.1.8.1.1 | EnBolsinEnviado → self | `crearEstadoRem(): RecibidoYAceptado` |
| 1.3.1.8.1.2 | EnBolsinEnviado → `nuevo:RecibidoYAceptado` | `new(): RecibidoYAceptado` |
| 1.3.1.8.1.3 | EnBolsinEnviado → self | `cambiarEstadoDocumentacion(fechaActual: DateTime)` |

#### `loop [Mientras haya documentacion]` — anidado, sobre `detalle: DetalleRemito[]`

| Nº | Emisor → Receptor | Mensaje |
|---|---|---|
| 1.3.1.8.1.4 | EnBolsinEnviado (remito) → `:DetalleRemito` | `aceptar(fechaActual: DateTime, nombre: Empleado)` |
| 2 | DetalleRemito → `:Documentacion` | `aceptar(fechaActual: DateTime, nombre: Empleado)` |
| 2.1 | Documentacion → `:EnBolsinEnviado` (doc) | `aceptar(this: Documentacion, fechaActual: DateTime, nombre: Empleado, cambioEstado: CambioEstadoDocumentacion[])` |
| 2.1.1 | EnBolsinEnviado → self | `cambiarEstadoDoc(fechaActual: DateTime)` |
| 2.1.2 | EnBolsinEnviado → `:CambioEstadoDocumentacion` | `*sosActual(): boolean` |
| 2.1.3 | EnBolsinEnviado → `Actual:CambioEstadoDocumentacion` | `setFechaHoraFin(fechaActual: DateTime)` |
| 2.1.4 | EnBolsinEnviado → self | `crearEstadoDoc(): RecibidaYAceptada` |
| 2.1.5 | EnBolsinEnviado → `nuevo:RecibidaYAceptada` | `new(): RecibidaYAceptada` |
| 2.1.6 | EnBolsinEnviado → self | `crearCambioEstadoDocumentacion(fechaActual: DateTime, estado: EstadoDocumentacion, nombre: Empleado)` |
| 2.1.7 | EnBolsinEnviado → `nuevo:CambioEstadoDocumentacion` | `new(fechaActual: DateTime, estado: EstadoDocumentacion)` |
| 2.1.8 | EnBolsinEnviado → `:Documentacion` | `setEstado(estado: EstadoDocumentacion)` |
| 2.1.9 | EnBolsinEnviado → `:Documentacion` | `agregarCambioEstado(cambioEstado: CambioEstadoDocumentacion)` |

#### fin loop documentación

| Nº | Emisor → Receptor | Mensaje |
|---|---|---|
| 1.3.1.8.1.5 | EnBolsinEnviado (remito) → `:Remito` | `setEstado(estado: EstadoRemito)` |

### fin loop remitos

| Nº | Emisor → Receptor | Mensaje |
|---|---|---|
| 1.3.1.9 | Enviado → `Sel:Bolsin` | `setEstado(estado: EstadoBolsin)` |
| 1.3.1.10 | Enviado → `Sel:Bolsin` | `agregarCambioEstado(cambioEstado: CambioEstadoBolsin)` |

---

## 3. Pseudocódigo de referencia

```text
Gestor.tomarConfirmacion():
    fechaActual = obtenerFechaYHoraActual()
    actualizarEstados():
        bolsinSel.recibirYAceptarBolsin(fechaActual, empleado)

Bolsin.recibirYAceptarBolsin(fechaActual, empleado):
    estado.recibirYAceptarBolsin(this, fechaActual, empleado, remitos, cambiosEstado)

Enviado.recibirYAceptarBolsin(bolsin, fechaActual, empleado, remitos, cambios):
    cambiarEstadoBolsin(fechaActual)        # busca sosActual() y setFechaHoraFin
    nuevoEstado = crearEstado()             # new RecibidoEnCMDestino
    nuevoCambio = crearCambioEstadoBolsin(fechaActual, nuevoEstado)
    for remito in remitos:
        remito.recibirYAceptar(fechaActual, empleado)
    bolsin.setEstado(nuevoEstado)
    bolsin.agregarCambioEstado(nuevoCambio)

Remito.recibirYAceptar(fechaActual, empleado):
    estado.recibirYAceptar(this, fechaActual, empleado, detalles)

estados.remito.EnBolsinEnviado.recibirYAceptar(remito, fechaActual, empleado, detalles):
    nuevoEstado = crearEstadoRem()          # new RecibidoYAceptado
    cambiarEstadoDocumentacion(fechaActual):
        for detalle in detalles:
            detalle.aceptar(fechaActual, empleado)
    remito.setEstado(nuevoEstado)

DetalleRemito.aceptar(fechaActual, empleado):
    documentacion.aceptar(fechaActual, empleado)

Documentacion.aceptar(fechaActual, empleado):
    estado.aceptar(this, fechaActual, empleado, cambiosEstado)

estados.documentacion.EnBolsinEnviado.aceptar(doc, fechaActual, empleado, cambios):
    cambiarEstadoDoc(fechaActual)           # busca sosActual() y setFechaHoraFin
    nuevoEstado = crearEstadoDoc()          # new RecibidaYAceptada
    nuevoCambio = crearCambioEstadoDocumentacion(fechaActual, nuevoEstado, empleado)
    doc.setEstado(nuevoEstado)
    doc.agregarCambioEstado(nuevoCambio)
```

---

## 4. Cambios respecto al diagrama de clases

Aplicar al modelo antes de implementar:

- Boundary: `PantallaAdmRecpBolsin`.
- `estados.remito.EnBolsinEnviado`: agregar `+crearEstadoRem(): RecibidoYAceptado` (no figura en el diagrama de clases).
- `DetalleRemito.aceptar()` pasa a `aceptar(fechaActual: DateTime, nombre: Empleado)`.
- `Estado` genérico se resuelve como `EstadoDocumentacion` en `CambioEstadoDocumentacion.new()` y `crearCambioEstadoDocumentacion()`.
- `Gestor.obtenerFechaYHoraActual()` devuelve `DateTime`.
- `sosActual()` devuelve `boolean` en ambas clases `CambioEstado*`.
- `Remito` cambia estado solo con `setEstado()`. No crear historial de estados de remito.

## 5. A verificar (inconsistencias del diagrama)

- Numeración: el tramo de documentación arranca en `2` y no en `1.3.1.8.1.4.1`. Tratarlo como anidado en 1.3.1.8.1.4.
- Typo: `RecibidioYAceptado` en `crearEstadoRem()`. Usar `RecibidoYAceptado`.
- `crearCambioEstadoDocumentacion` recibe `nombre: Empleado` pero `new()` de 2.1.7 no lo recibe. Decidir si `CambioEstadoDocumentacion` guarda el empleado.
- El empleado (`nombre: Empleado`) se usa en todos los niveles, pero el diagrama no muestra de dónde lo obtiene el gestor en este tramo. Asumir el atributo `usuario`/empleado logueado del gestor.
- Etiqueta `:CambioEstadoDocumentación` con tilde. Usar sin tilde.
