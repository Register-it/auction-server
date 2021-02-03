package it.register.edu.auction.resolver.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.service.AuctionService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuctionNotificationResolver")
class AuctionNotificationResolverTest {

  private static final int ITEM_ID = 123;

  @Mock
  private AuctionService mockAuctionService;

  @InjectMocks
  private AuctionNotificationResolver resolver;

  @Test
  void getItemFromBidId() {
    Item item = new Item();
    when(mockAuctionService.getItem(ITEM_ID)).thenReturn(Optional.of(item));
    AuctionNotification notification = AuctionNotification.builder().bid(Bid.builder().itemId(ITEM_ID).build()).build();
    Item result = resolver.getItem(notification);
    assertEquals(item, result);
  }

  @Test
  void getItemFromBidIdNotFound() {
    when(mockAuctionService.getItem(ITEM_ID)).thenReturn(Optional.empty());
    AuctionNotification notification = AuctionNotification.builder().bid(Bid.builder().itemId(ITEM_ID).build()).build();
    Item result = resolver.getItem(notification);
    assertNull(result);
  }

  @Test
  void getItemFromProperty() {
    Item item = new Item();
    AuctionNotification notification = AuctionNotification.builder().item(item).build();
    Item result = resolver.getItem(notification);
    assertEquals(item, result);
    verify(mockAuctionService, times(0)).getItem(ITEM_ID);
  }
}