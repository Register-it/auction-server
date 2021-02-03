package it.register.edu.auction.dataloader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.service.AuctionService;
import java.util.List;
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
@DisplayName("ImageDataLoader")
class ImageDataLoaderTest {

  private static final int ITEM_ID1 = 1;
  private static final int ITEM_ID2 = 2;
  private static final int ITEM_ID3 = 3;
  private static final List<Image> IMAGES1 = List.of(Image.builder().id(1).build(), Image.builder().id(2).build());
  private static final List<Image> IMAGES2 = List.of(Image.builder().id(3).build());

  @Mock
  private AuctionService auctionService;

  @InjectMocks
  private ImageDataLoader dataLoaderBuilder;

  @Test
  @DisplayName("creates a data loader that gets a list of images for every itemId")
  void dataLoader() {
    when(auctionService.getImages(Set.of(ITEM_ID1, ITEM_ID2, ITEM_ID3), Format.FULLSIZE)).thenReturn(Map.of(ITEM_ID1, IMAGES1, ITEM_ID2, IMAGES2));

    DataLoader<Integer, List<Image>> dataLoader = dataLoaderBuilder.build();

    CompletableFuture<List<Image>> imagesForItem1 = dataLoader.load(ITEM_ID1);
    CompletableFuture<List<Image>> imagesForItem2 = dataLoader.load(ITEM_ID2);
    CompletableFuture<List<Image>> imagesForItem3 = dataLoader.load(ITEM_ID3);

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
}