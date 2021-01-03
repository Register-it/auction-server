package it.register.edu.auction.exception;

import java.util.Collections;
import java.util.Map;

public class UnauthorizedException extends GraphQLDataFetchingException {

  public UnauthorizedException() {
    super("Unauthorized");
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Collections.singletonMap(ERROR_CODE_EXTENSION, ERROR_CODE_UNAUTHORIZED);
  }

}
