package it.register.edu.auction.scheduler;

import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTaskScheduler {

  @Autowired
  private UserSessionService userSessionService;

  @Autowired
  private AuctionService auctionService;

  @Scheduled(fixedRateString = "${scheduler.fixed-rate.delete-expired-tokens}")
  public void deleteExpiredSessionTokens() {
    userSessionService.deleteExpiredSessionTokens();
  }

  @Scheduled(fixedRateString = "${scheduler.fixed-rate.conclude-expired-auctions}")
  public void concludeExpiredAuctions() {
    auctionService.concludeExpiredAuctions();
  }

}
