package com.example.ui.screens.wishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    onProductClick: (Product) -> Unit,
    onNavigateToTryOn: (Product) -> Unit
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wishlist (${wishlistItems.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (wishlistItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(
                        Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your wishlist is empty", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tap the heart icon on any product to save it for later.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp)
                    .testTag("wishlist_grid"),
                contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(wishlistItems, key = { it.product.id }) { wish ->
                    ProductCard(
                        product = wish.product,
                        isInWishlist = true,
                        onProductClick = {
                            viewModel.selectProduct(wish.product)
                            onProductClick(wish.product)
                        },
                        onAddToCart = { viewModel.addToCart(wish.product) },
                        onToggleWishlist = { viewModel.toggleWishlist(wish.product) },
                        onTryOnClick = if (wish.product.tryOnCompatible) {
                            { onNavigateToTryOn(wish.product) }
                        } else null
                    )
                }
            }
        }
    }
}
