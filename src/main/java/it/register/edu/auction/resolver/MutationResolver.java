package it.register.edu.auction.resolver;

import static it.register.edu.auction.service.UserSessionService.COOKIE_NAME;
import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.AuthUtils.getLoggedUser;
import static it.register.edu.auction.util.CookieUtils.setCookie;

import graphql.kickstart.tools.GraphQLMutationResolver;
import it.register.edu.auction.entity.Token;
import it.register.edu.auction.service.UserSessionService;
import it.register.edu.auction.service.WatchlistService;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component
public class MutationResolver implements GraphQLMutationResolver {

  @Autowired
  private HttpServletResponse response;

  @Autowired
  private UserSessionService userSessionService;

  @Autowired
  private WatchlistService watchlistService;

  public void login(String username, String password) {
    Token token = userSessionService.issueSessionToken(username, password);
    setCookie(response, COOKIE_NAME, token.getId(), token.getExpiresAt());
  }

  @Secured(ROLE_AUTHENTICATED)
  public void watch(int itemId) {
    watchlistService.addToWatchlist(getLoggedUser().getId(), itemId);
  }
}
