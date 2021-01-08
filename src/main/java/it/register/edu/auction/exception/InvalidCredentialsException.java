package it.register.edu.auction.exception;

import java.util.Collections;
import java.util.Map;

public class InvalidCredentialsException extends GraphQLDataFetchingException {

  public InvalidCredentialsException() {
    super("Invalid credentials");
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Collections.singletonMap(ERROR_CODE_EXTENSION, ERROR_CODE_INVALID_CREDENTIALS);
  }

}
