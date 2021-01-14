package it.register.edu.auction.exception.handler;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.GraphqlErrorException;
import graphql.execution.ExecutionPath;
import graphql.kickstart.spring.error.ErrorContext;
import graphql.kickstart.spring.error.ThrowableGraphQLError;
import it.register.edu.auction.exception.GraphQLDataFetchingException;
import it.register.edu.auction.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@Slf4j
public class GraphQLExceptionHandler {

  @ExceptionHandler(AccessDeniedException.class)
  public GraphQLError handleAccessDeniedException(AccessDeniedException e) {
    return new UnauthorizedException();
  }

  @ExceptionHandler(GraphQLDataFetchingException.class)
  public GraphQLError handleDataFetchingException(GraphQLDataFetchingException e, ErrorContext context) {
    log.error("Data fetching exception", e);
    return new ExceptionWhileDataFetching(ExecutionPath.fromList(context.getPath()), e, context.getLocations().get(0));
  }

  @ExceptionHandler(GraphqlErrorException.class)
  public GraphQLError handleGraphQLException(GraphqlErrorException e) {
    log.error("GraphQL error exception", e);
    return e;
  }

  @ExceptionHandler(Throwable.class)
  public GraphQLError handleInternalExceptions(Throwable e) {
    log.error("Internal exception", e);
    return new ThrowableGraphQLError(e, "Internal server error");
  }
}
