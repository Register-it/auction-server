package it.register.edu.auction.dataloader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.register.edu.auction.exception.IllegalDataLoaderUsageException;
import it.register.edu.auction.service.WatchlistService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.dataloader.DataLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("WatchlistDataLoader")
class WatchlistDataLoaderTest {

  private static final int ITEM_ID1 = 1;
  private static final int ITEM_ID2 = 2;
  private static final int ITEM_ID3 = 3;
  private static final int USER_ID = 123;

  @Mock
  private WatchlistService watchlistService;

  @InjectMocks
  private WatchlistDataLoader dataLoaderBuilder;

  @Test
  @DisplayName("creates a data loader that checks if some items are in the watchlist of a user")
  void dataLoader() {
    when(watchlistService.areInWatchlist(USER_ID, List.of(ITEM_ID1, ITEM_ID2, ITEM_ID3))).thenReturn(List.of(true, false, true));

    DataLoader<Integer, Boolean> dataLoader = dataLoaderBuilder.build();

    CompletableFuture<Boolean> isInWatchlist1 = dataLoader.load(ITEM_ID1, USER_ID);
    CompletableFuture<Boolean> isInWatchlist2 = dataLoader.load(ITEM_ID2, USER_ID);
    CompletableFuture<Boolean> isInWatchlist3 = dataLoader.load(ITEM_ID3, USER_ID);

    dataLoader.dispatchAndJoin();

    assertAll(
        () -> assertTrue(isInWatchlist1.isDone()),
        () -> assertTrue(isInWatchlist2.isDone()),
        () -> assertTrue(isInWatchlist3.isDone()),
        () -> assertTrue(isInWatchlist1.get()),
        () -> assertFalse(isInWatchlist2.get()),
        () -> assertTrue(isInWatchlist3.get())
    );
  }

  @Test
  @DisplayName("throws an exception if userId is not equal for every request")
  void dataLoaderThrowsExceptionIfUsersAreDifferent() {
    DataLoader<Integer, Boolean> dataLoader = dataLoaderBuilder.build();

    CompletableFuture<Boolean> isInWatchlist1 = dataLoader.load(ITEM_ID1, 123);
    CompletableFuture<Boolean> isInWatchlist2 = dataLoader.load(ITEM_ID2, 234);

    dataLoader.dispatchAndJoin();

    assertAll(
        () -> assertTrue(isInWatchlist1.isDone()),
        () -> assertTrue(isInWatchlist2.isDone())
    );

    ExecutionException exception1 = assertThrows(ExecutionException.class, isInWatchlist1::get);
    assertTrue(exception1.getCause() instanceof IllegalDataLoaderUsageException);

    ExecutionException exception2 = assertThrows(ExecutionException.class, isInWatchlist2::get);
    assertTrue(exception2.getCause() instanceof IllegalDataLoaderUsageException);
  }

  @Test
  @DisplayName("throws an exception if userId is not present in all the requests")
  void dataLoaderThrowsExceptionIfUserIdIsNotPresent() {
    DataLoader<Integer, Boolean> dataLoader = dataLoaderBuilder.build();

    CompletableFuture<Boolean> isInWatchlist1 = dataLoader.load(ITEM_ID1);
    CompletableFuture<Boolean> isInWatchlist2 = dataLoader.load(ITEM_ID2);

    dataLoader.dispatchAndJoin();

    assertAll(
        () -> assertTrue(isInWatchlist1.isDone()),
        () -> assertTrue(isInWatchlist2.isDone())
    );

    ExecutionException exception1 = assertThrows(ExecutionException.class, isInWatchlist1::get);
    assertTrue(exception1.getCause() instanceof IllegalDataLoaderUsageException);

    ExecutionException exception2 = assertThrows(ExecutionException.class, isInWatchlist2::get);
    assertTrue(exception2.getCause() instanceof IllegalDataLoaderUsageException);
  }
}