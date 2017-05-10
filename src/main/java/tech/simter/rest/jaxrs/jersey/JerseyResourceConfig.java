package tech.simter.rest.jaxrs.jersey;

import com.owlike.genson.ext.jaxrs.GensonJaxRSFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private JerseyConfiguration jerseyConfiguration;
  private List<Class<?>> excludeTypes;

  /**
   * By default, this class will register all injectable bean with annotation @Path and @Provider,
   * but exclude all bean type within <code>excludeTypes</code>.
   */
  public JerseyResourceConfig() {
  }

  @PostConstruct
  public void init() throws Exception {
    excludeTypes = jerseyConfiguration.getExcludeTypes() == null ?
      Collections.emptyList() : jerseyConfiguration.getExcludeTypes();

    // auto register all jax-rs annotation resources（@Path、@Provider）
    if (jerseyConfiguration.getPackages() != null && jerseyConfiguration.getPackages().length > 0) {
      logger.info("register packages - {}", StringUtils.arrayToCommaDelimitedString(jerseyConfiguration.getPackages()));
      packages(jerseyConfiguration.getPackages());
    }

    // register injectable @javax.ws.rs.Path bean
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

    // set properties
    if (jerseyConfiguration.getProperties() != null) jerseyConfiguration.getProperties().forEach(this::property);

    // register genson feature
    if (jerseyConfiguration.getGenson() != null) {
      GensonJaxRSFeature auto = jerseyConfiguration.getGenson().auto(applicationContext, excludeTypes);
      if (auto != null) register(auto);
    }
  }
}