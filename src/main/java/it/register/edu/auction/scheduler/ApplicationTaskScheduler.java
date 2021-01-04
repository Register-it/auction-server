package it.register.edu.auction.scheduler;

import it.register.edu.auction.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTaskScheduler {

  private static final long HALF_AN_HOUR = 1000L * 60L * 30L;

  @Autowired
  private UserSessionService userSessionService;

  @Scheduled(fixedRate = HALF_AN_HOUR)
  public void deleteExpiredSessionTokens() {
    userSessionService.deleteExpiredSessionTokens();
  }
}
