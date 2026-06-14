package com.caventilator.presentation.ui.screens.department

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.caventilator.domain.model.Department
import com.caventilator.presentation.ui.components.*
import androidx.compose.ui.tooling.preview.Preview
import com.caventilator.R
import com.caventilator.presentation.ui.theme.CAVentilatorTheme
import com.caventilator.presentation.viewmodel.DepartmentUiState
import com.caventilator.presentation.viewmodel.DepartmentViewModel

@Composable
fun DepartmentScreen(
    viewModel: DepartmentViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DepartmentScreenContent(
        state = state,
        onOpenAddDialog = viewModel::openAddDialog,
        onClearError = viewModel::clearError,
        onEditDialog = viewModel::openEditDialog,
        onDeleteDepartment = viewModel::deleteDepartment,
        onNameChange = viewModel::onNameChange,
        onS1Change = viewModel::onS1Change,
        onS2Change = viewModel::onS2Change,
        onSaveDepartment = viewModel::saveDepartment,
        onDismissDialog = viewModel::dismissDialog
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DepartmentScreenContent(
    state: DepartmentUiState,
    onOpenAddDialog: () -> Unit,
    onClearError: () -> Unit,
    onEditDialog: (Department) -> Unit,
    onDeleteDepartment: (Long) -> Unit,
    onNameChange: (String) -> Unit,
    onS1Change: (String) -> Unit,
    onS2Change: (String) -> Unit,
    onSaveDepartment: () -> Unit,
    onDismissDialog: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.departements)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenAddDialog,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.new_departement)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(Modifier
            .fillMaxSize()
            .padding(padding)) {
            when {
                state.isLoading -> FullScreenLoading()
                state.departments.isEmpty() -> EmptyState(
                    "Aucun département.\nAppuyez sur + pour en créer un."
                )
                else -> DepartmentList(
                    departments = state.departments,
                    onEdit = onEditDialog,
                    onDelete = onDeleteDepartment
                )
            }
        }
    }

    if (state.showDialog) {
        DepartmentDialog(
            name = state.dialogName,
            targetS1 = state.dialogTargetS1,
            targetS2 = state.dialogTargetS2,
            nameError = state.dialogNameError,
            s1Error = state.dialogS1Error,
            s2Error = state.dialogS2Error,
            isEditing = state.editingDepartment != null,
            onNameChange = onNameChange,
            onS1Change = onS1Change,
            onS2Change = onS2Change,
            onConfirm = onSaveDepartment,
            onDismiss = onDismissDialog
        )
    }
}

@Composable
private fun DepartmentList(
    departments: List<Department>,
    onEdit: (Department) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(departments, key = { it.id }) { dept ->
            DepartmentCard(
                department = dept,
                onEdit = { onEdit(dept) },
                onDelete = { onDelete(dept.id) }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun DepartmentCard(
    department: Department,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Business,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(department.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CAChip(label = "S1", amount = department.targetS1)
                    CAChip(label = "S2", amount = department.targetS2)
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier")
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer ce département ?") },
            text = { Text("« ${department.name} » sera définitivement supprimé.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false; onDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Supprimer") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
private fun CAChip(label: String, amount: Double) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = amount.formatCurrency(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun DepartmentDialog(
    name: String,
    targetS1: String,
    targetS2: String,
    nameError: String?,
    s1Error: String?,
    s2Error: String?,
    isEditing: Boolean,
    onNameChange: (String) -> Unit,
    onS1Change: (String) -> Unit,
    onS2Change: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Modifier le département" else "Nouveau département") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nom du département") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetS1,
                    onValueChange = onS1Change,
                    label = { Text("CA cible Semestre 1 (€)") },
                    isError = s1Error != null,
                    supportingText = s1Error?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetS2,
                    onValueChange = onS2Change,
                    label = { Text("CA cible Semestre 2 (€)") },
                    isError = s2Error != null,
                    supportingText = s2Error?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(if (isEditing) "Enregistrer" else "Créer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DepartmentScreenPreview() {
    val sampleDepartments = listOf(
        Department(id = 1, name = "Ventes", targetS1 = 150000.0, targetS2 = 180000.0),
        Department(id = 2, name = "Marketing", targetS1 = 50000.0, targetS2 = 60000.0),
        Department(id = 3, name = "R&D", targetS1 = 200000.0, targetS2 = 220000.0)
    )
    val state = DepartmentUiState(
        departments = sampleDepartments,
        isLoading = false
    )

    CAVentilatorTheme {
        DepartmentScreenContent(
            state = state,
            onOpenAddDialog = {},
            onClearError = {},
            onEditDialog = {},
            onDeleteDepartment = {},
            onNameChange = {},
            onS1Change = {},
            onS2Change = {},
            onSaveDepartment = {},
            onDismissDialog = {}
        )
    }
}
