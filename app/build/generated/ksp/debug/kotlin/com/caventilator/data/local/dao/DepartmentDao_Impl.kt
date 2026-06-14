package com.caventilator.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.caventilator.`data`.local.entity.DepartmentEntity
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
public class DepartmentDao_Impl(
  __db: RoomDatabase,
) : DepartmentDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDepartmentEntity: EntityInsertAdapter<DepartmentEntity>

  private val __deleteAdapterOfDepartmentEntity: EntityDeleteOrUpdateAdapter<DepartmentEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDepartmentEntity = object : EntityInsertAdapter<DepartmentEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `departments` (`id`,`name`,`targetS1`,`targetS2`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DepartmentEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindDouble(3, entity.targetS1)
        statement.bindDouble(4, entity.targetS2)
      }
    }
    this.__deleteAdapterOfDepartmentEntity = object : EntityDeleteOrUpdateAdapter<DepartmentEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `departments` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: DepartmentEntity) {
        statement.bindLong(1, entity.id)
      }
    }
  }

  public override suspend fun upsert(department: DepartmentEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfDepartmentEntity.insertAndReturnId(_connection, department)
    _result
  }

  public override suspend fun delete(department: DepartmentEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfDepartmentEntity.handle(_connection, department)
  }

  public override fun observeAll(): Flow<List<DepartmentEntity>> {
    val _sql: String = "SELECT * FROM departments ORDER BY name ASC"
    return createFlow(__db, false, arrayOf("departments")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfTargetS1: Int = getColumnIndexOrThrow(_stmt, "targetS1")
        val _columnIndexOfTargetS2: Int = getColumnIndexOrThrow(_stmt, "targetS2")
        val _result: MutableList<DepartmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DepartmentEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpTargetS1: Double
          _tmpTargetS1 = _stmt.getDouble(_columnIndexOfTargetS1)
          val _tmpTargetS2: Double
          _tmpTargetS2 = _stmt.getDouble(_columnIndexOfTargetS2)
          _item = DepartmentEntity(_tmpId,_tmpName,_tmpTargetS1,_tmpTargetS2)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<DepartmentEntity> {
    val _sql: String = "SELECT * FROM departments ORDER BY name ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfTargetS1: Int = getColumnIndexOrThrow(_stmt, "targetS1")
        val _columnIndexOfTargetS2: Int = getColumnIndexOrThrow(_stmt, "targetS2")
        val _result: MutableList<DepartmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DepartmentEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpTargetS1: Double
          _tmpTargetS1 = _stmt.getDouble(_columnIndexOfTargetS1)
          val _tmpTargetS2: Double
          _tmpTargetS2 = _stmt.getDouble(_columnIndexOfTargetS2)
          _item = DepartmentEntity(_tmpId,_tmpName,_tmpTargetS1,_tmpTargetS2)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): DepartmentEntity? {
    val _sql: String = "SELECT * FROM departments WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfTargetS1: Int = getColumnIndexOrThrow(_stmt, "targetS1")
        val _columnIndexOfTargetS2: Int = getColumnIndexOrThrow(_stmt, "targetS2")
        val _result: DepartmentEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpTargetS1: Double
          _tmpTargetS1 = _stmt.getDouble(_columnIndexOfTargetS1)
          val _tmpTargetS2: Double
          _tmpTargetS2 = _stmt.getDouble(_columnIndexOfTargetS2)
          _result = DepartmentEntity(_tmpId,_tmpName,_tmpTargetS1,_tmpTargetS2)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Long) {
    val _sql: String = "DELETE FROM departments WHERE id = ?"
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
