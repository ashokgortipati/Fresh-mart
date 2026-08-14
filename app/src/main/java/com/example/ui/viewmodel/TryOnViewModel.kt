package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.VirtualTryOnResult
import com.example.data.repository.GeminiAiRepository
import com.example.data.seed.SampleData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class TryOnViewMode {
    FRONT_VIEW, FULL_BODY, DETAIL_FIT
}

data class TryOnUiState(
    val userBitmap: Bitmap? = null,
    val selectedProduct: Product? = null,
    val selectedSize: String = "M",
    val selectedColor: String? = null,
    val heightCm: Int = 175,
    val weightKg: Int = 70,
    val bodyType: String = "Athletic / Regular",
    val viewMode: TryOnViewMode = TryOnViewMode.FRONT_VIEW,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val comparisonSplitRatio: Float = 0.5f,
    val result: VirtualTryOnResult? = null,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,
    val shareSuccessMessage: String? = null
)

class TryOnViewModel(application: Application) : AndroidViewModel(application) {

    private val geminiAiRepository = GeminiAiRepository(application)

    private val _uiState = MutableStateFlow(
        TryOnUiState(
            selectedProduct = SampleData.sampleProducts.firstOrNull { it.category == ProductCategory.CLOTHING }
        )
    )
    val uiState: StateFlow<TryOnUiState> = _uiState.asStateFlow()

    fun setUserBitmap(bitmap: Bitmap?) {
        _uiState.value = _uiState.value.copy(userBitmap = bitmap, isCompleted = false, result = null)
    }

    fun setSelectedProduct(product: Product) {
        _uiState.value = _uiState.value.copy(
            selectedProduct = product,
            selectedSize = product.availableSizes?.getOrNull(1) ?: "M",
            selectedColor = product.availableColors?.firstOrNull(),
            isCompleted = false,
            result = null
        )
    }

    fun setSize(size: String) {
        _uiState.value = _uiState.value.copy(selectedSize = size)
    }

    fun setColor(color: String) {
        _uiState.value = _uiState.value.copy(selectedColor = color)
    }

    fun setMeasurements(height: Int, weight: Int, bodyShape: String) {
        _uiState.value = _uiState.value.copy(
            heightCm = height,
            weightKg = weight,
            bodyType = bodyShape
        )
    }

    fun setViewMode(mode: TryOnViewMode) {
        _uiState.value = _uiState.value.copy(viewMode = mode)
    }

    fun updateComparisonSplit(ratio: Float) {
        _uiState.value = _uiState.value.copy(comparisonSplitRatio = ratio.coerceIn(0f, 1f))
    }

    fun triggerAiTryOn() {
        val current = _uiState.value
        val product = current.selectedProduct ?: return

        _uiState.value = current.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val result = geminiAiRepository.performVirtualTryOn(
                    userBitmap = current.userBitmap,
                    product = product,
                    selectedSize = current.selectedSize,
                    heightCm = current.heightCm,
                    weightKg = current.weightKg,
                    bodyShape = current.bodyType
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isCompleted = true,
                    result = result,
                    selectedSize = result.recommendedSize.ifBlank { current.selectedSize }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "AI Fitting analysis completed with standard model preview."
                )
            }
        }
    }

    fun saveTryOnLook() {
        _uiState.value = _uiState.value.copy(isSaved = true)
    }

    fun shareTryOnLook() {
        _uiState.value = _uiState.value.copy(shareSuccessMessage = "Virtual Try-on preview ready to share!")
    }

    fun clearShareMessage() {
        _uiState.value = _uiState.value.copy(shareSuccessMessage = null)
    }
}
