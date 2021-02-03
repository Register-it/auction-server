package it.register.edu.auction.dataloader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.exception.IllegalDataLoaderUsageException;
import it.register.edu.auction.service.AuctionService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.dataloader.DataLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("ThumbnailDataLoader")
class ThumbnailDataLoaderTest {

  private static final int ITEM_ID1 = 1;
  private static final int ITEM_ID2 = 2;
  private static final int ITEM_ID3 = 3;
  private static final List<Image> IMAGES1 = List.of(Image.builder().id(1).build(), Image.builder().id(2).build());
  private static final List<Image> IMAGES2 = List.of(Image.builder().id(3).build());

  @Mock
  private AuctionService auctionService;

  @InjectMocks
  private ThumbnailDataLoader dataLoaderBuilder;

  @Test
  @DisplayName("creates a data loader that gets a paginated list of thumbnails for every itemId")
  void dataLoader() {
    Pageable pageable = PageRequest.of(0, 10);
    when(auctionService.getImages(Set.of(ITEM_ID1, ITEM_ID2, ITEM_ID3), Format.THUMBNAIL, pageable))
        .thenReturn(Map.of(ITEM_ID1, IMAGES1, ITEM_ID2, IMAGES2));

    DataLoader<Integer, List<Image>> dataLoader = dataLoaderBuilder.build();

    CompletableFuture<List<Image>> imagesForItem1 = dataLoader.load(ITEM_ID1, pageable);
    CompletableFuture<List<Image>> imagesForItem2 = dataLoader.load(ITEM_ID2, pageable);
    CompletableFuture<List<Image>> imagesForItem3 = dataLoader.load(ITEM_ID3, pageable);

    dataLoader.dispatchAndJoin();

    assertAll(
        () -> assertTrue(imagesForItem1.isDone()),
        () -> assertTrue(imagesForItem2.isDone()),
        () -> assertTrue(imagesForItem3.isDone()),
        () -> assertEquals(IMAGES1, imagesForItem1.get()),
        () -> assertEquals(IMAGES2, imagesForItem2.get()),
        () -> assertNull(imagesForItem3.get())
    );
  }

  @Test
  @DisplayName("throws an exception if pagination is not equal for every request")
  void dataLoaderThrowsException() {
    DataLoader<Integer, List<Image>> dataLoader = dataLoaderBuilder.build();

    CompletableFuture<List<Image>> imagesForItem1 = dataLoader.load(ITEM_ID1, PageRequest.of(0, 10));
    CompletableFuture<List<Image>> imagesForItem2 = dataLoader.load(ITEM_ID2, PageRequest.of(1, 5));

    dataLoader.dispatchAndJoin();

    assertAll(
        () -> assertTrue(imagesForItem1.isDone()),
        () -> assertTrue(imagesForItem2.isDone())
    );

    ExecutionException exception1 = assertThrows(ExecutionException.class, imagesForItem1::get);
    assertTrue(exception1.getCause() instanceof IllegalDataLoaderUsageException);

    ExecutionException exception2 = assertThrows(ExecutionException.class, imagesForItem2::get);
    assertTrue(exception2.getCause() instanceof IllegalDataLoaderUsageException);
  }
}