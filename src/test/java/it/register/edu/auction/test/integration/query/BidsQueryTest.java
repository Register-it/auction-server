package it.register.edu.auction.test.integration.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.graphql.spring.boot.test.GraphQLResponse;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.test.integration.IntegrationTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The 'bids' query")
class BidsQueryTest extends IntegrationTest {

  private static final int BIDS_TOTAL = 7;

  private final List<Bid> bids = getTestBidList();

  @Test
  @DisplayName("returns the bids made on the given item")
  void getBids() throws IOException {
    when(bidRepository.findByItemIdOrderByDateTimeDesc(ITEM_ID)).thenReturn(bids);
    when(userRepository.findAllById(Set.of(USER_ID))).thenReturn(Collections.singletonList(getTestUser()));

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/bids.graphql");

    assertTrue(response.isOk());
    Object[] result = response.get("$.data.bids", Object[].class);
    assertEquals(BIDS_TOTAL, result.length);
    IntStream.range(0, result.length).forEach(i -> assertBidEquals(bids.get(i), result[i]));
  }

  @SuppressWarnings("unchecked")
  private void assertBidEquals(Bid expected, Object actual) {
    Map<String, Object> map = (Map<String, Object>) actual;
    assertEquals(expected.getId(), Integer.valueOf((String) map.get("id")));
    assertEquals(0, expected.getAmount().compareTo(BigDecimal.valueOf((double) map.get("amount"))));
    assertEquals(USERNAME, map.get("username"));
  }

  private List<Bid> getTestBidList() {
    return IntStream.range(300, 300 + BIDS_TOTAL).mapToObj(this::getTestBid).collect(Collectors.toList());
  }
}
