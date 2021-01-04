package it.register.edu.auction.resolver;

import static it.register.edu.auction.service.UserSessionService.COOKIE_NAME;
import static it.register.edu.auction.util.CookieUtils.setCookie;

import graphql.kickstart.tools.GraphQLMutationResolver;
import it.register.edu.auction.entity.Token;
import it.register.edu.auction.service.UserSessionService;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MutationResolver implements GraphQLMutationResolver {

  @Autowired
  private HttpServletResponse response;

  @Autowired
  private UserSessionService userSessionService;

  public void login(String username, String password) {
    Token token = userSessionService.issueSessionToken(username, password);
    setCookie(response, COOKIE_NAME, token.getId(), token.getExpiresAt());
  }

}
