package com.caventilator.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.caventilator.`data`.local.entity.ProductEntity
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ProductDao_Impl(
  __db: RoomDatabase,
) : ProductDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfProductEntity: EntityInsertAdapter<ProductEntity>

  private val __deleteAdapterOfProductEntity: EntityDeleteOrUpdateAdapter<ProductEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfProductEntity = object : EntityInsertAdapter<ProductEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `products` (`id`,`name`,`unitPrice`) VALUES (nullif(?, 0),?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProductEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindDouble(3, entity.unitPrice)
      }
    }
    this.__deleteAdapterOfProductEntity = object : EntityDeleteOrUpdateAdapter<ProductEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `products` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ProductEntity) {
        statement.bindLong(1, entity.id)
      }
    }
  }

  public override suspend fun upsert(product: ProductEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfProductEntity.insertAndReturnId(_connection, product)
    _result
  }

  public override suspend fun delete(product: ProductEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfProductEntity.handle(_connection, product)
  }

  public override fun observeAll(): Flow<List<ProductEntity>> {
    val _sql: String = "SELECT * FROM products ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("products")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfUnitPrice: Int = getColumnIndexOrThrow(_stmt, "unitPrice")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpUnitPrice: Double
          _tmpUnitPrice = _stmt.getDouble(_columnIndexOfUnitPrice)
          _item = ProductEntity(_tmpId,_tmpName,_tmpUnitPrice)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<ProductEntity> {
    val _sql: String = "SELECT * FROM products ORDER BY name ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfUnitPrice: Int = getColumnIndexOrThrow(_stmt, "unitPrice")
        val _result: MutableList<ProductEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ProductEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpUnitPrice: Double
          _tmpUnitPrice = _stmt.getDouble(_columnIndexOfUnitPrice)
          _item = ProductEntity(_tmpId,_tmpName,_tmpUnitPrice)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Long) {
    val _sql: String = "DELETE FROM products WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
