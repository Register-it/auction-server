package it.register.edu.auction.exception.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphqlErrorException;
import graphql.kickstart.spring.error.ErrorContext;
import graphql.kickstart.spring.error.ThrowableGraphQLError;
import graphql.language.SourceLocation;
import it.register.edu.auction.exception.InvalidCredentialsException;
import it.register.edu.auction.exception.UnauthorizedException;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

@DisplayName("GraphQLExceptionHandler")
class GraphQLExceptionHandlerTest {

  private GraphQLExceptionHandler hander;

  @BeforeEach
  void setUp() {
    hander = new GraphQLExceptionHandler();
  }

  @Test
  @DisplayName("converts access denied exception to unauthorized GraphQL error")
  void handleAccessDeniedException() {
    assertTrue(hander.handleAccessDeniedException(new AccessDeniedException("")) instanceof UnauthorizedException);
    assertTrue(hander.handleAccessDeniedException(new AuthenticationCredentialsNotFoundException("")) instanceof UnauthorizedException);
  }

  @Test
  @DisplayName("converts access denied exception to unauthorized GraphQL error")
  void handleDataFetchingException() {
    ErrorContext mockContext = mock(ErrorContext.class);
    when(mockContext.getLocations()).thenReturn(Collections.singletonList(new SourceLocation(1, 2)));
    assertTrue(hander.handleDataFetchingException(new InvalidCredentialsException(), mockContext) instanceof ExceptionWhileDataFetching);
  }

  @Test
  @DisplayName("returns GraphqlErrorException as is")
  void handleGraphQLException() {
    GraphqlErrorException mockError = mock(GraphqlErrorException.class);
    assertEquals(mockError, hander.handleGraphQLException(mockError));
  }

  @Test
  @DisplayName("returns any unrecognized exception as an internal error")
  void handleInternalExceptions() {
    assertTrue(hander.handleInternalExceptions(new RuntimeException()) instanceof ThrowableGraphQLError);
  }

}