package com.caventilator.di

import android.content.Context
import androidx.room.Room
import com.caventilator.data.local.AppDatabase
import com.caventilator.data.local.dao.DepartmentDao
import com.caventilator.data.local.dao.ProductDao
import com.caventilator.data.repository.DepartmentRepository
import com.caventilator.data.repository.DepartmentRepositoryImpl
import com.caventilator.data.repository.ProductRepository
import com.caventilator.data.repository.ProductRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ca_ventilator.db"
        ).build()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao = db.productDao()

    @Provides
    fun provideDepartmentDao(db: AppDatabase): DepartmentDao = db.departmentDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindDepartmentRepository(impl: DepartmentRepositoryImpl): DepartmentRepository
}
