package it.register.edu.auction.resolver;

import static it.register.edu.auction.service.TokenService.COOKIE_NAME;
import static it.register.edu.auction.util.CookieUtils.setCookie;

import graphql.kickstart.tools.GraphQLMutationResolver;
import it.register.edu.auction.service.TokenService;
import java.time.Duration;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MutationResolver implements GraphQLMutationResolver {

  @Autowired
  private HttpServletResponse response;

  @Autowired
  private TokenService tokenService;

  @Value("${auctions.session.durationInMinutes}")
  private int sessionDurationInMinutes;

  public void login(String username, String password) {
    String token = tokenService.issueToken(username, password);
    setCookie(response, COOKIE_NAME, token, Duration.ofMinutes(sessionDurationInMinutes));
  }

}
