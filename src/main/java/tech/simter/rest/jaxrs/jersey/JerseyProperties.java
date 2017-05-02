package tech.simter.rest.jaxrs.jersey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The type-safe Configuration Properties for {@link JerseyResourceConfig}.
 * <p>
 * see also &lt;https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html&gt;
 *
 * @author RJ 2017-05-02
 */
@Component
@ConfigurationProperties(prefix = "simter.jersey")
public class JerseyProperties {
  private String[] packages;
  private List<Class<?>> excludeTypes;

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
}