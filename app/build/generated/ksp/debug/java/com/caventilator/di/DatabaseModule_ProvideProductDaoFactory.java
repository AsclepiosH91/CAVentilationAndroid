package com.caventilator.di;

import com.caventilator.data.local.AppDatabase;
import com.caventilator.data.local.dao.ProductDao;
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
public final class DatabaseModule_ProvideProductDaoFactory implements Factory<ProductDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideProductDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ProductDao get() {
    return provideProductDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideProductDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideProductDaoFactory(dbProvider);
  }

  public static ProductDao provideProductDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideProductDao(db));
  }
}
