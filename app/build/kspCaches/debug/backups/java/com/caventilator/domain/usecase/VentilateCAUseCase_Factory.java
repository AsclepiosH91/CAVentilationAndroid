package com.caventilator.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class VentilateCAUseCase_Factory implements Factory<VentilateCAUseCase> {
  @Override
  public VentilateCAUseCase get() {
    return newInstance();
  }

  public static VentilateCAUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VentilateCAUseCase newInstance() {
    return new VentilateCAUseCase();
  }

  private static final class InstanceHolder {
    static final VentilateCAUseCase_Factory INSTANCE = new VentilateCAUseCase_Factory();
  }
}
