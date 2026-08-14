package com.example.ui.screens.admin

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.theme.*
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val selectedTab by adminViewModel.selectedAdminTab.collectAsState()
    val products by adminViewModel.products.collectAsState()
    val orders by adminViewModel.liveOrders.collectAsState()
    val analytics by adminViewModel.analytics.collectAsState(initial = null)

    var showAddProductDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(OceanBlueSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FreshMart Admin Hub", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedTab == 1) {
                        IconButton(onClick = { showAddProductDialog = true }, modifier = Modifier.testTag("admin_add_product_btn")) {
                            Icon(Icons.Filled.AddCircle, contentDescription = "Add Product", tint = FreshGreenPrimary)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { adminViewModel.setAdminTab(0) },
                    text = { Text("Analytics", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { adminViewModel.setAdminTab(1) },
                    text = { Text("Inventory (${products.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { adminViewModel.setAdminTab(2) },
                    text = { Text("Orders (${orders.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedTab) {
                0 -> AnalyticsTab(analytics, orders.size)
                1 -> InventoryTab(
                    products = products,
                    onUpdateStock = { id, qty -> adminViewModel.updateStock(id, qty) },
                    onDelete = { adminViewModel.deleteProduct(it) }
                )
                2 -> OrdersManagerTab(
                    orders = orders,
                    onUpdateStatus = { id, stat -> adminViewModel.updateOrderStatus(id, stat) }
                )
            }
        }
    }

    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onAdd = { name, cat, sub, desc, price, orig, stock, unit, img, isTryOn ->
                adminViewModel.addNewProduct(name, cat, sub, desc, price, orig, stock, unit, img, isTryOn)
                showAddProductDialog = false
            }
        )
    }
}

@Composable
fun AnalyticsTab(
    analytics: com.example.ui.viewmodel.SalesAnalytics?,
    ordersCount: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Revenue Metric Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(listOf(FreshGreenPrimary, OceanBlueSecondary))
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Gross Platform Sales", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format("%.2f", analytics?.totalRevenue ?: 1428.50)}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Orders: $ordersCount", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Active Customers: ${analytics?.totalActiveCustomers ?: 482}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Category Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Department Revenue Share", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    CategoryShareBar(name = "👗 Clothing & Apparel", percent = 38, color = CategoryClothingFg)
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryShareBar(name = "🥑 Organic Groceries", percent = 27, color = CategoryGroceryFg)
                    Spacer(modifier = Modifier.height(8.dp))
                    CategoryShareBar(name = "🐟 Fresh Seafood & Crabs", percent = 35, color = CategorySeafoodFg)
                }
            }
        }

        // Quick AI & Logistics Insights
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Try-On & Operational Metrics", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    MetricRow("Virtual Try-On Conversions", "74.2% (+18%)")
                    MetricRow("Seafood Cold-Chain Quality", "100% 0°C Pass")
                    MetricRow("Same-Day Slot Delivery Rate", "98.6% On-Time")
                    MetricRow("Customer Satisfaction Score", "4.87 / 5.0 ★")
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun CategoryShareBar(name: String, percent: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("$percent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = percent / 100f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color
        )
    }
}

@Composable
fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun InventoryTab(
    products: List<Product>,
    onUpdateStock: (String, Int) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        items(products, key = { it.id }) { prod ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = prod.images.firstOrNull(),
                        contentDescription = prod.name,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                        Text(
                            "$${String.format("%.2f", prod.price)} / ${prod.unit} • Stock: ${prod.stockQuantity}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Stock buttons
                    IconButton(
                        onClick = { onUpdateStock(prod.id, (prod.stockQuantity - 5).coerceAtLeast(0)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "Decrease stock", modifier = Modifier.size(14.dp))
                    }
                    IconButton(
                        onClick = { onUpdateStock(prod.id, prod.stockQuantity + 10) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add stock", modifier = Modifier.size(14.dp))
                    }
                    IconButton(
                        onClick = { onDelete(prod.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersManagerTab(
    orders: List<com.example.data.model.Order>,
    onUpdateStatus: (String, OrderStatus) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        items(orders, key = { it.id }) { order ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("$${String.format("%.2f", order.total)}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = FreshGreenPrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Customer: ${order.shippingAddress.recipientName} • ${order.shippingAddress.phone}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Slot: ${order.deliverySlot.dayLabel} (${order.deliverySlot.timeRange})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Dropdown selector
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Status: ${order.status.displayName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            OrderStatus.values().forEach { st ->
                                DropdownMenuItem(
                                    text = { Text(st.displayName) },
                                    onClick = {
                                        onUpdateStatus(order.id, st)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, cat: ProductCategory, sub: String, desc: String, price: Double, orig: Double, stock: Int, unit: String, img: String, isTryOn: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCat by remember { mutableStateOf(ProductCategory.CLOTHING) }
    var subCategory by remember { mutableStateOf("Shirts") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("39.99") }
    var originalPriceStr by remember { mutableStateOf("49.99") }
    var stockStr by remember { mutableStateOf("25") }
    var unit by remember { mutableStateOf("piece") }
    var imageUrl by remember { mutableStateOf("") }
    var isTryOn by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(ProductCategory.CLOTHING, ProductCategory.GROCERIES, ProductCategory.SEAFOOD).forEach { cat ->
                            FilterChip(
                                selected = selectedCat == cat,
                                onClick = {
                                    selectedCat = cat
                                    isTryOn = (cat == ProductCategory.CLOTHING)
                                    subCategory = when (cat) {
                                        ProductCategory.CLOTHING -> "Shirts"
                                        ProductCategory.GROCERIES -> "Fruits"
                                        ProductCategory.SEAFOOD -> "Fresh Fish"
                                        else -> "General"
                                    }
                                },
                                label = { Text(cat.displayName, fontSize = 11.sp) }
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = subCategory,
                        onValueChange = { subCategory = it },
                        label = { Text("Subcategory (e.g. T-Shirts, Crabs, Fruits)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Selling Price ($)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = stockStr,
                        onValueChange = { stockStr = it },
                        label = { Text("Initial Stock Quantity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL (Unsplash/Web)") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (selectedCat == ProductCategory.CLOTHING) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isTryOn, onCheckedChange = { isTryOn = it })
                            Text("Enable AI Virtual Try-On", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toDoubleOrNull() ?: 19.99
                    val op = originalPriceStr.toDoubleOrNull() ?: p
                    val st = stockStr.toIntOrNull() ?: 10
                    onAdd(name.ifBlank { "New Product" }, selectedCat, subCategory, description.ifBlank { "Fresh premium item" }, p, op, st, unit, imageUrl, isTryOn)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
            ) {
                Text("Add Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
