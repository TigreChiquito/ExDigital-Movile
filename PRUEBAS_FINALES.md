# ✅ PRUEBAS FINALES - App Android + Backend Spring Boot

## 🎯 Estado del Proyecto

### Backend Spring Boot ✅
- ✅ Servidor corriendo en `http://localhost:8081`
- ✅ Endpoints de Productos completos (GET, POST, PUT, DELETE)
- ✅ Endpoints de Órdenes completos (GET, POST, PUT, DELETE)
- ✅ Conexión con Supabase funcionando

### Android App ✅
- ✅ Compilando sin errores
- ✅ Modelos de Request/Response configurados
- ✅ ViewModels conectados a la API
- ✅ UI completada (AdminScreen, HomeScreen, CartScreen, OrdersScreen)

---

## 🧪 PLAN DE PRUEBAS COMPLETO

### PRUEBA 1: Ver Productos desde la API ✅

**Objetivo:** Verificar que HomeScreen carga productos desde Supabase

**Pasos:**
1. Ejecutar la app Android
2. Login como usuario normal (cualquier email válido)
3. Ver la pantalla Home
4. Verificar que aparecen productos

**Verificación en Logcat:**
```
SimpleProductViewModel: ✅ Productos cargados: X
```

**Resultado esperado:**
- ✅ Se ven productos en la lista
- ✅ Tienen nombre, precio, imagen
- ❌ Si está vacío: No hay productos en Supabase

---

### PRUEBA 2: Crear Producto desde Admin ✅

**Objetivo:** Crear un producto desde el panel de administración

**Pasos:**
1. Cerrar sesión (si estás logueado)
2. Login como admin:
   - Email: `admin@exdigital.com`
   - Password: `admin123`
3. Tocar icono de Settings (arriba derecha)
4. Ir a Panel de Administración
5. Pestaña "Productos"
6. Tocar botón flotante "+"
7. Rellenar formulario:
   ```
   Nombre: Auriculares Gamer
   Precio: 35000
   Stock: 12
   URL: (dejar vacío)
   ```
8. Click "Guardar"

**Verificación en Logcat:**
```
SimpleProductViewModel: 📤 Enviando producto: nombre=Auriculares Gamer, precio=35000.0, stock=12
SimpleProductViewModel: ✅ Producto creado: Auriculares Gamer
SimpleProductViewModel: ✅ Productos cargados: X
```

**Resultado esperado:**
- ✅ Dialog se cierra
- ✅ El producto aparece inmediatamente en la lista
- ✅ Si vuelves a Home, también aparece ahí

**Si falla:**
- ❌ Error 500: Problema en el backend (revisar logs de Spring Boot)
- ❌ Error 400: Validación fallida (revisar datos)

---

### PRUEBA 3: Agregar Productos al Carrito ✅

**Objetivo:** Verificar que el carrito funciona

**Pasos:**
1. Estar en HomeScreen (como usuario normal o admin)
2. Buscar un producto
3. Tocar el botón "Agregar" en la card del producto
4. Tocar el icono del carrito (arriba derecha)
5. Verificar que el producto está en el carrito

**Verificación en Logcat:**
```
CartViewModel: 🛒 addToCart llamado: Auriculares Gamer, cantidad: 1
CartViewModel: ✅ Producto agregado: Auriculares Gamer, cantidad: 1
CartViewModel: 📊 Total items en carrito: 1
CartViewModel: 💰 Total carrito: $35000.0
```

**Resultado esperado:**
- ✅ Badge del carrito muestra el número de items
- ✅ En CartScreen aparece el producto con cantidad y subtotal
- ✅ Total del carrito es correcto

---

### PRUEBA 4: Realizar un Checkout (Crear Orden) ✅

**Objetivo:** Completar una compra y guardarla en Supabase

**Pasos:**
1. Tener productos en el carrito (PRUEBA 3)
2. En CartScreen, hacer scroll hasta el final
3. Tocar botón "Realizar Compra" o "Checkout"
4. Confirmar la compra

**Verificación en Logcat:**
```
OrdersViewModel: ✅ Orden creada exitosamente: 123
OrdersViewModel: ✅ Órdenes cargadas: X
CartViewModel: 🗑️ Carrito limpiado
```

**Resultado esperado:**
- ✅ El carrito se vacía
- ✅ Aparece un mensaje de confirmación
- ✅ La orden se guarda en Supabase

**Si falla:**
- ❌ Error 500: Problema en POST /api/ordenes
- ❌ Carrito no se limpia: Problema en CartViewModel

---

### PRUEBA 5: Ver Órdenes como Usuario ✅

**Objetivo:** Ver las compras propias

**Pasos:**
1. Login como usuario normal (el mismo que hizo la compra)
2. Tocar icono de estrella (Orders) en HomeScreen
3. Ver la pantalla de órdenes

**Verificación en Logcat:**
```
OrdersViewModel: ✅ Órdenes cargadas: X
```

**Resultado esperado:**
- ✅ Aparecen las órdenes del usuario
- ✅ Cada orden muestra: ID, fecha, total, estado
- ✅ No aparecen órdenes de otros usuarios

---

### PRUEBA 6: Ver Todas las Órdenes como Admin ✅

**Objetivo:** Panel de admin muestra todas las compras

**Pasos:**
1. Login como admin (`admin@exdigital.com` / `admin123`)
2. Ir a Panel de Administración (icono Settings)
3. Pestaña "Órdenes"

**Verificación en Logcat:**
```
OrdersViewModel: ✅ Órdenes cargadas: X
```

**Resultado esperado:**
- ✅ Aparecen TODAS las órdenes de todos los usuarios
- ✅ Cada orden muestra el usuario que compró
- ✅ Se ven órdenes de diferentes usuarios

---

## 📊 CHECKLIST DE VERIFICACIÓN FINAL

### Funcionalidades Core ✅

- [ ] **HomeScreen carga productos desde Supabase**
- [ ] **Filtrar productos por categoría funciona**
- [ ] **Admin puede crear productos**
- [ ] **Productos creados aparecen inmediatamente**
- [ ] **Agregar al carrito funciona**
- [ ] **Badge del carrito actualiza**
- [ ] **CartScreen muestra items correctos**
- [ ] **Total del carrito es correcto**
- [ ] **Checkout crea orden en Supabase**
- [ ] **Carrito se limpia después de checkout**
- [ ] **Usuario ve solo sus órdenes**
- [ ] **Admin ve todas las órdenes**

### Navegación ✅

- [ ] **Login → Home funciona**
- [ ] **Home → ProductDetail funciona**
- [ ] **Home → Cart funciona**
- [ ] **Home → Profile funciona**
- [ ] **Home → Orders funciona**
- [ ] **Admin → Panel Admin funciona**
- [ ] **Logout funciona**

### Validaciones ✅

- [ ] **Login valida email y contraseña**
- [ ] **Registro valida campos**
- [ ] **Crear producto valida campos obligatorios**
- [ ] **Checkout valida carrito no vacío**

---

## 🎯 EJECUCIÓN DE PRUEBAS

### Preparación

```bash
# 1. Asegúrate de que el servidor Spring Boot esté corriendo
# Deberías ver en la terminal:
Started MsProductosApplication...
Tomcat started on port(s): 8081

# 2. En Android Studio
Build > Clean Project
Build > Make Project
Run
```

### Orden de Ejecución

```
1. PRUEBA 1: Ver productos (2 min)
2. PRUEBA 2: Crear producto como admin (3 min)
3. PRUEBA 3: Agregar al carrito (2 min)
4. PRUEBA 4: Realizar checkout (3 min)
5. PRUEBA 5: Ver órdenes como usuario (2 min)
6. PRUEBA 6: Ver órdenes como admin (2 min)

Total estimado: 14 minutos
```

---

## 🚨 ERRORES COMUNES Y SOLUCIONES

### Error: "No hay productos"
**Causa:** La tabla productos en Supabase está vacía  
**Solución:** Crear productos desde el Admin (PRUEBA 2)

### Error: "Error 500 al crear producto"
**Causa:** Problema en el backend  
**Solución:** Revisar logs de Spring Boot, verificar conexión con Supabase

### Error: "Las órdenes no aparecen"
**Causa:** No se ha realizado ninguna compra o problema de filtrado  
**Solución:** Hacer un checkout primero (PRUEBA 4)

### Error: "El carrito está vacío después de agregar"
**Causa:** CartViewModel no persiste o problema de navegación  
**Solución:** Revisar que uses el mismo `sharedCartViewModel` en NavGraph

### Error: "App se cierra al iniciar"
**Causa:** ProductViewModel intenta usar Room  
**Solución:** Verificar que HomeScreen y AdminScreen NO usen el ProductViewModel antiguo

---

## 📱 LOGCAT - Qué Buscar

### Filtros útiles

```
# Ver solo logs de ViewModels
Regex: (SimpleProductViewModel|OrdersViewModel|CartViewModel|AuthViewModel)

# Ver solo errores
Level: Error

# Ver comunicación con API
Regex: (API_TEST|Retrofit|OkHttp)
```

### Logs de éxito

```
✅ CONEXIÓN EXITOSA!
✅ Producto creado: ...
✅ Productos cargados: X
✅ Orden creada exitosamente: ...
✅ Órdenes cargadas: X
🛒 addToCart llamado: ...
📊 Total items en carrito: X
```

### Logs de error

```
❌ Error al crear: 500
❌ Error body: ...
💀 Error de red: ...
⚠️ Checkout cancelado: carrito vacío
```

---

## ✅ CRITERIOS DE ÉXITO

El proyecto está **100% funcional** cuando:

1. ✅ Puedes ver productos desde Supabase
2. ✅ El admin puede crear productos y se guardan
3. ✅ Puedes agregar productos al carrito
4. ✅ Puedes completar una compra (checkout)
5. ✅ Las órdenes se guardan en Supabase
6. ✅ Los usuarios ven sus propias órdenes
7. ✅ El admin ve todas las órdenes

---

## 🎓 SIGUIENTE PASO

**Ejecuta las 6 pruebas en orden** y dime:

1. ✅ Cuáles pasaron
2. ❌ Cuáles fallaron (con el error)

Entonces ajustaremos lo que haga falta para que todo funcione al 100%.

---

**¿Listo para empezar?** Ejecuta la app y comienza con la PRUEBA 1.

