package com.svelte.spring.demo.svelte.router;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class SvelteRouterConfigurer implements WebMvcConfigurer {

  private static final Logger logger = LogManager.getLogger();
  private static final String DIR = "classpath:/static/_app/immutable/components/pages/**";
  private final Set<String> files;

  public SvelteRouterConfigurer(ApplicationContext context) throws IOException {
    this.files = Stream.of(
            context.getResources(DIR))
        .map(r -> getParent(r))
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    for (String file : files) {
      String uri = StringUtils.substringAfter(file, "/pages");
      if (uri.length() > 0) {
        registry.addRedirectViewController(uri, "/");
      }
    }
  }

  private static String getParent(Resource resource) {
    return getFile(resource)
        .map(File::getParent)
        .map(p -> p.replace("\\", "/"))
        .orElse(null);
  }

  private static Optional<File> getFile(Resource resource) {
    try {
      return Optional.of(resource.getFile());
    } catch (IOException e) {
      logger.error("Exception while resolving views:", e);
      return Optional.empty();
    }
  }
}
