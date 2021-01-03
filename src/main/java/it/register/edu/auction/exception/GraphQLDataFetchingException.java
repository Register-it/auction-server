package it.register.edu.auction.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;

public abstract class GraphQLDataFetchingException extends RuntimeException implements GraphQLError {

  protected static final String ERROR_CODE_EXTENSION = "ERROR_CODE";
  protected static final String ERROR_CODE_UNAUTHORIZED = "UNAUTHORIZED";

  public GraphQLDataFetchingException() {
  }

  public GraphQLDataFetchingException(String message) {
    super(message);
  }

  public GraphQLDataFetchingException(String message, Throwable cause) {
    super(message, cause);
  }

  public GraphQLDataFetchingException(Throwable cause) {
    super(cause);
  }

  @Override
  public List<SourceLocation> getLocations() {
    return null;
  }

  @Override
  public ErrorClassification getErrorType() {
    return null;
  }

}
