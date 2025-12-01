# 🔧 GUÍA COMPLETA: Actualizar Backend Spring Boot

## 📋 Situación Actual

- ✅ **Android App**: Ya implementada con todos los endpoints
- ⚠️ **Backend Spring Boot**: Solo tiene GET /api/productos
- ❌ **Falta implementar**: POST /api/productos, GET/POST /api/ordenes

---

## 🚀 PASO A PASO - Backend Spring Boot

### 📂 Estructura de archivos a crear/modificar

```
ms-productos/
├── src/main/java/com/tienda/ms_productos/
│   ├── controller/
│   │   ├── ProductoController.java    ← MODIFICAR
│   │   └── OrdenController.java       ← CREAR NUEVO
│   ├── entity/
│   │   ├── Producto.java              ← Ya existe
│   │   └── Orden.java                 ← CREAR NUEVO
│   └── repository/
│       ├── ProductoRepository.java    ← Ya existe
│       └── OrdenRepository.java       ← CREAR NUEVO
```

---

## 1️⃣ MODIFICAR ProductoController.java

**Ubicación:** `ms-productos/src/main/java/.../controller/ProductoController.java`

```java
package com.tienda.ms_productos.controller;

import com.tienda.ms_productos.entity.Producto;
import com.tienda.ms_productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Permite llamadas desde Android
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    // Endpoint existente - Obtener todos los productos
    @GetMapping
    public List<Producto> obtenerProductos() {
        return productoRepository.findAll();
    }

    // ⭐ NUEVO - Crear un producto desde Android
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        try {
            // Validación básica
            if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            if (producto.getStock() == null || producto.getStock() < 0) {
                return ResponseEntity.badRequest().build();
            }

            // Guardar en Supabase
            Producto productoGuardado = productoRepository.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(productoGuardado);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

---

## 2️⃣ CREAR Orden.java (Entidad)

**Ubicación:** `ms-productos/src/main/java/.../entity/Orden.java`

```java
package com.tienda.ms_productos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private String usuarioId;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private String estado; // "PAGADO", "PENDIENTE", etc.

    // Guardamos los items del carrito como JSON (texto)
    // Esto evita crear tablas complejas ahora
    @Column(name = "items", columnDefinition = "TEXT")
    private String items;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public Orden() {}

    public Orden(String usuarioId, Double total, String estado, String items) {
        this.usuarioId = usuarioId;
        this.total = total;
        this.estado = estado;
        this.items = items;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
```

---

## 3️⃣ CREAR OrdenRepository.java

**Ubicación:** `ms-productos/src/main/java/.../repository/OrdenRepository.java`

```java
package com.tienda.ms_productos.repository;

import com.tienda.ms_productos.entity.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    
    // Buscar órdenes de un usuario específico
    List<Orden> findByUsuarioId(String usuarioId);
    
    // Buscar órdenes por estado
    List<Orden> findByEstado(String estado);
}
```

---

## 4️⃣ CREAR OrdenController.java

**Ubicación:** `ms-productos/src/main/java/.../controller/OrdenController.java`

```java
package com.tienda.ms_productos.controller;

import com.tienda.ms_productos.entity.Orden;
import com.tienda.ms_productos.repository.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@CrossOrigin(origins = "*") // Permite llamadas desde Android
public class OrdenController {

    @Autowired
    private OrdenRepository ordenRepository;

    // Obtener todas las órdenes (Para el Admin)
    @GetMapping
    public List<Orden> obtenerTodas() {
        return ordenRepository.findAll();
    }

    // Obtener órdenes de un usuario específico
    @GetMapping("/usuario/{usuarioId}")
    public List<Orden> obtenerPorUsuario(@PathVariable String usuarioId) {
        return ordenRepository.findByUsuarioId(usuarioId);
    }

    // Crear una nueva orden (Desde el Checkout en Android)
    @PostMapping
    public ResponseEntity<Orden> crearOrden(@RequestBody Orden orden) {
        try {
            // Validaciones básicas
            if (orden.getUsuarioId() == null || orden.getUsuarioId().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (orden.getTotal() == null || orden.getTotal() <= 0) {
                return ResponseEntity.badRequest().build();
            }
            if (orden.getItems() == null || orden.getItems().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // Si no viene el estado, poner "PAGADO" por defecto
            if (orden.getEstado() == null || orden.getEstado().isEmpty()) {
                orden.setEstado("PAGADO");
            }

            // Guardar en Supabase
            Orden ordenGuardada = ordenRepository.save(orden);
            return ResponseEntity.status(HttpStatus.CREATED).body(ordenGuardada);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

---

## 5️⃣ CREAR LA TABLA EN SUPABASE

Ejecuta este SQL en el editor SQL de Supabase:

```sql
-- Tabla de órdenes
CREATE TABLE IF NOT EXISTS ordenes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id TEXT NOT NULL,
    total NUMERIC NOT NULL,
    estado TEXT NOT NULL DEFAULT 'PAGADO',
    items TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Índice para búsquedas rápidas por usuario
CREATE INDEX idx_ordenes_usuario ON ordenes(usuario_id);

-- Índice para búsquedas por estado
CREATE INDEX idx_ordenes_estado ON ordenes(estado);
```

---

## 6️⃣ VERIFICAR application.properties

Asegúrate de que tu archivo `application.properties` tenga la configuración correcta:

```properties
# Supabase Connection
spring.datasource.url=jdbc:postgresql://<TU_HOST_SUPABASE>:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=<TU_PASSWORD_SUPABASE>

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Puerto del servidor
server.port=8081
```

---

## 7️⃣ REINICIAR EL SERVIDOR

```bash
# Detener el servidor actual (Ctrl + C en la terminal)

# Reiniciar
./mvnw spring-boot:run

# o si usas Gradle
./gradlew bootRun
```

---

## 8️⃣ PROBAR CON POSTMAN

### Test 1: Crear un Producto

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

**Respuesta esperada (201 Created):**
```json
{
  "id": 1,
  "nombre": "Mouse Gamer RGB",
  "precio": 25000.0,
  "stock": 15,
  "imagenUrl": "https://example.com/mouse.jpg"
}
```

### Test 2: Crear una Orden

```
POST http://localhost:8081/api/ordenes
Content-Type: application/json

{
  "usuarioId": "user_123",
  "total": 50000.0,
  "estado": "PAGADO",
  "items": "[{\"product\":{\"id\":\"1\",\"name\":\"Mouse\"},\"quantity\":2}]"
}
```

**Respuesta esperada (201 Created):**
```json
{
  "id": 1,
  "usuarioId": "user_123",
  "total": 50000.0,
  "estado": "PAGADO",
  "items": "[{\"product\":{\"id\":\"1\",\"name\":\"Mouse\"},\"quantity\":2}]",
  "createdAt": "2025-11-30T22:00:00"
}
```

### Test 3: Obtener Todas las Órdenes

```
GET http://localhost:8081/api/ordenes
```

---

## ✅ CHECKLIST DE IMPLEMENTACIÓN

- [ ] Modificar `ProductoController.java` (agregar método POST)
- [ ] Crear `Orden.java` (entidad)
- [ ] Crear `OrdenRepository.java` (interfaz)
- [ ] Crear `OrdenController.java` (controlador)
- [ ] Ejecutar SQL en Supabase (tabla ordenes)
- [ ] Verificar `application.properties`
- [ ] Reiniciar servidor Spring Boot
- [ ] Probar POST /api/productos con Postman ✅
- [ ] Probar POST /api/ordenes con Postman ✅
- [ ] Probar GET /api/ordenes con Postman ✅
- [ ] Probar desde la app Android ✅

---

## 🎯 SIGUIENTE PASO

Una vez que:

1. Hayas copiado estos 4 archivos en tu proyecto Spring Boot
2. Hayas ejecutado el SQL en Supabase
3. Hayas reiniciado el servidor sin errores

**Entonces ejecuta la app Android** y:

- Ve al Panel de Admin
- Intenta crear un producto
- Realiza una compra como cliente
- Verifica en Admin que aparece la orden

Todo debería funcionar perfectamente porque **Android ya está listo** y solo esperaba que el backend tuviera estos endpoints.

---

**¿Alguna duda sobre algún paso?** Ahoramismo implementa esto en tu Spring Boot y me avisas si hay algún error.

