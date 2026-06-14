package com.caventilator.data.repository

import com.caventilator.data.local.dao.DepartmentDao
import com.caventilator.data.local.dao.ProductDao
import com.caventilator.data.local.entity.toDomain
import com.caventilator.data.local.entity.toEntity
import com.caventilator.domain.model.Department
import com.caventilator.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// ─── Interfaces ────────────────────────────────────────────────────────────────

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    suspend fun getProducts(): List<Product>
    suspend fun saveProduct(product: Product): Long
    suspend fun deleteProduct(id: Long)
}

interface DepartmentRepository {
    fun observeDepartments(): Flow<List<Department>>
    suspend fun getDepartmentById(id: Long): Department?
    suspend fun saveDepartment(department: Department): Long
    suspend fun deleteDepartment(id: Long)
}

// ─── Implementations ───────────────────────────────────────────────────────────

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val dao: ProductDao
) : ProductRepository {

    override fun observeProducts(): Flow<List<Product>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getProducts(): List<Product> =
        dao.getAll().map { it.toDomain() }

    override suspend fun saveProduct(product: Product): Long =
        dao.upsert(product.toEntity())

    override suspend fun deleteProduct(id: Long) =
        dao.deleteById(id)
}

@Singleton
class DepartmentRepositoryImpl @Inject constructor(
    private val dao: DepartmentDao
) : DepartmentRepository {

    override fun observeDepartments(): Flow<List<Department>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getDepartmentById(id: Long): Department? =
        dao.getById(id)?.toDomain()

    override suspend fun saveDepartment(department: Department): Long =
        dao.upsert(department.toEntity())

    override suspend fun deleteDepartment(id: Long) =
        dao.deleteById(id)
}
