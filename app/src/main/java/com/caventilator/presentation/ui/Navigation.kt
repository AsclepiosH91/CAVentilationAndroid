package com.caventilator.presentation.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Products : AppDestination("products", "Produits", Icons.Default.Inventory2)
    object Departments : AppDestination("departments", "Départements", Icons.Default.Business)
    object Ventilation : AppDestination("ventilation", "Ventilation", Icons.Default.BarChart)

    companion object {
        val all get() = listOf(Products, Departments, Ventilation)
    }
}
