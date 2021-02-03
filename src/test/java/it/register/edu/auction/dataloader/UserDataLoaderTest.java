package it.register.edu.auction.dataloader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import it.register.edu.auction.entity.User;
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
@DisplayName("UserDataLoader")
class UserDataLoaderTest {

  private static final int USER_ID1 = 1;
  private static final int USER_ID2 = 2;
  private static final int USER_ID3 = 3;
  private static final User USER1 = User.builder().id(USER_ID1).build();
  private static final User USER2 = User.builder().id(USER_ID2).build();
  private static final User USER3 = User.builder().id(USER_ID3).build();

  @Mock
  private AuctionService auctionService;

  @InjectMocks
  private UserDataLoader dataLoaderBuilder;

  @Test
  @DisplayName("creates a data loader that gets a user object for every userId")
  void dataLoader() {
    when(auctionService.getUsers(Set.of(USER_ID1, USER_ID2, USER_ID3))).thenReturn(Map.of(USER_ID1, USER1, USER_ID2, USER2, USER_ID3, USER3));

    DataLoader<Integer, User> dataLoader = dataLoaderBuilder.build();

    CompletableFuture<User> user1 = dataLoader.load(USER_ID1);
    CompletableFuture<User> user2 = dataLoader.load(USER_ID2);
    CompletableFuture<User> user3 = dataLoader.load(USER_ID3);

    dataLoader.dispatchAndJoin();

    assertAll(
        () -> assertTrue(user1.isDone()),
        () -> assertTrue(user2.isDone()),
        () -> assertTrue(user3.isDone()),
        () -> assertEquals(USER1, user1.get()),
        () -> assertEquals(USER2, user2.get()),
        () -> assertEquals(USER3, user3.get())
    );
  }
}