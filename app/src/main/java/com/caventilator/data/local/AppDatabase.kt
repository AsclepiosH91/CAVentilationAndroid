package com.caventilator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.caventilator.data.local.dao.DepartmentDao
import com.caventilator.data.local.dao.ProductDao
import com.caventilator.data.local.entity.DepartmentEntity
import com.caventilator.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, DepartmentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun departmentDao(): DepartmentDao
}
