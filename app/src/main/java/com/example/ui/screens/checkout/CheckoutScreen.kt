package com.example.ui.screens.checkout

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.seed.SampleData
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShopViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    onOrderSuccess: (Order) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val selectedSlot by viewModel.selectedDeliverySlot.collectAsState()
    val selectedPayment by viewModel.selectedPaymentMethod.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()

    var isPlacingOrder by remember { mutableStateOf(false) }
    var iceBoxGuaranteed by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val subtotal = remember(cartItems) { cartItems.sumOf { it.product.price * it.quantity } }
    val discount = remember(appliedCoupon, subtotal) {
        when {
            appliedCoupon == null -> 0.0
            appliedCoupon!!.flatDiscount > 0 -> appliedCoupon!!.flatDiscount
            appliedCoupon!!.discountPercent > 0 -> subtotal * (appliedCoupon!!.discountPercent / 100.0)
            else -> 0.0
        }
    }
    val deliveryFee = if (subtotal > 50.0) 0.0 else 4.99
    val tax = (subtotal - discount).coerceAtLeast(0.0) * 0.08
    val total = (subtotal - discount + deliveryFee + tax).coerceAtLeast(0.0)

    val hasSeafood = remember(cartItems) {
        cartItems.any { it.product.category == ProductCategory.SEAFOOD }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout & Payment", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Payable", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "$${String.format("%.2f", total)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FreshGreenPrimary
                            )
                        }
                        Button(
                            onClick = {
                                if (!isPlacingOrder) {
                                    isPlacingOrder = true
                                    scope.launch {
                                        val placed = viewModel.placeOrder()
                                        isPlacingOrder = false
                                        if (placed != null) {
                                            onOrderSuccess(placed)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("btn_confirm_pay"),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isPlacingOrder && cartItems.isNotEmpty()
                        ) {
                            if (isPlacingOrder) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Processing...")
                            } else {
                                Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Place Order ($${String.format("%.2f", total)})", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Shipping Address Section
            item {
                SectionTitle("1. Delivery Address", icon = Icons.Filled.LocationOn)
                Spacer(modifier = Modifier.height(8.dp))
                SampleData.sampleAddresses.forEach { addr ->
                    val isSelected = selectedAddress.id == addr.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectAddress(addr) }
                            .testTag("address_card_${addr.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(FreshGreenPrimary),
                            width = 2.dp
                        ) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.selectAddress(addr) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(addr.recipientName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(addr.tag, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(addr.streetAddress, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${addr.city}, ${addr.state} ${addr.postalCode} • ${addr.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // 2. Delivery Slot Picker (Same-Day / Tomorrow)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("2. Delivery Slot", icon = Icons.Filled.Schedule)
                Spacer(modifier = Modifier.height(8.dp))
                SampleData.sampleDeliverySlots.forEach { slot ->
                    val isSelected = selectedSlot.slotId == slot.slotId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectDeliverySlot(slot) }
                            .testTag("slot_card_${slot.slotId}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(FreshGreenPrimary),
                            width = 2.dp
                        ) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = isSelected, onClick = { viewModel.selectDeliverySlot(slot) })
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("${slot.dayLabel} • ${slot.timeRange}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        if (slot.isSameDay) "⚡ Express Same-Day Guaranteed" else "Standard Scheduled Delivery",
                                        fontSize = 11.sp,
                                        color = if (slot.isSameDay) CoralTertiary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (slot.isSameDay) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CoralContainer
                                ) {
                                    Text("FAST", color = CoralOnContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 3. Seafood 0°C Ice Delivery Guarantee (if seafood in cart)
            if (hasSeafood) {
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
                            Checkbox(
                                checked = iceBoxGuaranteed,
                                onCheckedChange = { iceBoxGuaranteed = it }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Seafood 0°C Insulated Thermal Packaging", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0369A1))
                                Text("Delivered in gel-iced insulated box to maintain dockside freshness.", fontSize = 11.sp, color = Color(0xFF075985))
                            }
                        }
                    }
                }
            }

            // 4. Payment Method Selection (UPI, Cards, Net Banking, COD)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("3. Payment Method", icon = Icons.Filled.Payment)
                Spacer(modifier = Modifier.height(8.dp))
                PaymentMethod.values().forEach { method ->
                    val isSelected = selectedPayment == method
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectPaymentMethod(method) }
                            .testTag("pay_${method.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(FreshGreenPrimary),
                            width = 2.dp
                        ) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = isSelected, onClick = { viewModel.selectPaymentMethod(method) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = when (method) {
                                    PaymentMethod.UPI -> Icons.Filled.AccountBalanceWallet
                                    PaymentMethod.CREDIT_DEBIT_CARD -> Icons.Filled.CreditCard
                                    PaymentMethod.NET_BANKING -> Icons.Filled.AccountBalance
                                    PaymentMethod.CASH_ON_DELIVERY -> Icons.Filled.Payments
                                },
                                contentDescription = null,
                                tint = FreshGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(method.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    when (method) {
                                        PaymentMethod.UPI -> "Instant payment via Google Pay, PhonePe, Paytm"
                                        PaymentMethod.CREDIT_DEBIT_CARD -> "Visa, MasterCard, Amex & RuPay cards"
                                        PaymentMethod.NET_BANKING -> "Direct net banking from 50+ major banks"
                                        PaymentMethod.CASH_ON_DELIVERY -> "Pay cash or QR code on arrival"
                                    },
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 5. Final Summary Checklist
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Order Breakdown (${cartItems.size} Items)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.quantity}x ${item.product.name}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                Text("$${String.format("%.2f", item.product.price * item.quantity)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("$${String.format("%.2f", total)}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = FreshGreenPrimary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = FreshGreenPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}
