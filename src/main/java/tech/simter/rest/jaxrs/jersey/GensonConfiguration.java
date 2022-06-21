package tech.simter.rest.jaxrs.jersey;

import com.owlike.genson.Converter;
import com.owlike.genson.Factory;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.convert.ContextualFactory;
import com.owlike.genson.ext.GensonBundle;
import com.owlike.genson.ext.jaxb.JAXBBundle;
import com.owlike.genson.ext.jaxrs.GensonJaxRSFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * The genson configuration for JAX-RS. Use by {@link JerseyConfiguration#getGenson}
 *
 * @author RJ
 */
public class GensonConfiguration {
  private final Logger logger = LoggerFactory.getLogger(GensonConfiguration.class);
  private boolean disabled = false;
  private List<Class<Factory<? extends Converter<?>>>> converterFactories;
  private List<Class<ContextualFactory<?>>> contextualFactories;
  private List<Class<GensonBundle>> bundles;
  private List<Class<Converter<?>>> converters;

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

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

  /**
   * Generate a new {@link GensonJaxRSFeature} instance by spring context auto bean discover.
   *
   * @param applicationContext the spring context
   * @param excludeTypes       to exclude types
   * @return the instance
   * @throws Exception if instance class failed
   */
  @SuppressWarnings("unchecked")
  public GensonJaxRSFeature auto(ApplicationContext applicationContext, final List<Class<?>> excludeTypes)
    throws Exception {
    if (this.isDisabled()) return null;
    logger.info("config genson feature for jax-rs...");

    // initial genson
    GensonBuilder gensonBuilder = new GensonBuilder()
      .withBundle(new JAXBBundle())
      .useRuntimeType(true)
      //.useClassMetadata(true)
      .useConstructorWithArguments(true);

    // withConverterFactory: register injectable com.owlike.genson.Factory bean
    applicationContext.getBeansOfType(Factory.class).forEach(
      (k, v) -> {
        if (!excludeTypes.contains(v.getClass())) {
          logger.info("config genson injectable ConverterFactories - {}", v.getClass().getName());
          gensonBuilder.withConverterFactory(v);
        }
      });
    // withConverterFactory: register not injectable bean
    if (this.getConverterFactories() != null) {
      logger.info("config genson custom ConverterFactories");
      for (Class<Factory<? extends Converter<?>>> clazz : this.getConverterFactories())
        gensonBuilder.withConverterFactory(instanceClass(clazz));
    }

    // withContextualFactory
    applicationContext.getBeansOfType(ContextualFactory.class).forEach(
      (k, v) -> {
        if (!excludeTypes.contains(v.getClass())) {
          logger.info("config genson injectable ContextualFactories - {}", v.getClass().getName());
          gensonBuilder.withContextualFactory(v);
        }
      });
    if (this.getContextualFactories() != null) {
      logger.info("config genson custom ContextualFactories");
      for (Class<ContextualFactory<?>> clazz : this.getContextualFactories())
        gensonBuilder.withContextualFactory(instanceClass(clazz));
    }

    // withBundle
    applicationContext.getBeansOfType(GensonBundle.class).forEach(
      (k, v) -> {
        if (!excludeTypes.contains(v.getClass())) {
          logger.info("config genson injectable Bundles - {}", v.getClass().getName());
          gensonBuilder.withBundle(v);
        }
      });
    if (this.getBundles() != null) {
      logger.info("config genson custom Bundles");
      for (Class<GensonBundle> clazz : this.getBundles())
        gensonBuilder.withBundle(instanceClass(clazz));
    }

    // withConverters
    applicationContext.getBeansOfType(Converter.class).forEach(
      (k, v) -> {
        if (!excludeTypes.contains(v.getClass())) {
          logger.info("register genson injectable Converters - {}", v.getClass().getName());
          gensonBuilder.withConverters(v);
        }
      });
    if (this.getConverters() != null) {
      logger.info("register genson custom Converters");
      for (Class<Converter<?>> clazz : this.getConverters())
        gensonBuilder.withConverters(instanceClass(clazz));
    }

    return new GensonJaxRSFeature().use(gensonBuilder.create());
  }

  private static <T> T instanceClass(Class<T> clazz) throws Exception {
    return clazz.getDeclaredConstructor().newInstance();
  }
}