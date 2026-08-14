package com.example.data.model

enum class ProductCategory(val displayName: String, val iconName: String) {
    ALL("All", "ic_all"),
    CLOTHING("Clothing", "ic_clothing"),
    GROCERIES("Groceries", "ic_groceries"),
    SEAFOOD("Fresh Seafood", "ic_seafood")
}

enum class ClothingSubCategory(val displayName: String) {
    MENS_WEAR("Men's Wear"),
    WOMENS_WEAR("Women's Wear"),
    KIDS_WEAR("Kids Wear"),
    SHIRTS("Shirts"),
    T_SHIRTS("T-Shirts"),
    JEANS("Jeans"),
    DRESSES("Dresses"),
    FOOTWEAR("Footwear"),
    ACCESSORIES("Accessories")
}

enum class GrocerySubCategory(val displayName: String) {
    FRUITS("Fruits"),
    VEGETABLES("Vegetables"),
    DAIRY("Dairy Products"),
    RICE_GRAINS("Rice & Grains"),
    SNACKS("Snacks"),
    BEVERAGES("Beverages"),
    COOKING_ESSENTIALS("Cooking Essentials")
}

enum class SeafoodSubCategory(val displayName: String) {
    FRESH_FISH("Fresh Fish"),
    PRAWNS("Prawns"),
    CRABS("Crabs"),
    LOBSTERS("Lobsters"),
    SQUID("Squid"),
    DRY_FISH("Dry Fish"),
    READY_TO_COOK("Ready-to-Cook Seafood")
}

data class SeafoodFreshnessInfo(
    val catchTime: String = "Today 4:00 AM",
    val origin: String = "Local Harbor Dock #4",
    val storageTemp: String = "0°C to 2°C on Fresh Ice",
    val freshnessGrade: String = "Grade A+ Sashimi/Export Quality",
    val cleaningOption: String = "Cleaned, Scaled & Vacuum Packed"
)

data class Product(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val subCategory: String,
    val description: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int = (((originalPrice - price) / originalPrice) * 100).toInt(),
    val rating: Float = 4.8f,
    val reviewCount: Int = 128,
    val inStock: Boolean = true,
    val stockQuantity: Int = 50,
    val unit: String = "each", // "1 kg", "500g", "per piece"
    val images: List<String> = emptyList(),
    val isDeal: Boolean = false,
    val isNewArrival: Boolean = false,
    val isFeatured: Boolean = false,
    val isBestSeller: Boolean = false,
    val tryOnCompatible: Boolean = false,
    val seafoodFreshness: SeafoodFreshnessInfo? = null,
    val availableSizes: List<String>? = null, // e.g. ["S", "M", "L", "XL", "XXL"]
    val availableColors: List<String>? = null,
    val fabricOrIngredients: String? = null,
    val tags: List<String> = emptyList()
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1,
    val selectedSize: String? = null,
    val selectedColor: String? = null,
    val isSavedForLater: Boolean = false
)

data class WishlistItem(
    val product: Product,
    val addedAt: Long = System.currentTimeMillis()
)

data class Address(
    val id: String,
    val tag: String = "Home", // "Home", "Work", "Other"
    val recipientName: String,
    val phone: String,
    val streetAddress: String,
    val city: String,
    val state: String,
    val postalCode: String,
    val isDefault: Boolean = false
)

enum class OrderStatus(val displayName: String, val stepIndex: Int) {
    ORDER_PLACED("Order Placed", 0),
    PROCESSING("Processing", 1),
    PACKED("Packed", 2),
    SHIPPED("Shipped", 3),
    OUT_FOR_DELIVERY("Out for Delivery", 4),
    DELIVERED("Delivered", 5),
    CANCELLED("Cancelled", -1)
}

data class OrderTimelineStep(
    val title: String,
    val description: String,
    val timestamp: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

enum class PaymentMethod(val title: String, val subtitle: String, val iconType: String) {
    UPI("Instant UPI", "Google Pay, PhonePe, Paytm, BHIM", "upi"),
    CREDIT_DEBIT_CARD("Credit / Debit Card", "Visa, Mastercard, RuPay", "card"),
    NET_BANKING("Net Banking", "All major banks supported", "bank"),
    CASH_ON_DELIVERY("Cash on Delivery", "Pay in cash or UPI upon delivery", "cod")
}

data class DeliverySlot(
    val slotId: String,
    val dayLabel: String, // "Today", "Tomorrow", "15 Aug"
    val timeRange: String, // "08:00 AM - 11:00 AM", "02:00 PM - 05:00 PM"
    val isSameDay: Boolean = true,
    val isAvailable: Boolean = true,
    val surcharge: Double = 0.0
)

data class Order(
    val id: String,
    val orderNumber: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double,
    val deliveryFee: Double,
    val tax: Double,
    val total: Double,
    val status: OrderStatus,
    val deliverySlot: DeliverySlot,
    val shippingAddress: Address,
    val paymentMethod: PaymentMethod,
    val isSameDay: Boolean,
    val freshnessGuaranteed: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    val deliveryPersonName: String = "Rajesh Kumar (Express Delivery)",
    val deliveryPersonPhone: String = "+1 (555) 019-2834"
)

data class Review(
    val id: String,
    val productId: String,
    val userName: String,
    val userAvatar: String? = null,
    val rating: Float,
    val date: String,
    val comment: String,
    val verifiedPurchase: Boolean = true,
    val sizePurchased: String? = null
)

data class Coupon(
    val code: String,
    val title: String,
    val discountPercent: Int = 0,
    val flatDiscount: Double = 0.0,
    val minOrderValue: Double = 0.0,
    val description: String
)

enum class NotificationType {
    ORDER, FLASH_SALE, SPECIAL_OFFER, NEW_ARRIVAL, SYSTEM
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val actionDeepLink: String? = null
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String? = null,
    val addresses: List<Address> = emptyList(),
    val preferredLanguage: String = "English",
    val isAdmin: Boolean = true // enable admin switch for reviewing features
)

data class VirtualTryOnResult(
    val userImageBase64: String? = null,
    val generatedImageBase64: String? = null,
    val generatedImageUrl: String? = null,
    val clothingProduct: Product? = null,
    val recommendedSize: String = "M",
    val fitAdvice: String = "",
    val drapingNotes: String = "",
    val styleMatchingTips: List<String> = emptyList(),
    val heightCm: Int = 175,
    val weightKg: Int = 70,
    val bodyType: String = "Regular Athletic"
)
