package it.register.edu.auction.exception;

public class IllegalSizeException extends GraphQLDataFetchingException {

  private static final long serialVersionUID = -760577431742035413L;

  public IllegalSizeException(int requestedSize, int maxSize) {
    super("Requested elements number (" + requestedSize + ") exceed maximum value of " + maxSize);
  }

}
