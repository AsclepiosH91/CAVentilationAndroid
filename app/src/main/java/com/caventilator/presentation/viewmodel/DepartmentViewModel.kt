package com.caventilator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caventilator.data.repository.DepartmentRepository
import com.caventilator.domain.model.Department
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DepartmentUiState(
    val departments: List<Department> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showDialog: Boolean = false,
    val editingDepartment: Department? = null,
    val dialogName: String = "",
    val dialogTargetS1: String = "",
    val dialogTargetS2: String = "",
    val dialogNameError: String? = null,
    val dialogS1Error: String? = null,
    val dialogS2Error: String? = null
)

@HiltViewModel
class DepartmentViewModel @Inject constructor(
    private val repository: DepartmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DepartmentUiState(isLoading = true))
    val uiState: StateFlow<DepartmentUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeDepartments()
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message, isLoading = false) } }
                .collect { departments ->
                    _uiState.update { it.copy(departments = departments, isLoading = false) }
                }
        }
    }

    fun openAddDialog() {
        _uiState.update {
            it.copy(
                showDialog = true,
                editingDepartment = null,
                dialogName = "",
                dialogTargetS1 = "",
                dialogTargetS2 = "",
                dialogNameError = null,
                dialogS1Error = null,
                dialogS2Error = null
            )
        }
    }

    fun openEditDialog(department: Department) {
        _uiState.update {
            it.copy(
                showDialog = true,
                editingDepartment = department,
                dialogName = department.name,
                dialogTargetS1 = if (department.targetS1 == 0.0) "" else department.targetS1.toString(),
                dialogTargetS2 = if (department.targetS2 == 0.0) "" else department.targetS2.toString(),
                dialogNameError = null,
                dialogS1Error = null,
                dialogS2Error = null
            )
        }
    }

    fun dismissDialog() { _uiState.update { it.copy(showDialog = false) } }

    fun onNameChange(v: String) { _uiState.update { it.copy(dialogName = v, dialogNameError = null) } }
    fun onS1Change(v: String) { _uiState.update { it.copy(dialogTargetS1 = v, dialogS1Error = null) } }
    fun onS2Change(v: String) { _uiState.update { it.copy(dialogTargetS2 = v, dialogS2Error = null) } }

    fun saveDepartment() {
        val state = _uiState.value
        val nameError = if (state.dialogName.isBlank()) "Le nom est obligatoire" else null

        fun parseAmount(raw: String, label: String): Pair<Double?, String?> {
            if (raw.isBlank()) return 0.0 to null
            val v = raw.replace(",", ".").toDoubleOrNull()
            return when {
                v == null -> null to "Montant invalide"
                v < 0 -> null to "Le montant doit être positif"
                else -> v to null
            }
        }

        val (s1, s1Err) = parseAmount(state.dialogTargetS1, "S1")
        val (s2, s2Err) = parseAmount(state.dialogTargetS2, "S2")

        if (nameError != null || s1Err != null || s2Err != null) {
            _uiState.update { it.copy(dialogNameError = nameError, dialogS1Error = s1Err, dialogS2Error = s2Err) }
            return
        }

        viewModelScope.launch {
            val dept = Department(
                id = state.editingDepartment?.id ?: 0L,
                name = state.dialogName.trim(),
                targetS1 = s1 ?: 0.0,
                targetS2 = s2 ?: 0.0
            )
            repository.saveDepartment(dept)
            _uiState.update { it.copy(showDialog = false) }
        }
    }

    fun deleteDepartment(id: Long) {
        viewModelScope.launch { repository.deleteDepartment(id) }
    }

    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
}
