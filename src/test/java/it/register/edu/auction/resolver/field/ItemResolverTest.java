package it.register.edu.auction.resolver.field;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.test.AuthenticationExtension;
import it.register.edu.auction.test.WithAuthenticatedUser;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("ItemResolver")
@ExtendWith(MockitoExtension.class)
@ExtendWith(AuthenticationExtension.class)
class ItemResolverTest extends WithDataLoader {

  private static final int ITEM_ID = 123;
  private static final Item ITEM = Item.builder().id(ITEM_ID).build();
  private static final String URL1 = "https://url.one";
  private static final String URL2 = "https://url.two";
  private static final int LIMIT = 10;
  private static final int BIDS_NUMBER = 42;
  private static final int USER_ID = 321;

  @InjectMocks
  private ItemResolver resolver;

  @Override
  @BeforeEach
  void setUp() {
    super.setUp();
    ReflectionTestUtils.setField(resolver, "maxImages", 50);
  }

  @Test
  @DisplayName("resolve 'images' field to a list of URLs")
  void getImages() throws MalformedURLException {
    URL url1 = new URL(URL1);
    Image image1 = Image.builder().url(url1).build();
    URL url2 = new URL(URL2);
    Image image2 = Image.builder().url(url2).build();

    when(mockDataLoader.load(ITEM_ID)).thenReturn(CompletableFuture.completedFuture(List.of(image1, image2)));
    CompletableFuture<List<URL>> result = resolver.getImages(ITEM, mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertEquals(2, result.get().size()),
        () -> assertTrue(result.get().contains(url1)),
        () -> assertTrue(result.get().contains(url2))
    );
  }

  @Test
  @DisplayName("resolve 'images' field to an empty collection if nothing is found")
  void getImagesNotFound() {
    when(mockDataLoader.load(ITEM_ID)).thenReturn(CompletableFuture.completedFuture(null));
    CompletableFuture<List<URL>> result = resolver.getImages(ITEM, mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertEquals(0, result.get().size())
    );
  }

  @Test
  @DisplayName("resolve 'thumbnails' field to a list of URLs")
  void getThumbnails() throws MalformedURLException {
    URL url1 = new URL(URL1);
    Image image1 = Image.builder().url(url1).build();
    URL url2 = new URL(URL2);
    Image image2 = Image.builder().url(url2).build();

    when(mockDataLoader.load(eq(ITEM_ID), any(Pageable.class))).thenReturn(CompletableFuture.completedFuture(List.of(image1, image2)));
    CompletableFuture<List<URL>> result = resolver.getThumbnails(ITEM, LIMIT, mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertEquals(2, result.get().size()),
        () -> assertTrue(result.get().contains(url1)),
        () -> assertTrue(result.get().contains(url2))
    );
  }

  @Test
  @DisplayName("resolve 'thumbnails' field to an empty collection if nothing is found")
  void getThumbnailsNotFound() {
    when(mockDataLoader.load(eq(ITEM_ID), any(Pageable.class))).thenReturn(CompletableFuture.completedFuture(null));
    CompletableFuture<List<URL>> result = resolver.getThumbnails(ITEM, LIMIT, mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertEquals(0, result.get().size())
    );
  }

  @Test
  @DisplayName("resolve 'bidsNumber' field to the number of bids for this item")
  void getBidsNumber() {
    when(mockDataLoader.load(ITEM_ID)).thenReturn(CompletableFuture.completedFuture(BIDS_NUMBER));
    CompletableFuture<Integer> result = resolver.getBidsNumber(ITEM, mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertEquals(BIDS_NUMBER, result.get())
    );
  }

  @Test
  @DisplayName("resolve 'bidsNumber' field to zero if no bids are found for this item")
  void getBidsNumberNoBidsFound() {
    when(mockDataLoader.load(ITEM_ID)).thenReturn(CompletableFuture.completedFuture(null));
    CompletableFuture<Integer> result = resolver.getBidsNumber(ITEM, mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertEquals(0, result.get())
    );
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("resolve 'isWatched' field to true if the item is watched by the current logged in user")
  void isWatchedTrue() {
    when(mockDataLoader.load(ITEM_ID, USER_ID)).thenReturn(CompletableFuture.completedFuture(true));
    CompletableFuture<Boolean> result = resolver.isWatched(ITEM, mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertTrue(result.get())
    );
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("resolve 'isWatched' field to false if the item is not watched by the current logged in user")
  void isWatchedFalse() {
    when(mockDataLoader.load(ITEM_ID, USER_ID)).thenReturn(CompletableFuture.completedFuture(false));
    CompletableFuture<Boolean> result = resolver.isWatched(ITEM, mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertFalse(result.get())
    );
  }

  @Test
  @DisplayName("resolve 'isWatched' field to null if no user is logged in")
  void isWatchedNullIfUserIsNotAuthenticated() {
    CompletableFuture<Boolean> result = resolver.isWatched(ITEM, mockEnvironment);

    assertAll(
        () -> assertTrue(result.isDone()),
        () -> assertNull(result.get())
    );

    verify(mockDataLoader, times(0)).load(ITEM_ID, USER_ID);
  }
}