package it.register.edu.auction.resolver.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.WatchlistService;
import java.util.List;
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

@DisplayName("UserResolver")
@ExtendWith(MockitoExtension.class)
class UserResolverTest {

  private static final int USER_ID = 123;
  private static final User USER = User.builder().id(USER_ID).build();
  private static final int LIMIT = 10;
  private static final Item ITEM1 = Item.builder().id(1).build();
  private static final Item ITEM2 = Item.builder().id(2).build();
  private static final Item ITEM3 = Item.builder().id(3).build();
  private static final List<Item> ITEMS = List.of(ITEM1, ITEM2, ITEM3);

  @Mock
  private AuctionService mockAuctionService;

  @Mock
  private WatchlistService mockWatchlistService;

  @InjectMocks
  private UserResolver resolver;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(resolver, "maxItems", 50);
  }

  @Test
  @DisplayName("resolve 'watched' field to the list of items watched by the user")
  void getWatched() {
    when(mockWatchlistService.getWatchedItems(eq(USER_ID), any(Pageable.class))).thenReturn(Page.of(new PageImpl<>(ITEMS)));
    List<Item> result = resolver.getWatched(USER, LIMIT);
    assertEquals(ITEMS, result);
  }

  @Test
  @DisplayName("resolve 'bid' field to the list of items for which the user made a bids")
  void getBid() {
    when(mockAuctionService.getBidItems(eq(USER_ID), any(Pageable.class))).thenReturn(Page.of(new PageImpl<>(ITEMS)));
    List<Item> result = resolver.getBid(USER, LIMIT);
    assertEquals(ITEMS, result);
  }

  @Test
  @DisplayName("resolve 'awarded' field to the list of items awarded by the user")
  void getAwarded() {
    when(mockAuctionService.getAwardedItems(eq(USER_ID), any(Pageable.class))).thenReturn(Page.of(new PageImpl<>(ITEMS)));
    List<Item> result = resolver.getAwarded(USER, LIMIT);
    assertEquals(ITEMS, result);
  }

}