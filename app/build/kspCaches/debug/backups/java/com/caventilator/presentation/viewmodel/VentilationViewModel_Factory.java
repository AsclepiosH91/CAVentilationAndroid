package com.caventilator.presentation.viewmodel;

import com.caventilator.data.repository.DepartmentRepository;
import com.caventilator.data.repository.ProductRepository;
import com.caventilator.domain.usecase.ExportExcelUseCase;
import com.caventilator.domain.usecase.VentilateCAUseCase;
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
public final class VentilationViewModel_Factory implements Factory<VentilationViewModel> {
  private final Provider<DepartmentRepository> departmentRepositoryProvider;

  private final Provider<ProductRepository> productRepositoryProvider;

  private final Provider<VentilateCAUseCase> ventilateCAProvider;

  private final Provider<ExportExcelUseCase> exportExcelProvider;

  private VentilationViewModel_Factory(Provider<DepartmentRepository> departmentRepositoryProvider,
      Provider<ProductRepository> productRepositoryProvider,
      Provider<VentilateCAUseCase> ventilateCAProvider,
      Provider<ExportExcelUseCase> exportExcelProvider) {
    this.departmentRepositoryProvider = departmentRepositoryProvider;
    this.productRepositoryProvider = productRepositoryProvider;
    this.ventilateCAProvider = ventilateCAProvider;
    this.exportExcelProvider = exportExcelProvider;
  }

  @Override
  public VentilationViewModel get() {
    return newInstance(departmentRepositoryProvider.get(), productRepositoryProvider.get(), ventilateCAProvider.get(), exportExcelProvider.get());
  }

  public static VentilationViewModel_Factory create(
      Provider<DepartmentRepository> departmentRepositoryProvider,
      Provider<ProductRepository> productRepositoryProvider,
      Provider<VentilateCAUseCase> ventilateCAProvider,
      Provider<ExportExcelUseCase> exportExcelProvider) {
    return new VentilationViewModel_Factory(departmentRepositoryProvider, productRepositoryProvider, ventilateCAProvider, exportExcelProvider);
  }

  public static VentilationViewModel newInstance(DepartmentRepository departmentRepository,
      ProductRepository productRepository, VentilateCAUseCase ventilateCA,
      ExportExcelUseCase exportExcel) {
    return new VentilationViewModel(departmentRepository, productRepository, ventilateCA, exportExcel);
  }
}
