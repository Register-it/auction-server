package it.register.edu.auction.auth;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class TokenAuthorizationFilter extends BasicAuthenticationFilter {

  private final TokenAuthenticationProvider tokenAuthenticationProvider;

  public TokenAuthorizationFilter(AuthenticationManager authManager, TokenAuthenticationProvider tokenAuthenticationProvider) {
    super(authManager);
    this.tokenAuthenticationProvider = tokenAuthenticationProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    tokenAuthenticationProvider.auth(req);
    chain.doFilter(req, res);
  }

}
