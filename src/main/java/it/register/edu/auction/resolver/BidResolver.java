package it.register.edu.auction.resolver;

import graphql.kickstart.tools.GraphQLResolver;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BidResolver implements GraphQLResolver<Bid> {

  @Autowired
  private AuctionService auctionService;

  public String getUsername(Bid bid) {
    return auctionService.getUser(bid.getUserId()).map(User::getUsername).orElse(null);
  }

}
