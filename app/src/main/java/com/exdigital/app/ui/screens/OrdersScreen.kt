package com.exdigital.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exdigital.app.ui.theme.*
import com.exdigital.app.ui.viewmodels.AuthViewModel
import com.exdigital.app.ui.viewmodels.Order
import com.exdigital.app.ui.viewmodels.OrdersViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    ordersViewModel: OrdersViewModel // ✅ Parámetro obligatorio
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val isAdmin by authViewModel.isAdmin.collectAsState()
    val orders by ordersViewModel.orders.collectAsState()
    val selectedOrder by ordersViewModel.selectedOrder.collectAsState()

    // Cargar órdenes según el rol
    val userId = currentUser?.id
    if (isAdmin) {
        ordersViewModel.loadAllOrders()
    } else if (userId != null) {
        ordersViewModel.loadUserOrders(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isAdmin) "Todas las órdenes" else "Mis órdenes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${orders.size} órdenes registradas",
                            fontSize = 14.sp,
                            color = TextTertiary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        ) {
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isAdmin) "No hay órdenes todavía" else "Aún no has realizado compras",
                        fontSize = 18.sp,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        OrderCard(
                            order = order,
                            isAdmin = isAdmin,
                            onClick = {
                                ordersViewModel.selectOrder(order)
                            }
                        )
                    }
                }
            }

            // Panel de detalle de orden
            selectedOrder?.let { order ->
                OrderDetailPanel(order = order)
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, isAdmin: Boolean, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Orden #${order.id}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dateFormat.format(Date(order.timestamp)),
                        fontSize = 12.sp,
                        color = TextTertiary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format(Locale.getDefault(), "%.2f", order.total)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryOrange
                    )
                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Usuario: ${order.userId}",
                            fontSize = 12.sp,
                            color = TealAccent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDark.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = "Estado: PAGADO",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun OrderDetailPanel(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundDark
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Detalle de la orden #${order.id}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TealAccent
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            order.items.forEach { cartItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = cartItem.product.name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "x${cartItem.quantity}",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "$${String.format(Locale.getDefault(), "%.2f", cartItem.subtotal)}",
                        color = PrimaryOrange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format(Locale.getDefault(), "%.2f", order.total)}",
                    color = PrimaryOrange,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
