package com.caventilator.domain.usecase

import com.caventilator.domain.model.Department
import com.caventilator.domain.model.Product
import com.caventilator.domain.model.Semester
import com.caventilator.domain.model.VentilationItem
import com.caventilator.domain.model.VentilationResult
import javax.inject.Inject

/**
 * Algorithme de ventilation du CA sur les produits.
 *
 * Stratégie : Problème du sac-à-dos non-borné (Unbounded Knapsack).
 * On cherche à maximiser l'absorption du CA cible sans le dépasser.
 *
 * Approche :
 * 1. Trier les produits par prix unitaire décroissant.
 * 2. Allocation gloutonne : pour chaque produit (du plus cher au moins cher),
 *    allouer autant d'unités que possible sans dépasser le budget restant.
 * 3. Pour affiner, on tente plusieurs combinaisons en variant la quantité
 *    du premier produit pour optimiser le résidu final.
 */
class VentilateCAUseCase @Inject constructor() {

    operator fun invoke(
        department: Department,
        semester: Semester,
        products: List<Product>
    ): VentilationResult {
        val targetCA = when (semester) {
            Semester.S1 -> department.targetS1
            Semester.S2 -> department.targetS2
        }

        if (products.isEmpty() || targetCA <= 0.0) {
            return VentilationResult(
                department = department,
                semester = semester,
                targetCA = targetCA,
                items = emptyList(),
                totalVentilated = 0.0,
                residual = targetCA
            )
        }

        val sortedProducts = products
            .filter { it.unitPrice > 0.0 }
            .sortedByDescending { it.unitPrice }

        // On cherche la meilleure allocation via un algorithme glouton amélioré.
        val bestAllocation = findBestAllocation(sortedProducts, targetCA)

        val items = bestAllocation.toList()
            .mapIndexedNotNull { index, qty ->
                if (qty > 0L) {
                    val product = sortedProducts[index]
                    VentilationItem(
                        product = product,
                        quantity = qty,
                        totalAmount = qty * product.unitPrice
                    )
                } else null
            }

        val totalVentilated = items.sumOf { it.totalAmount }
        val residual = targetCA - totalVentilated

        return VentilationResult(
            department = department,
            semester = semester,
            targetCA = targetCA,
            items = items,
            totalVentilated = totalVentilated,
            residual = residual
        )
    }

    /**
     * Trouve la meilleure allocation en itérant sur les quantités possibles
     * du produit le plus cher pour minimiser le résidu.
     *
     * Pour chaque valeur possible de q[0] (0..floor(budget/price[0])),
     * on remplit les produits suivants de façon gloutonne.
     * On conserve l'allocation dont le résidu est minimal.
     *
     * Complexité : O(budget/price[0] * n) — raisonnable pour des prix > 1 €.
     */
    private fun findBestAllocation(
        products: List<Product>,
        targetCA: Double
    ): LongArray {
        if (products.isEmpty()) return LongArray(0)

        var bestAllocation = LongArray(products.size) { 0L }
        var bestTotal = 0.0

        val maxQ0 = (targetCA / products[0].unitPrice).toLong()

        for (q0 in maxQ0 downTo 0L) {
            val allocation = LongArray(products.size)
            allocation[0] = q0
            var remaining = targetCA - q0 * products[0].unitPrice

            // Remplissage glouton pour les produits suivants
            for (i in 1 until products.size) {
                val qty = (remaining / products[i].unitPrice).toLong()
                allocation[i] = qty
                remaining -= qty * products[i].unitPrice
            }

            val total = allocation.indices.sumOf { i ->
                allocation[i] * products[i].unitPrice
            }

            if (total > bestTotal && total <= targetCA + 0.001) {
                bestTotal = total
                bestAllocation = allocation.copyOf()
            }

            // Optimisation : si le résidu est inférieur au plus petit prix,
            // on ne peut pas faire mieux — on arrête.
            val minPrice = products.minOf { it.unitPrice }
            if (targetCA - bestTotal < minPrice) break
        }

        return bestAllocation
    }
}
