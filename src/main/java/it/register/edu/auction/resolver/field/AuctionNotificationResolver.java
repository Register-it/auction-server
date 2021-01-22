package it.register.edu.auction.resolver.field;

import graphql.kickstart.tools.GraphQLResolver;
import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuctionNotificationResolver implements GraphQLResolver<AuctionNotification> {

  @Autowired
  private AuctionService auctionService;

  @Autowired
  private WatchlistService watchlistService;

  public Item getItem(AuctionNotification notification) {
    if (notification.getItem() != null) {
      return notification.getItem();
    }

    return auctionService.getItem(notification.getBid().getItemId()).orElse(null);
  }
}
