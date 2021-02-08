package it.register.edu.auction.test.integration.query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.graphql.spring.boot.test.GraphQLResponse;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.WatchlistEntry;
import it.register.edu.auction.test.integration.IntegrationTest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("The 'item' query")
class ItemQueryTest extends IntegrationTest {

  private final List<Image> images = getTestImagesList();
  private final List<Image> thumbnails = getTestThumbnailsList();

  @Test
  @DisplayName("returns null if the requested item is not found")
  void getItemNotFound() throws IOException {
    when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/item.graphql");
    assertTrue(response.isOk());
    assertNull(response.get("$.data.item"));
  }

  @Test
  @DisplayName("returns the requested full item if found")
  void getItemFound() throws IOException {
    int limit = maxImages;
    prepareGetItem(limit);
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/item.graphql");
    assertItem(response, limit);
  }

  @Test
  @DisplayName("returns the requested item with limited thumbnails if a limit is specified")
  void getItemWithLimitedThumbs() throws IOException {
    int limit = 2;
    prepareGetItem(limit);
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/query/itemWithLimitedThumbs.graphql");
    assertItem(response, limit);
  }

  @Test
  @DisplayName("returns the requested item with watched true if the logged in user is watching it")
  void getItemWatched() throws IOException {
    when(watchlistRepository.findByUserIdAndItemIdIn(USER_ID, List.of(ITEM_ID)))
        .thenReturn(Collections.singletonList(WatchlistEntry.builder().itemId(ITEM_ID).userId(USER_ID).build()));

    int limit = maxImages;
    prepareGetItem(limit);
    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/query/item.graphql");
    assertItem(response, limit, true);
  }

  @Test
  @DisplayName("returns the requested item with watched false if the logged in user is not watching it")
  void getItemNotWatched() throws IOException {
    int limit = maxImages;
    prepareGetItem(limit);
    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/query/item.graphql");
    assertItem(response, limit, false);
  }

  private void prepareGetItem(int limit) {
    List<Image> thumbnailsSlice = thumbnails.subList(0, Math.min(limit, thumbnails.size()));
    when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(getTestItem()));
    when(bidRepository.countByItemIdInGroupByItemId(Set.of(ITEM_ID))).thenReturn(Collections.singletonList(getTestBidsNumber()));
    when(imageRepository.findByItemIdInAndFormatAndLimit(Set.of(ITEM_ID), Format.THUMBNAIL, limit)).thenReturn(thumbnailsSlice);
    when(imageRepository.findByItemIdInAndFormat(Set.of(ITEM_ID), Format.FULLSIZE)).thenReturn(images);
  }

  private void assertItem(GraphQLResponse response, int limit) {
    assertItem(response, limit, null);
  }

  private void assertItem(GraphQLResponse response, int limit, Boolean watched) {
    assertTrue(response.isOk());

    assertEquals(ITEM_ID, response.get("$.data.item.id", Integer.class));
    assertEquals(ITEM_TITLE, response.get("$.data.item.title"));
    assertEquals(ITEM_DESCRIPTION, response.get("$.data.item.description"));
    assertEquals(AUCTION_EXPIRATION_STR, response.get("$.data.item.auctionExpiration"));
    assertEquals(ITEM_INITIAL_PRICE_STR, response.get("$.data.item.initialPrice"));
    assertEquals(ITEM_CURRENT_PRICE_STR, response.get("$.data.item.currentPrice"));
    assertEquals(BIDS_NUMBER, response.get("$.data.item.bidsNumber", Integer.class));

    String[] thumbUrls = response.get("$.data.item.thumbnails", String[].class);
    assertEquals(Math.min(limit, thumbnails.size()), thumbUrls.length);
    IntStream.range(0, thumbUrls.length).forEach(i -> assertEquals(thumbnails.get(i).getUrl().toString(), thumbUrls[i]));

    String[] imageUrls = response.get("$.data.item.images", String[].class);
    assertEquals(images.size(), imageUrls.length);
    IntStream.range(0, imageUrls.length).forEach(i -> assertEquals(images.get(i).getUrl().toString(), imageUrls[i]));

    assertEquals(watched, response.get("$.data.item.watched", Boolean.class));
  }
}
