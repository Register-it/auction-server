package it.register.edu.auction.dataloader;

import it.register.edu.auction.annotation.GraphQLDataLoader;
import it.register.edu.auction.builder.DataLoaderBuilder;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.AuctionService;
import java.util.concurrent.CompletableFuture;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GraphQLDataLoader
public class UserDataLoader implements DataLoaderBuilder<Integer, User> {

  @Autowired
  private AuctionService auctionService;

  @Override
  public DataLoader<Integer, User> build() {
    return DataLoader.newMappedDataLoader(userIds ->
        CompletableFuture.supplyAsync(() -> auctionService.getUsers(userIds))
    );
  }
}
