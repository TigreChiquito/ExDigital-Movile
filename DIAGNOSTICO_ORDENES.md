# 🔍 DIAGNÓSTICO: ÓRDENES NO SE GUARDAN

## ❌ PROBLEMA

Las órdenes no aparecen:
- ❌ No se guardan en Supabase
- ❌ No aparecen en el Panel de Administración

---

## 🔍 CAUSAS POSIBLES

### 1. Usuario no está logueado correctamente
### 2. userId no es válido (no es numérico)
### 3. Error al convertir items a OrdenItemRequest
### 4. Backend rechaza la petición (error 400/500)
### 5. Navegación prematura (se navega antes de crear orden)

---

## ✅ CAMBIOS REALIZADOS

### 1. **CartScreen.kt** - Logging detallado

**Ahora verás en Logcat:**
```
CartScreen: 🛒 Usuario actual: test@gmail.com, ID: 1
CartScreen: 🛒 Items en carrito: 2, Total: 50000.0
CartScreen: 🛒 Botón Checkout presionado
CartScreen: 🛒 Usuario ID: 1
CartScreen: 🛒 Items: 2
CartScreen: ✅ Llamando checkout con userId: 1
```

**Errores que detectará:**
```
CartScreen: ❌ Error: Usuario no está logueado o no tiene ID
CartScreen: ❌ Error: Carrito vacío
```

### 2. **Delay en navegación**

Antes navegaba inmediatamente, ahora espera 500ms para que se procese el checkout.

---

## 🧪 PRUEBA DE DIAGNÓSTICO

### Paso 1: Clean y Build

```bash
Build > Clean Project
Build > Make Project
Run
```

### Paso 2: Flujo completo

```
1. Login con usuario registrado
   - Email: test@gmail.com
   - Password: Test123

2. Agregar 2 productos al carrito

3. Ir a CartScreen

4. Presionar "Realizar Compra"
```

### Paso 3: Revisar Logcat

**Filtrar por:**
```
CartScreen|CartViewModel|OrdersViewModel
```

**Logs esperados en ORDEN:**

```
✅ CORRECTO:
CartScreen: 🛒 Usuario actual: test@gmail.com, ID: 1
CartScreen: 🛒 Items en carrito: 2, Total: 50000.0
CartScreen: 🛒 Botón Checkout presionado
CartScreen: 🛒 Usuario ID: 1
CartScreen: 🛒 Items: 2
CartScreen: ✅ Llamando checkout con userId: 1
CartViewModel: ✅ Checkout iniciado: 2 items, total: $50000.0, usuario: 1
OrdersViewModel: 📤 Creando orden para usuario: 1 con 2 items
OrdersViewModel: ✅ Orden creada exitosamente: 5
```

```
❌ ERROR 1: Usuario no tiene ID numérico
CartScreen: 🛒 Usuario ID: uuid-random-string-here
CartViewModel: ❌ Error: userId no es un número válido: uuid-random-string
```

```
❌ ERROR 2: Usuario no logueado
CartScreen: 🛒 Usuario actual: null, ID: null
CartScreen: ❌ Error: Usuario no está logueado o no tiene ID
```

```
❌ ERROR 3: Carrito vacío
CartScreen: 🛒 Items en carrito: 0
CartScreen: ❌ Error: Carrito vacío
```

```
❌ ERROR 4: Backend rechaza
OrdersViewModel: 📤 Creando orden para usuario: 1 con 2 items
OrdersViewModel: ❌ Error al crear orden: 400
OrdersViewModel: ❌ Error body: {...}
```

---

## 🔧 SOLUCIONES SEGÚN ERROR

### ERROR 1: Usuario tiene UUID en lugar de ID numérico

**Causa:** El usuario se creó con la versión anterior del AuthViewModel

**Solución:**
1. Cerrar sesión
2. Registrarse de nuevo (esto creará usuario en Supabase con ID numérico)
3. Login con el nuevo usuario
4. Intentar checkout

**Verificar en Logcat:**
```
AuthViewModel: ✅ Login exitoso: test@gmail.com, rol: CUSTOMER
```

Luego en CartScreen:
```
CartScreen: 🛒 Usuario ID: 1  (← Debe ser número, no UUID)
```

---

### ERROR 2: Usuario no logueado

**Causa:** DataStore no guardó el usuario

**Solución:**
1. Verificar que el login fue exitoso
2. Re-login
3. Verificar logs:
```
AuthViewModel: ✅ Login exitoso: ...
```

---

### ERROR 3: Carrito vacío

**Causa:** El carrito no tiene items

**Solución:**
1. Agregar productos al carrito desde HomeScreen
2. Verificar en CartScreen que aparecen
3. Luego hacer checkout

---

### ERROR 4: Backend rechaza la petición

**Causa:** Error en el servidor o datos inválidos

**Verificar:**
1. Logs del servidor Spring Boot
2. Que el usuario con ese ID existe en Supabase
3. Que los productos con esos IDs existen

**Revisar en OrdersViewModel:**
```
OrdersViewModel: ❌ Error body: {"message":"..."}
```

---

## 🎯 ACCIÓN INMEDIATA

### Ejecuta esto AHORA:

```bash
1. Build > Clean Project
2. Build > Make Project
3. Run
```

### Flujo de prueba:

```
1. Si ya estás logueado → LOGOUT primero

2. REGISTRO NUEVO (importante):
   - Email: prueba@gmail.com
   - Password: Prueba123
   - Nombre: Usuario Prueba
   - Crear cuenta

3. (Se loguea automáticamente)

4. Agregar 2 productos al carrito

5. Ir a carrito

6. "Realizar Compra"
```

### Copiar y pegar TODOS estos logs:

**Filtro en Logcat:**
```
Regex: (CartScreen|CartViewModel|OrdersViewModel|AuthViewModel)
```

**Buscar desde:**
- "Login exitoso"
- Hasta "Orden creada" o error

---

## 📊 CHECKLIST DE VERIFICACIÓN

Después de la prueba, verifica:

- [ ] Usuario tiene ID numérico (no UUID)
- [ ] Carrito tiene items antes de checkout
- [ ] Aparece log "Llamando checkout"
- [ ] Aparece log "Checkout iniciado"
- [ ] Aparece log "Creando orden para usuario"
- [ ] Aparece log "Orden creada exitosamente"
- [ ] Orden aparece en Supabase (tabla `ordenes`)
- [ ] Items aparecen en Supabase (tabla `orden_items`)
- [ ] Orden aparece en Panel Admin

---

## 📞 REPORTE NECESARIO

Copia y pega:

**1. Logs completos de Logcat (desde login hasta checkout):**
```
[Pegar aquí]
```

**2. ¿El usuario tiene ID numérico?**
```
CartScreen: 🛒 Usuario ID: ___
```

**3. ¿Aparece "Orden creada exitosamente"?**
- Sí/No

**4. ¿Hay algún error en rojo (❌)?**
- Cuál

---

## 🔄 FLUJO ESPERADO COMPLETO

```
1. Login
   AuthViewModel: ✅ Login exitoso: prueba@gmail.com, rol: CUSTOMER

2. Pantalla Home cargada
   CartScreen: 🛒 Usuario actual: prueba@gmail.com, ID: 2

3. Agregar productos
   CartViewModel: ✅ Producto agregado: Mouse Gaming, cantidad: 1

4. Ir a Cart
   CartScreen: 🛒 Items en carrito: 2, Total: 50000.0

5. Presionar Checkout
   CartScreen: ✅ Llamando checkout con userId: 2
   CartViewModel: ✅ Checkout iniciado: 2 items, total: $50000.0, usuario: 2
   OrdersViewModel: 📤 Creando orden para usuario: 2 con 2 items

6. Respuesta del servidor
   OrdersViewModel: ✅ Orden creada exitosamente: 3

7. Navega a Orders
   OrdersViewModel: ✅ Órdenes cargadas: 1
```

---

**Ejecuta la prueba y comparte los logs completos.**

---

**Fecha:** 2025-11-30  
**Problema:** Órdenes no se guardan  
**Cambios:** Logging detallado + validaciones + delay navegación

