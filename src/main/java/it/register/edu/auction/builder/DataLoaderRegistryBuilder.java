package it.register.edu.auction.builder;

import it.register.edu.auction.annotation.GraphQLDataLoader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DataLoaderRegistryBuilder {

  @Autowired
  private ApplicationContext applicationContext;

  public DataLoaderRegistry build() {
    DataLoaderRegistry dataLoaderRegistry = new DataLoaderRegistry();
    findAllDataLoaders().forEach((name, builder) -> dataLoaderRegistry.register(name, builder.build()));
    return dataLoaderRegistry;
  }

  private Map<String, DataLoaderBuilder<?, ?>> findAllDataLoaders() {
    return applicationContext.getBeansWithAnnotation(GraphQLDataLoader.class)
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Entry::getKey, e -> (DataLoaderBuilder<?, ?>) e.getValue()));
  }
}
