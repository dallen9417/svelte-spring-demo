package com.svelte.spring.demo.configuration.yml;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "yaml")
@PropertySource(value = "classpath:words.yml", factory = YamlPropertySourceFactory.class)
@Getter
@Setter
public class Words {
  private List<String> words;
  private List<String> allowed;
}
