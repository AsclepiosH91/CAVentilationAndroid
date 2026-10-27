package com.caventilator.presentation.ui.screens.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.caventilator.R
import com.caventilator.domain.model.Product
import com.caventilator.presentation.ui.components.EmptyState
import com.caventilator.presentation.ui.components.FullScreenLoading
import com.caventilator.presentation.ui.components.LanguageSwitchIconButton
import com.caventilator.presentation.ui.components.formatCurrency
import com.caventilator.presentation.ui.theme.CAVentilatorTheme
import com.caventilator.presentation.ui.util.UiText
import com.caventilator.presentation.viewmodel.ProductUiState
import com.caventilator.presentation.viewmodel.ProductViewModel

@Composable
fun ProductScreen(
    viewModel: ProductViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProductScreenContent(
        state = state,
        onOpenAddDialog = viewModel::openAddDialog,
        onClearError = viewModel::clearError,
        onEditDialog = viewModel::openEditDialog,
        onDeleteProduct = viewModel::deleteProduct,
        onNameChange = viewModel::onDialogNameChange,
        onPriceChange = viewModel::onDialogPriceChange,
        onSaveProduct = viewModel::saveProduct,
        onDismissDialog = viewModel::dismissDialog
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreenContent(
    state: ProductUiState,
    onOpenAddDialog: () -> Unit,
    onClearError: () -> Unit,
    onEditDialog: (Product) -> Unit,
    onDeleteProduct: (Long) -> Unit,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onSaveProduct: () -> Unit,
    onDismissDialog: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Snackbar pour les erreurs
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error.asString(context))
            onClearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_products)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    LanguageSwitchIconButton()
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenAddDialog,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.action_new_product)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> FullScreenLoading()
                state.products.isEmpty() -> EmptyState(
                    stringResource(R.string.empty_products)
                )
                else -> ProductList(
                    products = state.products,
                    onEdit = onEditDialog,
                    onDelete = onDeleteProduct
                )
            }
        }
    }

    // Dialog ajout/édition
    if (state.showDialog) {
        ProductDialog(
            name = state.dialogName,
            price = state.dialogPrice,
            nameError = state.dialogNameError,
            priceError = state.dialogPriceError,
            isEditing = state.editingProduct != null,
            onNameChange = onNameChange,
            onPriceChange = onPriceChange,
            onConfirm = onSaveProduct,
            onDismiss = onDismissDialog
        )
    }
}

@Composable
fun ProductList(
    products: List<Product>,
    onEdit: (Product) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onEdit = { onEdit(product) },
                onDelete = { onDelete(product.id) }
            )
        }
        item { Spacer(Modifier.height(80.dp)) } // FAB clearance
    }
}

@Composable
fun ProductCard(
    product: Product,
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
                Icons.Default.Inventory2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = product.unitPrice.formatCurrency(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.cd_edit))
            }
            IconButton(onClick = { showDeleteConfirm = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.cd_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.dialog_delete_product_title)) },
            text = { Text(stringResource(R.string.dialog_delete_product_msg, product.name)) },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false; onDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDialog(
    name: String,
    price: String,
    nameError: UiText?,
    priceError: UiText?,
    isEditing: Boolean,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isEditing) stringResource(R.string.dialog_title_edit_product)
                else stringResource(R.string.dialog_title_new_product)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.label_product_name)) },
                    isError = nameError != null,
                    supportingText = nameError?.let { err -> { Text(err.asString()) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = onPriceChange,
                    label = { Text(stringResource(R.string.label_unit_price)) },
                    isError = priceError != null,
                    supportingText = priceError?.let { err -> { Text(err.asString()) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(
                    if (isEditing) stringResource(R.string.action_save)
                    else stringResource(R.string.action_create)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ProductScreenPreview() {
    val sampleProducts = listOf(
        Product(id = 1, name = "Ticket unité", unitPrice = 1.50),
        Product(id = 2, name = "Pass 24h", unitPrice = 5.00),
        Product(id = 3, name = "Carnet 10 tickets", unitPrice = 12.00)
    )
    val state = ProductUiState(
        products = sampleProducts,
        isLoading = false
    )

    CAVentilatorTheme {
        ProductScreenContent(
            state = state,
            onOpenAddDialog = {},
            onClearError = {},
            onEditDialog = {},
            onDeleteProduct = {},
            onNameChange = {},
            onPriceChange = {},
            onSaveProduct = {},
            onDismissDialog = {}
        )
    }
}
