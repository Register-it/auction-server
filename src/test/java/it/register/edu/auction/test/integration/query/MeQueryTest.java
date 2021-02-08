package it.register.edu.auction.test.integration.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.graphql.spring.boot.test.GraphQLResponse;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.test.integration.IntegrationTest;
import it.register.edu.auction.test.integration.PageableMatcher;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;

@DisplayName("The 'me' query")
class MeQueryTest extends IntegrationTest {

  private static final int WATCHED_ITEMS_NUMBER = 15;
  private static final int BID_ITEMS_NUMBER = 5;
  private static final int AWARDED_ITEMS_NUMBER = 1;

  @Value("${auctions.pagination.max-profile-items}")
  private int maxItems;

  private final List<Item> watched = getTestItemList(WATCHED_ITEMS_NUMBER, 400);
  private final List<Item> bid = getTestItemList(BID_ITEMS_NUMBER, 500);
  private final List<Item> awarded = getTestItemList(AWARDED_ITEMS_NUMBER, 600);

  @Test
  @DisplayName("returns an unauthorized error if the user is not logged in")
  void getMeNotLogged() throws IOException {
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/me.graphql");
    assertUnauthorized(response);
  }

  @Test
  @DisplayName("returns all the user information if the user is logged in")
  void getMeLogged() throws IOException {
    when(itemRepository.findWatchedByUser(eq(USER_ID), argThat(new PageableMatcher(0, maxItems)))).thenReturn(new PageImpl<>(watched));
    when(itemRepository.findBidByUser(eq(USER_ID), argThat(new PageableMatcher(0, maxItems)))).thenReturn(new PageImpl<>(bid));
    when(itemRepository.findAwardedByUser(eq(USER_ID), argThat(new PageableMatcher(0, maxItems)))).thenReturn(new PageImpl<>(awarded));

    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/query/me.graphql");

    assertTrue(response.isOk());
    assertEquals(USER_FIRST_NAME, response.get("$.data.me.firstName"));
    assertEquals(USER_LAST_NAME, response.get("$.data.me.lastName"));
    assertEquals(USER_PROFILE_IMAGE, response.get("$.data.me.image"));
    assertItemList(response, watched, "watched");
    assertItemList(response, bid, "bid");
    assertItemList(response, awarded, "awarded");
  }

  @Test
  @DisplayName("returns a limited number of items if a limit is specified")
  void getMeLimitedItems() throws IOException {
    int watchedLimit = 1;
    int bidLimit = 2;
    int awardedLimit = 3;
    List<Item> limitedWatched = watched.subList(0, Math.min(watchedLimit, watched.size()));
    List<Item> limitedBid = bid.subList(0, Math.min(bidLimit, bid.size()));
    List<Item> limitedAwarded = awarded.subList(0, Math.min(awardedLimit, awarded.size()));
    when(itemRepository.findWatchedByUser(eq(USER_ID), argThat(new PageableMatcher(0, watchedLimit)))).thenReturn(new PageImpl<>(limitedWatched));
    when(itemRepository.findBidByUser(eq(USER_ID), argThat(new PageableMatcher(0, bidLimit)))).thenReturn(new PageImpl<>(limitedBid));
    when(itemRepository.findAwardedByUser(eq(USER_ID), argThat(new PageableMatcher(0, awardedLimit)))).thenReturn(new PageImpl<>(limitedAwarded));

    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/query/meWithLimitedItems.graphql");

    assertTrue(response.isOk());
    assertEquals(USER_FIRST_NAME, response.get("$.data.me.firstName"));
    assertEquals(USER_LAST_NAME, response.get("$.data.me.lastName"));
    assertEquals(USER_PROFILE_IMAGE, response.get("$.data.me.image"));
    assertItemList(response, limitedWatched, "watched");
    assertItemList(response, limitedBid, "bid");
    assertItemList(response, limitedAwarded, "awarded");
  }

  private void assertItemList(GraphQLResponse response, List<Item> expected, String name) {
    Item[] items = response.get("$.data.me." + name, Item[].class);
    assertEquals(expected.size(), items.length);
    IntStream.range(0, items.length).forEach(i -> assertEquals(expected.get(i).getId(), items[i].getId()));
  }
}
