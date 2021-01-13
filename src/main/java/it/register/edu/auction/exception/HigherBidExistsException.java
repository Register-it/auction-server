package it.register.edu.auction.exception;

import it.register.edu.auction.entity.Bid;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;

public class HigherBidExistsException extends GraphQLDataFetchingException {

  private static final long serialVersionUID = 1376742873974860123L;

  @Getter
  private final Bid bid;

  public HigherBidExistsException(Bid bid) {
    super("An higher bid exists for this item");
    this.bid = bid;
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Collections.singletonMap(ERROR_CODE_EXTENSION, ERROR_CODE_HIGHER_BID_EXISTS);
  }

}
