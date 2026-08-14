package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreshMartTopBar(
    title: String = "FreshMart",
    cartCount: Int = 0,
    wishlistCount: Int = 0,
    notificationCount: Int = 0,
    onCartClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(FreshGreenPrimary, OceanBlueSecondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = "FreshMart Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Clothing • Groceries • Seafood",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSearchClick, modifier = Modifier.testTag("top_search_button")) {
                Icon(Icons.Outlined.Search, contentDescription = "Search Products")
            }
            IconButton(onClick = onWishlistClick, modifier = Modifier.testTag("top_wishlist_button")) {
                BadgedBox(
                    badge = {
                        if (wishlistCount > 0) {
                            Badge(containerColor = CoralTertiary) {
                                Text("$wishlistCount")
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Wishlist")
                }
            }
            IconButton(onClick = onNotificationsClick, modifier = Modifier.testTag("top_notif_button")) {
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge(containerColor = CoralTertiary) {
                                Text("$notificationCount")
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                }
            }
            IconButton(onClick = onCartClick, modifier = Modifier.testTag("top_cart_button")) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = FreshGreenPrimary) {
                                Text("$cartCount")
                            }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.ShoppingCart, contentDescription = "Shopping Cart")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun FreshMartBottomNav(
    currentRoute: String,
    cartCount: Int,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FreshGreenPrimary,
                indicatorColor = FreshGreenContainer
            ),
            modifier = Modifier.testTag("nav_home")
        )
        NavigationBarItem(
            selected = currentRoute == "catalog",
            onClick = { onNavigate("catalog") },
            icon = { Icon(if (currentRoute == "catalog") Icons.Filled.Storefront else Icons.Outlined.Storefront, contentDescription = "Catalog") },
            label = { Text("Explore", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FreshGreenPrimary,
                indicatorColor = FreshGreenContainer
            ),
            modifier = Modifier.testTag("nav_catalog")
        )
        NavigationBarItem(
            selected = currentRoute == "tryon",
            onClick = { onNavigate("tryon") },
            icon = {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF7C3AED), Color(0xFFEC4899))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.AutoAwesome,
                        contentDescription = "AI Try-On",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            },
            label = { Text("AI Try-On", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED)) },
            modifier = Modifier.testTag("nav_tryon")
        )
        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = { onNavigate("cart") },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = FreshGreenPrimary) {
                                Text("$cartCount")
                            }
                        }
                    }
                ) {
                    Icon(if (currentRoute == "cart") Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart, contentDescription = "Cart")
                }
            },
            label = { Text("Cart", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FreshGreenPrimary,
                indicatorColor = FreshGreenContainer
            ),
            modifier = Modifier.testTag("nav_cart")
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = { Icon(if (currentRoute == "profile") Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FreshGreenPrimary,
                indicatorColor = FreshGreenContainer
            ),
            modifier = Modifier.testTag("nav_profile")
        )
    }
}

data class PromoBanner(
    val title: String,
    val subtitle: String,
    val tag: String,
    val gradient: List<Color>,
    val imageUrl: String,
    val category: ProductCategory
)

@Composable
fun PromoBannerCarousel(
    onBannerClick: (ProductCategory) -> Unit = {}
) {
    val banners = listOf(
        PromoBanner(
            title = "AI Virtual Try-On Live",
            subtitle = "See how dresses & shirts look on you before buying",
            tag = "✨ AI Powered",
            gradient = listOf(Color(0xFF6366F1), Color(0xFF9333EA)),
            imageUrl = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=600",
            category = ProductCategory.CLOTHING
        ),
        PromoBanner(
            title = "Morning Seafood Catch",
            subtitle = "Live Mud Crabs & Atlantic Salmon with 0°C Ice Delivery",
            tag = "🐟 Fresh Today",
            gradient = listOf(Color(0xFF0284C7), Color(0xFF0D9488)),
            imageUrl = "https://images.unsplash.com/photo-1534483509719-3feaee7c30da?w=600",
            category = ProductCategory.SEAFOOD
        ),
        PromoBanner(
            title = "100% Farm Groceries",
            subtitle = "Organic Hass Avocados, A2 Milk & Heirloom Grains",
            tag = "🥑 Farm Direct",
            gradient = listOf(Color(0xFF059669), Color(0xFF10B981)),
            imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=600",
            category = ProductCategory.GROCERIES
        )
    )

    val pagerState = rememberPagerState(pageCount = { banners.size })

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp)
        ) { page ->
            val banner = banners[page]
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onBannerClick(banner.category) },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.horizontalGradient(banner.gradient))
                ) {
                    // Decorative background image with blend
                    AsyncImage(
                        model = banner.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop,
                        alpha = 0.25f
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = banner.tag,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Column {
                            Text(
                                text = banner.title,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = banner.subtitle,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Shop Now",
                                color = Color(0xFF0F172A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Pager indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(
                            width = if (pagerState.currentPage == index) 18.dp else 6.dp,
                            height = 6.dp
                        )
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) FreshGreenPrimary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: ProductCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (bgGradient, iconVector, badgeLabel) = when (category) {
        ProductCategory.ALL -> Triple(
            listOf(Color(0xFF475569), Color(0xFF334155)),
            Icons.Default.Apps,
            "All"
        )
        ProductCategory.CLOTHING -> Triple(
            listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
            Icons.Default.Checkroom,
            "Try-On"
        )
        ProductCategory.GROCERIES -> Triple(
            listOf(Color(0xFF10B981), Color(0xFF059669)),
            Icons.Default.Eco,
            "Organic"
        )
        ProductCategory.SEAFOOD -> Triple(
            listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
            Icons.Default.SetMeal,
            "0°C Fresh"
        )
    }

    Card(
        modifier = Modifier
            .width(105.dp)
            .height(115.dp)
            .clickable { onClick() }
            .testTag("category_${category.name.lowercase()}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp),
        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(FreshGreenPrimary, OceanBlueSecondary)),
            width = 2.dp
        ) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(bgGradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = category.displayName,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = category.displayName,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = badgeLabel,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    isInWishlist: Boolean = false,
    onProductClick: () -> Unit = {},
    onAddToCart: () -> Unit = {},
    onToggleWishlist: () -> Unit = {},
    onTryOnClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = product.images.firstOrNull(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Category or Deal badge
                if (product.discountPercent > 0) {
                    Surface(
                        color = CoralTertiary,
                        shape = RoundedCornerShape(bottomEnd = 10.dp, topStart = 16.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "${product.discountPercent}% OFF",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }

                // Seafood freshness badge or Try-On Pill
                if (product.category == ProductCategory.SEAFOOD) {
                    Surface(
                        color = OceanBlueSecondary.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Filled.AcUnit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Iced 0°C",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Wishlist Heart Icon
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isInWishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isInWishlist) Color(0xFFEF4444) else Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                // SubCategory chip
                Text(
                    text = product.subCategory.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (product.category) {
                        ProductCategory.CLOTHING -> CategoryClothingFg
                        ProductCategory.GROCERIES -> CategoryGroceryFg
                        ProductCategory.SEAFOOD -> CategorySeafoodFg
                        else -> MaterialTheme.colorScheme.primary
                    }
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating & Review Count
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = GoldRating,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${product.rating}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " (${product.reviewCount})",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Pricing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$${String.format("%.2f", product.price)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = FreshGreenPrimary
                            )
                            if (product.originalPrice > product.price) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$${String.format("%.2f", product.originalPrice)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }
                        Text(
                            text = product.unit,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Add to Cart mini button
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier
                            .size(32.dp)
                            .background(FreshGreenPrimary, RoundedCornerShape(8.dp))
                            .testTag("add_cart_${product.id}")
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // AI Try-On Button for Clothing
                if (product.tryOnCompatible && onTryOnClick != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = onTryOnClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7C3AED)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.horizontalGradient(listOf(Color(0xFF7C3AED), Color(0xFFEC4899))),
                            width = 1.dp
                        )
                    ) {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Try On AI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
