package com.example.ui.screens.cart

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItem
import com.example.data.model.Coupon
import com.example.data.model.ProductCategory
import com.example.data.seed.SampleData
import com.example.ui.theme.CoralTertiary
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    onNavigateToCheckout: () -> Unit,
    onNavigateToExplore: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val savedForLaterItems by viewModel.savedForLaterItems.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()

    var couponInput by remember { mutableStateOf("") }
    var couponMessage by remember { mutableStateOf<String?>(null) }

    val subtotal = remember(cartItems) {
        cartItems.sumOf { it.product.price * it.quantity }
    }
    val discount = remember(appliedCoupon, subtotal) {
        when {
            appliedCoupon == null -> 0.0
            appliedCoupon!!.flatDiscount > 0 -> appliedCoupon!!.flatDiscount
            appliedCoupon!!.discountPercent > 0 -> subtotal * (appliedCoupon!!.discountPercent / 100.0)
            else -> 0.0
        }
    }
    val deliveryFee = if (subtotal > 50.0 || cartItems.isEmpty()) 0.0 else 4.99
    val tax = (subtotal - discount).coerceAtLeast(0.0) * 0.08
    val total = (subtotal - discount + deliveryFee + tax).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Shopping Cart (${cartItems.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
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
                                Text("Grand Total", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "$${String.format("%.2f", total)}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = FreshGreenPrimary
                                )
                            }
                            Button(
                                onClick = onNavigateToCheckout,
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("cart_checkout_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Proceed to Checkout", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty() && savedForLaterItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(
                        Icons.Outlined.RemoveShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your cart is empty", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Explore fresh seafood, organic groceries, and apparel with AI Virtual Try-On.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateToExplore,
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Start Shopping")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Free delivery progress
                item {
                    val progress = (subtotal / 50.0).coerceIn(0.0, 1.0).toFloat()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (subtotal >= 50.0) "🎉 You have FREE delivery!" else "Add $${String.format("%.2f", 50.0 - subtotal)} more for FREE delivery",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = FreshGreenPrimary
                            )
                        }
                    }
                }

                // Active Cart Items
                items(cartItems, key = { it.product.id }) { item ->
                    CartItemRow(
                        item = item,
                        onIncrement = { viewModel.updateCartQuantity(item.product.id, item.quantity + 1) },
                        onDecrement = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                        onRemove = { viewModel.removeFromCart(item.product.id) },
                        onSaveForLater = { viewModel.saveForLater(item.product.id) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Coupon Code Section
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Coupons & Offers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it.uppercase() },
                                    placeholder = { Text("Enter promo code", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val match = SampleData.sampleCoupons.find { it.code.equals(couponInput.trim(), ignoreCase = true) }
                                        if (match != null) {
                                            viewModel.applyCoupon(match)
                                            couponMessage = "Applied '${match.code}': ${match.description}"
                                        } else {
                                            couponMessage = "Invalid coupon code. Try FRESHMART50 or SEAFOOD20"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Apply")
                                }
                            }

                            // Available coupon shortcuts
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                SampleData.sampleCoupons.take(3).forEach { coup ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (appliedCoupon?.code == coup.code) CoralTertiary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable {
                                            viewModel.applyCoupon(coup)
                                            couponInput = coup.code
                                            couponMessage = "Applied '${coup.code}'"
                                        }
                                    ) {
                                        Text(
                                            text = coup.code,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (appliedCoupon?.code == coup.code) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }

                            if (couponMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = couponMessage!!,
                                    fontSize = 11.sp,
                                    color = if (appliedCoupon != null) FreshGreenPrimary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Bill Details Breakdown
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Bill Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            BillRow("Item Subtotal", "$${String.format("%.2f", subtotal)}")
                            if (discount > 0) {
                                BillRow("Discount (${appliedCoupon?.code})", "-$${String.format("%.2f", discount)}", isDiscount = true)
                            }
                            BillRow("Delivery Fee", if (deliveryFee == 0.0) "FREE" else "$${String.format("%.2f", deliveryFee)}")
                            BillRow("Estimated Taxes (8%)", "$${String.format("%.2f", tax)}")
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("To Pay", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                                Text(
                                    "$${String.format("%.2f", total)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    color = FreshGreenPrimary
                                )
                            }
                        }
                    }
                }

                // Saved For Later Items Section
                if (savedForLaterItems.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Saved for Later (${savedForLaterItems.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(savedForLaterItems, key = { "saved_" + it.product.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = item.product.images.firstOrNull(),
                                    contentDescription = item.product.name,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                    Text("$${String.format("%.2f", item.product.price)}", color = FreshGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { viewModel.moveToCart(item.product.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Move to Cart", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit,
    onSaveForLater: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.product.images.firstOrNull(),
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )

                if (item.selectedSize != null || item.selectedColor != null) {
                    Text(
                        text = listOfNotNull(item.selectedSize?.let { "Size: $it" }, item.selectedColor?.let { "Color: $it" }).joinToString(" • "),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${String.format("%.2f", item.product.price)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = FreshGreenPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Save for later",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSaveForLater() }
                    )
                    Text(
                        text = "Remove",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onRemove() }
                    )
                }
            }

            // Quantity stepper
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    IconButton(onClick = onDecrement, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                    }
                    Text(
                        text = "${item.quantity}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    IconButton(onClick = onIncrement, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BillRow(label: String, value: String, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isDiscount) FreshGreenPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
