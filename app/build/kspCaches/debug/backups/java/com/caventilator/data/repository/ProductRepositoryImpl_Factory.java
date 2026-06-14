package com.caventilator.data.repository;

import com.caventilator.data.local.dao.ProductDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ProductRepositoryImpl_Factory implements Factory<ProductRepositoryImpl> {
  private final Provider<ProductDao> daoProvider;

  private ProductRepositoryImpl_Factory(Provider<ProductDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ProductRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static ProductRepositoryImpl_Factory create(Provider<ProductDao> daoProvider) {
    return new ProductRepositoryImpl_Factory(daoProvider);
  }

  public static ProductRepositoryImpl newInstance(ProductDao dao) {
    return new ProductRepositoryImpl(dao);
  }
}
