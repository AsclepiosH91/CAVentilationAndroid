package com.caventilator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caventilator.R
import com.caventilator.data.repository.ProductRepository
import com.caventilator.domain.model.Product
import com.caventilator.presentation.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null,
    // Dialog state
    val showDialog: Boolean = false,
    val editingProduct: Product? = null,
    val dialogName: String = "",
    val dialogPrice: String = "",
    val dialogNameError: UiText? = null,
    val dialogPriceError: UiText? = null
)

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState(isLoading = true))
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeProducts()
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message?.let { msg -> UiText.DynamicString(msg) }, isLoading = false) } }
                .collect { products ->
                    _uiState.update { it.copy(products = products, isLoading = false) }
                }
        }
    }

    // ── Dialog open/close ──────────────────────────────────────────────────────

    fun openAddDialog() {
        _uiState.update {
            it.copy(
                showDialog = true,
                editingProduct = null,
                dialogName = "",
                dialogPrice = "",
                dialogNameError = null,
                dialogPriceError = null
            )
        }
    }

    fun openEditDialog(product: Product) {
        _uiState.update {
            it.copy(
                showDialog = true,
                editingProduct = product,
                dialogName = product.name,
                dialogPrice = product.unitPrice.toString(),
                dialogNameError = null,
                dialogPriceError = null
            )
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showDialog = false) }
    }

    fun onDialogNameChange(value: String) {
        _uiState.update { it.copy(dialogName = value, dialogNameError = null) }
    }

    fun onDialogPriceChange(value: String) {
        _uiState.update { it.copy(dialogPrice = value, dialogPriceError = null) }
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    fun saveProduct() {
        val state = _uiState.value
        val nameError = if (state.dialogName.isBlank()) UiText.StringResource(R.string.error_name_required) else null
        val price = state.dialogPrice.replace(",", ".").toDoubleOrNull()
        val priceError = when {
            state.dialogPrice.isBlank() -> UiText.StringResource(R.string.error_price_required)
            price == null -> UiText.StringResource(R.string.error_price_invalid)
            price <= 0.0 -> UiText.StringResource(R.string.error_price_positive)
            else -> null
        }

        if (nameError != null || priceError != null) {
            _uiState.update { it.copy(dialogNameError = nameError, dialogPriceError = priceError) }
            return
        }

        viewModelScope.launch {
            val product = Product(
                id = state.editingProduct?.id ?: 0L,
                name = state.dialogName.trim(),
                unitPrice = price!!
            )
            repository.saveProduct(product)
            _uiState.update { it.copy(showDialog = false) }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch { repository.deleteProduct(id) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
