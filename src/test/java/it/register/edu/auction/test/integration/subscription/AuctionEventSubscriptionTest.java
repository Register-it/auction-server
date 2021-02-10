package it.register.edu.auction.test.integration.subscription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.when;

import com.graphql.spring.boot.test.GraphQLResponse;
import it.register.edu.auction.domain.AuctionNotification.Type;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.test.integration.IntegrationTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.security.disabled=true")
@DisplayName("The 'auctionEvent' subscription")
class AuctionEventSubscriptionTest extends IntegrationTest {

  public static final int SUBSCRIBED_USER_ID = 2424;
  private static final int TIMEOUT = 10000;
  private static final long WAIT_TIME = 5000L;

  @Test
  @DisplayName("sends a notification for a new bid on a watched item")
  void subscribeNewBidOnWatched() throws IOException {
    when(itemRepository.findWatchedByUser(SUBSCRIBED_USER_ID)).thenReturn(Collections.singletonList(getTestItem()));

    executeAfter(WAIT_TIME, this::makeBid);

    List<GraphQLResponse> responses = graphQLTestSubscription.start("graphql/subscription/auctionEvent.graphql")
        .awaitAndGetNextResponses(TIMEOUT, 1);

    assertEquals(1, responses.size());
    assertEquals(ITEM_ID, responses.get(0).get("$.data.auctionEvent.item.id", Integer.class));
    assertEquals(0, new BigDecimal("234.56").compareTo(responses.get(0).get("$.data.auctionEvent.bid.amount", BigDecimal.class)));
    assertEquals(Type.NEW_BID.name(), responses.get(0).get("$.data.auctionEvent.type"));
  }

  @SneakyThrows
  private void makeBid() {
    BigDecimal amount = new BigDecimal("234.56");
    Item item = getTestItem();
    when(itemRepository.findByIdAndAuctionExpirationAfter(eq(ITEM_ID), any(LocalDateTime.class))).thenReturn(Optional.of(item));
    when(bidRepository.findFirstByItemIdAndAmountGreaterThanEqual(eq(ITEM_ID), refEq(amount))).thenReturn(Optional.empty());
    when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArgument(0));
    when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

    authenticated(graphQLTestTemplate).postForResource("graphql/mutation/bid.graphql");
  }

  private void executeAfter(long millis, Runnable runnable) {
    new Thread(() -> {
      try {
        Thread.sleep(millis);
        runnable.run();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).start();
  }
}
