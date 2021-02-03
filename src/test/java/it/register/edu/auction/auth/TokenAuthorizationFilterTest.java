package it.register.edu.auction.auth;

import static org.mockito.Mockito.verify;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenAuthorizationFilter")
class TokenAuthorizationFilterTest {

  @Mock
  private AuthenticationManager mockAuthManager;

  @Mock
  private TokenAuthenticationProvider mockAuthProvider;

  @Mock
  private HttpServletRequest mockHttpServletRequest;

  @Mock
  private HttpServletResponse mockHttpServletResponse;

  @Mock
  private FilterChain mockFilterChain;

  @InjectMocks
  private TokenAuthorizationFilter filter;

  @Test
  @DisplayName("invokes the authentication provider, then proceeds")
  void doFilterInternal() throws IOException, ServletException {
    filter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);
    verify(mockAuthProvider).auth(mockHttpServletRequest);
    verify(mockFilterChain).doFilter(mockHttpServletRequest, mockHttpServletResponse);
  }
}