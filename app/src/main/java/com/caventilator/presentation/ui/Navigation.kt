package com.caventilator.presentation.ui

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.ui.graphics.vector.ImageVector
import com.caventilator.R

sealed class AppDestination(
    val route: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    object Products : AppDestination("products", R.string.nav_products, Icons.Default.Inventory2)
    object Departments : AppDestination("departments", R.string.nav_departments, Icons.Default.Business)
    object Ventilation : AppDestination("ventilation", R.string.nav_ventilation, Icons.Default.BarChart)

    companion object {
        val all get() = listOf(Products, Departments, Ventilation)
    }
}
