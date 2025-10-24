# 🛒 ExDigital - E-commerce de Periféricos Gaming

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

ExDigital es una aplicación móvil de e-commerce especializada en periféricos gaming y accesorios tecnológicos. Construida con Jetpack Compose y siguiendo las mejores prácticas de desarrollo Android moderno.

## 📱 Características

### Para Clientes
- 🏠 **Pantalla Principal**: Exploración de productos por categorías
- 🔍 **Búsqueda y Filtrado**: Búsqueda por nombre y filtrado por categorías
- 📦 **Detalle de Producto**: Información completa con imágenes y especificaciones
- 🛒 **Carrito de Compras**: Gestión de productos, cantidades y totales
- 👤 **Autenticación**: Registro e inicio de sesión
- 💳 **Proceso de Compra**: Flujo completo de checkout

### Para Administradores
- 📊 **Panel de Administración**: Gestión completa del inventario
- ➕ **Agregar Productos**: Crear nuevos productos con toda su información
- ✏️ **Editar Productos**: Modificar productos existentes
- 🗑️ **Eliminar Productos**: Gestionar el catálogo de productos
- 📈 **Gestión de Stock**: Control de inventario en tiempo real

## 🏗️ Arquitectura

La aplicación sigue una arquitectura **MVVM (Model-View-ViewModel)** con las siguientes capas:

```
app/
├── data/                    # Capa de datos
│   ├── UserRepository.kt    # Repositorio de usuarios
│   └── ProductRepository.kt # Repositorio de productos
├── models/                  # Modelos de datos
│   ├── User.kt             # Modelo de usuario y roles
│   ├── Product.kt          # Modelo de producto y categorías
│   └── CartItem.kt         # Modelo de items del carrito
├── ui/
│   ├── components/         # Componentes reutilizables
│   │   ├── ExDigitalButton.kt
│   │   ├── ProductCard.kt
│   │   └── CartItemCard.kt
│   ├── screens/            # Pantallas de la aplicación
│   │   ├── SplashScreen.kt
│   │   ├── LoginScreen.kt
│   │   ├── RegisterScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── ProductDetailScreen.kt
│   │   ├── CartScreen.kt
│   │   └── AdminScreen.kt
│   ├── navigation/         # Sistema de navegación
│   │   └── NavGraph.kt
│   ├── theme/             # Tema y estilos
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/         # ViewModels
│       ├── AuthViewModel.kt
│       ├── ProductViewModel.kt
│       └── CartViewModel.kt
└── MainActivity.kt         # Actividad principal
```

## 🎨 Diseño y UI

### Paleta de Colores
- **Primary Orange**: `#FF6B35` - Color principal de la marca
- **Dark Orange**: `#FF4500` - Variante oscura para gradientes
- **Background**: `#1A1A1A` - Fondo oscuro principal
- **Surface**: `#2D2D2D` - Superficie de tarjetas
- **Text Primary**: `#FFFFFF` - Texto principal
- **Text Secondary**: `#B0B0B0` - Texto secundario

### Componentes Personalizados
- **ExDigitalButton**: Botón con gradiente horizontal naranja
- **ProductCard**: Tarjeta de producto con imagen, precio y rating
- **CartItemCard**: Tarjeta de item del carrito con controles de cantidad
- **SearchBar**: Barra de búsqueda personalizada
- **CategoryChip**: Chips para filtrar por categoría

## 🛠️ Tecnologías Utilizadas

### Core
- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - Framework UI declarativo
- **Material Design 3** - Sistema de diseño

### Jetpack Components
- **Navigation Compose** (2.7.7) - Navegación entre pantallas
- **ViewModel** (2.7.0) - Gestión de estados UI
- **DataStore Preferences** (1.0.0) - Almacenamiento de datos local
- **Lifecycle Runtime KTX** - Gestión del ciclo de vida

### Otras Librerías
- **Material Icons Extended** (1.6.3) - Iconos extendidos
- **Gson** (2.10.1) - Serialización JSON

## 📋 Requisitos

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 11
- Android SDK API 24+ (Android 7.0 Nougat)
- Gradle 8.0+

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/exdigital.git
cd exdigital
```

### 2. Abrir en Android Studio
1. Abre Android Studio
2. Selecciona "Open an Existing Project"
3. Navega hasta la carpeta del proyecto
4. Espera a que Gradle sincronice las dependencias

### 3. Ejecutar la aplicación
1. Conecta un dispositivo Android o inicia un emulador
2. Click en el botón "Run" (▶️) o presiona `Shift + F10`

## 👥 Usuarios de Prueba

### Administradores
```
Email: admin@exdigital.com
Password: admin123

Email: manager@exdigital.com
Password: manager123
```

### Clientes
Los clientes pueden registrarse libremente desde la aplicación.

## 📦 Categorías de Productos

- 🖱️ **Mouse** - Ratones gaming y profesionales
- ⌨️ **Teclado** - Teclados mecánicos y gaming
- 🎧 **Audífonos** - Headsets gaming y audiophile
- 🎮 **Control** - Controles para PC y consolas
- 🖥️ **Monitor** - Monitores gaming y profesionales
- 🎤 **Micrófono** - Micrófonos para streaming y podcasting
- 📦 **Otros** - Accesorios variados

## 🔐 Seguridad

> ⚠️ **Nota de Desarrollo**: Las credenciales de administrador están hardcodeadas solo para propósitos de desarrollo y demostración. En un ambiente de producción, se debe implementar:
> - Autenticación con Firebase o backend propio
> - Encriptación de contraseñas (bcrypt, argon2)
> - Tokens JWT para sesiones
> - HTTPS para todas las comunicaciones

## 🗄️ Persistencia de Datos

Actualmente, la aplicación utiliza:
- **DataStore Preferences**: Para preferencias de usuario y sesión
- **En Memoria**: Los productos y carritos se mantienen en memoria

### Roadmap de Persistencia
- [ ] Implementar Room Database para persistencia local
- [ ] Integrar con Firebase Firestore
- [ ] Implementar sincronización offline-first

## 🚧 Funcionalidades Futuras

- [ ] Integración con pasarela de pago (Mercado Pago, WebPay)
- [ ] Sistema de favoritos/wishlist
- [ ] Historial de pedidos
- [ ] Notificaciones push
- [ ] Sistema de reseñas y ratings
- [ ] Chat de soporte
- [ ] Integración con API de seguimiento de envíos
- [ ] Modo oscuro/claro personalizable
- [ ] Soporte multi-idioma

## 📱 Capturas de Pantalla

> TODO: Agregar capturas de pantalla de la aplicación

## 🧪 Testing

```bash
# Ejecutar tests unitarios
./gradlew test

# Ejecutar tests de instrumentación
./gradlew connectedAndroidTest
```

## 📄 Licencia

Este proyecto es un proyecto educativo y de demostración.

## 👨‍💻 Autor

Desarrollado como proyecto de demostración de aplicación Android con Jetpack Compose.

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📞 Soporte

Para preguntas o soporte, por favor abre un issue en el repositorio.

---

**¡Hecho con ❤️ usando Jetpack Compose!**

