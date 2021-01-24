package it.register.edu.auction.resolver.root;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.AuthUtils.getLoggedUser;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.domain.AuctionNotification.Type;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.WatchlistService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class SubscriptionResolver implements GraphQLSubscriptionResolver {

  @Autowired
  private Flux<Bid> bids;

  @Autowired
  private WatchlistService watchlistService;

  @Autowired
  private AuctionService auctionService;

  @Secured(ROLE_AUTHENTICATED)
  public Publisher<AuctionNotification> auctionEvent() {
    User user = getLoggedUser();
    return bids.flatMap(bid -> toAuctionNotification(bid, user.getId()));
  }

  private Flux<AuctionNotification> toAuctionNotification(Bid bid, int userId) {
    if (bid.getUserId() == userId) {
      return Flux.empty();
    }

    if (auctionService.hasBeenBid(userId, bid.getItemId())) {
      return Flux.just(AuctionNotification.builder().bid(bid).type(Type.BID_EXCEEDED).build());
    }

    if (watchlistService.isInWatchlist(userId, bid.getItemId())) {
      return Flux.just(AuctionNotification.builder().bid(bid).type(Type.NEW_BID).build());
    }

    return Flux.empty();
  }
}
