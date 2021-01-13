package it.register.edu.auction.exception;

import java.util.Collections;
import java.util.Map;

public class InvalidCredentialsException extends GraphQLDataFetchingException {

  private static final long serialVersionUID = 1142874302466474894L;

  public InvalidCredentialsException() {
    super("Invalid credentials");
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Collections.singletonMap(ERROR_CODE_EXTENSION, ERROR_CODE_INVALID_CREDENTIALS);
  }

}
