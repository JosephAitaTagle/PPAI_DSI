# Modelo de clases — CU Registrar Recepción de Bolsín

Especificación extraída del diagrama de clases (Visual Paradigm).
Usar como fuente de verdad para firmas, atributos y relaciones.
Patrón aplicado: **State** para `Bolsin`, `Remito` y `Documentacion`, con historial vía clases `CambioEstado*`.

## Convenciones

- `-` atributo privado, `+` método público.
- Tipos no indicados en el diagrama: inferir el más razonable (String, int, DateTime) sin agregar métodos nuevos.
- No agregar métodos ni atributos que no estén listados acá. Si hacen falta getters/setters triviales para compilar, agregarlos y marcarlos con un comentario `// auxiliar, no está en el diagrama`.
- Hay nombres de estado repetidos entre jerarquías (`Creado`, `DeBaja`, `EnBolsinSaliente`, `EnBolsinEnviado`). Separarlos en paquetes/módulos distintos:
  - `estados.bolsin`
  - `estados.remito`
  - `estados.documentacion`

---

## 1. Capa de interfaz y control

### `PantallaRecpBolsin` «boundary»
> El nombre aparece truncado en el diagrama. Confirmar.

Atributos:
- `-btnConfirmar`
- `-selectBolsin`
- `-selectRecepcion`

Métodos:
- `+selOpcRegRecpBolsin()`
- `+habilitarPantalla()`
- `+mostrarCM()`
- `+mostrarDatosBolsin()`
- `+solicitarSeleccion()`
- `+tomarSeleccionBolsin()`
- `+mostrarDatosRemitoYDoc()`
- `+mostrarOpcRecepcion()`
- `+solicitarOpcRecepcion()`
- `+tomarOpcRecepcion()`
- `+solicitarConfirmacion()`
- `+tomarConfirmacion()`
- `+informarEjecucion()`

Relaciones:
- Dependencia → `GestorRecpBolsin`

### `GestorRecpBolsin` «control»

Atributos:
- `-usuario`
- `-nombreCM`
- `-bolsin`
- `-fechaHoraActual`
- `-estados`

Métodos:
- `+selOpcRegRecpBolsin()`
- `+buscarCM()`
- `+buscarBolsines()`
- `+buscarDatosBolsin()`
- `+tomarSeleccionBolsin()`
- `+mostrarDatosRemitoYDoc()`
- `+tomarOpcRecepcion()`
- `+tomarConfirmacion()`
- `+obtenerFechaYHoraActual()`
- `+actualizarEstados()`
- `+llamarCU29()`
- `+finCU()`

Relaciones (dependencias, flechas punteadas):
- → `PantallaRecpBolsin`
- → `Sesion`
- → `Empleado`
- → `Bolsin`
- → `EstadoBolsin`

---

## 2. Entidades

### `Sesion`
Atributos: `-fechaHoraInicio`, `-fechaHoraFin`
Métodos:
- `+getUsuario()`

Relaciones:
- `usuario` → `Usuario` (1)

### `Usuario`
Atributos: `-nombre`, `-contraseña`
Métodos: ninguno.

### `Empleado`
Atributos: `-apellido`, `-nombre`, `-email`
Métodos:
- `+esTuUsuario()`
- `+obtenerCM()`

Relaciones:
- `usuario` → `Usuario` (1)
- → `ComisionMedica` (1) — inferido de `obtenerCM()`, verificar.

### `ComisionMedica`
Atributos: `-nombre`, `-codigo`, `-direccion`, `-email`, `-telefono`
Métodos:
- `+getNombre()`

### `Bolsin`
Atributos: `-numeroPrecinto`, `-fecha`, `-numeroBolsin`, `-peso`
Métodos:
- `+sosEstadoEnviado()`
- `+esTuCmDestino()`
- `+getCMOrigen()`
- `+getNumeroPrecinto()`
- `+getDatosRemito()`
- `+recibirYAceptarBolsin(fechaActual: DateTime, nombre: Empleado)`
- `+setEstado(estado: EstadoBolsin)`
- `+agregarCambioEstado(cambioEstado: CambioEstadoBolsin)`
- `+actualizarBolsin()`
- `+darDeBaja()`
- `+cerrar()`
- `+reabrir()`
- `+enviar()`

Relaciones:
- `origen` → `ComisionMedica` (1)
- `destino` → `ComisionMedica` (1)
- `estado` → `EstadoBolsin` (1) — estado actual
- `cambioEstadoBolsin` → `CambioEstadoBolsin` (1..*) — composición
- `remitos` → `Remito` (1..*)

### `CambioEstadoBolsin`
Atributos: `-fechaHoraInicio`, `-fechaHoraFin`
Métodos:
- `+sosActual()`
- `+sosEnviado()`
- `+new(fechaActual: DateTime, estado: EstadoBolsin)`
- `+setFechaHoraFin(fechaActual: DateTime)`

Relaciones:
- `estado` → `EstadoBolsin` (1)

### `Remito`
Atributos: `-fecha`, `-numero`
Métodos:
- `+getNumero()`
- `+recibirYAceptar(fechaActual: DateTime, nombre: Empleado)`
- `+setEstado(estado: EstadoRemito)`
- `+cancelar()`
- `+actualizarRemito()`
- `+agregarEnBolsin()`
- `+quitarDeBolsin()`
- `+enviarEnBolsin()`
- `+recibirYAceptarParcialmente()`

Relaciones:
- `estado` → `EstadoRemito` (1)
- `detalleRemito` → `DetalleRemito` (1..*) — composición

> Remito no tiene clase `CambioEstadoRemito` en el diagrama. No crearla.

### `DetalleRemito`
Atributos: `-areaCMCDestino`
Métodos:
- `+getDatosDoc()`
- `+aceptar()`

Relaciones:
- `documentacion` → `Documentacion` (1)

### `Documentacion`
> En el diagrama figura con tilde (`Documentación`). Usar `Documentacion` en código.

Atributos: `-asunto`, `-fechaPase`, `-numero`
Métodos:
- `+getAsunto()`
- `+getTipoDoc()`
- `+new()`
- `+remitar()`
- `+cancelarRemito()`
- `+agregarAlBolsin()`
- `+quitarDocDelBolsin()`
- `+retirar()`
- `+aceptar(fechaActual: DateTime, nombre: Empleado)`
- `+redirigir()`
- `+rechazar()`
- `+denegar()`
- `+darDeBaja()`
- `+volverRegistrar()`
- `+modificarBolsin()`
- `+mantenerBolsin()`
- `+setEstado(estado: EstadoDocumentacion)`
- `+agregarCambioEstado(cambioEstado: CambioEstadoDocumentacion)`

Relaciones:
- `tipoDocumentacion` → `TipoDocumentacion` (1)
- `estado` → `EstadoDocumentacion` (1) — estado actual
- `cambioEstadoDocumentacion` → `CambioEstadoDocumentacion` (1..*) — composición
- `destino` → `ComisionMedica` (1) — verificar en el diagrama.

### `TipoDocumentacion`
Atributos: `-nombre`, `-descripcion`
Métodos:
- `+getNombre()`

### `CambioEstadoDocumentacion`
Atributos: `-fechaHoraInicio`, `-fechaHoraFin`
Métodos:
- `+sosActual()`
- `+new(fechaActual: DateTime, estado: Estado)`
- `+setFechaHoraFin(fechaActual: DateTime)`

Relaciones:
- `estado` → `EstadoDocumentacion` (1)

> `crearCambioEstadoDocumentacion` recibe `nombre: Empleado`, pero `new()` no lo recibe ni hay atributo empleado. Ver sección "A verificar".

---

## 3. Jerarquía de estados del Bolsín (`estados.bolsin`)

### `EstadoBolsin` (abstracta)
Atributos: `-nombre`, `-descripcion`
Métodos (todos con implementación por defecto que lanza excepción / no válido en este estado):
- `+recibirYAceptarBolsin(this: Bolsin, fechaActual: DateTime, nombre: Empleado, remito: Remito[], cambioEstado: CambioEstadoBolsin[])`
- `+cambiarEstadoBolsin(fechaActual: DateTime)`
- `+crearCambioEstadoBolsin(fechaActual: DateTime, estado: EstadoBolsin)`
- `+actualizarBolsin()`
- `+darDeBaja()`
- `+cerrar()`
- `+reabrir()`
- `+enviar()`

### Subclases (heredan de `EstadoBolsin`)

| Clase | Métodos que sobrescribe / define |
|---|---|
| `Creado` | `new(): Creado`, `actualizarBolsin()`, `darDeBaja()`, `cerrar()` |
| `Cerrado` | `new(): Cerrado`, `reabrir()`, `darDeBaja()`, `enviar()` |
| `Enviado` | `new(): Enviado`, `recibirYAceptarBolsin(this: Bolsin, fechaActual: DateTime, nombre: Empleado, remito: Remito[], cambioEstadoBolsin: CambioEstadoBolsin[])`, `cambiarEstadoBolsin(fechaActual: DateTime)`, `crearEstado(): RecibidoEnCMDestino`, `crearCambioEstadoBolsin(fechaActual: DateTime, estado: EstadoBolsin)` |
| `RecibidoEnCMDestino` | `new(): RecibidoEnCMDestino` |
| `DeBaja` | `new(): DeBaja` |

> `Enviado` es el estado central del CU: implementa la recepción completa.

---

## 4. Jerarquía de estados del Remito (`estados.remito`)

### `EstadoRemito` (abstracta)
Atributos: `-nombre`, `-descripcion`
Métodos:
- `+recibirYAceptar(this: Remito, fechaActual: DateTime, nombre: Empleado, detalle: DetalleRemito[])`
- `+recibirYAceptarParcialmente()`
- `+cambiarEstadoDocumentacion(fechaActual: DateTime)`
- `+enviarBolsin()`
- `+quitarDeBolsin()`
- `+actualizarRemito()`
- `+cancelar()`
- `+agregarEnBolsin()`

### Subclases (heredan de `EstadoRemito`)

| Clase | Métodos |
|---|---|
| `Creado` | `new(): Creado`, `actualizarRemito()`, `cancelar()`, `agregarEnBolsin()` |
| `EnBolsinSaliente` | `new(): EnBolsinSaliente`, `enviarBolsin()`, `quitarDeBolsin()` |
| `EnBolsinEnviado` | `new(): EnBolsinEnviado`, `recibirYAceptar(this: Remito, fechaActual: DateTime, nombre: Empleado, detalle: DetalleRemito[])`, `recibirYAceptarParcialmente()`, `cambiarEstadoDocumentacion(fechaActual: DateTime)` |
| `RecibidoYAceptado` | `new(): RecibidoYAceptado` |
| `RecibidoYAceptadoParcial` | `new(): RecibidoYAceptadoParcial` |
| `Cancelado` | `new(): Cancelado` |

> En el diagrama aparecen como `Creado.`, `ENBolsinSaliente`, `ENBolsinEnviado` para diferenciarlos. Con paquetes separados, usar los nombres normalizados de la tabla.

---

## 5. Jerarquía de estados de la Documentación (`estados.documentacion`)

### `EstadoDocumentacion` (abstracta)
Atributos: `-nombre`, `-descripcion`
Métodos:
- `+agregarAlBolsin()`
- `+cancelarRemito()`
- `+mantenerBolsin()`
- `+modificarBolsin()`
- `+retirar()`
- `+quitarDocDelBolsin()`
- `+volverRegistrar()`
- `+darDeBaja()`
- `+denegar()`
- `+aceptar(this: Documentacion, fechaActual: DateTime, nombre: Empleado, cambioEstado: CambioEstadoDocumentacion[])`
- `+rechazar()`
- `+redirigir()`
- `+cambiarEstadoDoc(fechaActual: DateTime)`
- `+crearCambioEstadoDocumentacion(fechaActual: DateTime, estado: Estado, nombre: Empleado)`

### Subclases (heredan de `EstadoDocumentacion`)

| Clase | Métodos |
|---|---|
| `Registrada` | `new(): Registrada`, `remitar()` |
| `EnRemito` | `new(): EnRemito`, `agregarAlBolsin()`, `cancelarRemito()` |
| `EnBolsinSaliente` | `new(): EnBolsinSaliente`, `mantenerBolsin()`, `modificarBolsin()`, `retirar()`, `quitarDocDelBolsin()` |
| `EnBolsinEnviado` | `new(): EnBolsinEnviado`, `denegar()`, `aceptar(this: Documentacion, fechaActual: DateTime, nombre: Empleado, cambioEstado: CambioEstadoDocumentacion[])`, `rechazar()`, `redirigir()`, `cambiarEstadoDoc(fechaActual: DateTime)`, `crearEstadoDoc(): RecibidaYAceptada`, `crearCambioEstadoDocumentacion(fechaActual: DateTime, estado: Estado, nombre: Empleado)` |
| `RecibidaYAceptada` | `new(): RecibidaYAceptada` |
| `RecibidaYRechazada` | `new(): RecibidaYRechazada`, `volverRegistrar()` |
| `NoRecibida` | `new(): NoRecibida`, `volverRegistrar()`, `darDeBaja()` |
| `ParaRedirigir` | `new(): ParaRedirigir`, `aceptar()`, `rechazar()` |
| `DeBaja` | `new(): DeBaja` |

---

## 6. Flujo principal del CU (secuencia de delegación esperada)

1. `PantallaRecpBolsin.selOpcRegRecpBolsin()` → `GestorRecpBolsin.selOpcRegRecpBolsin()`
2. Gestor: `Sesion.getUsuario()` → `Empleado.esTuUsuario()` → `Empleado.obtenerCM()` → `ComisionMedica.getNombre()`
3. Gestor `buscarBolsines()`: por cada `Bolsin`, `sosEstadoEnviado()` y `esTuCmDestino()`; luego `getCMOrigen()`, `getNumeroPrecinto()`.
4. Usuario elige bolsín → `tomarSeleccionBolsin()` → `Bolsin.getDatosRemito()` → `Remito.getNumero()` → `DetalleRemito.getDatosDoc()` → `Documentacion.getAsunto()`, `getTipoDoc()` → `TipoDocumentacion.getNombre()`.
5. Usuario elige opción y confirma → `obtenerFechaYHoraActual()` → `actualizarEstados()`.
6. `Bolsin.recibirYAceptarBolsin(fechaActual, empleado)` → `estado.recibirYAceptarBolsin(this, ...)` (estado `Enviado`):
   - `cambiarEstadoBolsin()`: busca `CambioEstadoBolsin` actual (`sosActual()`), le hace `setFechaHoraFin()`.
   - `crearEstado()` → `RecibidoEnCMDestino`; `crearCambioEstadoBolsin()` → `new CambioEstadoBolsin(...)`; `Bolsin.setEstado()` y `agregarCambioEstado()`.
   - Por cada `Remito`: `recibirYAceptar()` → `EnBolsinEnviado.recibirYAceptar(this, ...)` → `cambiarEstadoDocumentacion()`.
   - Por cada `DetalleRemito.aceptar()` → `Documentacion.aceptar()` → `EnBolsinEnviado.aceptar(this, ...)` → `cambiarEstadoDoc()`, `crearEstadoDoc()`, `crearCambioEstadoDocumentacion()`.
7. `llamarCU29()` → `informarEjecucion()` → `finCU()`.

> Este flujo se deduce de las firmas. Si existe diagrama de secuencia, priorizarlo.

---

## 7. A verificar (inconsistencias del diagrama)

- Nombre de la boundary truncado.
- `EstadoDocumentacion` no declara `remitar()`, pero `Registrada` sí. Decidir si agregarlo a la abstracta.
- `EstadoDocumentacion` no declara `crearEstadoDoc()` ni `EstadoBolsin` declara `crearEstado()`; solo están en las subclases.
- Parámetro `estado: Estado` en `CambioEstadoDocumentacion.new()` y `crearCambioEstadoDocumentacion()`: debería ser `EstadoDocumentacion`.
- `crearCambioEstadoDocumentacion(..., nombre: Empleado)` pasa un empleado que `CambioEstadoDocumentacion.new()` no recibe. Agregar atributo `empleado` o quitar parámetro.
- `Bolsin.recibirYAceptarBolsin` y `Remito.recibirYAceptar` reciben `nombre: Empleado`: el nombre del parámetro sugiere String, el tipo dice Empleado. Respetar el tipo.
- `Remito.recibirYAceptar` del estado tiene parámetro `detalle`; en `EstadoBolsin` el último parámetro es `cambioEstado` pero en `Enviado` se llama `cambioEstadoBolsin`. Unificar.
- Destinos de las flechas `destino` (dos en el diagrama) y relación `Empleado` → `ComisionMedica`: confirmar.
