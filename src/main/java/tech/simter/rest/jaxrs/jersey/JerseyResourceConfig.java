package tech.simter.rest.jaxrs.jersey;

import com.owlike.genson.Converter;
import com.owlike.genson.Factory;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.convert.ContextualFactory;
import com.owlike.genson.ext.GensonBundle;
import com.owlike.genson.ext.jaxb.JAXBBundle;
import com.owlike.genson.ext.jaxrs.GensonJaxRSFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.List;

/**
 * The JAX-RS application initiator for jersey.
 *
 * @author RJ
 */
@Component
public class JerseyResourceConfig extends ResourceConfig implements ApplicationContextAware {
  private final Logger logger = LoggerFactory.getLogger(JerseyResourceConfig.class);
  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  private JerseyConfiguration jerseyConfiguration;

  /**
   * By default, this class will register all injectable bean with annotation @Path and @Provider,
   * but exclude all bean type within <code>excludeTypes</code>.
   *
   * @param jerseyConfiguration the jersey ResourceConfig configuration
   */
  public JerseyResourceConfig(JerseyConfiguration jerseyConfiguration) {
    this.jerseyConfiguration = jerseyConfiguration;
  }

  @PostConstruct
  public void init() throws Exception {
    // auto register all jax-rs annotation resources（@Path、@Provider）
    if (jerseyConfiguration.getPackages() != null && jerseyConfiguration.getPackages().length > 0) {
      logger.info("register packages - {}", StringUtils.arrayToCommaDelimitedString(jerseyConfiguration.getPackages()));
      packages(jerseyConfiguration.getPackages());
    } else {
      // register injectable @javax.ws.rs.Path bean
      final List<Class<?>> excludeTypes = jerseyConfiguration.getExcludeTypes() == null ?
        Collections.emptyList() : jerseyConfiguration.getExcludeTypes();
      applicationContext.getBeansWithAnnotation(Path.class).forEach(
        (k, v) -> {
          if (!excludeTypes.contains(v.getClass())) {
            logger.info("register @Path component - {}", v.getClass().getName());
            register(v);
          }
        });

      // register injectable @javax.ws.rs.ext.Provider bean
      applicationContext.getBeansWithAnnotation(Provider.class).forEach(
        (k, v) -> {
          if (!excludeTypes.contains(v.getClass())) {
            logger.info("register @Provider component - {}", v.getClass().getName());
            register(v);
          }
        });
    }

    // set properties
    if (jerseyConfiguration.getProperties() != null) jerseyConfiguration.getProperties().forEach(this::property);

    // custom genson ConverterFactory
    if (jerseyConfiguration.getGenson() != null) {
      configGenson();
    }
  }

  private void configGenson() throws Exception {
    GensonConfiguration config = jerseyConfiguration.getGenson();
    if (config == null) return;

    logger.info("register genson custom feature");

    // initial genson
    GensonBuilder gensonBuilder = new GensonBuilder()
      .withBundle(new JAXBBundle())
      .useConstructorWithArguments(true);

    // withConverterFactory
    if (config.getConverterFactories() != null) {
      logger.info("register genson ConverterFactories");
      for (Class<Factory<? extends Converter<?>>> clazz : config.getConverterFactories())
        gensonBuilder.withConverterFactory(instanceClass(clazz));
    }

    // withContextualFactory
    if (config.getContextualFactories() != null) {
      logger.info("register genson ContextualFactories");
      for (Class<ContextualFactory<?>> clazz : config.getContextualFactories())
        gensonBuilder.withContextualFactory(instanceClass(clazz));
    }

    // withBundle
    if (config.getBundles() != null) {
      logger.info("register genson Bundles");
      for (Class<GensonBundle> clazz : config.getBundles())
        gensonBuilder.withBundle(instanceClass(clazz));
    }

    // withConverters
    if (config.getConverters() != null) {
      logger.info("register genson Converters");
      for (Class<Converter<?>> clazz : config.getConverters())
        gensonBuilder.withConverters(instanceClass(clazz));
    }

    // register genson feature
    register(new GensonJaxRSFeature().use(gensonBuilder.create()));
  }

  private <T> T instanceClass(Class<T> clazz) throws Exception {
    return clazz.newInstance();
  }
}