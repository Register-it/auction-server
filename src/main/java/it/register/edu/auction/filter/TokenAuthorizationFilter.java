package it.register.edu.auction.filter;

import static it.register.edu.auction.service.TokenService.ROLE_AUTHENTICATED;

import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.TokenService;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
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

  private static final String UNAUTHORIZED_MESSAGE = "Unauthorized";
  private static final int UNAUTHORIZED_STATUS = 401;

  private final TokenService tokenService;

  public TokenAuthorizationFilter(AuthenticationManager authManager, TokenService tokenService) {
    super(authManager);
    this.tokenService = tokenService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

    Optional<String> token = getToken(req, TokenService.COOKIE_NAME);
    if (token.isEmpty()) {
      chain.doFilter(req, res);
      return;
    }

    try {
      SecurityContextHolder.getContext().setAuthentication(getAuthentication(token.get()));
    } catch (Exception e) {
      log.debug("Authentication failed", e);
      res.setHeader("Access-Control-Allow-Origin", "*");
      res.setStatus(UNAUTHORIZED_STATUS);
      res.getOutputStream().write(UNAUTHORIZED_MESSAGE.getBytes());
      res.getOutputStream().flush();
      return;
    }

    chain.doFilter(req, res);
  }

  private Authentication getAuthentication(String token) {
    User user = tokenService.validateToken(token);
    return new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(new SimpleGrantedAuthority(ROLE_AUTHENTICATED)));
  }


  private Optional<String> getToken(HttpServletRequest request, String name) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }

    return Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(name))
        .map(Cookie::getValue)
        .findAny();
  }

}
