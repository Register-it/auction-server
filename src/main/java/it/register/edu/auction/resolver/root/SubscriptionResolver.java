package it.register.edu.auction.resolver.root;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.AuthUtils.getLoggedUser;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.WatchlistService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class SubscriptionResolver implements GraphQLSubscriptionResolver {

  @Autowired
  private Flux<AuctionNotification> notifications;

  @Autowired
  private WatchlistService watchlistService;

  @Secured(ROLE_AUTHENTICATED)
  public Publisher<AuctionNotification> auctionEvent() {
    User user = getLoggedUser();

    // TODO: filter out bids made by the user himself
    return notifications.filter(n -> n.getItemId().isPresent() && watchlistService.isInWatchlist(user.getId(), n.getItemId().get()));
  }

}
