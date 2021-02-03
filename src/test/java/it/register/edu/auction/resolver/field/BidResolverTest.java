package it.register.edu.auction.resolver.field;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.User;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

@DisplayName("BidResolver")
class BidResolverTest extends WithDataLoader {

  private static final int USER_ID = 123;
  private static final String USERNAME = "username";

  @InjectMocks
  private BidResolver resolver;

  @Test
  @DisplayName("resolve 'username' field to the name of the user that made this bid")
  void getUsername() {
    when(mockDataLoader.load(USER_ID)).thenReturn(CompletableFuture.completedFuture(User.builder().username(USERNAME).build()));

    CompletableFuture<String> result = resolver.getUsername(Bid.builder().userId(USER_ID).build(), mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertEquals(USERNAME, result.get())
    );
  }

  @Test
  @DisplayName("resolve 'username' field to null if user is not found")
  void getUsernameNotFound() {
    when(mockDataLoader.load(USER_ID)).thenReturn(CompletableFuture.completedFuture(null));

    CompletableFuture<String> result = resolver.getUsername(Bid.builder().userId(USER_ID).build(), mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertNull(result.get())
    );
  }
}