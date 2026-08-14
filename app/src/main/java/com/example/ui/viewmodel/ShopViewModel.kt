package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.GeminiAiRepository
import com.example.data.repository.ShopRepository
import com.example.data.seed.SampleData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption(val title: String) {
    POPULARITY("Popularity"),
    RATING("Highest Rating"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    NEWEST("New Arrivals")
}

data class FilterState(
    val category: ProductCategory = ProductCategory.ALL,
    val selectedSubCategory: String? = null,
    val minPrice: Double = 0.0,
    val maxPrice: Double = 150.0,
    val minRating: Float = 0.0f,
    val dealsOnly: Boolean = false,
    val newArrivalsOnly: Boolean = false,
    val tryOnOnly: Boolean = false,
    val sortBy: SortOption = SortOption.POPULARITY
)

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    val repository = ShopRepository(application)
    val geminiAiRepository = GeminiAiRepository(application)

    // Search & Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filters = MutableStateFlow(FilterState())
    val filters: StateFlow<FilterState> = _filters.asStateFlow()

    // Selected Product for Detail View
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // AI Outfit Recommendations for current detail
    private val _currentOutfitTips = MutableStateFlow<List<String>>(emptyList())
    val currentOutfitTips: StateFlow<List<String>> = _currentOutfitTips.asStateFlow()

    // Chef advice for current seafood product
    private val _currentChefTip = MutableStateFlow<String?>(null)
    val currentChefTip: StateFlow<String?> = _currentChefTip.asStateFlow()

    // Filtered Products
    val filteredProducts: StateFlow<List<Product>> = combine(
        repository.products,
        _searchQuery,
        _filters
    ) { allProds, query, filter ->
        var list = allProds

        if (filter.category != ProductCategory.ALL) {
            list = list.filter { it.category == filter.category }
        }

        if (!filter.selectedSubCategory.isNullOrBlank() && filter.selectedSubCategory != "All") {
            list = list.filter { it.subCategory.equals(filter.selectedSubCategory, ignoreCase = true) }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                it.subCategory.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.tags.any { tag -> tag.lowercase().contains(q) }
            }
        }

        list = list.filter { it.price in filter.minPrice..filter.maxPrice }

        if (filter.minRating > 0f) {
            list = list.filter { it.rating >= filter.minRating }
        }

        if (filter.dealsOnly) {
            list = list.filter { it.isDeal }
        }

        if (filter.newArrivalsOnly) {
            list = list.filter { it.isNewArrival }
        }

        if (filter.tryOnOnly) {
            list = list.filter { it.tryOnCompatible }
        }

        when (filter.sortBy) {
            SortOption.POPULARITY -> list.sortedByDescending { it.reviewCount }
            SortOption.RATING -> list.sortedByDescending { it.rating }
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.price }
            SortOption.NEWEST -> list.sortedByDescending { it.isNewArrival }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SampleData.sampleProducts)

    // Reactive Cart & Wishlist
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedForLaterItems: StateFlow<List<CartItem>> = repository.savedForLaterItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistItems: StateFlow<List<WishlistItem>> = repository.wishlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyViewed: StateFlow<List<Product>> = repository.recentlyViewedProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveOrders: StateFlow<List<Order>> = repository.liveOrders
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val appliedCoupon: StateFlow<Coupon?> = repository.appliedCoupon
    val selectedDeliverySlot: StateFlow<DeliverySlot> = repository.selectedDeliverySlot
    val selectedAddress: StateFlow<Address> = repository.selectedAddress
    val selectedPaymentMethod: StateFlow<PaymentMethod> = repository.selectedPaymentMethod

    // Selected order for Tracking Screen
    private val _trackingOrder = MutableStateFlow<Order?>(null)
    val trackingOrder: StateFlow<Order?> = _trackingOrder.asStateFlow()

    init {
        // Default tracking order to newest
        _trackingOrder.value = SampleData.sampleOrders.firstOrNull()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: ProductCategory) {
        _filters.value = _filters.value.copy(category = category, selectedSubCategory = null)
    }

    fun selectSubCategory(subCategory: String?) {
        _filters.value = _filters.value.copy(selectedSubCategory = subCategory)
    }

    fun updateFilters(newFilters: FilterState) {
        _filters.value = newFilters
    }

    fun selectProduct(product: Product) {
        _selectedProduct.value = product
        viewModelScope.launch {
            repository.recordProductView(product.id)
            if (product.category == ProductCategory.CLOTHING) {
                _currentOutfitTips.value = geminiAiRepository.getFashionOutfitAdvice(product)
            } else if (product.category == ProductCategory.SEAFOOD) {
                _currentChefTip.value = geminiAiRepository.getSeafoodCookingChefAdvice(product)
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1, size: String? = null, color: String? = null) {
        viewModelScope.launch {
            repository.addToCart(product, quantity, size, color)
        }
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun saveForLater(productId: String) {
        viewModelScope.launch {
            repository.saveForLater(productId)
        }
    }

    fun moveToCart(productId: String) {
        viewModelScope.launch {
            repository.moveToCart(productId)
        }
    }

    fun toggleWishlist(product: Product) {
        viewModelScope.launch {
            val isInWish = wishlistItems.value.any { it.product.id == product.id }
            repository.toggleWishlist(product, isInWish)
        }
    }

    fun applyCoupon(coupon: Coupon?) {
        repository.applyCoupon(coupon)
    }

    fun selectDeliverySlot(slot: DeliverySlot) {
        repository.setDeliverySlot(slot)
    }

    fun selectAddress(address: Address) {
        repository.setShippingAddress(address)
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        repository.setPaymentMethod(method)
    }

    fun selectOrderForTracking(order: Order) {
        _trackingOrder.value = order
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun setLanguage(lang: String) {
        repository.updateLanguage(lang)
    }

    suspend fun placeOrder(): Order? {
        val currentItems = cartItems.value
        if (currentItems.isEmpty()) return null
        val newOrder = repository.placeOrder(currentItems)
        _trackingOrder.value = newOrder
        return newOrder
    }
}
