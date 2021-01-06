package it.register.edu.auction.exception;

import java.util.Collections;
import java.util.Map;

public class ExpiredAuctionException extends GraphQLDataFetchingException {

  public ExpiredAuctionException() {
    super("The auction is expired");
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Collections.singletonMap(ERROR_CODE_EXTENSION, ERROR_CODE_AUCTION_EXPIRED);
  }

}
