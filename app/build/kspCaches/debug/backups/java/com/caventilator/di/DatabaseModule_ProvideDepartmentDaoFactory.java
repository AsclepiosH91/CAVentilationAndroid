package com.caventilator.di;

import com.caventilator.data.local.AppDatabase;
import com.caventilator.data.local.dao.DepartmentDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideDepartmentDaoFactory implements Factory<DepartmentDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideDepartmentDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DepartmentDao get() {
    return provideDepartmentDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideDepartmentDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideDepartmentDaoFactory(dbProvider);
  }

  public static DepartmentDao provideDepartmentDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDepartmentDao(db));
  }
}
