package it.register.edu.auction.resolver.root;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.AuthUtils.getLoggedUser;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.domain.AuctionNotification.Type;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
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
  private Flux<AuctionNotification> notifications;

  @Autowired
  private WatchlistService watchlistService;

  @Autowired
  private AuctionService auctionService;

  @Secured(ROLE_AUTHENTICATED)
  public Publisher<AuctionNotification> auctionEvent() {
    User user = getLoggedUser();
    return Flux.merge(getBidNotifications(user.getId()), getCompletedAuctionNotifications(user.getId()));
  }

  private Flux<AuctionNotification> getBidNotifications(int userId) {
    return bids
        .filter(bid -> bid.getUserId() != userId)
        .flatMap(bid -> toAuctionNotification(bid, userId));
  }

  private Flux<AuctionNotification> getCompletedAuctionNotifications(int userId) {
    return notifications
        .map(notification -> currentUserAwareNotification(notification, userId))
        .filter(notification -> mustBeSentToTheUser(notification, userId));
  }

  private Flux<AuctionNotification> toAuctionNotification(Bid bid, int userId) {
    if (hasBeenBid(userId, bid.getItemId())) {
      return Flux.just(AuctionNotification.builder().bid(bid).type(Type.BID_EXCEEDED).build());
    }

    if (isInWatchlist(userId, bid.getItemId())) {
      return Flux.just(AuctionNotification.builder().bid(bid).type(Type.NEW_BID).build());
    }

    return Flux.empty();
  }

  private AuctionNotification currentUserAwareNotification(AuctionNotification notification, int userId) {
    Bid bid = notification.getBid();
    if (bid != null && bid.getUserId() == userId) {
      return AuctionNotification.builder().item(notification.getItem()).bid(notification.getBid()).type(Type.ITEM_AWARDED).build();
    }
    return notification;
  }

  private boolean mustBeSentToTheUser(AuctionNotification notification, int userId) {
    return notification.getItemId()
        .map(itemId -> hasBeenBid(userId, itemId) || isInWatchlist(userId, itemId))
        .orElse(false);
  }

  private boolean hasBeenBid(int userId, int itemId) {
    return auctionService.getBidItems(userId).stream().map(Item::getId).anyMatch(id -> id == itemId);
  }

  private boolean isInWatchlist(int userId, int itemId) {
    return watchlistService.getWatchedItems(userId).stream().map(Item::getId).anyMatch(id -> id == itemId);
  }
}
