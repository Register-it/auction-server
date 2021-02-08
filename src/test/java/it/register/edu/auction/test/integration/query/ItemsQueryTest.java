package it.register.edu.auction.test.integration.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
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

@DisplayName("The 'items' query")
class ItemsQueryTest extends IntegrationTest {

  private static final int ITEMS_TOTAL_NUMBER = 12;
  private static final int DEFAULT_PAGE_SIZE = 10;

  private final List<Item> items = getTestItemList(ITEMS_TOTAL_NUMBER);

  @Test
  @DisplayName("returns a paged list of items using default page size if nothing is specified")
  void getItemsWithNoParams() throws IOException {
    when(itemRepository.findAll(argThat(new PageableMatcher(0, DEFAULT_PAGE_SIZE))))
        .thenAnswer(i -> new PageImpl<>(items.subList(0, DEFAULT_PAGE_SIZE), i.getArgument(0), ITEMS_TOTAL_NUMBER));

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/items.graphql");

    assertPageResult(response, 0, DEFAULT_PAGE_SIZE, true, false, 2);
  }

  @Test
  @DisplayName("returns a paged list of items using given page parameters")
  void getItemsWithPageParams() throws IOException {
    int page = 2;
    int size = 2;
    when(itemRepository.findAll(argThat(new PageableMatcher(page, size))))
        .thenAnswer(i -> new PageImpl<>(items.subList(0, size), i.getArgument(0), ITEMS_TOTAL_NUMBER));
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/itemsWithPageParams.graphql");
    assertPageResult(response, page, size, false, false, 6);
  }

  @Test
  @DisplayName("returns an error if the page size exceeds the max value")
  void getItemsWithSizeExceedingMaxValue() throws IOException {
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/itemsWithExceedingParams.graphql");
    assertTrue(response.isOk());
    assertNull(response.get("$.data"));
    Object[] errors = response.get("$.errors", Object[].class);
    assertEquals(1, errors.length);
  }

  private void assertPageResult(GraphQLResponse response, int current, int size, boolean isFirst, boolean isLast, int totalPages) {
    assertTrue(response.isOk());

    assertEquals(current, response.get("$.data.items.current", Integer.class));
    assertEquals(isFirst, response.get("$.data.items.isFirst", Boolean.class));
    assertEquals(isLast, response.get("$.data.items.isLast", Boolean.class));
    assertEquals(ITEMS_TOTAL_NUMBER, response.get("$.data.items.totalElements", Integer.class));
    assertEquals(totalPages, response.get("$.data.items.totalPages", Integer.class));

    Item[] elements = response.get("$.data.items.elements", Item[].class);
    assertEquals(size, elements.length);
    IntStream.range(0, elements.length).forEach(i -> assertEquals(items.get(i).getId(), elements[i].getId()));
  }
}
