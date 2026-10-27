package com.caventilator.domain.model

import androidx.annotation.StringRes
import com.caventilator.R

/**
 * Représente un produit du catalogue (ex: titre de transport)
 */
data class Product(
    val id: Long = 0L,
    val name: String,
    val unitPrice: Double
)

/**
 * Représente un département avec ses CA cibles semestriels
 */
data class Department(
    val id: Long = 0L,
    val name: String,
    val targetS1: Double = 0.0,
    val targetS2: Double = 0.0
)

/**
 * Semestre sélectionné pour la ventilation
 */
enum class Semester(@param:StringRes val labelRes: Int) {
    S1(R.string.semester_1),
    S2(R.string.semester_2)
}

/**
 * Résultat de la ventilation pour un produit donné
 */
data class VentilationItem(
    val product: Product,
    val quantity: Long,
    val totalAmount: Double
)

/**
 * Résultat global de la ventilation d'un département pour un semestre
 */
data class VentilationResult(
    val department: Department,
    val semester: Semester,
    val targetCA: Double,
    val items: List<VentilationItem>,
    val totalVentilated: Double,
    val residual: Double
)
