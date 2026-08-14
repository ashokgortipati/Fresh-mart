package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

data class SalesAnalytics(
    val totalRevenue: Double,
    val totalOrdersCount: Int,
    val averageOrderValue: Double,
    val clothingSalesPercent: Int,
    val grocerySalesPercent: Int,
    val seafoodSalesPercent: Int,
    val totalActiveCustomers: Int
)

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ShopRepository(application)

    val products: StateFlow<List<Product>> = repository.products
    val liveOrders: StateFlow<List<Order>> = repository.liveOrders

    private val _selectedAdminTab = MutableStateFlow(0) // 0: Dashboard/Analytics, 1: Products, 2: Orders, 3: Customers
    val selectedAdminTab: StateFlow<Int> = _selectedAdminTab.asStateFlow()

    val analytics = liveOrders.map { orders ->
        val revenue = orders.sumOf { it.total }
        val count = orders.size.coerceAtLeast(1)
        SalesAnalytics(
            totalRevenue = revenue,
            totalOrdersCount = orders.size,
            averageOrderValue = revenue / count,
            clothingSalesPercent = 38,
            grocerySalesPercent = 27,
            seafoodSalesPercent = 35,
            totalActiveCustomers = 482
        )
    }

    fun setAdminTab(tab: Int) {
        _selectedAdminTab.value = tab
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        repository.adminUpdateOrderStatus(orderId, newStatus)
    }

    fun updateStock(productId: String, newQuantity: Int) {
        repository.adminUpdateStock(productId, newQuantity, inStock = newQuantity > 0)
    }

    fun addNewProduct(
        name: String,
        category: ProductCategory,
        subCategory: String,
        description: String,
        price: Double,
        originalPrice: Double,
        stock: Int,
        unit: String,
        imageUrl: String,
        isTryOn: Boolean
    ) {
        val newProduct = Product(
            id = "custom_" + System.currentTimeMillis(),
            name = name,
            category = category,
            subCategory = subCategory,
            description = description,
            price = price,
            originalPrice = originalPrice,
            stockQuantity = stock,
            inStock = stock > 0,
            unit = unit,
            images = listOf(imageUrl.ifBlank { "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=600" }),
            isDeal = originalPrice > price,
            isNewArrival = true,
            tryOnCompatible = isTryOn,
            availableSizes = if (category == ProductCategory.CLOTHING) listOf("S", "M", "L", "XL") else null
        )
        repository.adminAddProduct(newProduct)
    }

    fun deleteProduct(productId: String) {
        repository.adminDeleteProduct(productId)
    }
}
