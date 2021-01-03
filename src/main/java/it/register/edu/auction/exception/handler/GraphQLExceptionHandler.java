package it.register.edu.auction.exception.handler;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.execution.ExecutionPath;
import graphql.kickstart.spring.error.ErrorContext;
import graphql.kickstart.spring.error.ThrowableGraphQLError;
import it.register.edu.auction.exception.GraphQLDataFetchingException;
import it.register.edu.auction.exception.UnauthorizedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class GraphQLExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  public GraphQLError handleAccessDeniedException(AccessDeniedException e) {
    return new UnauthorizedException();
  }

  @ExceptionHandler(GraphQLDataFetchingException.class)
  public GraphQLError handleDataFetchingException(GraphQLDataFetchingException e, ErrorContext context) {
    return new ExceptionWhileDataFetching(ExecutionPath.fromList(context.getPath()), e, context.getLocations().get(0));
  }

  @ExceptionHandler(Throwable.class)
  public GraphQLError handleInternalExceptions(Throwable e) {
    return new ThrowableGraphQLError(e, "Internal server error");
  }
}
