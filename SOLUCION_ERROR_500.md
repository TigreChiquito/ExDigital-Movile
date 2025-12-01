# 🔧 Solución al Error 500 al Crear Productos

## 📋 El Problema

La app Android está intentando crear un producto pero el servidor Spring Boot responde con error 500.

Error en Logcat:
```
SimpleProductViewModel: ❌ Error al crear: 500
```

## ✅ Solución en el Backend (Spring Boot)

### 1. Verifica tu Controlador

Tu endpoint POST debe verse así:

```kotlin
@RestController
@RequestMapping("/api")
class ProductoController(
    private val productoRepository: ProductoRepository
) {
    
    @PostMapping("/productos")
    fun crearProducto(@RequestBody request: CreateProductoRequest): ResponseEntity<Producto> {
        try {
            val producto = Producto(
                nombre = request.nombre,
                precio = request.precio,
                stock = request.stock,
                imagenUrl = request.imagenUrl
            )
            
            val productoGuardado = productoRepository.save(producto)
            return ResponseEntity.ok(productoGuardado)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(500).build()
        }
    }
}
```

### 2. DTO de Solicitud

```kotlin
data class CreateProductoRequest(
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val imagenUrl: String?
)
```

### 3. Entidad en Supabase

```kotlin
@Entity
@Table(name = "productos")
data class Producto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val nombre: String,
    
    @Column(nullable = false)
    val precio: Double,
    
    @Column(nullable = false)
    val stock: Int,
    
    @Column(name = "imagen_url")
    val imagenUrl: String? = null,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
```

### 4. Tabla en Supabase

Ejecuta este SQL en Supabase:

```sql
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre TEXT NOT NULL,
    precio NUMERIC NOT NULL,
    stock INTEGER NOT NULL,
    imagen_url TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);
```

## 🧪 Prueba Manual con Postman/cURL

### Request:
```
POST http://localhost:8081/api/productos
Content-Type: application/json

{
  "nombre": "Mouse Gamer RGB",
  "precio": 25000.0,
  "stock": 15,
  "imagenUrl": "https://example.com/mouse.jpg"
}
```

### Response esperada (200 OK):
```json
{
  "id": 1,
  "nombre": "Mouse Gamer RGB",
  "precio": 25000.0,
  "stock": 15,
  "imagenUrl": "https://example.com/mouse.jpg",
  "createdAt": "2025-11-30T21:54:28.123456"
}
```

## 🔍 Cómo Diagnosticar el Error

### 1. Revisa los logs del servidor Spring Boot

Busca el stacktrace completo en la consola de tu servidor:

```
2025-11-30 21:54:28.638 ERROR [...] 
org.springframework.dao.DataIntegrityViolationException: ...
```

### 2. Errores comunes y soluciones:

| Error | Causa | Solución |
|-------|-------|----------|
| `Column 'nombre' cannot be null` | Campo obligatorio vacío | Validar en el DTO |
| `Table 'productos' doesn't exist` | Tabla no creada en Supabase | Ejecutar el SQL CREATE TABLE |
| `Connection refused` | Supabase no conecta | Verificar application.properties |
| `JSON parse error` | Nombres de campos diferentes | Usar @SerializedName en Android |

### 3. Verifica la conexión a Supabase

En `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://<SUPABASE_HOST>:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=<TU_PASSWORD>
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 📱 Siguiente Prueba en Android

Después de corregir el backend:

1. **Build > Clean Project**
2. **Reinicia el servidor Spring Boot**
3. **Run la app Android**
4. **Login como admin**
5. **Panel Admin > Productos > Botón +**
6. **Rellena:**
   - Nombre: "Mouse Gaming"
   - Precio: 25000
   - Stock: 10
   - URL: (dejar vacío o poner una URL válida)
7. **Click Guardar**

Deberías ver en Logcat:
```
SimpleProductViewModel: 📤 Enviando producto: nombre=Mouse Gaming, precio=25000.0, stock=10
SimpleProductViewModel: ✅ Producto creado: Mouse Gaming
SimpleProductViewModel: ✅ Productos cargados: 2
```

## 🆘 Si Sigue Fallando

Comparte:
1. El stacktrace completo del servidor Spring Boot
2. El nuevo log de Logcat con el error body:
   ```
   SimpleProductViewModel: ❌ Error body: {...}
   ```
3. Tu código del controlador POST /api/productos

---

**Fecha:** 2025-11-30  
**Estado:** Error 500 detectado, solución implementada en Android, pendiente corrección en backend

