package com.exdigital.app.data

import com.exdigital.app.models.Product
import com.exdigital.app.models.ProductCategory

object MockData {

    val products = listOf(
        Product(
            id = "1",
            name = "Logitech G Pro X Superlight",
            description = "Mouse gaming inalámbrico ultraligero de alta precisión. Diseñado para profesionales con sensor HERO 25K.",
            price = 149.99,
            imageUrl = "https://via.placeholder.com/300x300/FF8A3D/FFFFFF?text=Mouse",
            category = ProductCategory.MOUSE,
            stock = 15,
            brand = "Logitech",
            rating = 4.8
        ),
        Product(
            id = "2",
            name = "Razer BlackWidow V3",
            description = "Teclado mecánico RGB con switches Razer Green. Iluminación Chroma y reposamuñecas ergonómico.",
            price = 139.99,
            imageUrl = "https://via.placeholder.com/300x300/4ECDC4/FFFFFF?text=Teclado",
            category = ProductCategory.KEYBOARD,
            stock = 8,
            brand = "Razer",
            rating = 4.6
        ),
        Product(
            id = "3",
            name = "HyperX Cloud II",
            description = "Audífonos gaming con sonido 7.1 virtual. Micrófono desmontable con cancelación de ruido.",
            price = 99.99,
            imageUrl = "https://via.placeholder.com/300x300/FF8A3D/FFFFFF?text=Audifonos",
            category = ProductCategory.HEADSET,
            stock = 20,
            brand = "HyperX",
            rating = 4.7
        ),
        Product(
            id = "4",
            name = "Xbox Elite Controller Series 2",
            description = "Control inalámbrico premium con sticks intercambiables y gatillos ajustables.",
            price = 179.99,
            imageUrl = "https://via.placeholder.com/300x300/4ECDC4/FFFFFF?text=Control",
            category = ProductCategory.CONTROLLER,
            stock = 5,
            brand = "Microsoft",
            rating = 4.9
        ),
        Product(
            id = "5",
            name = "BenQ ZOWIE XL2546K",
            description = "Monitor gaming 24.5\" 240Hz con tecnología DyAc+. Tiempo de respuesta 0.5ms.",
            price = 499.99,
            imageUrl = "https://via.placeholder.com/300x300/FF8A3D/FFFFFF?text=Monitor",
            category = ProductCategory.MONITOR,
            stock = 3,
            brand = "BenQ",
            rating = 4.8
        ),
        Product(
            id = "7",
            name = "Blue Yeti X",
            description = "Micrófono USB profesional con 4 patrones de captación. Perfecto para streaming y podcasts.",
            price = 169.99,
            imageUrl = "https://via.placeholder.com/300x300/FF8A3D/FFFFFF?text=Microfono",
            category = ProductCategory.MICROPHONE,
            stock = 10,
            brand = "Blue",
            rating = 4.7
        ),
        Product(
            id = "8",
            name = "Corsair K70 RGB",
            description = "Teclado mecánico gaming con switches Cherry MX. Estructura de aluminio resistente.",
            price = 159.99,
            imageUrl = "https://via.placeholder.com/300x300/4ECDC4/FFFFFF?text=Teclado",
            category = ProductCategory.KEYBOARD,
            stock = 7,
            brand = "Corsair",
            rating = 4.6
        ),
        Product(
            id = "9",
            name = "Razer DeathAdder V3 Pro",
            description = "Mouse inalámbrico ergonómico con sensor Focus Pro 30K. Batería de hasta 90 horas.",
            price = 149.99,
            imageUrl = "https://via.placeholder.com/300x300/FF8A3D/FFFFFF?text=Mouse",
            category = ProductCategory.MOUSE,
            stock = 18,
            brand = "Razer",
            rating = 4.8
        ),
        Product(
            id = "10",
            name = "SteelSeries Arctis Nova Pro",
            description = "Audífonos gaming premium con audio Hi-Res. Sistema de cancelación activa de ruido.",
            price = 349.99,
            imageUrl = "https://via.placeholder.com/300x300/4ECDC4/FFFFFF?text=Audifonos",
            category = ProductCategory.HEADSET,
            stock = 6,
            brand = "SteelSeries",
            rating = 4.9
        )
    )

    fun getProductById(id: String): Product? {
        return products.find { it.id == id }
    }

    fun getProductsByCategory(category: ProductCategory): List<Product> {
        return products.filter { it.category == category }
    }

    fun searchProducts(query: String): List<Product> {
        return products.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.brand.contains(query, ignoreCase = true)
        }
    }
}