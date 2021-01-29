package it.register.edu.auction.resolver.root;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.AuthUtils.getLoggedUser;

import graphql.kickstart.tools.GraphQLQueryResolver;
import it.register.edu.auction.domain.LimitedPageRequest;
import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.WatchlistService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component
public class QueryResolver implements GraphQLQueryResolver {

  public static final Sort ITEM_DEFAULT_SORT = Sort.by("auctionExpiration").and(Sort.by("title"));

  @Autowired
  private AuctionService auctionService;

  @Autowired
  private WatchlistService watchlistService;

  @Value("${auctions.pagination.max-items}")
  private int maxPageSize;

  public Page<Item> getItems(int page, int size) {
    return auctionService.getItems(LimitedPageRequest.of(page, size, maxPageSize, ITEM_DEFAULT_SORT));
  }

  public Optional<Item> getItem(int id) {
    return auctionService.getItem(id);
  }

  public List<Bid> getBids(int itemId) {
    return auctionService.getBids(itemId);
  }

  @Secured(ROLE_AUTHENTICATED)
  public User me() {
    return getLoggedUser();
  }

  @Secured(ROLE_AUTHENTICATED)
  public Page<Item> getWatchedItems(int page, int size) {
    return watchlistService.getWatchedItems(getLoggedUser().getId(), LimitedPageRequest.of(page, size, maxPageSize, ITEM_DEFAULT_SORT));
  }

  @Secured(ROLE_AUTHENTICATED)
  public Page<Item> getBidItems(int page, int size) {
    return auctionService.getBidItems(getLoggedUser().getId(), LimitedPageRequest.of(page, size, maxPageSize, ITEM_DEFAULT_SORT));
  }

  @Secured(ROLE_AUTHENTICATED)
  public Page<Item> getAwardedItems(int page, int size) {
    return auctionService.getAwardedItems(getLoggedUser().getId(), LimitedPageRequest.of(page, size, maxPageSize, ITEM_DEFAULT_SORT));
  }

}
