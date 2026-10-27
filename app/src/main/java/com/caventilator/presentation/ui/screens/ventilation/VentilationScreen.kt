package com.caventilator.presentation.ui.screens.ventilation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.caventilator.R
import com.caventilator.domain.model.Department
import com.caventilator.domain.model.Semester
import com.caventilator.domain.model.VentilationItem
import com.caventilator.domain.model.VentilationResult
import com.caventilator.presentation.ui.components.EmptyState
import com.caventilator.presentation.ui.components.LanguageSwitchIconButton
import com.caventilator.presentation.ui.components.LabeledValue
import com.caventilator.presentation.ui.components.SectionHeader
import com.caventilator.presentation.ui.components.formatCurrency
import com.caventilator.presentation.viewmodel.VentilationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentilationScreen(
    viewModel: VentilationViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Lance la boîte de dialogue SAF pour choisir l'emplacement du .xlsx
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri: Uri? ->
        uri?.let { viewModel.exportToUri(it) }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error.asString(context), duration = SnackbarDuration.Long)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.exportSuccess) {
        state.exportSuccess?.let { success ->
            snackbarHostState.showSnackbar(success.asString(context))
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_ventilation)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                actions = {
                    if (state.result != null) {
                        IconButton(
                            onClick = {
                                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                                    .format(Date())
                                exportLauncher.launch("ventilation_${timestamp}.xlsx")
                            },
                            enabled = !state.isExporting
                        ) {
                            if (state.isExporting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.FileDownload,
                                    contentDescription = stringResource(R.string.cd_export_excel),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    LanguageSwitchIconButton()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Sélection département ────────────────────────────────────────
            item {
                DepartmentSelector(
                    departments = state.departments,
                    selected = state.selectedDepartment,
                    onSelect = viewModel::selectDepartment
                )
            }

            // ── Sélection semestre ───────────────────────────────────────────
            item {
                SemesterSelector(
                    selected = state.selectedSemester,
                    onSelect = viewModel::selectSemester
                )
            }

            // ── Bouton Ventiler ──────────────────────────────────────────────
            item {
                val canVentilate = state.selectedDepartment != null && state.products.isNotEmpty()
                Button(
                    onClick = viewModel::runVentilation,
                    enabled = canVentilate && !state.isCalculating,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    if (state.isCalculating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                    } else {
                        Icon(Icons.Default.BarChart, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(stringResource(R.string.btn_calculate_ventilation))
                }

                if (state.products.isEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(R.string.warning_no_products),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // ── Résultats ────────────────────────────────────────────────────
            state.result?.let { result ->
                item {
                    AnimatedVisibility(visible = true, enter = fadeIn() + expandVertically()) {
                        VentilationSummaryCard(result)
                    }
                }

                if (result.items.isNotEmpty()) {
                    item { SectionHeader(stringResource(R.string.header_product_detail)) }

                    items(result.items, key = { it.product.id }) { item ->
                        VentilationItemCard(item)
                    }
                } else {
                    item {
                        EmptyState(stringResource(R.string.empty_ventilation_result))
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// ── Sélecteur de département ──────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DepartmentSelector(
    departments: List<Department>,
    selected: Department?,
    onSelect: (Department) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.label_department),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selected?.name ?: stringResource(R.string.select_department_placeholder),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    if (departments.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.no_department_available)) },
                            onClick = { expanded = false },
                            enabled = false
                        )
                    } else {
                        departments.forEach { dept ->
                            DropdownMenuItem(
                                text = { Text(dept.name) },
                                onClick = { onSelect(dept); expanded = false }
                            )
                        }
                    }
                }
            }

            // Afficher les CA cibles du département sélectionné
            selected?.let { dept ->
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(R.string.chip_s1_format, dept.targetS1.formatCurrency())) },
                        leadingIcon = { Icon(Icons.Default.Euro, null, Modifier.size(16.dp)) }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(stringResource(R.string.chip_s2_format, dept.targetS2.formatCurrency())) },
                        leadingIcon = { Icon(Icons.Default.Euro, null, Modifier.size(16.dp)) }
                    )
                }
            }
        }
    }
}

// ── Sélecteur de semestre ─────────────────────────────────────────────────────

@Composable
private fun SemesterSelector(
    selected: Semester,
    onSelect: (Semester) -> Unit
) {
    Card(shape = MaterialTheme.shapes.medium) {
        Column(Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.label_semester),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                Semester.entries.forEachIndexed { index, semester ->
                    SegmentedButton(
                        selected = selected == semester,
                        onClick = { onSelect(semester) },
                        shape = SegmentedButtonDefaults.itemShape(index, Semester.entries.size)
                    ) {
                        Text(stringResource(semester.labelRes))
                    }
                }
            }
        }
    }
}

// ── Carte de synthèse ─────────────────────────────────────────────────────────

@Composable
private fun VentilationSummaryCard(result: VentilationResult) {
    val absorptionPct = if (result.targetCA > 0) {
        (result.totalVentilated / result.targetCA * 100).coerceIn(0.0, 100.0)
    } else 0.0

    Card(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.ventilation_result_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(
                    R.string.ventilation_subtitle_format,
                    result.department.name,
                    stringResource(result.semester.labelRes)
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(Modifier.height(16.dp))

            LabeledValue(stringResource(R.string.label_target_ca), result.targetCA.formatCurrency())
            Spacer(Modifier.height(6.dp))
            LabeledValue(stringResource(R.string.label_total_ventilated), result.totalVentilated.formatCurrency())
            Spacer(Modifier.height(6.dp))
            LabeledValue(stringResource(R.string.label_residual), result.residual.formatCurrency())

            Spacer(Modifier.height(12.dp))

            // Barre de progression de l'absorption
            Text(
                text = stringResource(R.string.absorption_rate_format, absorptionPct),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (absorptionPct / 100).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = when {
                    absorptionPct >= 95 -> MaterialTheme.colorScheme.tertiary
                    absorptionPct >= 80 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

// ── Carte produit ventilé ─────────────────────────────────────────────────────

@Composable
private fun VentilationItemCard(item: VentilationItem) {
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
            Column(Modifier.weight(1f)) {
                Text(item.product.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = stringResource(R.string.unit_price_prefix_format, item.product.unitPrice.formatCurrency()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Text(
                        "× ${item.quantity}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = item.totalAmount.formatCurrency(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
