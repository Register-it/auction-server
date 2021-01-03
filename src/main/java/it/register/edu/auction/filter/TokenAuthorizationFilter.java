package it.register.edu.auction.filter;

import static it.register.edu.auction.service.TokenService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.CookieUtils.getToken;

import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.TokenService;
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

  private final TokenService tokenService;

  public TokenAuthorizationFilter(AuthenticationManager authManager, TokenService tokenService) {
    super(authManager);
    this.tokenService = tokenService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

    getToken(req, TokenService.COOKIE_NAME)
        .ifPresent(s -> SecurityContextHolder.getContext().setAuthentication(getAuthentication(s)));

    chain.doFilter(req, res);
  }

  private Authentication getAuthentication(String token) {
    try {
      User user = tokenService.validateToken(token);
      return new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(new SimpleGrantedAuthority(ROLE_AUTHENTICATED)));
    } catch (Exception e) {
      return null;
    }
  }

}
