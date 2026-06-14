package com.caventilator.data.repository;

import com.caventilator.data.local.dao.DepartmentDao;
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
public final class DepartmentRepositoryImpl_Factory implements Factory<DepartmentRepositoryImpl> {
  private final Provider<DepartmentDao> daoProvider;

  private DepartmentRepositoryImpl_Factory(Provider<DepartmentDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public DepartmentRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static DepartmentRepositoryImpl_Factory create(Provider<DepartmentDao> daoProvider) {
    return new DepartmentRepositoryImpl_Factory(daoProvider);
  }

  public static DepartmentRepositoryImpl newInstance(DepartmentDao dao) {
    return new DepartmentRepositoryImpl(dao);
  }
}
