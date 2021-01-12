package it.register.edu.auction.resolver;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.AuthUtils.getUserWithRole;
import static it.register.edu.auction.util.GraphQLUtils.bidsNumberDataLoader;
import static it.register.edu.auction.util.GraphQLUtils.imageDataLoader;
import static it.register.edu.auction.util.GraphQLUtils.thumbnailDataLoader;

import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;
import it.register.edu.auction.domain.LimitedPageRequest;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.WatchlistService;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ItemResolver implements GraphQLResolver<Item> {

  @Autowired
  private AuctionService auctionService;

  @Autowired
  private WatchlistService watchlistService;

  @Value("${auctions.pagination.max-images}")
  private int maxImages;

  public CompletableFuture<List<URL>> getImages(Item item, DataFetchingEnvironment env) {
    return imageDataLoader(env).load(item.getId()).thenApply(this::getUrl);
  }

  public CompletableFuture<List<URL>> getThumbnails(Item item, Integer limit, DataFetchingEnvironment env) {
    return thumbnailDataLoader(env).load(item.getId(), LimitedPageRequest.of(0, limit, maxImages)).thenApply(this::getUrl);
  }

  public CompletableFuture<Integer> getBidsNumber(Item item, DataFetchingEnvironment env) {
    return bidsNumberDataLoader(env).load(item.getId()).thenApply(number -> number != null ? number : 0);
  }

  public Boolean isWatched(Item item) {
    return getUserWithRole(ROLE_AUTHENTICATED)
        .map(user -> watchlistService.isInWatchlist(user.getId(), item.getId()))
        .orElse(null);
  }

  private List<URL> getUrl(List<Image> images) {
    return images != null ? images.stream().map(Image::getUrl).collect(Collectors.toList()) : Collections.emptyList();
  }

}
