package it.register.edu.auction.test.integration.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import com.graphql.spring.boot.test.GraphQLResponse;
import it.register.edu.auction.entity.WatchlistId;
import it.register.edu.auction.test.integration.IntegrationTest;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@DisplayName("The 'unwatch' mutation")
class UnwatchMutationTest extends IntegrationTest {

  @Captor
  private ArgumentCaptor<WatchlistId> watchlistCaptor;

  @Test
  @DisplayName("returns an error if the user is not logged in")
  void unwatchNotLogged() throws IOException {
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/unwatch.graphql");
    assertUnauthorized(response);
  }

  @Test
  @DisplayName("removes the given item to the watchlist if the user is logged in")
  void unwatchSuccessful() throws IOException {
    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/mutation/unwatch.graphql");

    assertTrue(response.isOk());
    assertNull(response.get("$.data.unwatch"));

    verify(watchlistRepository).deleteById(watchlistCaptor.capture());
    assertEquals(ITEM_ID, watchlistCaptor.getValue().getItemId());
    assertEquals(USER_ID, watchlistCaptor.getValue().getUserId());
  }
}
