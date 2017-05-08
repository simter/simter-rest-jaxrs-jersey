package tech.simter.rest.jaxrs.jersey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * The type-safe Configuration for {@link JerseyResourceConfig}.
 * <p>
 * see also &lt;https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html&gt;
 *
 * @author RJ
 */
@Component
@ConfigurationProperties(prefix = "simter.jersey")
public class JerseyConfiguration {
  private String[] packages;
  private List<Class<?>> excludeTypes;
  private Map<String, Object> properties;
  private GensonConfiguration genson;

  /**
   * The auto scan packages for Jersey ResourceConfig.
   * <p>
   * Config it through property <code>simter.jersey.packages</code>.
   * This can not be exclude by <code>excludeTypes</code> configuration.
   *
   * @return the packages
   */
  public String[] getPackages() {
    return packages;
  }

  public void setPackages(String[] packages) {
    this.packages = packages;
  }

  /**
   * The exclude bean type for Jersey ResourceConfig.
   * <p>
   * Config it through property <code>simter.jersey.excludeTypes</code>.
   *
   * @return the exclude types
   */
  public List<Class<?>> getExcludeTypes() {
    return excludeTypes;
  }

  public void setExcludeTypes(List<Class<?>> excludeTypes) {
    this.excludeTypes = excludeTypes;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }

  public GensonConfiguration getGenson() {
    return genson;
  }

  public void setGenson(GensonConfiguration genson) {
    this.genson = genson;
  }
}