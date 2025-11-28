package com.exdigital.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.exdigital.app.models.Product
import com.exdigital.app.models.ProductCategory
import com.exdigital.app.models.displayName
import com.exdigital.app.ui.navigation.Screen
import com.exdigital.app.ui.theme.BackgroundDarkest
import com.exdigital.app.ui.theme.BackgroundLight
import com.exdigital.app.ui.theme.BackgroundMedium
import com.exdigital.app.ui.theme.PrimaryOrange
import com.exdigital.app.ui.theme.TealAccent
import com.exdigital.app.ui.theme.TextPrimary
import com.exdigital.app.ui.theme.TextTertiary
import com.exdigital.app.ui.viewmodels.AuthViewModel
import com.exdigital.app.ui.viewmodels.CartViewModel
import com.exdigital.app.ui.viewmodels.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    cartViewModel: CartViewModel, // ✅ Parámetro obligatorio (sin default)
    productViewModel: ProductViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val cart by cartViewModel.cart.collectAsState()
    val products by productViewModel.products.collectAsState()

    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }

    val filteredProducts = selectedCategory?.let { category ->
        products.filter { it.category == category }
    } ?: products

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Hola, ${currentUser?.name ?: "Usuario"}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Encuentra tus periféricos",
                            fontSize = 14.sp,
                            color = TextTertiary
                        )
                    }
                },
                actions = {
                    // Botón Admin (solo visible para administradores)
                    val isAdmin by authViewModel.isAdmin.collectAsState()
                    if (isAdmin) {
                        IconButton(onClick = { navController.navigate(Screen.Admin.route) }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Admin",
                                tint = PrimaryOrange
                            )
                        }
                    }


                    BadgedBox(
                        badge = {
                            if (cart.itemCount > 0) {
                                Badge(
                                    containerColor = PrimaryOrange
                                ) {
                                    Text(
                                        text = "${cart.itemCount}",
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = TextPrimary
                            )
                        }
                    }

                    IconButton(onClick = { navController.navigate(Screen.Profile.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = TextPrimary
                        )
                    }

                    IconButton(onClick = { navController.navigate(Screen.Orders.route) }) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Órdenes",
                            tint = PrimaryOrange
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDarkest
                )
            )
        },
        containerColor = BackgroundDarkest
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Banner promocional
            item {
                PromotionalBanner()
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Categorías
            item {
                Text(
                    text = "Categorías",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                CategoriesRow(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = if (selectedCategory == category) null else category
                    }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Productos destacados
            item {
                Text(
                    text = "Productos Destacados",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(filteredProducts) { product ->  // ✅ ESTO ES LO NUEVO
                ProductCard(
                    product = product,
                    onClick = {
                        navController.navigate(Screen.ProductDetail.createRoute(product.id))
                    },
                    onAddToCart = {
                        cartViewModel.addToCart(product)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PromotionalBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryOrange, TealAccent)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🎮 DESCUENTOS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hasta 30% OFF",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "En periféricos gaming seleccionados",
                fontSize = 14.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun CategoriesRow(
    selectedCategory: ProductCategory? = null,
    onCategorySelected: (ProductCategory) -> Unit
) {
    val categories = ProductCategory.values()

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories.toList()) { category ->
            CategoryChip(
                category = category,
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: ProductCategory,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) PrimaryOrange else BackgroundMedium)
            .border(2.dp, BackgroundLight, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = category.displayName(),
            color = if (selected) TextPrimary else TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Imagen del producto
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BackgroundLight),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = product.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = product.brand,
                        fontSize = 12.sp,
                        color = TealAccent
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = PrimaryOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${product.rating}",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stock: ${product.stock}",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", product.price)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryOrange
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(PrimaryOrange, TealAccent)
                                )
                            )
                            .clickable(onClick = onAddToCart)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Agregar",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}