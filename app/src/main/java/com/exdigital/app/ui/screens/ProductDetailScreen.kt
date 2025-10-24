package com.exdigital.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exdigital.app.data.MockData
import com.exdigital.app.models.Product
import com.exdigital.app.models.displayName
import com.exdigital.app.ui.components.ExDigitalButton
import com.exdigital.app.ui.theme.BackgroundDark
import com.exdigital.app.ui.theme.BackgroundDarkest
import com.exdigital.app.ui.theme.BackgroundLight
import com.exdigital.app.ui.theme.BackgroundMedium
import com.exdigital.app.ui.theme.DarkOrange
import com.exdigital.app.ui.theme.PrimaryOrange
import com.exdigital.app.ui.theme.TealAccent
import com.exdigital.app.ui.theme.TextPrimary
import com.exdigital.app.ui.theme.TextSecondary
import com.exdigital.app.ui.theme.TextTertiary
import com.exdigital.app.ui.viewmodels.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String?,
    cartViewModel: CartViewModel = viewModel()
) {
    val product = MockData.getProductById(productId ?: "")
    var quantity by remember { mutableIntStateOf(1) }

    if (product == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDarkest),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Producto no encontrado",
                color = TextPrimary,
                fontSize = 18.sp
            )
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDarkest,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundDarkest
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(BackgroundMedium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📦",
                    fontSize = 120.sp
                )
            }

            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Category Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundLight)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = product.category.displayName(),
                        color = TealAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Product Name
                Text(
                    text = product.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Brand
                Text(
                    text = product.brand,
                    fontSize = 16.sp,
                    color = TealAccent,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rating and Stock
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = PrimaryOrange,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${product.rating}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "(250 reseñas)",
                            fontSize = 14.sp,
                            color = TextTertiary
                        )
                    }

                    Text(
                        text = if (product.stock > 10) "En stock" else "Últimas ${product.stock} unidades",
                        fontSize = 14.sp,
                        color = if (product.stock > 10) TealAccent else PrimaryOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Price
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BackgroundMedium)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Precio",
                            fontSize = 14.sp,
                            color = TextTertiary
                        )
                        Text(
                            text = "$${String.format("%.2f", product.price)}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryOrange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = "Descripción",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.description,
                    fontSize = 16.sp,
                    color = TextSecondary,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Specifications Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BackgroundMedium)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Especificaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    SpecificationRow("Marca", product.brand)
                    SpecificationRow("Categoría", product.category.displayName())
                    SpecificationRow("Stock disponible", "${product.stock} unidades")
                    SpecificationRow("Garantía", "1 año")
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Quantity Selector
                Text(
                    text = "Cantidad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundMedium)
                            .padding(8.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Disminuir",
                                tint = if (quantity > 1) TextPrimary else TextTertiary
                            )
                        }

                        Text(
                            text = "$quantity",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        IconButton(
                            onClick = { if (quantity < product.stock) quantity++ },
                            enabled = quantity < product.stock
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Aumentar",
                                tint = if (quantity < product.stock) TextPrimary else TextTertiary
                            )
                        }
                    }

                    Text(
                        text = "Total: $${String.format("%.2f", product.price * quantity)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Add to Cart Button
                ExDigitalButton(
                    text = "Agregar al Carrito",
                    onClick = {
                        cartViewModel.addToCart(product, quantity)
                        navController.navigateUp()
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SpecificationRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextTertiary
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold
        )
    }
}