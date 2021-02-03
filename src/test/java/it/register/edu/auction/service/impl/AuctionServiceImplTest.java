package it.register.edu.auction.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.domain.AuctionNotification.Type;
import it.register.edu.auction.domain.BidsNumber;
import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.AwardedItem;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.exception.ExpiredAuctionException;
import it.register.edu.auction.exception.HigherBidExistsException;
import it.register.edu.auction.exception.InvalidBidException;
import it.register.edu.auction.repository.AwardedItemRepository;
import it.register.edu.auction.repository.BidRepository;
import it.register.edu.auction.repository.ImageRepository;
import it.register.edu.auction.repository.ItemRepository;
import it.register.edu.auction.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Sinks.Many;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuctionServiceImpl")
class AuctionServiceImplTest {

  private static final int PAGE_NUMBER = 1;
  private static final int PAGE_SIZE = 10;
  private static final int ITEM_ID = 123;
  private static final int ITEM_ID2 = 234;
  private static final int BIDS_TOTAL = 100;
  private static final int BIDS_TOTAL2 = 101;
  private static final int USER_ID = 1000;
  private static final int USER_ID2 = 1001;
  private static final BigDecimal AMOUNT = new BigDecimal("123.45");
  private static final User USER = User.builder().id(USER_ID).build();
  private static final User USER2 = User.builder().id(USER_ID2).build();
  private static final Set<Integer> USER_IDS = Set.of(USER_ID, USER_ID2);
  private static final List<User> USERS = List.of(USER, USER2);
  private static final Set<Integer> ITEM_IDS = Set.of(ITEM_ID, ITEM_ID2);
  private static final Item ITEM = Item.builder().id(ITEM_ID).initialPrice(new BigDecimal("0")).build();
  private static final Item ITEM2 = Item.builder().id(ITEM_ID2).initialPrice(new BigDecimal("0")).build();
  private static final Item ITEM_PRICED = Item.builder().id(ITEM_ID).initialPrice(AMOUNT).build();
  private static final List<Item> ITEMS = List.of(ITEM, ITEM2);
  private static final Bid BID = Bid.builder().id(11).userId(USER_ID).build();
  private static final Bid BID2 = Bid.builder().id(22).build();
  private static final List<Bid> BIDS = List.of(BID, BID2);
  private static final Image IMAGE = Image.builder().id(999).itemId(ITEM_ID).build();
  private static final Image IMAGE2 = Image.builder().id(888).itemId(ITEM_ID).build();
  private static final Image IMAGE3 = Image.builder().id(777).itemId(ITEM_ID2).build();
  private static final List<Image> IMAGES = List.of(IMAGE, IMAGE2, IMAGE3);

  @Mock
  private ItemRepository mockItemRepository;

  @Mock
  private BidRepository mockBidRepository;

  @Mock
  private ImageRepository mockImageRepository;

  @Mock
  private UserRepository mockUserRepository;

  @Mock
  private AwardedItemRepository mockAwardedItemRepository;

  @Mock
  private Many<AuctionNotification> mockNotificationSink;

  @InjectMocks
  private AuctionServiceImpl service;

  @Captor
  private ArgumentCaptor<Item> itemCaptor;

  @Captor
  private ArgumentCaptor<AwardedItem> awardedItemCaptor;

  @Captor
  private ArgumentCaptor<AuctionNotification> notificationCaptor;

  @Test
  @DisplayName("retrieves a page of the list of items")
  void getItems() {
    when(mockItemRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(ITEMS));
    Page<Item> result = service.getItems(PageRequest.of(PAGE_NUMBER, PAGE_SIZE));
    assertEquals(ITEMS, result.getElements());
  }

  @Test
  @DisplayName("retrieve a single item by id")
  void getItem() {
    when(mockItemRepository.findById(ITEM_ID)).thenReturn(Optional.of(ITEM));
    Optional<Item> result = service.getItem(ITEM_ID);
    assertTrue(result.isPresent());
    assertEquals(ITEM, result.get());
  }

  @Test
  @DisplayName("retrieves all the bids for a given item")
  void getBids() {
    when(mockBidRepository.findByItemIdOrderByDateTimeDesc(ITEM_ID)).thenReturn(BIDS);
    List<Bid> result = service.getBids(ITEM_ID);
    assertEquals(BIDS, result);
  }

  @Test
  @DisplayName("returns the number of bids for every given item id")
  void getBidsNumber() {
    List<BidsNumber> list = List.of(bidsNumber(ITEM_ID, BIDS_TOTAL), bidsNumber(ITEM_ID2, BIDS_TOTAL2));
    when(mockBidRepository.countByItemIdInGroupByItemId(ITEM_IDS)).thenReturn(list);

    Map<Integer, Integer> result = service.getBidsNumber(ITEM_IDS);

    assertEquals(2, result.entrySet().size());
    assertTrue(result.containsKey(ITEM_ID));
    assertEquals(BIDS_TOTAL, result.get(ITEM_ID));
    assertTrue(result.containsKey(ITEM_ID2));
    assertEquals(BIDS_TOTAL2, result.get(ITEM_ID2));
  }

  @Test
  @DisplayName("retrieves the list of images in a specific format for every given item id")
  void getImages() {
    when(mockImageRepository.findByItemIdInAndFormat(ITEM_IDS, Format.FULLSIZE)).thenReturn(IMAGES);

    Map<Integer, List<Image>> result = service.getImages(ITEM_IDS, Format.FULLSIZE);

    assertEquals(2, result.entrySet().size());
    assertTrue(result.containsKey(ITEM_ID));
    assertEquals(2, result.get(ITEM_ID).size());
    assertTrue(result.get(ITEM_ID).contains(IMAGE));
    assertTrue(result.get(ITEM_ID).contains(IMAGE2));
    assertEquals(1, result.get(ITEM_ID2).size());
    assertTrue(result.get(ITEM_ID2).contains(IMAGE3));
  }

  @Test
  @DisplayName("retrieves the paginated list of images in a specific format for every given item id")
  void getPaginatedImages() {
    when(mockImageRepository.findByItemIdInAndFormatAndLimit(ITEM_IDS, Format.THUMBNAIL, PAGE_SIZE)).thenReturn(IMAGES);

    Map<Integer, List<Image>> result = service.getImages(ITEM_IDS, Format.THUMBNAIL, PageRequest.of(PAGE_NUMBER, PAGE_SIZE));

    assertEquals(2, result.entrySet().size());
    assertTrue(result.containsKey(ITEM_ID));
    assertEquals(2, result.get(ITEM_ID).size());
    assertTrue(result.get(ITEM_ID).contains(IMAGE));
    assertTrue(result.get(ITEM_ID).contains(IMAGE2));
    assertEquals(1, result.get(ITEM_ID2).size());
    assertTrue(result.get(ITEM_ID2).contains(IMAGE3));
  }

  @Test
  @DisplayName("retrieves all the users corresponding to the given ids")
  void getUsers() {
    when(mockUserRepository.findAllById(USER_IDS)).thenReturn(USERS);

    Map<Integer, User> result = service.getUsers(USER_IDS);

    assertEquals(2, result.entrySet().size());
    assertEquals(USER, result.get(USER_ID));
    assertEquals(USER2, result.get(USER_ID2));
  }

  @Test
  @DisplayName("successfully places a bid and updates item current price")
  void bidSuccess() {
    when(mockItemRepository.findByIdAndAuctionExpirationAfter(eq(ITEM_ID), any(LocalDateTime.class))).thenReturn(Optional.of(ITEM));
    when(mockBidRepository.findFirstByItemIdAndAmountGreaterThanEqual(ITEM_ID, AMOUNT)).thenReturn(Optional.empty());
    when(mockBidRepository.save(any(Bid.class))).thenAnswer((Answer<Bid>) invocationOnMock -> invocationOnMock.getArgument(0));
    when(mockItemRepository.findById(ITEM_ID)).thenReturn(Optional.of(ITEM));

    Bid result = service.bid(USER_ID, ITEM_ID, AMOUNT);

    assertEquals(ITEM_ID, result.getItemId());
    assertEquals(USER_ID, result.getUserId());
    assertEquals(AMOUNT, result.getAmount());

    verify(mockItemRepository).save(itemCaptor.capture());
    assertEquals(AMOUNT, itemCaptor.getValue().getCurrentPrice());
  }

  @Test
  @DisplayName("tries to place a bid and throw exception if higher bid exists")
  void bidHigherBidExists() {
    when(mockItemRepository.findByIdAndAuctionExpirationAfter(eq(ITEM_ID), any(LocalDateTime.class))).thenReturn(Optional.of(ITEM));
    when(mockBidRepository.findFirstByItemIdAndAmountGreaterThanEqual(ITEM_ID, AMOUNT)).thenReturn(Optional.of(BID));

    HigherBidExistsException exception = assertThrows(HigherBidExistsException.class, () -> service.bid(USER_ID, ITEM_ID, AMOUNT));

    assertEquals(BID, exception.getBid());

    verify(mockBidRepository, times(0)).save(any(Bid.class));
    verify(mockItemRepository, times(0)).save(any(Item.class));
  }

  @Test
  @DisplayName("tries to place a bid and throw exception if the amount is not greater than item initial price")
  void bidInvalid() {
    when(mockItemRepository.findByIdAndAuctionExpirationAfter(eq(ITEM_ID), any(LocalDateTime.class))).thenReturn(Optional.of(ITEM_PRICED));

    assertThrows(InvalidBidException.class, () -> service.bid(USER_ID, ITEM_ID, AMOUNT));

    verify(mockBidRepository, times(0)).save(any(Bid.class));
    verify(mockItemRepository, times(0)).save(any(Item.class));
  }

  @Test
  @DisplayName("tries to place a bid and throw exception if auction is already expired")
  void bidAuctionExpired() {
    when(mockItemRepository.findByIdAndAuctionExpirationAfter(eq(ITEM_ID), any(LocalDateTime.class))).thenReturn(Optional.empty());

    assertThrows(ExpiredAuctionException.class, () -> service.bid(USER_ID, ITEM_ID, AMOUNT));

    verify(mockBidRepository, times(0)).save(any(Bid.class));
    verify(mockItemRepository, times(0)).save(any(Item.class));
  }

  @Test
  @DisplayName("retrieves a paginated list of items for which the given user made a bid")
  void getBidItemsPaginated() {
    when(mockItemRepository.findBidByUser(eq(USER_ID), any(Pageable.class))).thenReturn(new PageImpl<>(ITEMS));
    Page<Item> result = service.getBidItems(USER_ID, PageRequest.of(PAGE_NUMBER, PAGE_SIZE));
    assertEquals(ITEMS, result.getElements());
  }

  @Test
  @DisplayName("retrieves all the items for which the given user made a bid")
  void getAllBidItems() {
    when(mockItemRepository.findBidByUser(USER_ID)).thenReturn(ITEMS);
    List<Item> result = service.getBidItems(USER_ID);
    assertEquals(ITEMS, result);
  }

  @Test
  @DisplayName("retrieves a paginated list of items awarded by the given user")
  void getAwardedItems() {
    when(mockItemRepository.findAwardedByUser(eq(USER_ID), any(Pageable.class))).thenReturn(new PageImpl<>(ITEMS));
    Page<Item> result = service.getAwardedItems(USER_ID, PageRequest.of(PAGE_NUMBER, PAGE_SIZE));
    assertEquals(ITEMS, result.getElements());
  }

  @Test
  @DisplayName("finds expired auctions and assign the items if a winning bid exists")
  void concludeExpiredAuctions() {
    when(mockItemRepository.findExpiredNotAwarded()).thenReturn(ITEMS);
    when(mockBidRepository.findFirstByItemIdOrderByAmountDesc(ITEM_ID)).thenReturn(Optional.empty());
    when(mockBidRepository.findFirstByItemIdOrderByAmountDesc(ITEM_ID2)).thenReturn(Optional.of(BID));

    service.concludeExpiredAuctions();

    verify(mockAwardedItemRepository, times(2)).save(awardedItemCaptor.capture());
    verify(mockNotificationSink, times(2)).tryEmitNext(notificationCaptor.capture());

    AwardedItem awardedItem1 = awardedItemCaptor.getAllValues().get(0);
    AwardedItem awardedItem2 = awardedItemCaptor.getAllValues().get(1);
    AuctionNotification notification1 = notificationCaptor.getAllValues().get(0);
    AuctionNotification notification2 = notificationCaptor.getAllValues().get(1);

    assertNull(awardedItem1.getBidId());
    assertEquals(ITEM_ID, awardedItem1.getItemId());
    assertNull(awardedItem1.getUserId());
    assertEquals(ITEM, notification1.getItem());
    assertEquals(Type.AUCTION_EXPIRED, notification1.getType());
    assertNull(notification1.getBid());

    assertEquals(BID.getId(), awardedItem2.getBidId());
    assertEquals(ITEM_ID2, awardedItem2.getItemId());
    assertEquals(USER_ID, awardedItem2.getUserId());
    assertEquals(ITEM2, notification2.getItem());
    assertEquals(Type.AUCTION_EXPIRED, notification2.getType());
    assertEquals(BID, notification2.getBid());
  }

  private BidsNumber bidsNumber(int itemId, int total) {
    return new BidsNumber() {
      @Override
      public Integer getItemId() {
        return itemId;
      }

      @Override
      public Integer getTotal() {
        return total;
      }
    };
  }
}