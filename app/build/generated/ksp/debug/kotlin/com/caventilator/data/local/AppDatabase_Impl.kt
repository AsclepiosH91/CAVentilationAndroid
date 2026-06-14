package com.caventilator.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.caventilator.`data`.local.dao.DepartmentDao
import com.caventilator.`data`.local.dao.DepartmentDao_Impl
import com.caventilator.`data`.local.dao.ProductDao
import com.caventilator.`data`.local.dao.ProductDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _productDao: Lazy<ProductDao> = lazy {
    ProductDao_Impl(this)
  }

  private val _departmentDao: Lazy<DepartmentDao> = lazy {
    DepartmentDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "77d6de1588e3162ba66f89d8fa00e3ae", "1ca2b7b5ade3d884ff243b9ee6fd1ac7") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `products` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `unitPrice` REAL NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `departments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `targetS1` REAL NOT NULL, `targetS2` REAL NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '77d6de1588e3162ba66f89d8fa00e3ae')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `products`")
        connection.execSQL("DROP TABLE IF EXISTS `departments`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsProducts: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsProducts.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsProducts.put("unitPrice", TableInfo.Column("unitPrice", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysProducts: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesProducts: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoProducts: TableInfo = TableInfo("products", _columnsProducts, _foreignKeysProducts, _indicesProducts)
        val _existingProducts: TableInfo = read(connection, "products")
        if (!_infoProducts.equals(_existingProducts)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |products(com.caventilator.data.local.entity.ProductEntity).
              | Expected:
              |""".trimMargin() + _infoProducts + """
              |
              | Found:
              |""".trimMargin() + _existingProducts)
        }
        val _columnsDepartments: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDepartments.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDepartments.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDepartments.put("targetS1", TableInfo.Column("targetS1", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDepartments.put("targetS2", TableInfo.Column("targetS2", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDepartments: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesDepartments: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoDepartments: TableInfo = TableInfo("departments", _columnsDepartments, _foreignKeysDepartments, _indicesDepartments)
        val _existingDepartments: TableInfo = read(connection, "departments")
        if (!_infoDepartments.equals(_existingDepartments)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |departments(com.caventilator.data.local.entity.DepartmentEntity).
              | Expected:
              |""".trimMargin() + _infoDepartments + """
              |
              | Found:
              |""".trimMargin() + _existingDepartments)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "products", "departments")
  }

  public override fun clearAllTables() {
    super.performClear(false, "products", "departments")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(ProductDao::class, ProductDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DepartmentDao::class, DepartmentDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun productDao(): ProductDao = _productDao.value

  public override fun departmentDao(): DepartmentDao = _departmentDao.value
}
