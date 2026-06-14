package com.caventilator.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// ── Loading ────────────────────────────────────────────────────────────────────

@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// ── Empty state ────────────────────────────────────────────────────────────────

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}

// ── Section header ─────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

// ── Labeled value ──────────────────────────────────────────────────────────────

@Composable
fun LabeledValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

// ── Snackbar host helper ───────────────────────────────────────────────────────

@Composable
fun ErrorSnackbar(
    message: String?,
    onDismiss: () -> Unit
) {
    if (message != null) {
        // Ce composable doit être utilisé dans un Scaffold avec snackbarHostState
        // L'appel réel est géré dans les écrans via LaunchedEffect
    }
}

// ── Currency formatter ─────────────────────────────────────────────────────────

fun Double.formatCurrency(): String = "%,.2f €".format(this)
