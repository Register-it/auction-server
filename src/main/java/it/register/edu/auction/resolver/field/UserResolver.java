package it.register.edu.auction.resolver.field;

import graphql.kickstart.tools.GraphQLResolver;
import it.register.edu.auction.domain.LimitedPageRequest;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.WatchlistService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class UserResolver implements GraphQLResolver<User> {

  @Autowired
  private AuctionService auctionService;

  @Autowired
  private WatchlistService watchlistService;

  @Value("${auctions.pagination.max-profile-items}")
  private int maxItems;

  public List<Item> getWatched(User user, Integer limit) {
    return watchlistService.getWatchedItems(user.getId(), pageable(limit)).getElements();
  }

  public List<Item> getBidded(User user, Integer limit) {
    return auctionService.getBiddedItems(user.getId(), pageable(limit)).getElements();
  }

  public List<Item> getAwarded(User user, Integer limit) {
    return auctionService.getAwardedItems(user.getId(), pageable(limit)).getElements();
  }

  private Pageable pageable(Integer limit) {
    return LimitedPageRequest.of(0, limit, maxItems);
  }
}
