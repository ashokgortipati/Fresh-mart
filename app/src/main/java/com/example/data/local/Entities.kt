package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val productId: String,
    val quantity: Int = 1,
    val selectedSize: String? = null,
    val selectedColor: String? = null,
    val isSavedForLater: Boolean = false
)

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val productId: String,
    val viewedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String,
    val isRead: Boolean = false
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val subtotal: Double,
    val discount: Double,
    val deliveryFee: Double,
    val tax: Double,
    val total: Double,
    val status: String,
    val deliverySlotDay: String,
    val deliverySlotTime: String,
    val isSameDay: Boolean,
    val addressRecipient: String,
    val addressPhone: String,
    val addressStreet: String,
    val addressCity: String,
    val addressPostal: String,
    val paymentMethod: String,
    val itemsSummary: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items WHERE isSavedForLater = 0")
    fun getActiveCartItems(): Flow<List<CartEntity>>

    @Query("SELECT * FROM cart_items WHERE isSavedForLater = 1")
    fun getSavedForLaterItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CartEntity)

    @Query("UPDATE cart_items SET quantity = :quantity WHERE productId = :productId")
    suspend fun updateQuantity(productId: String, quantity: Int)

    @Query("UPDATE cart_items SET isSavedForLater = :savedForLater WHERE productId = :productId")
    suspend fun setSavedForLater(productId: String, savedForLater: Boolean)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteItem(productId: String)

    @Query("DELETE FROM cart_items WHERE isSavedForLater = 0")
    suspend fun clearActiveCart()
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist_items ORDER BY addedAt DESC")
    fun getAllWishlist(): Flow<List<WishlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    fun isInWishlist(productId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: WishlistEntity)

    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun delete(productId: String)
}

@Dao
interface RecentlyViewedDao {
    @Query("SELECT * FROM recently_viewed ORDER BY viewedAt DESC LIMIT 10")
    fun getRecentlyViewed(): Flow<List<RecentlyViewedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecentlyViewedEntity)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
}
