package it.register.edu.auction.exception;

public class IllegalSizeException extends GraphQLDataFetchingException {

  public IllegalSizeException(int requestedSize, int maxSize) {
    super("Requested elements number (" + requestedSize + ") exceed maximum value of " + maxSize);
  }

}
