# 🚀 INICIO RÁPIDO - Ejecutar y Probar la App

## ⚡ ACCESO RÁPIDO

### 🔑 Credenciales de Prueba

**Admin:**
```
Email: admin@exdigital.com
Password: admin123
```

**Usuario de prueba:**
```
Email: test@gmail.com
Password: Test123
```

---

## 🎯 PRUEBA RÁPIDA DE 5 MINUTOS

### ✅ PASO 1: Iniciar (1 min)

```bash
# Android Studio
Build > Clean Project
Build > Make Project
Run
```

**Verificar:**
- ✅ App se instala sin errores
- ✅ Splash screen → Login screen

---

### ✅ PASO 2: Login Admin (30 seg)

1. Email: `admin@exdigital.com`
2. Password: `admin123`
3. Click "Iniciar Sesión"

**Verificar:**
- ✅ Navega a HomeScreen
- ✅ Aparece icono de Settings (engranaje) arriba

---

### ✅ PASO 3: Ver Productos (30 seg)

**En HomeScreen:**
- ✅ ¿Se ven productos en la lista?
- ✅ ¿Tienen nombre, precio, imagen?

**En Logcat buscar:**
```
✅ Productos cargados: X
```

**Si no hay productos:**
→ Ir a PASO 4

---

### ✅ PASO 4: Crear Producto (1 min)

1. Tocar icono de **Settings** (engranaje)
2. Ir a pestaña **"Productos"**
3. Tocar botón **"+"** (flotante abajo derecha)
4. Rellenar:
   - Nombre: `Mouse RGB`
   - Precio: `25000`
   - Stock: `10`
   - URL: (vacío)
5. Click **"Guardar"**

**Verificar en Logcat:**
```
📤 Enviando producto: nombre=Mouse RGB, precio=25000.0, stock=10
✅ Producto creado: Mouse RGB
✅ Productos cargados: X
```

**Resultado esperado:**
- ✅ Dialog se cierra
- ✅ Producto aparece en la lista de Admin
- ✅ Si vuelves a Home, también aparece

**Si falla con error 500:**
→ Revisar logs de Spring Boot
→ Verificar que la tabla `productos` existe en Supabase

---

### ✅ PASO 5: Comprar y Ver Orden (2 min)

**A. Agregar al carrito:**
1. Volver a Home (botón atrás)
2. Tocar "Agregar" en cualquier producto
3. Tocar icono del carrito (arriba)
4. Verificar que está el producto

**B. Realizar compra:**
1. En CartScreen, hacer scroll abajo
2. Tocar "Realizar Compra"
3. Confirmar

**Verificar en Logcat:**
```
✅ Orden creada exitosamente: 123
🗑️ Carrito limpiado
```

**C. Ver la orden:**
1. Ir a Home
2. Tocar icono de Settings
3. Pestaña **"Órdenes"**
4. Debería aparecer la compra

**Resultado esperado:**
- ✅ Carrito se vació
- ✅ Orden aparece en Admin
- ✅ Muestra total, fecha, usuario

---

## 📊 RESULTADO DE LA PRUEBA RÁPIDA

### ✅ TODO FUNCIONA SI:

- [x] App inicia sin crashes
- [x] Login funciona
- [x] Se ven productos desde Supabase
- [x] Se pueden crear productos
- [x] Se puede agregar al carrito
- [x] Checkout crea orden
- [x] Órdenes aparecen en Admin

---

## 🚨 SI ALGO FALLA

### Error al crear producto (500)

**Revisar:**
1. Logs del servidor Spring Boot
2. Conexión con Supabase
3. Tabla `productos` existe

**Solución:**
- Ejecutar SQL de creación de tabla (ver BACKEND_SPRING_BOOT_IMPLEMENTACION.md)

### No aparecen productos en Home

**Causa:** Lista vacía en Supabase  
**Solución:** Crear productos desde Admin (PASO 4)

### Error al hacer checkout (500)

**Revisar:**
1. Logs del servidor Spring Boot
2. Tabla `ordenes` existe

**Solución:**
- Ejecutar SQL de creación de tabla `ordenes`

### App se cierra al iniciar

**Causa:** ProductViewModel intenta usar Room  
**Solución:** Ya está corregido, hacer Clean + Build

---

## 📞 REPORTE DE RESULTADOS

Después de la prueba rápida, reporta:

**✅ Funcionó:**
- Lista lo que funcionó bien

**❌ Falló:**
- Qué paso falló
- Qué error viste (Logcat o pantalla)
- Log del servidor Spring Boot (si aplica)

---

## 🎯 SIGUIENTE NIVEL

Si todo funcionó en la prueba rápida:

→ Lee **PRUEBAS_FINALES.md** para pruebas más exhaustivas
→ Prueba todos los flujos de usuario
→ Verifica persistencia de datos

---

**Tiempo total estimado: 5 minutos**

¡Suerte! 🚀

