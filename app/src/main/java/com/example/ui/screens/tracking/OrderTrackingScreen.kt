package com.example.ui.screens.tracking

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    order: Order?,
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val currentOrder = order ?: viewModel.liveOrders.collectAsState().value.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Track Order", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(currentOrder?.orderNumber ?: "FM-00000", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Filled.Home, contentDescription = "Home")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (currentOrder == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No order selected for tracking.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .testTag("tracking_screen"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // 1. Order Status Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when (currentOrder.status) {
                                OrderStatus.DELIVERED -> FreshGreenContainer
                                OrderStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
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
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(FreshGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (currentOrder.status) {
                                        OrderStatus.DELIVERED -> Icons.Filled.CheckCircle
                                        OrderStatus.OUT_FOR_DELIVERY -> Icons.Filled.ElectricMoped
                                        OrderStatus.SHIPPED -> Icons.Filled.LocalShipping
                                        else -> Icons.Filled.Inventory2
                                    },
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = currentOrder.status.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Estimated: ${currentOrder.deliverySlot.dayLabel} (${currentOrder.deliverySlot.timeRange})",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // 2. Timeline Stepper
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Delivery Progress", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            val steps = listOf(
                                OrderStatus.ORDER_PLACED to "Order Confirmed",
                                OrderStatus.PROCESSING to "Fresh Items Handpicked",
                                OrderStatus.PACKED to "Packed in Insulated Container",
                                OrderStatus.SHIPPED to "Dispatched to Local Hub",
                                OrderStatus.OUT_FOR_DELIVERY to "Out for Delivery",
                                OrderStatus.DELIVERED to "Delivered to Doorstep"
                            )

                            val currentOrdinal = currentOrder.status.ordinal

                            steps.forEachIndexed { index, (status, label) ->
                                val isDone = currentOrdinal >= status.ordinal
                                val isCurrent = currentOrdinal == status.ordinal

                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isDone) FreshGreenPrimary
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isDone) {
                                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        } else {
                                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Gray))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = label,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (isCurrent) {
                                            Text("Active Step", fontSize = 10.sp, color = FreshGreenPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (index < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 11.dp)
                                            .width(2.dp)
                                            .height(20.dp)
                                            .background(if (currentOrdinal > status.ordinal) FreshGreenPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Delivery Partner Info
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(OceanBlueSecondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Michael Rodriguez", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("FreshMart Express Courier • 4.9 ★", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            FilledTonalIconButton(onClick = {}) {
                                Icon(Icons.Filled.Call, contentDescription = "Call Courier", tint = FreshGreenPrimary)
                            }
                        }
                    }
                }

                // 4. Seafood & Freshness Protection Notice
                if (currentOrder.freshnessGuaranteed) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.AcUnit, contentDescription = null, tint = OceanBlueSecondary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("0°C Cold-Chain Tracking Active", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0369A1))
                                    Text("Seafood temperature is safely maintained at 0°C under ice monitoring.", fontSize = 11.sp, color = Color(0xFF075985))
                                }
                            }
                        }
                    }
                }

                // 5. Items in this order
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Items in this Order (${currentOrder.items.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            currentOrder.items.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = item.product.images.firstOrNull(),
                                        contentDescription = item.product.name,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.product.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                        Text("${item.quantity}x • $${String.format("%.2f", item.product.price)}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text("$${String.format("%.2f", item.product.price * item.quantity)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Paid", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("$${String.format("%.2f", currentOrder.total)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = FreshGreenPrimary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
