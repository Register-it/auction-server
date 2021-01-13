package it.register.edu.auction.exception;

import java.util.Collections;
import java.util.Map;

public class ExpiredAuctionException extends GraphQLDataFetchingException {

  private static final long serialVersionUID = -3274260905917874491L;

  public ExpiredAuctionException() {
    super("The auction is expired");
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Collections.singletonMap(ERROR_CODE_EXTENSION, ERROR_CODE_AUCTION_EXPIRED);
  }

}
