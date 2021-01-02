package it.register.edu.auction.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;

public class IllegalSizeException extends RuntimeException implements GraphQLError {

  public IllegalSizeException(int requestedSize, int maxSize) {
    super("Requested elements number (" + requestedSize + ") exceed maximum value of " + maxSize);
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
