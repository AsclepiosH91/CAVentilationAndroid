package com.caventilator.data.local.entity

import com.caventilator.domain.model.Department
import com.caventilator.domain.model.Product

// ─── Product mappers ───────────────────────────────────────────────────────────

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    unitPrice = unitPrice
)

fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    unitPrice = unitPrice
)

// ─── Department mappers ────────────────────────────────────────────────────────

fun DepartmentEntity.toDomain() = Department(
    id = id,
    name = name,
    targetS1 = targetS1,
    targetS2 = targetS2
)

fun Department.toEntity() = DepartmentEntity(
    id = id,
    name = name,
    targetS1 = targetS1,
    targetS2 = targetS2
)
