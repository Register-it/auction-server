package it.register.edu.auction.filter;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.CookieUtils.getToken;

import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.UserSessionService;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Slf4j
public class TokenAuthorizationFilter extends BasicAuthenticationFilter {

  private final UserSessionService userSessionService;

  public TokenAuthorizationFilter(AuthenticationManager authManager, UserSessionService userSessionService) {
    super(authManager);
    this.userSessionService = userSessionService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

    getToken(req, UserSessionService.COOKIE_NAME)
        .ifPresent(s -> SecurityContextHolder.getContext().setAuthentication(getAuthentication(s)));

    chain.doFilter(req, res);
  }

  private Authentication getAuthentication(String token) {
    try {
      User user = userSessionService.validateSessionToken(token);
      return new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(new SimpleGrantedAuthority(ROLE_AUTHENTICATED)));
    } catch (Exception e) {
      return null;
    }
  }

}
