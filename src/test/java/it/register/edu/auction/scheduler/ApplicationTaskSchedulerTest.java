package it.register.edu.auction.scheduler;

import static org.mockito.Mockito.verify;

import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.UserSessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationTaskScheduler")
class ApplicationTaskSchedulerTest {

  @Mock
  private UserSessionService mockUserSessionService;

  @Mock
  private AuctionService mockAuctionService;

  @InjectMocks
  private ApplicationTaskScheduler scheduler;

  @Test
  @DisplayName("has a task to delete expired session tokens")
  void deleteExpiredSessionTokens() {
    scheduler.deleteExpiredSessionTokens();
    verify(mockUserSessionService).deleteExpiredSessionTokens();
  }

  @Test
  @DisplayName("has a task to conclude expired auctions")
  void concludeExpiredAuctions() {
    scheduler.concludeExpiredAuctions();
    verify(mockAuctionService).concludeExpiredAuctions();
  }
}