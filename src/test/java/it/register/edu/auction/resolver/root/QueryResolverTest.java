package it.register.edu.auction.resolver.root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.WatchlistService;
import it.register.edu.auction.test.AuthenticationExtension;
import it.register.edu.auction.test.WithAuthenticatedUser;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@ExtendWith(AuthenticationExtension.class)
@DisplayName("QueryResolver")
class QueryResolverTest {

  private static final int PAGE_NUMBER = 1;
  private static final int PAGE_SIZE = 10;
  private static final int ITEM_ID = 123;
  private static final Item ITEM1 = Item.builder().id(ITEM_ID).build();
  private static final Item ITEM2 = Item.builder().id(2).build();
  private static final Item ITEM3 = Item.builder().id(3).build();
  private static final List<Item> ITEMS = List.of(ITEM1, ITEM2, ITEM3);
  private static final Bid BID1 = Bid.builder().id(11).build();
  private static final Bid BID2 = Bid.builder().id(12).build();
  private static final List<Bid> BIDS = List.of(BID1, BID2);
  private static final int USER_ID = 1313;

  @Mock
  private AuctionService mockAuctionService;

  @Mock
  private WatchlistService mockWatchlistService;

  @InjectMocks
  private QueryResolver resolver;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(resolver, "maxPageSize", 100);
  }

  @Test
  @DisplayName("returns a paginated list of items")
  void getItems() {
    when(mockAuctionService.getItems(any(Pageable.class))).thenReturn(Page.of(new PageImpl<>(ITEMS)));
    Page<Item> result = resolver.getItems(PAGE_NUMBER, PAGE_SIZE);
    assertEquals(ITEMS, result.getElements());
  }

  @Test
  @DisplayName("returns a specific item, if it exists")
  void getItem() {
    Optional<Item> item = Optional.of(ITEM1);
    when(mockAuctionService.getItem(ITEM_ID)).thenReturn(item);
    Optional<Item> result = resolver.getItem(ITEM_ID);
    assertEquals(item, result);
  }

  @Test
  @DisplayName("returns the list of bids for an item")
  void getBids() {
    when(mockAuctionService.getBids(ITEM_ID)).thenReturn(BIDS);
    List<Bid> result = resolver.getBids(ITEM_ID);
    assertEquals(BIDS, result);
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns the current logged in user")
  void me() {
    User result = resolver.me();
    assertEquals(USER_ID, result.getId());
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns a paginated list of the items watched by the current logged in user")
  void getWatchedItems() {
    when(mockWatchlistService.getWatchedItems(eq(USER_ID), any(Pageable.class))).thenReturn(Page.of(new PageImpl<>(ITEMS)));
    Page<Item> result = resolver.getWatchedItems(PAGE_NUMBER, PAGE_SIZE);
    assertEquals(ITEMS, result.getElements());
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns a paginated list of the items for which the current logged in user made a bid")
  void getBidItems() {
    when(mockAuctionService.getBidItems(eq(USER_ID), any(Pageable.class))).thenReturn(Page.of(new PageImpl<>(ITEMS)));
    Page<Item> result = resolver.getBidItems(PAGE_NUMBER, PAGE_SIZE);
    assertEquals(ITEMS, result.getElements());
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns a paginated list of the items awarded by the current logged in user")
  void getAwardedItems() {
    when(mockAuctionService.getAwardedItems(eq(USER_ID), any(Pageable.class))).thenReturn(Page.of(new PageImpl<>(ITEMS)));
    Page<Item> result = resolver.getAwardedItems(PAGE_NUMBER, PAGE_SIZE);
    assertEquals(ITEMS, result.getElements());
  }
}