package com.exdigital.app.ui.screens

import com.exdigital.app.models.ProductCategory
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exdigital.app.models.Product
import com.exdigital.app.models.displayName
import com.exdigital.app.ui.theme.BackgroundDark
import com.exdigital.app.ui.theme.BackgroundDarkest
import com.exdigital.app.ui.theme.BackgroundLight
import com.exdigital.app.ui.theme.BackgroundMedium
import com.exdigital.app.ui.theme.ErrorColor
import com.exdigital.app.ui.theme.PrimaryOrange
import com.exdigital.app.ui.theme.TealAccent
import com.exdigital.app.ui.theme.TextPrimary
import com.exdigital.app.ui.theme.TextSecondary
import com.exdigital.app.ui.theme.TextTertiary
import com.exdigital.app.ui.viewmodels.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel()
) {
    val products by productViewModel.products.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Panel de Administración",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${products.size} productos en stock",
                            fontSize = 14.sp,
                            color = TextTertiary
                        )
                    }
                },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryOrange,
                contentColor = TextPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar producto"
                )
            }
        },
        containerColor = BackgroundDarkest
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AdminStatsCard(
                    totalProducts = products.size,
                    totalStock = products.sumOf { it.stock },
                    lowStockCount = products.count { it.stock < 5 }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            items(products, key = { it.id }) { product ->
                AdminProductCard(
                    product = product,
                    onEdit = {
                        selectedProduct = product
                        showAddDialog = true
                    },
                    onDelete = {
                        productViewModel.deleteProduct(product.id)
                    }
                )
            }
        }
    }

    // Dialog para agregar/editar producto
    if (showAddDialog) {
        AddEditProductDialog(
            product = selectedProduct,
            onDismiss = {
                showAddDialog = false
                selectedProduct = null
            },
            onSave = { product ->
                if (selectedProduct != null) {
                    productViewModel.updateProduct(product)
                } else {
                    productViewModel.addProduct(product)
                }
                showAddDialog = false
                selectedProduct = null
            }
        )
    }
}

@Composable
fun AdminStatsCard(
    totalProducts: Int,
    totalStock: Int,
    lowStockCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem("Productos", totalProducts.toString(), PrimaryOrange)
            StatItem("Stock Total", totalStock.toString(), TealAccent)
            StatItem("Stock Bajo", lowStockCount.toString(), if (lowStockCount > 0) ErrorColor else TealAccent)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextTertiary
        )
    }
}

@Composable
fun AdminProductCard(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundLight),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📦", fontSize = 32.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = product.category.displayName(),
                            fontSize = 12.sp,
                            color = TealAccent
                        )
                        Text(
                            text = product.brand,
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${String.format("%.2f", product.price)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange
                        )
                        Text(
                            text = "Stock: ${product.stock}",
                            fontSize = 12.sp,
                            color = if (product.stock < 5) ErrorColor else TextSecondary,
                            fontWeight = if (product.stock < 5) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = TealAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BackgroundDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = ErrorColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var brand by remember { mutableStateOf(product?.brand ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(product?.category ?: ProductCategory.OTHER) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = BackgroundDark
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = if (product == null) "Nuevo Producto" else "Editar Producto",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Name
                com.exdigital.app.ui.components.ExDigitalTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre del producto",
                    placeholder = "Ej: Logitech G Pro"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Brand
                com.exdigital.app.ui.components.ExDigitalTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = "Marca",
                    placeholder = "Ej: Logitech"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category
                Box {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BackgroundMedium)
                            .clickable { showCategoryMenu = !showCategoryMenu }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Categoría",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                        Text(
                            text = selectedCategory.displayName(),
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }

                    if (showCategoryMenu) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 70.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = BackgroundMedium
                            )
                        ) {
                            Column {
                                ProductCategory.values().forEach { category ->
                                    Text(
                                        text = category.displayName(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedCategory = category
                                                showCategoryMenu = false
                                            }
                                            .padding(16.dp),
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Price
                    com.exdigital.app.ui.components.ExDigitalTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = "Precio",
                        placeholder = "99.99",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        )
                    )

                    // Stock
                    com.exdigital.app.ui.components.ExDigitalTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = "Stock",
                        placeholder = "10",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                com.exdigital.app.ui.components.ExDigitalTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Descripción",
                    placeholder = "Descripción del producto"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BackgroundMedium)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancelar",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    com.exdigital.app.ui.components.ExDigitalButton(
                        text = "Guardar",
                        onClick = {
                            val priceValue = price.toDoubleOrNull() ?: 0.0
                            val stockValue = stock.toIntOrNull() ?: 0

                            val newProduct = if (product != null) {
                                product.copy(
                                    name = name,
                                    description = description,
                                    price = priceValue,
                                    brand = brand,
                                    stock = stockValue,
                                    category = selectedCategory
                                )
                            } else {
                                Product(
                                    id = java.util.UUID.randomUUID().toString(),
                                    name = name,
                                    description = description,
                                    price = priceValue,
                                    imageUrl = "https://via.placeholder.com/300x300/FF8A3D/FFFFFF?text=Producto",
                                    category = selectedCategory,
                                    stock = stockValue,
                                    brand = brand,
                                    rating = 0.0
                                )
                            }
                            onSave(newProduct)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}