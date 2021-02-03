package it.register.edu.auction.resolver.root;

import static org.mockito.Mockito.when;

import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.domain.AuctionNotification.Type;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.WatchlistService;
import it.register.edu.auction.test.AuthenticationExtension;
import it.register.edu.auction.test.WithAuthenticatedUser;
import java.time.Duration;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@ExtendWith(AuthenticationExtension.class)
@DisplayName("SubscriptionResolver")
class SubscriptionResolverTest {

  private static final int NO_EVENTS_WAIT_MILLIS = 500;
  private static final int TIMEOUT = 2;
  private static final int USER_ID = 123;
  private static final int ITEM_ID = 987;
  private static final Bid CURRENT_USER_BID = Bid.builder().itemId(ITEM_ID).userId(USER_ID).build();
  private static final Bid BID = Bid.builder().itemId(ITEM_ID).build();
  private static final Item ITEM = Item.builder().id(ITEM_ID).build();

  @Mock(lenient = true)
  private WatchlistService mockWatchlistService;

  @Mock(lenient = true)
  private AuctionService mockAuctionService;

  @InjectMocks
  private SubscriptionResolver resolver;

  private final Many<Bid> bidSink = Sinks.many().multicast().directBestEffort();
  private final Flux<Bid> bids = bidSink.asFlux();
  private final Many<AuctionNotification> notificationSink = Sinks.many().multicast().directBestEffort();
  private final Flux<AuctionNotification> notifications = notificationSink.asFlux();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(resolver, "bids", bids);
    ReflectionTestUtils.setField(resolver, "notifications", notifications);
    when(mockWatchlistService.getWatchedItems(USER_ID)).thenReturn(Collections.emptyList());
    when(mockAuctionService.getBidItems(USER_ID)).thenReturn(Collections.emptyList());
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns no notification for a new bid made by the logged in user herself")
  void auctionEventBidOfUserHerself() {
    Publisher<AuctionNotification> result = resolver.auctionEvent();
    sendBidAndVerifyNoNotifications(result, CURRENT_USER_BID);
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns a NEW_BID notification for a new bid made on an item in the watchlist of the current logged in user")
  void auctionEventNewBid() {
    setItemWatched();
    Publisher<AuctionNotification> result = resolver.auctionEvent();
    sendBidAndVerifyNotification(result, BID, Type.NEW_BID);
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns a BID_EXCEEDED notification for a new bid made on an item previously bid by the current logged in user")
  void auctionEventBidExceeded() {
    setItemPreviouslyBid();
    Publisher<AuctionNotification> result = resolver.auctionEvent();
    sendBidAndVerifyNotification(result, BID, Type.BID_EXCEEDED);
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns no notifications for a new bid made on an item nor watched nor previously bid by the current logged in user")
  void auctionEventNotInterestingBid() {
    Publisher<AuctionNotification> result = resolver.auctionEvent();
    sendBidAndVerifyNoNotifications(result, BID);
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns a ITEM_AWARDED notification for for an auction end event with the winning bid belonging to the current logged in user")
  void auctionEventItemAwarded() {
    setItemPreviouslyBid();
    Publisher<AuctionNotification> result = resolver.auctionEvent();
    sendAuctionExpiredAndVerifyNotification(result, getAuctionExpiredNotificationWon(), Type.ITEM_AWARDED);
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns an AUCTION_EXPIRED notification for an auction expiration event on an item watched by the current logged in user")
  void auctionEventAuctionExpiredWatched() {
    setItemWatched();
    Publisher<AuctionNotification> result = resolver.auctionEvent();
    sendAuctionExpiredAndVerifyNotification(result, getAuctionExpiredNotification(), Type.AUCTION_EXPIRED);
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns an AUCTION_EXPIRED notification for an auction expiration event on an item previously bid by the current logged in user")
  void auctionEventAuctionExpiredPreviouslyBid() {
    setItemPreviouslyBid();
    Publisher<AuctionNotification> result = resolver.auctionEvent();
    sendAuctionExpiredAndVerifyNotification(result, getAuctionExpiredNotification(), Type.AUCTION_EXPIRED);
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns no notifications for an auction expiration event on an item nor watched nor previously bid by the current logged in user")
  void auctionEventNotInterestingAuctionExpiration() {
    Publisher<AuctionNotification> result = resolver.auctionEvent();
    sendAuctionExpiredAndVerifyNoNotifications(result, getAuctionExpiredNotification());
  }

  private void setItemWatched() {
    when(mockWatchlistService.getWatchedItems(USER_ID)).thenReturn(Collections.singletonList(ITEM));
  }

  private void setItemPreviouslyBid() {
    when(mockAuctionService.getBidItems(USER_ID)).thenReturn(Collections.singletonList(ITEM));
  }

  private AuctionNotification getAuctionExpiredNotification() {
    return AuctionNotification.builder()
        .item(Item.builder().id(ITEM_ID).build())
        .type(Type.AUCTION_EXPIRED)
        .build();
  }

  private AuctionNotification getAuctionExpiredNotificationWon() {
    return AuctionNotification.builder()
        .item(Item.builder().id(ITEM_ID).build())
        .type(Type.AUCTION_EXPIRED)
        .bid(CURRENT_USER_BID)
        .build();
  }

  private void sendBidAndVerifyNoNotifications(Publisher<AuctionNotification> publisher, Bid bid) {
    StepVerifier.create(publisher)
        .expectSubscription()
        .then(() -> bidSink.tryEmitNext(bid))
        .expectNoEvent(Duration.ofMillis(NO_EVENTS_WAIT_MILLIS))
        .thenCancel()
        .verify(Duration.ofSeconds(TIMEOUT));
  }

  private void sendBidAndVerifyNotification(Publisher<AuctionNotification> publisher, Bid bid, Type type) {
    StepVerifier.create(publisher)
        .expectSubscription()
        .then(() -> bidSink.tryEmitNext(bid))
        .expectNextMatches(notification -> assertNotification(notification, type, bid))
        .thenCancel()
        .verify(Duration.ofSeconds(TIMEOUT));
  }

  private void sendAuctionExpiredAndVerifyNotification(Publisher<AuctionNotification> publisher, AuctionNotification auctionExpired, Type type) {
    StepVerifier.create(publisher)
        .expectSubscription()
        .then(() -> notificationSink.tryEmitNext(auctionExpired))
        .expectNextMatches(notification -> assertNotification(notification, type))
        .thenCancel()
        .verify(Duration.ofSeconds(TIMEOUT));
  }

  private void sendAuctionExpiredAndVerifyNoNotifications(Publisher<AuctionNotification> publisher, AuctionNotification auctionExpired) {
    StepVerifier.create(publisher)
        .expectSubscription()
        .then(() -> notificationSink.tryEmitNext(auctionExpired))
        .expectNoEvent(Duration.ofMillis(NO_EVENTS_WAIT_MILLIS))
        .thenCancel()
        .verify(Duration.ofSeconds(TIMEOUT));
  }

  private boolean assertNotification(AuctionNotification notification, Type type) {
    return assertNotification(notification, type, null);
  }

  private boolean assertNotification(AuctionNotification notification, Type type, Bid bid) {
    if (notification.getType() != type) {
      return false;
    }

    if (bid != null && !notification.getBid().equals(bid)) {
      return false;
    }

    return notification.getItemId().orElseThrow(RuntimeException::new).equals(ITEM_ID);
  }
}