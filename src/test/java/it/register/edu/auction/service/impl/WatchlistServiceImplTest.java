package it.register.edu.auction.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.WatchlistEntry;
import it.register.edu.auction.entity.WatchlistId;
import it.register.edu.auction.repository.ItemRepository;
import it.register.edu.auction.repository.WatchlistRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("WatchlistServiceImpl")
class WatchlistServiceImplTest {

  private static final int ITEM_ID = 987;
  private static final int ITEM_ID2 = 876;
  private static final List<Integer> ITEM_IDS = List.of(ITEM_ID, ITEM_ID2);
  private static final int USER_ID = 123;
  private static final int PAGE_NUMBER = 1;
  private static final int PAGE_SIZE = 10;
  private static final Item ITEM = Item.builder().id(ITEM_ID).build();
  private static final Item ITEM2 = Item.builder().id(ITEM_ID2).build();
  private static final List<Item> ITEMS = List.of(ITEM, ITEM2);

  @Mock
  private WatchlistRepository mockWatchlistRepository;

  @Mock
  private ItemRepository mockItemRepository;

  @InjectMocks
  private WatchlistServiceImpl service;

  @Captor
  private ArgumentCaptor<WatchlistEntry> watchlistEntryCaptor;

  @Captor
  private ArgumentCaptor<WatchlistId> watchlistIdCaptor;

  @Test
  @DisplayName("saves an item in the watchlist of the given user")
  void addToWatchlist() {
    service.addToWatchlist(USER_ID, ITEM_ID);
    verify(mockWatchlistRepository).save(watchlistEntryCaptor.capture());
    assertEquals(ITEM_ID, watchlistEntryCaptor.getValue().getItemId());
    assertEquals(USER_ID, watchlistEntryCaptor.getValue().getUserId());
  }

  @Test
  @DisplayName("deletes an item from the watchlist of the given user")
  void removeFromWatchlist() {
    service.removeFromWatchlist(USER_ID, ITEM_ID);
    verify(mockWatchlistRepository).deleteById(watchlistIdCaptor.capture());
    assertEquals(ITEM_ID, watchlistIdCaptor.getValue().getItemId());
    assertEquals(USER_ID, watchlistIdCaptor.getValue().getUserId());
  }

  @Test
  @DisplayName("retrieves a paginated list of items watched by the given user")
  void getWatchedItemsPaginated() {
    when(mockItemRepository.findWatchedByUser(eq(USER_ID), any(Pageable.class))).thenReturn(new PageImpl<>(ITEMS));
    Page<Item> result = service.getWatchedItems(USER_ID, PageRequest.of(PAGE_NUMBER, PAGE_SIZE));
    assertEquals(ITEMS, result.getElements());
  }

  @Test
  @DisplayName("retrieves the list of all items watched by the given user")
  void getAllWatchedItems() {
    when(mockItemRepository.findWatchedByUser(USER_ID)).thenReturn(ITEMS);
    List<Item> result = service.getWatchedItems(USER_ID);
    assertEquals(ITEMS, result);
  }

  @Test
  @DisplayName("checks if the given items are in the watchlist of the given user")
  void areInWatchlist() {
    WatchlistEntry entry = WatchlistEntry.builder().userId(USER_ID).itemId(ITEM_ID).build();
    when(mockWatchlistRepository.findByUserIdAndItemIdIn(USER_ID, ITEM_IDS)).thenReturn(Collections.singletonList(entry));

    List<Boolean> result = service.areInWatchlist(USER_ID, ITEM_IDS);
    assertEquals(ITEM_IDS.size(), result.size());
    assertTrue(result.get(0));
    assertFalse(result.get(1));
  }
}