package it.register.edu.auction.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "caching")
public class CacheConfig {

  private List<CaffeineSpecs> caffeine;

  @Data
  public static class CaffeineSpecs {

    private List<String> names;
    private int ttlInSeconds;
    private int maxSize;
  }

  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager manager = new SimpleCacheManager();
    List<CaffeineCache> caches = caffeine.stream()
        .flatMap(specs -> build(specs).stream())
        .collect(Collectors.toList());
    manager.setCaches(caches);
    return manager;
  }

  private List<CaffeineCache> build(CaffeineSpecs specs) {
    return specs.getNames().stream()
        .map(name -> new CaffeineCache(name, Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(specs.ttlInSeconds))
            .maximumSize(specs.maxSize)
            .build()))
        .collect(Collectors.toList());
  }

}
