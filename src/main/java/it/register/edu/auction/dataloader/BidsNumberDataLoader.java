package it.register.edu.auction.dataloader;

import it.register.edu.auction.annotation.GraphQLDataLoader;
import it.register.edu.auction.builder.DataLoaderBuilder;
import it.register.edu.auction.service.AuctionService;
import java.util.concurrent.CompletableFuture;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GraphQLDataLoader
public class BidsNumberDataLoader implements DataLoaderBuilder<Integer, Integer> {

  @Autowired
  private AuctionService auctionService;

  @Override
  public DataLoader<Integer, Integer> build() {
    return DataLoader.newMappedDataLoader(itemIds ->
        CompletableFuture.supplyAsync(() -> auctionService.getBidsNumber(itemIds))
    );
  }
}
