package com.caventilator.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caventilator.R
import com.caventilator.data.repository.DepartmentRepository
import com.caventilator.data.repository.ProductRepository
import com.caventilator.domain.model.Department
import com.caventilator.domain.model.Product
import com.caventilator.domain.model.Semester
import com.caventilator.domain.model.VentilationResult
import com.caventilator.domain.usecase.ExportExcelUseCase
import com.caventilator.domain.usecase.VentilateCAUseCase
import com.caventilator.presentation.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class VentilationUiState(
    val departments: List<Department> = emptyList(),
    val products: List<Product> = emptyList(),
    val selectedDepartment: Department? = null,
    val selectedSemester: Semester = Semester.S1,
    val result: VentilationResult? = null,
    val isCalculating: Boolean = false,
    val isExporting: Boolean = false,
    val exportSuccess: UiText? = null,
    val errorMessage: UiText? = null
)

@HiltViewModel
class VentilationViewModel @Inject constructor(
    private val departmentRepository: DepartmentRepository,
    private val productRepository: ProductRepository,
    private val ventilateCA: VentilateCAUseCase,
    private val exportExcel: ExportExcelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VentilationUiState())
    val uiState: StateFlow<VentilationUiState> = _uiState.asStateFlow()

    init {
        // Observer les listes en parallèle
        viewModelScope.launch {
            combine(
                departmentRepository.observeDepartments(),
                productRepository.observeProducts()
            ) { depts, prods -> depts to prods }
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message?.let { msg -> UiText.DynamicString(msg) }) } }
                .collect { (depts, prods) ->
                    _uiState.update { state ->
                        // Si le département sélectionné n'existe plus, on le désélectionne
                        val selectedStillValid = state.selectedDepartment
                            ?.let { sel -> depts.find { it.id == sel.id } }
                        state.copy(
                            departments = depts,
                            products = prods,
                            selectedDepartment = selectedStillValid
                        )
                    }
                }
        }
    }

    fun selectDepartment(department: Department) {
        _uiState.update { it.copy(selectedDepartment = department, result = null) }
    }

    fun selectSemester(semester: Semester) {
        _uiState.update { it.copy(selectedSemester = semester, result = null) }
    }

    fun runVentilation() {
        val state = _uiState.value
        val dept = state.selectedDepartment ?: run {
            _uiState.update { it.copy(errorMessage = UiText.StringResource(R.string.error_select_department)) }
            return
        }
        if (state.products.isEmpty()) {
            _uiState.update { it.copy(errorMessage = UiText.StringResource(R.string.error_no_products)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculating = true) }
            val result = withContext(Dispatchers.Default) {
                ventilateCA(dept, state.selectedSemester, state.products)
            }
            _uiState.update { it.copy(result = result, isCalculating = false) }
        }
    }

    fun exportToUri(uri: Uri) {
        val result = _uiState.value.result ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            runCatching {
                withContext(Dispatchers.IO) { exportExcel.exportToUri(uri, result) }
            }.onSuccess {
                _uiState.update {
                    it.copy(isExporting = false, exportSuccess = UiText.StringResource(R.string.success_excel_export))
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isExporting = false, errorMessage = UiText.StringResource(R.string.error_export_failed, e.message ?: ""))
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, exportSuccess = null) }
    }
}
