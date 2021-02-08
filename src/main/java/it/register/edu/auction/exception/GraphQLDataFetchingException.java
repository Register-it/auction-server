package it.register.edu.auction.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.Collections;
import java.util.List;

public abstract class GraphQLDataFetchingException extends RuntimeException implements GraphQLError {

  public static final String ERROR_CODE_EXTENSION = "ERROR_CODE";
  public static final String ERROR_CODE_UNAUTHORIZED = "UNAUTHORIZED";
  public static final String ERROR_CODE_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
  public static final String ERROR_CODE_AUCTION_EXPIRED = "AUCTION_EXPIRED";
  public static final String ERROR_CODE_HIGHER_BID_EXISTS = "HIGHER_BID_EXISTS";
  public static final String ERROR_CODE_INVALID_BID = "INVALID_BID";

  protected GraphQLDataFetchingException() {
  }

  protected GraphQLDataFetchingException(String message) {
    super(message);
  }

  protected GraphQLDataFetchingException(String message, Throwable cause) {
    super(message, cause);
  }

  protected GraphQLDataFetchingException(Throwable cause) {
    super(cause);
  }

  @Override
  public List<SourceLocation> getLocations() {
    return Collections.emptyList();
  }

  @Override
  public ErrorClassification getErrorType() {
    return null;
  }

}
