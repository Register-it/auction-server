package it.register.edu.auction.resolver.field;

import graphql.kickstart.tools.GraphQLResolver;
import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuctionNotificationResolver implements GraphQLResolver<AuctionNotification> {

  @Autowired
  private AuctionService auctionService;

  public Item getItem(AuctionNotification notification) {
    if (notification.getItem() != null) {
      return notification.getItem();
    }

    return notification.getItemId()
        .flatMap(itemId -> auctionService.getItem(itemId))
        .orElse(null);
  }
}
