package it.register.edu.auction.resolver;

import static it.register.edu.auction.service.TokenService.COOKIE_NAME;

import graphql.kickstart.tools.GraphQLMutationResolver;
import it.register.edu.auction.service.TokenService;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import javax.servlet.http.Cookie;
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
    setCookie(COOKIE_NAME, token, Duration.ofMinutes(sessionDurationInMinutes));
  }

  private void setCookie(String name, String value, TemporalAmount duration) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setMaxAge((int) duration.get(ChronoUnit.SECONDS));
    response.addCookie(cookie);
  }

}
