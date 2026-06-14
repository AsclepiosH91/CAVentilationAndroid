package com.caventilator.presentation.viewmodel;

import com.caventilator.data.repository.DepartmentRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class DepartmentViewModel_Factory implements Factory<DepartmentViewModel> {
  private final Provider<DepartmentRepository> repositoryProvider;

  private DepartmentViewModel_Factory(Provider<DepartmentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public DepartmentViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static DepartmentViewModel_Factory create(
      Provider<DepartmentRepository> repositoryProvider) {
    return new DepartmentViewModel_Factory(repositoryProvider);
  }

  public static DepartmentViewModel newInstance(DepartmentRepository repository) {
    return new DepartmentViewModel(repository);
  }
}
