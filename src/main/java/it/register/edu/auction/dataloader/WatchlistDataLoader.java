package it.register.edu.auction.dataloader;

import it.register.edu.auction.annotation.GraphQLDataLoader;
import it.register.edu.auction.builder.DataLoaderBuilder;
import it.register.edu.auction.exception.IllegalDataLoaderUsageException;
import it.register.edu.auction.service.WatchlistService;
import java.util.concurrent.CompletableFuture;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GraphQLDataLoader
public class WatchlistDataLoader implements DataLoaderBuilder<Integer, Boolean> {

  @Autowired
  private WatchlistService watchlistService;

  @Override
  public DataLoader<Integer, Boolean> build() {
    return DataLoader.newDataLoader((itemIds, env) -> {
      int userId = (int) env.getKeyContexts().values().stream()
          .distinct()
          .reduce((a, b) -> {
            throw new IllegalDataLoaderUsageException("WatchlistDataLoader must have the same userId context for every load() call in a request");
          })
          .orElseThrow(() -> new IllegalDataLoaderUsageException("WatchlistDataLoader must be called with a userId in the load() context"));

      return CompletableFuture.supplyAsync(() -> watchlistService.areInWatchlist(userId, itemIds));
    });
  }
}
