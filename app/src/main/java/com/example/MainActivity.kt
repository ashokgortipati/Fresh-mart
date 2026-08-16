package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.cart.CartScreen
import com.example.ui.screens.catalog.CatalogScreen
import com.example.ui.screens.checkout.CheckoutScreen
import com.example.ui.screens.detail.ProductDetailScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.tracking.OrderTrackingScreen
import com.example.ui.screens.tryon.AiVirtualTryOnScreen
import com.example.ui.screens.wishlist.WishlistScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.viewmodel.TryOnViewModel

sealed class AppDestination {
    object Home : AppDestination()
    object Catalog : AppDestination()
    object TryOn : AppDestination()
    object Cart : AppDestination()
    object Profile : AppDestination()
    object Wishlist : AppDestination()
    object Notifications : AppDestination()
    object Admin : AppDestination()
    object Checkout : AppDestination()
    data class ProductDetail(val product: Product) : AppDestination()
    data class OrderTracking(val order: Order?) : AppDestination()
    object Auth : AppDestination()
}

class MainActivity : ComponentActivity() {

    private val shopViewModel: ShopViewModel by viewModels()
    private val tryOnViewModel: TryOnViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FreshMartApp(
                    shopViewModel = shopViewModel,
                    tryOnViewModel = tryOnViewModel,
                    authViewModel = authViewModel,
                    adminViewModel = adminViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreshMartApp(
    shopViewModel: ShopViewModel,
    tryOnViewModel: TryOnViewModel,
    authViewModel: AuthViewModel,
    adminViewModel: AdminViewModel
) {
    var currentDestination by remember { mutableStateOf<AppDestination>(AppDestination.Home) }
    val backStack = remember { mutableStateListOf<AppDestination>() }

    fun navigateTo(dest: AppDestination) {
        val authState = authViewModel.uiState.value
        val protectedDestinations = listOf(
            AppDestination.Profile,
            AppDestination.Cart,
            AppDestination.Checkout,
            AppDestination.Admin
        )

        val isProtected = protectedDestinations.any { 
            it::class == dest::class || it == dest 
        }

        if (isProtected && !authState.isLoggedIn) {
            if (currentDestination != AppDestination.Auth) {
                backStack.add(currentDestination)
            }
            currentDestination = AppDestination.Auth
        } else if (currentDestination != dest) {
            backStack.add(currentDestination)
            currentDestination = dest
        }
    }

    fun navigateBack() {
        if (backStack.isNotEmpty()) {
            currentDestination = backStack.removeAt(backStack.size - 1)
        } else if (currentDestination != AppDestination.Home) {
            currentDestination = AppDestination.Home
        }
    }

    BackHandler(enabled = backStack.isNotEmpty() || currentDestination != AppDestination.Home) {
        navigateBack()
    }

    val cartItems by shopViewModel.cartItems.collectAsState()
    val wishlistItems by shopViewModel.wishlistItems.collectAsState()
    val notifications by shopViewModel.notifications.collectAsState()
    val unreadNotificationsCount = remember(notifications) { notifications.count { !it.isRead } }
    val totalCartCount = remember(cartItems) { cartItems.sumOf { it.quantity } }

    val showBottomBar = currentDestination is AppDestination.Home ||
            currentDestination is AppDestination.Catalog ||
            currentDestination is AppDestination.TryOn ||
            currentDestination is AppDestination.Cart ||
            currentDestination is AppDestination.Profile

    val showTopAppBar = currentDestination is AppDestination.Home ||
            currentDestination is AppDestination.Catalog

    Scaffold(
        topBar = {
            if (showTopAppBar) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "FreshMart",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = FreshGreenContainer
                                    ) {
                                        Text(
                                            "Express",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FreshGreenPrimary,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    "📍 Deliver to: Home (742 Evergreen)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    actions = {
                        // Wishlist Icon with Badge
                        IconButton(
                            onClick = { navigateTo(AppDestination.Wishlist) },
                            modifier = Modifier.testTag("top_bar_wishlist_btn")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (wishlistItems.isNotEmpty()) {
                                        Badge(
                                            containerColor = CoralTertiary,
                                            contentColor = Color.White
                                        ) {
                                            Text("${wishlistItems.size}", fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Wishlist",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Notifications Icon with Badge
                        IconButton(
                            onClick = { navigateTo(AppDestination.Notifications) },
                            modifier = Modifier.testTag("top_bar_notifications_btn")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotificationsCount > 0) {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = Color.White
                                        ) {
                                            Text("$unreadNotificationsCount", fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentDestination is AppDestination.Home,
                        onClick = {
                            backStack.clear()
                            currentDestination = AppDestination.Home
                        },
                        icon = {
                            Icon(
                                if (currentDestination is AppDestination.Home) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                                contentDescription = "Shop"
                            )
                        },
                        label = { Text("Shop", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_item_shop")
                    )

                    NavigationBarItem(
                        selected = currentDestination is AppDestination.Catalog,
                        onClick = {
                            backStack.clear()
                            currentDestination = AppDestination.Catalog
                        },
                        icon = {
                            Icon(
                                if (currentDestination is AppDestination.Catalog) Icons.Filled.Category else Icons.Outlined.Category,
                                contentDescription = "Explore"
                            )
                        },
                        label = { Text("Explore", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_item_explore")
                    )

                    NavigationBarItem(
                        selected = currentDestination is AppDestination.TryOn,
                        onClick = {
                            backStack.clear()
                            currentDestination = AppDestination.TryOn
                        },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (currentDestination is AppDestination.TryOn) FreshGreenPrimary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.AutoAwesome,
                                    contentDescription = "AI Try-On",
                                    tint = if (currentDestination is AppDestination.TryOn) Color.White else FreshGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                "AI Try-On",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentDestination is AppDestination.TryOn) FreshGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("nav_item_tryon")
                    )

                    NavigationBarItem(
                        selected = currentDestination is AppDestination.Cart,
                        onClick = {
                            backStack.clear()
                            currentDestination = AppDestination.Cart
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (totalCartCount > 0) {
                                        Badge(
                                            containerColor = FreshGreenPrimary,
                                            contentColor = Color.White
                                        ) {
                                            Text("$totalCartCount", fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    if (currentDestination is AppDestination.Cart) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                    contentDescription = "Cart"
                                )
                            }
                        },
                        label = { Text("Cart", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_item_cart")
                    )

                    NavigationBarItem(
                        selected = currentDestination is AppDestination.Profile,
                        onClick = {
                            backStack.clear()
                            currentDestination = AppDestination.Profile
                        },
                        icon = {
                            Icon(
                                if (currentDestination is AppDestination.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        modifier = Modifier.testTag("nav_item_profile")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val dest = currentDestination) {
                is AppDestination.Home -> {
                    HomeScreen(
                        viewModel = shopViewModel,
                        onNavigateToCatalog = { category ->
                            if (category != null) {
                                shopViewModel.selectCategory(category)
                            }
                            navigateTo(AppDestination.Catalog)
                        },
                        onProductClick = { product ->
                            shopViewModel.selectProduct(product)
                            navigateTo(AppDestination.ProductDetail(product))
                        },
                        onNavigateToTryOn = { product ->
                            if (product != null) {
                                tryOnViewModel.setSelectedProduct(product)
                            }
                            navigateTo(AppDestination.TryOn)
                        },
                        onNavigateToAdmin = {
                            navigateTo(AppDestination.Admin)
                        }
                    )
                }

                is AppDestination.Catalog -> {
                    CatalogScreen(
                        viewModel = shopViewModel,
                        onProductClick = { product ->
                            shopViewModel.selectProduct(product)
                            navigateTo(AppDestination.ProductDetail(product))
                        },
                        onNavigateToTryOn = { product ->
                            tryOnViewModel.setSelectedProduct(product)
                            navigateTo(AppDestination.TryOn)
                        }
                    )
                }

                is AppDestination.TryOn -> {
                    AiVirtualTryOnScreen(
                        tryOnViewModel = tryOnViewModel,
                        shopViewModel = shopViewModel,
                        onBack = { navigateBack() },
                        onNavigateToProduct = { product ->
                            shopViewModel.selectProduct(product)
                            navigateTo(AppDestination.ProductDetail(product))
                        }
                    )
                }

                is AppDestination.Cart -> {
                    CartScreen(
                        viewModel = shopViewModel,
                        onBack = { navigateBack() },
                        onNavigateToCheckout = { navigateTo(AppDestination.Checkout) },
                        onNavigateToExplore = {
                            backStack.clear()
                            currentDestination = AppDestination.Catalog
                        }
                    )
                }

                is AppDestination.ProductDetail -> {
                    ProductDetailScreen(
                        product = dest.product,
                        viewModel = shopViewModel,
                        onBack = { navigateBack() },
                        onNavigateToTryOn = { product ->
                            tryOnViewModel.setSelectedProduct(product)
                            navigateTo(AppDestination.TryOn)
                        },
                        onNavigateToCart = {
                            navigateTo(AppDestination.Cart)
                        }
                    )
                }

                is AppDestination.Checkout -> {
                    CheckoutScreen(
                        viewModel = shopViewModel,
                        onBack = { navigateBack() },
                        onOrderSuccess = { placedOrder ->
                            shopViewModel.selectOrderForTracking(placedOrder)
                            navigateTo(AppDestination.OrderTracking(placedOrder))
                        }
                    )
                }

                is AppDestination.OrderTracking -> {
                    OrderTrackingScreen(
                        order = dest.order,
                        viewModel = shopViewModel,
                        onBack = { navigateBack() },
                        onNavigateToHome = {
                            backStack.clear()
                            currentDestination = AppDestination.Home
                        }
                    )
                }

                is AppDestination.Wishlist -> {
                    WishlistScreen(
                        viewModel = shopViewModel,
                        onBack = { navigateBack() },
                        onProductClick = { product ->
                            shopViewModel.selectProduct(product)
                            navigateTo(AppDestination.ProductDetail(product))
                        },
                        onNavigateToTryOn = { product ->
                            tryOnViewModel.setSelectedProduct(product)
                            navigateTo(AppDestination.TryOn)
                        }
                    )
                }

                is AppDestination.Notifications -> {
                    NotificationsScreen(
                        viewModel = shopViewModel,
                        onBack = { navigateBack() }
                    )
                }

                is AppDestination.Profile -> {
                    ProfileScreen(
                        shopViewModel = shopViewModel,
                        authViewModel = authViewModel,
                        onNavigateToOrders = {
                            val firstOrder = shopViewModel.liveOrders.value.firstOrNull()
                            navigateTo(AppDestination.OrderTracking(firstOrder))
                        },
                        onNavigateToTracking = { order ->
                            navigateTo(AppDestination.OrderTracking(order))
                        },
                        onNavigateToAdmin = {
                            navigateTo(AppDestination.Admin)
                        },
                        onNavigateToAuth = {
                            navigateTo(AppDestination.Auth)
                        }
                    )
                }

                is AppDestination.Admin -> {
                    AdminDashboardScreen(
                        adminViewModel = adminViewModel,
                        onBack = { navigateBack() }
                    )
                }

                is AppDestination.Auth -> {
                    AuthScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = {
                            backStack.clear()
                            currentDestination = AppDestination.Home
                        }
                    )
                }
            }
        }
    }
}
