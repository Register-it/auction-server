package it.register.edu.auction.util;

import graphql.kickstart.execution.context.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.exception.MissingDataLoaderException;
import java.util.List;
import java.util.Optional;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;

public class GraphQLUtils {

  private GraphQLUtils() {
  }

  @SuppressWarnings("unchecked")
  public static <K, V> DataLoader<K, V> getDataLoader(DataFetchingEnvironment env, String name) {
    GraphQLContext context = env.getContext();

    DataLoaderRegistry registry = context.getDataLoaderRegistry()
            .orElseThrow(() -> new MissingDataLoaderException("No DataLoaderRegistry available"));

    return Optional.ofNullable(registry.getDataLoader(name))
            .map(dataLoader -> (DataLoader<K, V>) dataLoader)
            .orElseThrow(() -> new MissingDataLoaderException("Cannot find a dataLoader named '" + name + "'"));
  }

  public static DataLoader<Integer, List<Image>> imageDataLoader(DataFetchingEnvironment env) {
    return getDataLoader(env, "imageDataLoader");
  }

  public static DataLoader<Integer, List<Image>> thumbnailDataLoader(DataFetchingEnvironment env) {
    return getDataLoader(env, "thumbnailDataLoader");
  }

  public static DataLoader<Integer, Integer> bidsNumberDataLoader(DataFetchingEnvironment env) {
    return getDataLoader(env, "bidsNumberDataLoader");
  }

  public static DataLoader<Integer, Boolean> watchlistDataLoader(DataFetchingEnvironment env) {
    return getDataLoader(env, "watchlistDataLoader");
  }

  public static DataLoader<Integer, User> userDataLoader(DataFetchingEnvironment env) {
    return getDataLoader(env, "userDataLoader");
  }
}
