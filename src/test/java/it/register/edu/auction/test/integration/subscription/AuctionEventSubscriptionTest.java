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
import it.register.edu.auction.scheduler.ApplicationTaskScheduler;
import it.register.edu.auction.test.integration.IntegrationTest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.security.disabled=true")
@DisplayName("The 'auctionEvent' subscription")
class AuctionEventSubscriptionTest extends IntegrationTest {

  public static final int SUBSCRIBED_USER_ID = 2424;
  private static final int TIMEOUT = 10000;
  private static final long WAIT_TIME = 5000L;

  @Autowired
  private ApplicationTaskScheduler scheduler;

  @Test
  @DisplayName("sends a notification for a new bid on a watched item")
  void subscribeNewBidOnWatched() {
    when(itemRepository.findWatchedByUser(SUBSCRIBED_USER_ID)).thenReturn(Collections.singletonList(getTestItem()));

    executeAfter(WAIT_TIME, this::makeBid);

    List<GraphQLResponse> responses = graphQLTestSubscription.start("graphql/subscription/auctionEvent.graphql")
            .awaitAndGetNextResponses(TIMEOUT, 1);

    assertEquals(1, responses.size());
    assertEquals(ITEM_ID, responses.get(0).get("$.data.auctionEvent.item.id", Integer.class));
    assertEquals(0, new BigDecimal("234.56").compareTo(responses.get(0).get("$.data.auctionEvent.bid.amount", BigDecimal.class)));
    assertEquals(Type.NEW_BID.name(), responses.get(0).get("$.data.auctionEvent.type"));
  }

  @Test
  @DisplayName("don't send any notification for a new bid made by the user himself/herself")
  void subscribeNoNotificationIfSameUser() {
    executeAfter(WAIT_TIME, () -> makeBid(SUBSCRIBED_USER_ID));

    graphQLTestSubscription.start("graphql/subscription/auctionEvent.graphql")
            .waitAndExpectNoResponse(TIMEOUT);
  }

  @Test
  @DisplayName("sends a notification for a bid exceeding a bid previously made by the logged in user")
  void subscribeNewBidOnPreviouslyBid() {
    when(itemRepository.findBidByUser(SUBSCRIBED_USER_ID)).thenReturn(Collections.singletonList(getTestItem()));

    executeAfter(WAIT_TIME, this::makeBid);

    List<GraphQLResponse> responses = graphQLTestSubscription.start("graphql/subscription/auctionEvent.graphql")
            .awaitAndGetNextResponses(TIMEOUT, 1);

    assertEquals(1, responses.size());
    assertEquals(ITEM_ID, responses.get(0).get("$.data.auctionEvent.item.id", Integer.class));
    assertEquals(0, new BigDecimal("234.56").compareTo(responses.get(0).get("$.data.auctionEvent.bid.amount", BigDecimal.class)));
    assertEquals(Type.BID_EXCEEDED.name(), responses.get(0).get("$.data.auctionEvent.type"));
  }

  @Test
  @DisplayName("sends a notification for a an ended auction on a watched item")
  void subscribeEndedAuctionOnWatchedItem() {
    when(itemRepository.findWatchedByUser(SUBSCRIBED_USER_ID)).thenReturn(Collections.singletonList(getTestItem()));

    executeAfter(WAIT_TIME, this::auctionExpired);

    List<GraphQLResponse> responses = graphQLTestSubscription.start("graphql/subscription/auctionEvent.graphql")
            .awaitAndGetNextResponses(TIMEOUT, 1);

    assertEquals(1, responses.size());
    assertEquals(ITEM_ID, responses.get(0).get("$.data.auctionEvent.item.id", Integer.class));
    assertEquals(0, new BigDecimal("111").compareTo(responses.get(0).get("$.data.auctionEvent.bid.amount", BigDecimal.class)));
    assertEquals(Type.AUCTION_EXPIRED.name(), responses.get(0).get("$.data.auctionEvent.type"));
  }

  @Test
  @DisplayName("sends a notification for an awarded item")
  void subscribeEndedAuctionOnAwardedItem() {
    when(itemRepository.findWatchedByUser(SUBSCRIBED_USER_ID)).thenReturn(Collections.singletonList(getTestItem()));

    executeAfter(WAIT_TIME, () -> auctionExpired(SUBSCRIBED_USER_ID));

    List<GraphQLResponse> responses = graphQLTestSubscription.start("graphql/subscription/auctionEvent.graphql")
            .awaitAndGetNextResponses(TIMEOUT, 1);

    assertEquals(1, responses.size());
    assertEquals(ITEM_ID, responses.get(0).get("$.data.auctionEvent.item.id", Integer.class));
    assertEquals(0, new BigDecimal("111").compareTo(responses.get(0).get("$.data.auctionEvent.bid.amount", BigDecimal.class)));
    assertEquals(Type.ITEM_AWARDED.name(), responses.get(0).get("$.data.auctionEvent.type"));
  }

  @AfterEach
  void tearDown() {
    graphQLTestSubscription.reset();
  }

  private void auctionExpired() {
    auctionExpired(USER_ID);
  }

  private void auctionExpired(int userId) {
    when(itemRepository.findExpiredNotAwarded()).thenReturn(Collections.singletonList(getTestItem()));
    when(bidRepository.findFirstByItemIdOrderByAmountDesc(ITEM_ID)).thenReturn(Optional.of(getTestBid(11, userId)));

    scheduler.concludeExpiredAuctions();
  }

  private void makeBid() {
    makeBid(USER_ID);
  }

  @SneakyThrows
  private void makeBid(int userId) {
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
