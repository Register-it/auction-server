package it.register.edu.auction.test.integration.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
import org.springframework.data.domain.PageImpl;

@DisplayName("The 'bidItems' query")
class BidItemsQueryTest extends IntegrationTest {

  private static final int ITEMS_TOTAL_NUMBER = 8;
  private static final int DEFAULT_PAGE_SIZE = 10;

  private final List<Item> items = getTestItemList(ITEMS_TOTAL_NUMBER);

  @Test
  @DisplayName("returns an error if the user is not logged in")
  void getItemsNotAuthenticated() throws IOException {
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/bidItems.graphql");
    assertUnauthorized(response);
  }

  @Test
  @DisplayName("returns a paged list of items using default page size if nothing is specified")
  void getItemsWithNoParams() throws IOException {
    when(itemRepository.findBidByUser(eq(USER_ID), argThat(new PageableMatcher(0, DEFAULT_PAGE_SIZE))))
        .thenAnswer(i -> new PageImpl<>(items.subList(0, Math.min(DEFAULT_PAGE_SIZE, items.size())), i.getArgument(1), ITEMS_TOTAL_NUMBER));
    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/query/bidItems.graphql");
    assertPageResult(response, 0, ITEMS_TOTAL_NUMBER, true, true, 1);
  }

  @Test
  @DisplayName("returns a paged list of items using given page parameters")
  void getItemsWithPageParams() throws IOException {
    int page = 1;
    int size = 4;
    when(itemRepository.findBidByUser(eq(USER_ID), argThat(new PageableMatcher(page, size))))
        .thenAnswer(i -> new PageImpl<>(items.subList(0, size), i.getArgument(1), ITEMS_TOTAL_NUMBER));
    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/query/bidItemsWithPageParams.graphql");
    assertPageResult(response, page, size, false, true, 2);
  }

  @Test
  @DisplayName("returns an error if the page size exceeds the max value")
  void getItemsWithSizeExceedingMaxValue() throws IOException {
    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/query/bidItemsWithExceedingParams.graphql");
    assertTrue(response.isOk());
    assertNull(response.get("$.data"));
    Object[] errors = response.get("$.errors", Object[].class);
    assertEquals(1, errors.length);
  }

  private void assertPageResult(GraphQLResponse response, int current, int size, boolean isFirst, boolean isLast, int totalPages) {
    assertTrue(response.isOk());

    assertEquals(current, response.get("$.data.bidItems.current", Integer.class));
    assertEquals(isFirst, response.get("$.data.bidItems.isFirst", Boolean.class));
    assertEquals(isLast, response.get("$.data.bidItems.isLast", Boolean.class));
    assertEquals(ITEMS_TOTAL_NUMBER, response.get("$.data.bidItems.totalElements", Integer.class));
    assertEquals(totalPages, response.get("$.data.bidItems.totalPages", Integer.class));

    Item[] elements = response.get("$.data.bidItems.elements", Item[].class);
    assertEquals(size, elements.length);
    IntStream.range(0, elements.length).forEach(i -> assertEquals(items.get(i).getId(), elements[i].getId()));
  }
}
