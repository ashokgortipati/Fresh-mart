package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.components.CategoryCard
import com.example.ui.components.ProductCard
import com.example.ui.components.PromoBannerCarousel
import com.example.ui.theme.*
import com.example.ui.viewmodel.ShopViewModel

@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    onNavigateToCatalog: (ProductCategory?) -> Unit,
    onProductClick: (Product) -> Unit,
    onNavigateToTryOn: (Product?) -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val products by viewModel.filteredProducts.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val selectedFilters by viewModel.filters.collectAsState()

    val clothingDeals = remember(products) {
        products.filter { it.category == ProductCategory.CLOTHING }
    }
    val freshSeafood = remember(products) {
        products.filter { it.category == ProductCategory.SEAFOOD }
    }
    val bestDeals = remember(products) {
        products.filter { it.isDeal }
    }
    val newArrivals = remember(products) {
        products.filter { it.isNewArrival }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Search Bar Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.searchQuery.collectAsState().value,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search Clothing, Seafood, Groceries...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = FreshGreenPrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = { onNavigateToCatalog(null) }) {
                            Icon(Icons.Filled.Tune, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = FreshGreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_bar")
                )
            }
        }

        // 2. Promotional Banners Carousel
        item {
            Spacer(modifier = Modifier.height(4.dp))
            PromoBannerCarousel(
                onBannerClick = { cat ->
                    viewModel.selectCategory(cat)
                    onNavigateToCatalog(cat)
                }
            )
        }

        // 3. Category Shortcuts
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shop by Category",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = { onNavigateToCatalog(null) }) {
                        Text("See All", color = FreshGreenPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProductCategory.values().forEach { cat ->
                        CategoryCard(
                            category = cat,
                            isSelected = selectedFilters.category == cat,
                            onClick = {
                                viewModel.selectCategory(cat)
                                if (cat != ProductCategory.ALL) {
                                    onNavigateToCatalog(cat)
                                }
                            }
                        )
                    }
                }
            }
        }

        // 4. AI Virtual Try-On Spotlight Hero Card
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNavigateToTryOn(null) },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF4F46E5), Color(0xFF7C3AED), Color(0xFFDB2777))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "NEW FEATURE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "AI Virtual Try-On",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Upload your selfie to try shirts, dresses & jeans with instant realistic fit preview.",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onNavigateToTryOn(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("home_launch_tryon")
                            ) {
                                Text(
                                    text = "Try It Now ✨",
                                    color = Color(0xFF6D28D9),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Fresh Catch Seafood Row
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "🐟 Today's Fresh Catch",
                subtitle = "Iced at 0°C • Same-Day Express Delivery",
                onSeeAll = { onNavigateToCatalog(ProductCategory.SEAFOOD) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(freshSeafood) { product ->
                    Box(modifier = Modifier.width(180.dp)) {
                        ProductCard(
                            product = product,
                            isInWishlist = wishlistItems.any { it.product.id == product.id },
                            onProductClick = {
                                viewModel.selectProduct(product)
                                onProductClick(product)
                            },
                            onAddToCart = { viewModel.addToCart(product) },
                            onToggleWishlist = { viewModel.toggleWishlist(product) }
                        )
                    }
                }
            }
        }

        // 6. Clothing & Apparel with AI Try-On
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "👗 Trending Apparel & Virtual Try-On",
                subtitle = "Try before you buy on any clothing item",
                onSeeAll = { onNavigateToCatalog(ProductCategory.CLOTHING) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(clothingDeals) { product ->
                    Box(modifier = Modifier.width(180.dp)) {
                        ProductCard(
                            product = product,
                            isInWishlist = wishlistItems.any { it.product.id == product.id },
                            onProductClick = {
                                viewModel.selectProduct(product)
                                onProductClick(product)
                            },
                            onAddToCart = { viewModel.addToCart(product) },
                            onToggleWishlist = { viewModel.toggleWishlist(product) },
                            onTryOnClick = { onNavigateToTryOn(product) }
                        )
                    }
                }
            }
        }

        // 7. Best Deals Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "⚡ Flash Deals & Big Savings",
                subtitle = "Limited time discounts across all departments",
                onSeeAll = { onNavigateToCatalog(null) }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(bestDeals) { product ->
                    Box(modifier = Modifier.width(180.dp)) {
                        ProductCard(
                            product = product,
                            isInWishlist = wishlistItems.any { it.product.id == product.id },
                            onProductClick = {
                                viewModel.selectProduct(product)
                                onProductClick(product)
                            },
                            onAddToCart = { viewModel.addToCart(product) },
                            onToggleWishlist = { viewModel.toggleWishlist(product) },
                            onTryOnClick = if (product.tryOnCompatible) {
                                { onNavigateToTryOn(product) }
                            } else null
                        )
                    }
                }
            }
        }

        // 8. FreshMart Guarantees Badge Row
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Why FreshMart?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GuaranteeItem(icon = Icons.Filled.AcUnit, title = "0°C Chilled", desc = "Seafood iced direct from dock")
                    GuaranteeItem(icon = Icons.Filled.AutoAwesome, title = "AI Try-On", desc = "Accurate sizing on your photo")
                    GuaranteeItem(icon = Icons.Filled.LocalShipping, title = "Same-Day", desc = "Express slot delivery")
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        TextButton(onClick = onSeeAll) {
            Text("See All", color = FreshGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun GuaranteeItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(95.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(FreshGreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = FreshGreenPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(text = desc, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
    }
}
