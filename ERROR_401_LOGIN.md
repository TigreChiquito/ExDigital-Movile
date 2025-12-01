# 🚨 SOLUCIÓN ERROR 401 - LOGIN

## ❌ EL PROBLEMA

**Error en Logcat:**
```
AuthViewModel: ❌ Error HTTP: 401
```

**Significado:**
- 401 = Unauthorized (No autorizado)
- Las credenciales (email/password) son incorrectas
- O el endpoint de login tiene un problema

---

## 🔍 DIAGNÓSTICO PASO A PASO

### PASO 1: Verificar el Servidor Spring Boot

Abre la consola donde corre tu servidor Spring Boot y busca:

```
Started MsProductosApplication...
Tomcat started on port(s): 8081
```

**Si NO está corriendo:**
```bash
cd /ruta/a/tu/proyecto-backend
./mvnw spring-boot:run
```

---

### PASO 2: Probar el Endpoint con Postman/cURL

**Antes de usar la app, prueba manualmente:**

```bash
POST http://localhost:8081/api/usuarios/login
Content-Type: application/json

{
  "email": "admin@tienda.com",
  "password": "admin123"
}
```

**Respuestas posibles:**

#### ✅ Caso 1: Login exitoso (200 OK)
```json
{
  "success": true,
  "message": "Login exitoso",
  "usuario": {
    "id": 1,
    "email": "admin@tienda.com",
    "nombre": "Administrador",
    "rol": "ADMIN"
  }
}
```
→ **Significa:** El endpoint funciona, el problema está en la app Android

#### ❌ Caso 2: Credenciales inválidas (401)
```json
{
  "success": false,
  "message": "Credenciales inválidas",
  "usuario": null
}
```
→ **Significa:** El email o password son incorrectos

#### ❌ Caso 3: Usuario no existe (401)
```json
{
  "success": false,
  "message": "Usuario no encontrado",
  "usuario": null
}
```
→ **Significa:** El usuario no está en la tabla `usuarios` de Supabase

#### ❌ Caso 4: Error 404
```
404 Not Found
```
→ **Significa:** El endpoint `/api/usuarios/login` no existe en tu backend

---

### PASO 3: Verificar que el Usuario Existe en Supabase

Abre Supabase y ejecuta:

```sql
SELECT * FROM usuarios WHERE email = 'admin@tienda.com';
```

**Debe devolver:**
```
id | email                | nombre         | password  | rol
1  | admin@tienda.com     | Administrador  | admin123  | ADMIN
```

**Si NO existe:**
```sql
INSERT INTO usuarios (email, nombre, password, rol)
VALUES ('admin@tienda.com', 'Administrador', 'admin123', 'ADMIN');
```

---

### PASO 4: Verificar Logs de la App Android

Después del cambio que hice, ahora verás logs más detallados:

```
AuthViewModel: 📤 Intentando login con email: admin@tienda.com
AuthViewModel: 📤 URL: http://10.0.2.2:8081/api/usuarios/login
AuthViewModel: 📥 Response code: 401
AuthViewModel: ❌ Error HTTP: 401
AuthViewModel: ❌ Error body: {"success":false,"message":"Credenciales inválidas"}
AuthViewModel: ❌ Email enviado: admin@tienda.com
```

**Esto te dice:**
- ✅ La app SÍ llega al servidor
- ✅ La URL es correcta
- ❌ El servidor responde 401 (credenciales incorrectas)

---

## 🛠️ SOLUCIONES SEGÚN EL CASO

### SOLUCIÓN 1: Email o Password Incorrectos

**Problema:** Estás usando credenciales que no existen

**Solución:**

1. **Opción A: Usar admin existente**
   - Email: `admin@tienda.com`
   - Password: `admin123`

2. **Opción B: Registrarte primero**
   - En la app, ir a "Regístrate aquí"
   - Crear una cuenta nueva
   - Luego hacer login con esa cuenta

---

### SOLUCIÓN 2: Usuario No Existe en Supabase

**Problema:** La tabla `usuarios` está vacía

**Solución:**

```sql
-- Verificar si existe
SELECT * FROM usuarios;

-- Si está vacía, insertar admin
INSERT INTO usuarios (email, nombre, password, rol)
VALUES ('admin@tienda.com', 'Administrador', 'admin123', 'ADMIN');

-- Insertar un usuario de prueba
INSERT INTO usuarios (email, nombre, password, rol)
VALUES ('test@gmail.com', 'Usuario Test', 'Test123', 'CLIENTE');
```

---

### SOLUCIÓN 3: Endpoint No Existe (404)

**Problema:** Tu `UsuarioController.java` no tiene el endpoint de login

**Verificar que tengas:**

```java
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    // ...código de login
}
```

**Ruta completa debe ser:**
```
POST http://localhost:8081/api/usuarios/login
```

---

### SOLUCIÓN 4: Error en UsuarioService

**Problema:** La lógica de validación de password está fallando

**Verificar en tu backend:**

```java
// UsuarioService.java
public LoginResponse login(String email, String password) {
    Usuario usuario = usuarioRepository.findByEmail(email)
        .orElse(null);
    
    if (usuario == null) {
        return new LoginResponse(false, "Usuario no encontrado", null);
    }
    
    // ⚠️ IMPORTANTE: Comparación de password
    if (!usuario.getPassword().equals(password)) {
        return new LoginResponse(false, "Contraseña incorrecta", null);
    }
    
    return new LoginResponse(true, "Login exitoso", usuario);
}
```

**Nota:** En producción, las contraseñas deben estar hasheadas con BCrypt, pero para desarrollo puedes usar comparación directa.

---

## 🧪 PRUEBA RÁPIDA DE 2 MINUTOS

### Test 1: Endpoint con Postman

```bash
POST http://localhost:8081/api/usuarios/login
{
  "email": "admin@tienda.com",
  "password": "admin123"
}
```

**Resultado esperado:** 200 OK con usuario

### Test 2: Verificar en Supabase

```sql
SELECT email, password, rol FROM usuarios WHERE email = 'admin@tienda.com';
```

**Resultado esperado:** 1 fila con admin123

### Test 3: Login en la App

```
1. Build > Clean Project
2. Run
3. Email: admin@tienda.com
4. Password: admin123
5. Login
```

**Logcat esperado:**
```
AuthViewModel: 📤 Intentando login con email: admin@tienda.com
AuthViewModel: 📥 Response code: 200
AuthViewModel: ✅ Login exitoso: admin@tienda.com, rol: ADMIN
```

---

## 📊 CHECKLIST DE VERIFICACIÓN

- [ ] Servidor Spring Boot corriendo en puerto 8081
- [ ] Tabla `usuarios` existe en Supabase
- [ ] Usuario admin insertado con password correcto
- [ ] Endpoint `/api/usuarios/login` implementado
- [ ] UsuarioService valida correctamente
- [ ] Test con Postman devuelve 200 OK
- [ ] App Android muestra logs detallados
- [ ] Login exitoso en la app

---

## 🎯 SIGUIENTE PASO INMEDIATO

**Ejecuta esto EN ORDEN:**

1. **Verificar servidor:**
   ```bash
   # ¿Está corriendo?
   curl http://localhost:8081/actuator/health
   ```

2. **Test manual:**
   ```bash
   curl -X POST http://localhost:8081/api/usuarios/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@tienda.com","password":"admin123"}'
   ```

3. **Si devuelve 401:**
   - Ir a Supabase
   - Verificar que el usuario existe
   - Verificar que el password coincide EXACTAMENTE

4. **Si devuelve 200:**
   - El problema está en la app Android
   - Rebuild y probar de nuevo

---

## 📞 REPORTE DE RESULTADOS

Después de seguir estos pasos, reporta:

1. ¿El servidor está corriendo? (Sí/No)
2. ¿El test con Postman/cURL funciona? (Sí/No + respuesta)
3. ¿El usuario existe en Supabase? (Sí/No)
4. ¿Qué logs aparecen ahora en Logcat? (Copiar todos los logs de AuthViewModel)

Con esa info, puedo darte la solución exacta.

---

**Fecha:** 2025-11-30  
**Error:** 401 Unauthorized en login  
**Estado:** Diagnóstico detallado + logging mejorado

