package it.register.edu.auction.resolver;

import graphql.kickstart.tools.GraphQLResolver;
import it.register.edu.auction.entity.Auction;
import it.register.edu.auction.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuctionResolver implements GraphQLResolver<Auction> {

  @Autowired
  private BidRepository bidRepository;

  public Integer getBidsNumber(Auction auction) {
    return bidRepository.countByAuctionId(auction.getId());
  }

}
