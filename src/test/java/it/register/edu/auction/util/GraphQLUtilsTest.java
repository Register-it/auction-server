package it.register.edu.auction.util;

import static it.register.edu.auction.util.GraphQLUtils.BIDS_NUMBER_DATA_LOADER;
import static it.register.edu.auction.util.GraphQLUtils.IMAGE_DATA_LOADER;
import static it.register.edu.auction.util.GraphQLUtils.THUMBNAIL_DATA_LOADER;
import static it.register.edu.auction.util.GraphQLUtils.USER_DATA_LOADER;
import static it.register.edu.auction.util.GraphQLUtils.WATCHLIST_DATA_LOADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import graphql.kickstart.execution.context.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.exception.MissingDataLoaderException;
import java.util.List;
import java.util.Optional;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GraphQLUtils")
class GraphQLUtilsTest {

  @Mock
  private DataFetchingEnvironment mockEnvironment;

  @Mock
  private GraphQLContext mockContext;

  @Mock
  private DataLoaderRegistry mockRegistry;

  @Mock
  private DataLoader<Object, Object> mockDataLoader;

  @BeforeEach
  void setUp() {
    when(mockEnvironment.getContext()).thenReturn(mockContext);
    when(mockContext.getDataLoaderRegistry()).thenReturn(Optional.of(mockRegistry));
  }

  @Test
  @DisplayName("throws exception when searching for a data loader while no registry is present")
  void throwsExceptionWhenNoRegistryIsPresent() {
    when(mockContext.getDataLoaderRegistry()).thenReturn(Optional.empty());
    assertThrows(MissingDataLoaderException.class, () -> GraphQLUtils.imageDataLoader(mockEnvironment));
  }

  @Test
  @DisplayName("throws exception when searching for a data loader is not found inside the registry")
  void throwsExceptionWhenDataLoaderNotFound() {
    assertThrows(MissingDataLoaderException.class, () -> GraphQLUtils.imageDataLoader(mockEnvironment));
  }

  @Test
  @DisplayName("retrieves image data loader from DataFetchingEnvironment")
  void imageDataLoader() {
    when(mockRegistry.getDataLoader(IMAGE_DATA_LOADER)).thenReturn(mockDataLoader);
    DataLoader<Integer, List<Image>> result = GraphQLUtils.imageDataLoader(mockEnvironment);
    assertEquals(mockDataLoader, result);
  }

  @Test
  @DisplayName("retrieves thumbnail data loader from DataFetchingEnvironment")
  void thumbnailDataLoader() {
    when(mockRegistry.getDataLoader(THUMBNAIL_DATA_LOADER)).thenReturn(mockDataLoader);
    DataLoader<Integer, List<Image>> result = GraphQLUtils.thumbnailDataLoader(mockEnvironment);
    assertEquals(mockDataLoader, result);
  }

  @Test
  @DisplayName("retrieves bidsNumber data loader from DataFetchingEnvironment")
  void bidsNumberDataLoader() {
    when(mockRegistry.getDataLoader(BIDS_NUMBER_DATA_LOADER)).thenReturn(mockDataLoader);
    DataLoader<Integer, Integer> result = GraphQLUtils.bidsNumberDataLoader(mockEnvironment);
    assertEquals(mockDataLoader, result);
  }

  @Test
  @DisplayName("retrieves watchlist data loader from DataFetchingEnvironment")
  void watchlistDataLoader() {
    when(mockRegistry.getDataLoader(WATCHLIST_DATA_LOADER)).thenReturn(mockDataLoader);
    DataLoader<Integer, Boolean> result = GraphQLUtils.watchlistDataLoader(mockEnvironment);
    assertEquals(mockDataLoader, result);
  }

  @Test
  @DisplayName("retrieves user data loader from DataFetchingEnvironment")
  void userDataLoader() {
    when(mockRegistry.getDataLoader(USER_DATA_LOADER)).thenReturn(mockDataLoader);
    DataLoader<Integer, User> result = GraphQLUtils.userDataLoader(mockEnvironment);
    assertEquals(mockDataLoader, result);
  }
}