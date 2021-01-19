package it.register.edu.auction.resolver;

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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component
public class QueryResolver implements GraphQLQueryResolver {

  @Autowired
  private AuctionService auctionService;

  @Autowired
  private WatchlistService watchlistService;

  @Value("${auctions.pagination.max-items}")
  private int maxPageSize;

  public Page<Item> getItems(int page, int size) {
    return auctionService.getItems(LimitedPageRequest.of(page, size, maxPageSize));
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

}
