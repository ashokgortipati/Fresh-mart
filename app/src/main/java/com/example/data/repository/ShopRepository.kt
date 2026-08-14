package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.seed.SampleData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShopRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val cartDao = db.cartDao()
    private val wishlistDao = db.wishlistDao()
    private val recentlyViewedDao = db.recentlyViewedDao()
    private val notificationDao = db.notificationDao()
    private val orderDao = db.orderDao()

    // In-memory catalog with live updates (allows Admin to add/edit products)
    private val _products = MutableStateFlow<List<Product>>(SampleData.sampleProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile>(
        UserProfile(
            id = "usr_fresh_101",
            name = "Ashok Gortipati",
            email = "ashokgortipati3@gmail.com",
            phone = "+1 (555) 234-5678",
            addresses = SampleData.sampleAddresses,
            preferredLanguage = "English",
            isAdmin = true
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _selectedDeliverySlot = MutableStateFlow<DeliverySlot>(SampleData.sampleDeliverySlots[0])
    val selectedDeliverySlot: StateFlow<DeliverySlot> = _selectedDeliverySlot.asStateFlow()

    private val _selectedAddress = MutableStateFlow<Address>(SampleData.sampleAddresses[0])
    val selectedAddress: StateFlow<Address> = _selectedAddress.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod>(PaymentMethod.UPI)
    val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

    // Dynamic orders combining seeded + created orders
    private val _liveOrders = MutableStateFlow<List<Order>>(SampleData.sampleOrders)
    val liveOrders: StateFlow<List<Order>> = _liveOrders.asStateFlow()

    init {
        // Seed initial notifications in background
        CoroutineScope(Dispatchers.IO).launch {
            SampleData.sampleNotifications.forEach { notif ->
                notificationDao.insert(
                    NotificationEntity(
                        id = notif.id,
                        title = notif.title,
                        message = notif.message,
                        timestamp = notif.timestamp,
                        type = notif.type.name,
                        isRead = notif.isRead
                    )
                )
            }
        }
    }

    // Active Cart Flow
    val cartItems: Flow<List<CartItem>> = cartDao.getActiveCartItems().combine(products) { entities, prods ->
        entities.mapNotNull { entity ->
            val product = prods.find { it.id == entity.productId }
            if (product != null) {
                CartItem(
                    product = product,
                    quantity = entity.quantity,
                    selectedSize = entity.selectedSize,
                    selectedColor = entity.selectedColor,
                    isSavedForLater = false
                )
            } else null
        }
    }

    // Saved For Later Items
    val savedForLaterItems: Flow<List<CartItem>> = cartDao.getSavedForLaterItems().combine(products) { entities, prods ->
        entities.mapNotNull { entity ->
            val product = prods.find { it.id == entity.productId }
            if (product != null) {
                CartItem(
                    product = product,
                    quantity = entity.quantity,
                    selectedSize = entity.selectedSize,
                    selectedColor = entity.selectedColor,
                    isSavedForLater = true
                )
            } else null
        }
    }

    // Wishlist Flow
    val wishlistItems: Flow<List<WishlistItem>> = wishlistDao.getAllWishlist().combine(products) { entities, prods ->
        entities.mapNotNull { entity ->
            val product = prods.find { it.id == entity.productId }
            if (product != null) {
                WishlistItem(product = product, addedAt = entity.addedAt)
            } else null
        }
    }

    // Recently Viewed Products Flow
    val recentlyViewedProducts: Flow<List<Product>> = recentlyViewedDao.getRecentlyViewed().combine(products) { entities, prods ->
        entities.mapNotNull { entity ->
            prods.find { it.id == entity.productId }
        }
    }

    // Notifications Flow
    val notifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications().map { entities ->
        entities.map { entity ->
            NotificationItem(
                id = entity.id,
                title = entity.title,
                message = entity.message,
                timestamp = entity.timestamp,
                type = try { NotificationType.valueOf(entity.type) } catch (e: Exception) { NotificationType.SYSTEM },
                isRead = entity.isRead
            )
        }
    }

    fun isProductInWishlist(productId: String): Flow<Boolean> = wishlistDao.isInWishlist(productId)

    suspend fun addToCart(product: Product, quantity: Int = 1, size: String? = null, color: String? = null) {
        cartDao.insertOrUpdate(
            CartEntity(
                productId = product.id,
                quantity = quantity,
                selectedSize = size ?: product.availableSizes?.firstOrNull(),
                selectedColor = color ?: product.availableColors?.firstOrNull(),
                isSavedForLater = false
            )
        )
    }

    suspend fun updateCartQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            cartDao.deleteItem(productId)
        } else {
            cartDao.updateQuantity(productId, quantity)
        }
    }

    suspend fun removeFromCart(productId: String) {
        cartDao.deleteItem(productId)
    }

    suspend fun saveForLater(productId: String) {
        cartDao.setSavedForLater(productId, true)
    }

    suspend fun moveToCart(productId: String) {
        cartDao.setSavedForLater(productId, false)
    }

    suspend fun toggleWishlist(product: Product, currentlyInWishlist: Boolean) {
        if (currentlyInWishlist) {
            wishlistDao.delete(product.id)
        } else {
            wishlistDao.insert(WishlistEntity(productId = product.id))
        }
    }

    suspend fun recordProductView(productId: String) {
        recentlyViewedDao.insert(RecentlyViewedEntity(productId = productId))
    }

    fun applyCoupon(coupon: Coupon?) {
        _appliedCoupon.value = coupon
    }

    fun setDeliverySlot(slot: DeliverySlot) {
        _selectedDeliverySlot.value = slot
    }

    fun setShippingAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun setPaymentMethod(method: PaymentMethod) {
        _selectedPaymentMethod.value = method
    }

    fun updateLanguage(lang: String) {
        _userProfile.value = _userProfile.value.copy(preferredLanguage = lang)
    }

    fun updateUserProfile(name: String, email: String, phone: String) {
        _userProfile.value = _userProfile.value.copy(name = name, email = email, phone = phone)
    }

    suspend fun markNotificationAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    suspend fun placeOrder(items: List<CartItem>): Order {
        val subtotal = items.sumOf { it.product.price * it.quantity }
        val coupon = _appliedCoupon.value
        val discount = when {
            coupon == null -> 0.0
            coupon.flatDiscount > 0 -> coupon.flatDiscount
            coupon.discountPercent > 0 -> subtotal * (coupon.discountPercent / 100.0)
            else -> 0.0
        }
        val deliveryFee = if (subtotal > 50.0) 0.0 else 4.99
        val tax = (subtotal - discount) * 0.08
        val total = (subtotal - discount + deliveryFee + tax).coerceAtLeast(0.0)

        val hasSeafood = items.any { it.product.category == ProductCategory.SEAFOOD }

        val newOrder = Order(
            id = "ord_" + System.currentTimeMillis(),
            orderNumber = "FM-" + (10000..99999).random(),
            items = items,
            subtotal = subtotal,
            discount = discount,
            deliveryFee = deliveryFee,
            tax = tax,
            total = total,
            status = OrderStatus.ORDER_PLACED,
            deliverySlot = _selectedDeliverySlot.value,
            shippingAddress = _selectedAddress.value,
            paymentMethod = _selectedPaymentMethod.value,
            isSameDay = _selectedDeliverySlot.value.isSameDay,
            freshnessGuaranteed = hasSeafood,
            createdAt = System.currentTimeMillis()
        )

        // Add to active live orders
        _liveOrders.value = listOf(newOrder) + _liveOrders.value

        // Persist to Room
        orderDao.insert(
            OrderEntity(
                id = newOrder.id,
                orderNumber = newOrder.orderNumber,
                subtotal = newOrder.subtotal,
                discount = newOrder.discount,
                deliveryFee = newOrder.deliveryFee,
                tax = newOrder.tax,
                total = newOrder.total,
                status = newOrder.status.name,
                deliverySlotDay = newOrder.deliverySlot.dayLabel,
                deliverySlotTime = newOrder.deliverySlot.timeRange,
                isSameDay = newOrder.isSameDay,
                addressRecipient = newOrder.shippingAddress.recipientName,
                addressPhone = newOrder.shippingAddress.phone,
                addressStreet = newOrder.shippingAddress.streetAddress,
                addressCity = newOrder.shippingAddress.city,
                addressPostal = newOrder.shippingAddress.postalCode,
                paymentMethod = newOrder.paymentMethod.name,
                itemsSummary = items.joinToString(", ") { "${it.quantity}x ${it.product.name}" },
                createdAt = newOrder.createdAt
            )
        )

        // Clear active cart
        cartDao.clearActiveCart()

        // Create new order notification
        notificationDao.insert(
            NotificationEntity(
                id = "notif_" + System.currentTimeMillis(),
                title = "Order Confirmed: ${newOrder.orderNumber}",
                message = "Your order of $${String.format("%.2f", total)} is placed! Slot: ${newOrder.deliverySlot.dayLabel}, ${newOrder.deliverySlot.timeRange}",
                timestamp = "Just now",
                type = NotificationType.ORDER.name,
                isRead = false
            )
        )

        return newOrder
    }

    // Admin Operations
    fun adminUpdateOrderStatus(orderId: String, newStatus: OrderStatus) {
        _liveOrders.value = _liveOrders.value.map { order ->
            if (order.id == orderId) {
                order.copy(status = newStatus)
            } else order
        }
        CoroutineScope(Dispatchers.IO).launch {
            orderDao.updateOrderStatus(orderId, newStatus.name)
        }
    }

    fun adminAddProduct(product: Product) {
        _products.value = listOf(product) + _products.value
    }

    fun adminUpdateStock(productId: String, newStock: Int, inStock: Boolean) {
        _products.value = _products.value.map { prod ->
            if (prod.id == productId) {
                prod.copy(stockQuantity = newStock, inStock = inStock)
            } else prod
        }
    }

    fun adminDeleteProduct(productId: String) {
        _products.value = _products.value.filter { it.id != productId }
    }
}
