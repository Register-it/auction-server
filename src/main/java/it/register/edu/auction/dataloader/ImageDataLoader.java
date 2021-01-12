package it.register.edu.auction.dataloader;

import it.register.edu.auction.annotation.GraphQLDataLoader;
import it.register.edu.auction.builder.DataLoaderBuilder;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.service.AuctionService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GraphQLDataLoader
public class ImageDataLoader implements DataLoaderBuilder<Integer, List<Image>> {

  @Autowired
  private AuctionService auctionService;

  @Override
  public DataLoader<Integer, List<Image>> build() {
    return DataLoader.newMappedDataLoader(itemIds ->
        CompletableFuture.supplyAsync(() -> auctionService.getImages(itemIds, Format.FULLSIZE))
    );
  }
}
