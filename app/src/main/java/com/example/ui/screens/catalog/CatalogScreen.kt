package com.example.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.ui.components.ProductCard
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.viewmodel.FilterState
import com.example.ui.viewmodel.ShopViewModel
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: ShopViewModel,
    onProductClick: (Product) -> Unit,
    onNavigateToTryOn: (Product) -> Unit
) {
    val products by viewModel.filteredProducts.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    val subCategories = remember(filters.category) {
        when (filters.category) {
            ProductCategory.CLOTHING -> listOf("All") + ClothingSubCategory.values().map { it.displayName }
            ProductCategory.GROCERIES -> listOf("All") + GrocerySubCategory.values().map { it.displayName }
            ProductCategory.SEAFOOD -> listOf("All") + SeafoodSubCategory.values().map { it.displayName }
            ProductCategory.ALL -> listOf("All", "Shirts", "Dresses", "Fruits", "Dairy Products", "Fresh Fish", "Prawns", "Crabs")
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Search Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Search 100+ items...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = FreshGreenPrimary) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("catalog_search_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier
                            .background(
                                if (filters.dealsOnly || filters.tryOnOnly || filters.minRating > 0 || filters.maxPrice < 150)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                            .testTag("catalog_filter_btn")
                    ) {
                        Icon(
                            Icons.Filled.FilterList,
                            contentDescription = "Filters",
                            tint = FreshGreenPrimary
                        )
                    }
                }

                // Main Category Filter Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProductCategory.values().forEach { cat ->
                        val isSelected = filters.category == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(cat) },
                            label = { Text(cat.displayName, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FreshGreenPrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }

                // SubCategory Horizontal Scroll
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subCategories.forEach { sub ->
                        val isSelected = (filters.selectedSubCategory == sub) || (filters.selectedSubCategory == null && sub == "All")
                        SuggestionChip(
                            onClick = { viewModel.selectSubCategory(if (sub == "All") null else sub) },
                            label = { Text(sub, fontSize = 12.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }

                // Count & Active Sort summary
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${products.size} Products Found",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = "Sort: ${filters.sortBy.title}",
                            fontSize = 11.sp,
                            color = FreshGreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (products.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No products match your filters",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your search or clearing price filters.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.updateSearchQuery("")
                            viewModel.updateFilters(FilterState())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(products, key = { it.id }) { product ->
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

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }
        ) {
            FilterBottomSheetContent(
                currentFilters = filters,
                onApply = { newFilters ->
                    viewModel.updateFilters(newFilters)
                    showFilterSheet = false
                },
                onReset = {
                    viewModel.updateFilters(FilterState(category = filters.category))
                    showFilterSheet = false
                }
            )
        }
    }
}

@Composable
fun FilterBottomSheetContent(
    currentFilters: FilterState,
    onApply: (FilterState) -> Unit,
    onReset: () -> Unit
) {
    var state by remember { mutableStateOf(currentFilters) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filter & Sort Products",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(onClick = onReset) {
                Text("Reset", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sort By
        Text("Sort By", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SortOption.values().forEach { sort ->
                val selected = state.sortBy == sort
                FilterChip(
                    selected = selected,
                    onClick = { state = state.copy(sortBy = sort) },
                    label = { Text(sort.title, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Price Range Slider
        Text("Max Price: $${state.maxPrice.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Slider(
            value = state.maxPrice.toFloat(),
            onValueChange = { state = state.copy(maxPrice = it.toDouble()) },
            valueRange = 5f..150f,
            steps = 29,
            colors = SliderDefaults.colors(thumbColor = FreshGreenPrimary, activeTrackColor = FreshGreenPrimary)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("AI Virtual Try-On Eligible", fontSize = 13.sp)
            Switch(
                checked = state.tryOnOnly,
                onCheckedChange = { state = state.copy(tryOnOnly = it) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Deals & Discounts Only", fontSize = 13.sp)
            Switch(
                checked = state.dealsOnly,
                onCheckedChange = { state = state.copy(dealsOnly = it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onApply(state) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Apply Filters", fontWeight = FontWeight.Bold)
        }
    }
}
