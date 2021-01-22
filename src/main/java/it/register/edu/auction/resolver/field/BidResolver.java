package it.register.edu.auction.resolver.field;

import static it.register.edu.auction.util.GraphQLUtils.userDataLoader;

import graphql.kickstart.tools.GraphQLResolver;
import graphql.schema.DataFetchingEnvironment;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.service.AuctionService;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BidResolver implements GraphQLResolver<Bid> {

  @Autowired
  private AuctionService auctionService;

  public CompletableFuture<String> getUsername(Bid bid, DataFetchingEnvironment env) {
    return userDataLoader(env).load(bid.getUserId()).thenApply(user -> user != null ? user.getUsername() : null);
  }

}
