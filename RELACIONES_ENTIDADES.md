# 🔗 ACTUALIZACIÓN: RELACIONES ENTRE ENTIDADES

## 📊 CAMBIOS REALIZADOS EN EL BACKEND

### 1. Nueva Estructura de Base de Datos

**Antes:**
- `ordenes` tenía `items` como TEXT (JSON serializado)
- No había relaciones formales entre tablas

**Ahora:**
```sql
-- Tabla usuarios (ID como BIGINT)
usuarios (
    id BIGINT PRIMARY KEY
)

-- Tabla ordenes (con FK a usuarios)
ordenes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuarios(id),
    total NUMERIC,
    estado TEXT,
    created_at TIMESTAMP
)

-- Tabla orden_items (con FK a ordenes y productos)
orden_items (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT REFERENCES ordenes(id),
    producto_id BIGINT REFERENCES productos(id),
    cantidad INTEGER,
    precio_unitario NUMERIC
)
```

### 2. Nuevas Entidades en Spring Boot

**OrdenItem.java:**
- Relación ManyToOne con Orden
- Relación ManyToOne con Producto
- Campos: cantidad, precioUnitario

**Orden.java (modificada):**
- Relación ManyToOne con Usuario
- Relación OneToMany con OrdenItem
- Ya no usa campo `items` como TEXT

### 3. Controlador Actualizado

**OrdenController.java:**
- Recibe `usuarioId` como Long
- Recibe lista de `OrdenItemRequest`
- Calcula total automáticamente
- Crea relaciones en cascada

---

## ✅ CAMBIOS REALIZADOS EN ANDROID

### 1. **ApiModels.kt** (Actualizado)

**Nuevos modelos:**

```kotlin
// Item individual de una orden
data class OrdenItemRequest(
    val productoId: Long,
    val cantidad: Int,
    val precioUnitario: Double
)

data class OrdenItemResponse(
    val id: Long,
    val producto: ProductResponse,
    val cantidad: Int,
    val precioUnitario: Double
)

// Orden actualizada
data class OrderResponse(
    val id: Long,
    val usuario: UsuarioResponse?,  // ← NUEVO: Objeto completo
    val items: List<OrdenItemResponse>,  // ← NUEVO: Lista de objetos
    val total: Double,
    val estado: String,
    val createdAt: String?
)

data class CreateOrderRequest(
    val usuarioId: Long,  // ← CAMBIADO: String → Long
    val items: List<OrdenItemRequest>,  // ← CAMBIADO: String → Lista
    val estado: String = "PAGADO"
)
```

### 2. **OrdersViewModel.kt** (Refactorizado)

**Cambios clave:**

```kotlin
// Ahora recibe Long en lugar de String
fun addOrder(usuarioId: Long, items: List<CartItem>, total: Double)

// Convierte CartItems a OrdenItemRequest
val ordenItems = items.map { cartItem ->
    OrdenItemRequest(
        productoId = cartItem.product.id.toLong(),
        cantidad = cartItem.quantity,
        precioUnitario = cartItem.product.price
    )
}

// Al cargar órdenes, convierte OrdenItemResponse a CartItem
val items = orderResponse.items.map { ordenItem ->
    CartItem(
        product = ordenItem.producto.toProduct(),
        quantity = ordenItem.cantidad
    )
}
```

**Nuevo campo en Order:**
```kotlin
data class Order(
    val id: Long,
    val userId: String,
    val userName: String,  // ← NUEVO: nombre del usuario
    val items: List<CartItem>,
    val total: Double,
    val timestamp: Long,
    val status: String
)
```

### 3. **CartViewModel.kt** (Actualizado)

**Cambio en checkout:**
```kotlin
fun checkout(userId: String, ordersViewModel: OrdersViewModel) {
    // Convierte String a Long
    val userIdLong = userId.toLongOrNull()
    
    ordersViewModel.addOrder(userIdLong, items, total)
    clearCart()
}
```

### 4. **OrdersScreen.kt** (Actualizado)

**Ahora muestra el nombre del usuario:**
```kotlin
if (isAdmin) {
    Text(text = "Cliente: ${order.userName}")  // Antes: order.userId
}
```

---

## 🔄 FLUJO COMPLETO DE CREACIÓN DE ORDEN

### Antes (JSON serializado):

```
CartScreen 
  → CartViewModel.checkout(userId: String, items, total)
  → OrdersViewModel.addOrder()
  → POST /api/ordenes
     Body: {
       "usuarioId": "123",
       "items": "[{\"product\":{...},\"quantity\":2}]",  ← JSON string
       "total": 50000,
       "estado": "PAGADO"
     }
```

### Ahora (Relaciones formales):

```
CartScreen 
  → CartViewModel.checkout(userId: String)
  → Convierte userId String → Long
  → OrdersViewModel.addOrder(usuarioId: Long, items, total)
  → Convierte CartItem → OrdenItemRequest
  → POST /api/ordenes
     Body: {
       "usuarioId": 123,  ← Long
       "items": [  ← Array de objetos
         {
           "productoId": 1,
           "cantidad": 2,
           "precioUnitario": 25000.0
         }
       ],
       "estado": "PAGADO"
     }
  
Backend Spring Boot:
  → Crea Orden con relación a Usuario
  → Crea OrdenItem para cada item con relación a Producto
  → Calcula total automáticamente
  → Guarda en cascada en 3 tablas:
     - ordenes
     - orden_items
     - (usuarios y productos ya existen)
```

---

## 🧪 PRUEBAS ACTUALIZADAS

### PRUEBA 1: Crear Orden desde Carrito

1. Login como usuario registrado
2. Agregar productos al carrito
3. Ir a CartScreen
4. Click "Realizar Compra"

**Verificar en Logcat:**
```
CartViewModel: ✅ Checkout iniciado: 2 items, total: $50000.0, usuario: 1
OrdersViewModel: 📤 Creando orden para usuario: 1 con 2 items
OrdersViewModel: ✅ Orden creada exitosamente: 5
```

**Verificar en Supabase:**
- Tabla `ordenes`: nueva fila con `usuario_id = 1`
- Tabla `orden_items`: 2 filas con `orden_id = 5` y `producto_id` respectivos

---

### PRUEBA 2: Ver Órdenes como Admin

1. Login como admin (`admin@tienda.com` / `admin123`)
2. Panel Admin → Pestaña "Órdenes"

**Resultado esperado:**
- ✅ Lista de todas las órdenes
- ✅ Cada orden muestra: "Cliente: [Nombre del usuario]"
- ✅ No solo el ID, sino el nombre real

---

### PRUEBA 3: Ver Órdenes como Usuario

1. Login como usuario normal
2. Icono de estrella (Orders)

**Resultado esperado:**
- ✅ Solo las órdenes de ese usuario
- ✅ Items detallados con nombre de producto
- ✅ Total correcto

---

## 🚨 POSIBLES ERRORES Y SOLUCIONES

### Error: "Cannot invoke Long value of String"

**Causa:** userId sigue siendo String en algún lugar  
**Solución:** Verificar que AuthViewModel guarde el ID como String pero que se convierta a Long al crear orden

### Error 400: "usuarioId required"

**Causa:** Conversión de String a Long falla  
**Solución:** Verificar que el usuario tenga un ID numérico válido en DataStore

### Error 500: "FK constraint violation"

**Causa:** Usuario o Producto no existen en la BD  
**Solución:**
- Verificar que el usuario esté registrado en tabla `usuarios`
- Verificar que los productos existan en tabla `productos`

### Órdenes no se ven en Admin

**Causa:** Problema al parsear la respuesta con relaciones  
**Solución:**
- Revisar logs: `OrdersViewModel: Error parseando orden`
- Verificar que Jackson esté serializing correctamente

---

## ✅ VENTAJAS DE LAS RELACIONES

### Antes (JSON):
- ❌ Items como texto plano
- ❌ Sin validación de FK
- ❌ Difícil hacer queries complejas
- ❌ Datos duplicados

### Ahora (Relaciones):
- ✅ Integridad referencial garantizada
- ✅ Queries SQL eficientes
- ✅ Cascada de eliminación
- ✅ Joins automáticos
- ✅ Nombre de usuario y productos disponibles
- ✅ Histórico de precios (precioUnitario)

---

## 📋 CHECKLIST DE VERIFICACIÓN

### Backend
- [x] Tabla `usuarios` con BIGINT
- [x] Tabla `ordenes` con FK a usuarios
- [x] Tabla `orden_items` creada
- [x] OrdenItem.java con relaciones
- [x] Orden.java actualizada
- [x] OrdenController con nueva lógica

### Android
- [x] ApiModels.kt con OrdenItemRequest/Response
- [x] OrdersViewModel con conversiones
- [x] CartViewModel con Long conversion
- [x] OrdersScreen mostrando userName

### Por Probar
- [ ] Crear orden desde carrito
- [ ] Ver órdenes como admin (con nombres)
- [ ] Ver órdenes como usuario
- [ ] Verificar datos en Supabase

---

## 🎯 PRÓXIMO PASO

**Ejecuta la app y prueba:**

1. **Build > Clean Project**
2. **Build > Make Project**
3. **Run**
4. Realizar una compra
5. Ver la orden en Admin
6. Verificar que muestra el nombre del cliente, no solo el ID

**Reporta:**
- ✅ Si la orden se crea correctamente
- ✅ Si aparece en Supabase en 2 tablas (ordenes + orden_items)
- ✅ Si el admin ve el nombre del cliente

---

**Fecha:** 2025-11-30  
**Estado:** Relaciones entre entidades implementadas  
**Versión:** 2.0 con relaciones formales

