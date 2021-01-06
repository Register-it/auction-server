package it.register.edu.auction.resolver;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.AuthUtils.getUserWithRole;

import graphql.kickstart.tools.GraphQLResolver;
import it.register.edu.auction.domain.LimitedPageRequest;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.WatchlistService;
import java.net.URL;
import java.util.List;
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

  public List<URL> getImages(Item item) {
    return auctionService.getImageUrls(item.getId(), Format.FULLSIZE);
  }

  public List<URL> getThumbnails(Item item, Integer limit) {
    return auctionService.getImageUrls(item.getId(), Format.THUMBNAIL, LimitedPageRequest.of(0, limit, maxImages));
  }

  public Integer getBidsNumber(Item item) {
    return auctionService.getBidsNumber(item.getId());
  }

  public Boolean isWatched(Item item) {
    return getUserWithRole(ROLE_AUTHENTICATED)
        .map(user -> watchlistService.isInWatchlist(user.getId(), item.getId()))
        .orElse(null);
  }

}
