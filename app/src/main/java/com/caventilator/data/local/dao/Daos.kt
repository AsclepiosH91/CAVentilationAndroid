package com.caventilator.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caventilator.data.local.entity.DepartmentEntity
import com.caventilator.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

// ─── Product DAO ───────────────────────────────────────────────────────────────

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAll(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(product: ProductEntity): Long

    @Delete
    suspend fun delete(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Long)
}

// ─── Department DAO ────────────────────────────────────────────────────────────

@Dao
interface DepartmentDao {

    @Query("SELECT * FROM departments ORDER BY name ASC")
    fun observeAll(): Flow<List<DepartmentEntity>>

    @Query("SELECT * FROM departments ORDER BY name ASC")
    suspend fun getAll(): List<DepartmentEntity>

    @Query("SELECT * FROM departments WHERE id = :id")
    suspend fun getById(id: Long): DepartmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(department: DepartmentEntity): Long

    @Delete
    suspend fun delete(department: DepartmentEntity)

    @Query("DELETE FROM departments WHERE id = :id")
    suspend fun deleteById(id: Long)
}
