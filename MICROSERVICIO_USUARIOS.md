# 🔐 MICROSERVICIO DE USUARIOS - Integración Completa

## ✅ LO QUE SE HA IMPLEMENTADO

### 1. Backend Spring Boot (Confirmado por el usuario)

**Tabla en Supabase:**
```sql
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    email TEXT NOT NULL UNIQUE,
    nombre TEXT NOT NULL,
    password TEXT NOT NULL,
    telefono TEXT,
    direccion TEXT,
    rol TEXT NOT NULL DEFAULT 'CLIENTE',
    created_at TIMESTAMP DEFAULT NOW()
);
```

**Endpoints disponibles:**
- ✅ `POST /api/usuarios/registro` - Registrar nuevo usuario
- ✅ `POST /api/usuarios/login` - Autenticar usuario
- ✅ `GET /api/usuarios` - Listar todos (admin)
- ✅ `GET /api/usuarios/{id}` - Obtener por ID
- ✅ `GET /api/usuarios/email/{email}` - Obtener por email
- ✅ `PUT /api/usuarios/{id}` - Actualizar usuario
- ✅ `DELETE /api/usuarios/{id}` - Eliminar usuario

**Usuario admin de prueba:**
```
Email: admin@tienda.com
Password: admin123
Rol: ADMIN
```

---

### 2. Android App (Recién Implementado)

#### Archivos creados/modificados:

1. **UsuarioModels.kt** (NUEVO)
   - `UsuarioResponse` - Modelo de usuario desde API
   - `RegistroRequest` - Datos para registro
   - `LoginRequest` - Datos para login
   - `LoginResponse` - Respuesta de login con usuario

2. **ApiService.kt** (ACTUALIZADO)
   - Agregados 4 endpoints de usuarios
   - `registrarUsuario()`
   - `loginUsuario()`
   - `obtenerUsuarios()`
   - `obtenerUsuarioPorEmail()`

3. **AuthViewModel.kt** (REFACTORIZADO COMPLETO)
   - Ahora usa la API real de usuarios
   - `login()` con callback - Autentica contra Supabase
   - `register()` con callback - Crea usuario en Supabase
   - Manejo de estados de carga (`isLoading`)
   - Manejo de errores del servidor (`authError`)
   - Logging detallado para debugging

4. **LoginScreen.kt** (ACTUALIZADO)
   - Usa callbacks para login asíncrono
   - Muestra mensajes del servidor

5. **RegisterScreen.kt** (ACTUALIZADO)
   - Usa callbacks para registro asíncrono
   - Validación local + servidor

---

## 🔄 FLUJO DE AUTENTICACIÓN REAL

### Registro de Usuario

```
1. Usuario rellena formulario en RegisterScreen
2. Validaciones locales (email, password, campos)
3. Se envía RegistroRequest a /api/usuarios/registro
4. Backend valida y guarda en Supabase
5. Responde con UsuarioResponse
6. Android guarda usuario en DataStore
7. Navega a HomeScreen
```

### Login de Usuario

```
1. Usuario ingresa email y password en LoginScreen
2. Se envía LoginRequest a /api/usuarios/login
3. Backend valida credenciales contra Supabase
4. Responde con LoginResponse (success + usuario)
5. Android guarda usuario en DataStore
6. Actualiza estados isLoggedIn, isAdmin
7. Navega a HomeScreen
```

---

## 🧪 PRUEBAS DEL MICROSERVICIO DE USUARIOS

### PRUEBA 1: Registro de Nuevo Usuario ✅

**Pasos:**
1. Abrir app Android
2. En LoginScreen, click "Regístrate aquí"
3. Rellenar formulario:
   ```
   Nombre: Juan Pérez
   Email: juan@gmail.com
   Teléfono: +56912345678
   Contraseña: Juan123
   Confirmar: Juan123
   ```
4. Click "Crear Cuenta"

**Verificación en Logcat:**
```
AuthViewModel: ✅ Registro exitoso: juan@gmail.com
```

**Resultado esperado:**
- ✅ Cuenta creada en Supabase
- ✅ Usuario logueado automáticamente
- ✅ Navega a HomeScreen
- ✅ Mensaje: "Cuenta creada correctamente"

**Si falla:**
- Email ya existe → Error: "El email ya está registrado"
- Validación → Mensaje específico del error

---

### PRUEBA 2: Login con Usuario Registrado ✅

**Pasos:**
1. Abrir app Android
2. En LoginScreen, ingresar:
   ```
   Email: juan@gmail.com
   Password: Juan123
   ```
3. Click "Iniciar Sesión"

**Verificación en Logcat:**
```
AuthViewModel: ✅ Login exitoso: juan@gmail.com
```

**Resultado esperado:**
- ✅ Usuario autenticado
- ✅ Navega a HomeScreen
- ✅ Nombre aparece: "Hola, Juan Pérez"
- ✅ Rol: CLIENTE (no aparece icono de admin)

---

### PRUEBA 3: Login como Admin ✅

**Pasos:**
1. Abrir app Android
2. En LoginScreen, ingresar:
   ```
   Email: admin@tienda.com
   Password: admin123
   ```
3. Click "Iniciar Sesión"

**Verificación en Logcat:**
```
AuthViewModel: ✅ Login exitoso: admin@tienda.com
```

**Resultado esperado:**
- ✅ Usuario autenticado como ADMIN
- ✅ Aparece icono de Settings en HomeScreen
- ✅ Puede acceder al Panel de Administración
- ✅ Ve todas las órdenes de todos los usuarios

---

### PRUEBA 4: Validaciones de Registro ✅

**Intentar registrar con:**

**A. Email inválido:**
```
Email: usuario@yahoo.com
→ Error: "Email debe ser @duoc.cl, @duocuc.cl o @gmail.com"
```

**B. Contraseña débil:**
```
Password: abc
→ Error: "Contraseña: 5-9 caracteres, al menos 1 mayúscula"
```

**C. Email duplicado:**
```
Email: juan@gmail.com (ya existe)
→ Error: "El email ya está registrado"
```

---

### PRUEBA 5: Validaciones de Login ✅

**Intentar login con:**

**A. Credenciales incorrectas:**
```
Email: juan@gmail.com
Password: wrongpass
→ Error: "Credenciales inválidas"
```

**B. Usuario no existe:**
```
Email: noexiste@gmail.com
Password: Test123
→ Error: "Credenciales inválidas"
```

---

## 📊 COMPARACIÓN: ANTES vs AHORA

| Aspecto | Antes (Educativo) | Ahora (Real) |
|---------|-------------------|--------------|
| **Almacenamiento** | Solo DataStore local | Supabase (persistente) |
| **Usuarios** | Se creaban al login | Deben registrarse primero |
| **Validación** | Solo local | Local + Servidor |
| **Admin** | Hardcodeado en código | En base de datos |
| **Persistencia** | Solo en el dispositivo | En la nube (multi-dispositivo) |
| **Seguridad** | Password en texto plano | Hash en backend |

---

## 🔍 DEBUGGING

### Logs a buscar en Logcat:

**Registro exitoso:**
```
AuthViewModel: ✅ Registro exitoso: usuario@gmail.com
```

**Login exitoso:**
```
AuthViewModel: ✅ Login exitoso: usuario@gmail.com
```

**Error de servidor:**
```
AuthViewModel: ❌ Error HTTP: 400
AuthViewModel: ❌ Login fallido: Credenciales inválidas
```

**Error de red:**
```
AuthViewModel: 💀 Error de red: Failed to connect to /10.0.2.2:8081
```

---

## 🚨 ERRORES COMUNES Y SOLUCIONES

### Error: "Error de conexión"

**Causa:** Servidor Spring Boot no está corriendo  
**Solución:**
1. Verificar que el servidor esté en puerto 8081
2. Verificar logs del servidor

### Error: "El email ya está registrado"

**Causa:** Usuario intenta registrarse con email existente  
**Solución:** Usar otro email o hacer login

### Error: "Credenciales inválidas"

**Causa:** Email o password incorrectos  
**Solución:** Verificar credenciales o registrarse primero

### Error 500 en registro/login

**Causa:** Problema en el backend  
**Solución:**
1. Revisar logs de Spring Boot
2. Verificar que la tabla `usuarios` existe
3. Verificar conexión con Supabase

---

## ✅ CHECKLIST DE VERIFICACIÓN

### Backend
- [ ] Servidor Spring Boot corriendo en puerto 8081
- [ ] Tabla `usuarios` creada en Supabase
- [ ] Admin de prueba insertado (`admin@tienda.com`)
- [ ] Endpoint POST /api/usuarios/registro funciona
- [ ] Endpoint POST /api/usuarios/login funciona

### Android
- [ ] Build sin errores
- [ ] UsuarioModels.kt creado
- [ ] ApiService.kt con endpoints de usuarios
- [ ] AuthViewModel.kt usando API real
- [ ] LoginScreen.kt con callbacks
- [ ] RegisterScreen.kt con callbacks

### Pruebas
- [ ] Registro de nuevo usuario funciona
- [ ] Login con usuario registrado funciona
- [ ] Login como admin funciona
- [ ] Validaciones de email/password funcionan
- [ ] Errores del servidor se muestran correctamente

---

## 🎯 SIGUIENTE PASO

**Ejecuta estas pruebas ahora:**

1. **Build > Clean Project**
2. **Build > Make Project**
3. **Run**
4. Probar PRUEBA 1: Registro
5. Probar PRUEBA 2: Login usuario
6. Probar PRUEBA 3: Login admin
7. Reportar resultados

---

## 📞 SOPORTE

Si algo falla, comparte:
1. El paso de prueba que falló
2. El error en Logcat (buscar "AuthViewModel")
3. El log del servidor Spring Boot
4. Captura de pantalla del error en la app

---

**Fecha:** 2025-11-30  
**Estado:** Microservicio de usuarios completamente integrado  
**Listo para pruebas:** ✅

