package com.example.ui.screens.detail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.seed.SampleData
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    onNavigateToTryOn: (Product) -> Unit,
    onNavigateToCart: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var selectedSize by remember { mutableStateOf(product.availableSizes?.firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(product.availableColors?.firstOrNull()) }
    var showAddedSnackbar by remember { mutableStateOf(false) }

    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val isInWishlist = wishlistItems.any { it.product.id == product.id }
    val outfitTips by viewModel.currentOutfitTips.collectAsState()
    val chefTip by viewModel.currentChefTip.collectAsState()

    val images = remember(product) {
        if (product.images.isNotEmpty()) product.images else listOf("https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=600")
    }
    val pagerState = rememberPagerState(pageCount = { images.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name, maxLines = 1, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWishlist(product) }) {
                        Icon(
                            imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isInWishlist) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = "Cart")
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Total Price info
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Price", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "$${String.format("%.2f", product.price * quantity)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = FreshGreenPrimary
                        )
                    }

                    // Add to Cart CTA
                    Button(
                        onClick = {
                            viewModel.addToCart(product, quantity, selectedSize, selectedColor)
                            showAddedSnackbar = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp)
                            .testTag("detail_add_to_cart_btn")
                    ) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        },
        snackbarHost = {
            if (showAddedSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = {
                            showAddedSnackbar = false
                            onNavigateToCart()
                        }) {
                            Text("VIEW CART", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    containerColor = FreshGreenPrimary
                ) {
                    Text("Added $quantity item(s) to cart!")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Image Pager Gallery
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        AsyncImage(
                            model = images[page],
                            contentDescription = "${product.name} image $page",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Pager Dots
                    if (images.size > 1) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(images.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f))
                                )
                            }
                        }
                    }

                    // In Stock badge
                    Surface(
                        color = if (product.inStock) FreshBadgeGreen else MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = if (product.inStock) "In Stock (${product.stockQuantity})" else "Out of Stock",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 2. Title, Subcategory & Price
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = when (product.category) {
                                ProductCategory.CLOTHING -> CategoryClothingBg
                                ProductCategory.GROCERIES -> CategoryGroceryBg
                                ProductCategory.SEAFOOD -> CategorySeafoodBg
                                else -> MaterialTheme.colorScheme.primaryContainer
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = product.subCategory,
                                color = when (product.category) {
                                    ProductCategory.CLOTHING -> CategoryClothingFg
                                    ProductCategory.GROCERIES -> CategoryGroceryFg
                                    ProductCategory.SEAFOOD -> CategorySeafoodFg
                                    else -> MaterialTheme.colorScheme.onPrimaryContainer
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (product.isDeal) {
                            Surface(
                                color = CoralContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${product.discountPercent}% OFF",
                                    color = CoralOnContainer,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price & Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${String.format("%.2f", product.price)}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FreshGreenPrimary
                            )
                            if (product.originalPrice > product.price) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$${String.format("%.2f", product.originalPrice)}",
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "/ ${product.unit}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = GoldRating, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = " (${product.reviewCount})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 3. AI Virtual Try-On Banner (For Clothing)
            if (product.tryOnCompatible) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF7C3AED),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AI Virtual Try-On Available",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF6B21A8)
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Preview how this item fits your body before ordering.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF4C1D95)
                                )
                            }
                            Button(
                                onClick = { onNavigateToTryOn(product) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_try_before_buy")
                            ) {
                                Text("Try On ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 4. Seafood Freshness Guarantee Card
            if (product.category == ProductCategory.SEAFOOD && product.seafoodFreshness != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Verified, contentDescription = null, tint = OceanBlueSecondary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Freshness & Catch Guarantee", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF075985))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            FreshnessDetailRow("Catch Timestamp", product.seafoodFreshness.catchTime)
                            FreshnessDetailRow("Marine Origin", product.seafoodFreshness.origin)
                            FreshnessDetailRow("Temperature Control", product.seafoodFreshness.storageTemp)
                            FreshnessDetailRow("Quality Standard", product.seafoodFreshness.freshnessGrade)
                            FreshnessDetailRow("Preparation", product.seafoodFreshness.cleaningOption)
                        }
                    }
                }
            }

            // 5. Size Selector (for Clothing)
            if (product.availableSizes != null) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text("Select Size", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            product.availableSizes.forEach { size ->
                                val isSelected = selectedSize == size
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) FreshGreenPrimary else MaterialTheme.colorScheme.surfaceVariant)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) FreshGreenPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedSize = size },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = size,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Quantity Stepper
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quantity", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = "$quantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 7. Product Description & Materials
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Description", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 19.sp
                    )

                    if (!product.fabricOrIngredients.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (product.category == ProductCategory.CLOTHING) "Fabric & Care" else "Key Ingredients",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = product.fabricOrIngredients,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 8. AI Styling Tips or Seafood Chef Tips
            if (outfitTips.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Stylist Outfit Recommendations", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF6D28D9))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        outfitTips.forEach { tip ->
                            Text("• $tip", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }

            if (!chefTip.isNullOrBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE0F2FE))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Restaurant, contentDescription = null, tint = OceanBlueSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Executive Chef Cooking Recommendation", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0369A1))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(chefTip!!, fontSize = 12.sp, color = Color(0xFF0F172A))
                    }
                }
            }

            // 9. Customer Reviews
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Customer Reviews", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    SampleData.sampleReviews.forEach { review ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(review.userName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(review.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(review.rating.toInt()) {
                                        Icon(Icons.Filled.Star, contentDescription = null, tint = GoldRating, modifier = Modifier.size(12.dp))
                                    }
                                    if (review.verifiedPurchase) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Verified Purchase", color = FreshGreenPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(review.comment, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FreshnessDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = Color(0xFF334155))
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
}
