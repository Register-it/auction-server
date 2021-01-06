package it.register.edu.auction.exception;

import java.util.Collections;
import java.util.Map;

public class InvalidBidException extends GraphQLDataFetchingException {

  public InvalidBidException(String message) {
    super("The bid is not valid for this auction: " + message);
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Collections.singletonMap(ERROR_CODE_EXTENSION, ERROR_CODE_INVALID_BID);
  }

}
