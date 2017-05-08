package tech.simter.rest.jaxrs.jersey;

import com.owlike.genson.Converter;
import com.owlike.genson.Factory;
import com.owlike.genson.convert.ContextualFactory;
import com.owlike.genson.ext.GensonBundle;

import java.util.List;

/**
 * @author RJ
 */
public class GensonConfiguration {
  private List<Class<Factory<? extends Converter<?>>>> converterFactories;
  private List<Class<ContextualFactory<?>>> contextualFactories;
  private List<Class<GensonBundle>> bundles;
  private List<Class<Converter<?>>> converters;

  public List<Class<Factory<? extends Converter<?>>>> getConverterFactories() {
    return converterFactories;
  }

  public void setConverterFactories(List<Class<Factory<? extends Converter<?>>>> converterFactories) {
    this.converterFactories = converterFactories;
  }

  public List<Class<ContextualFactory<?>>> getContextualFactories() {
    return contextualFactories;
  }

  public void setContextualFactories(List<Class<ContextualFactory<?>>> contextualFactories) {
    this.contextualFactories = contextualFactories;
  }

  public List<Class<GensonBundle>> getBundles() {
    return bundles;
  }

  public void setBundles(List<Class<GensonBundle>> bundles) {
    this.bundles = bundles;
  }

  public List<Class<Converter<?>>> getConverters() {
    return converters;
  }

  public void setConverters(List<Class<Converter<?>>> converters) {
    this.converters = converters;
  }
}