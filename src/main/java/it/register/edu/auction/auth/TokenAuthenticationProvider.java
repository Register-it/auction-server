package it.register.edu.auction.auth;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.CookieUtils.getToken;

import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.UserSessionService;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.HandshakeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationProvider {

  @Autowired
  private UserSessionService userSessionService;

  public void auth(HttpServletRequest req) {
    getToken(req, UserSessionService.COOKIE_NAME).ifPresent(this::auth);
  }

  public void auth(HandshakeRequest req) {
    getToken(req, UserSessionService.COOKIE_NAME).ifPresent(this::auth);
  }

  private void auth(String token) {
    SecurityContextHolder.getContext().setAuthentication(getAuthentication(token));
  }

  private Authentication getAuthentication(String token) {
    try {
      User user = userSessionService.validateSessionToken(token);
      return new UsernamePasswordAuthenticationToken(user, token, Collections.singleton(new SimpleGrantedAuthority(ROLE_AUTHENTICATED)));
    } catch (Exception e) {
      return null;
    }
  }

}
