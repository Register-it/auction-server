package it.register.edu.auction.dataloader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.register.edu.auction.service.AuctionService;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.dataloader.DataLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BidsNumberDataLoader")
class BidsNumberDataLoaderTest {

  private static final int ITEM_ID1 = 1;
  private static final int ITEM_ID2 = 2;
  private static final int ITEM_ID3 = 3;
  private static final int BIDS1 = 123;
  private static final int BIDS2 = 234;
  private static final int BIDS3 = 345;

  @Mock
  private AuctionService auctionService;

  @InjectMocks
  private BidsNumberDataLoader dataLoaderBuilder;

  @Test
  @DisplayName("creates a data loader that gets the bid number for every itemId")
  void dataLoader() {
    when(auctionService.getBidsNumber(Set.of(ITEM_ID1, ITEM_ID2, ITEM_ID3))).thenReturn(Map.of(ITEM_ID1, BIDS1, ITEM_ID2, BIDS2, ITEM_ID3, BIDS3));

    DataLoader<Integer, Integer> dataLoader = dataLoaderBuilder.build();

    CompletableFuture<Integer> bidsForItem1 = dataLoader.load(ITEM_ID1);
    CompletableFuture<Integer> bidsForItem2 = dataLoader.load(ITEM_ID2);
    CompletableFuture<Integer> bidsForItem3 = dataLoader.load(ITEM_ID3);

    dataLoader.dispatchAndJoin();

    assertAll(
        () -> assertTrue(bidsForItem1.isDone()),
        () -> assertTrue(bidsForItem2.isDone()),
        () -> assertTrue(bidsForItem3.isDone()),
        () -> assertEquals(BIDS1, bidsForItem1.get()),
        () -> assertEquals(BIDS2, bidsForItem2.get()),
        () -> assertEquals(BIDS3, bidsForItem3.get())
    );
  }
}