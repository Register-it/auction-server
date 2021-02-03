package it.register.edu.auction.auth;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.COOKIE;

import it.register.edu.auction.entity.User;
import it.register.edu.auction.exception.UnauthorizedException;
import it.register.edu.auction.service.UserSessionService;
import it.register.edu.auction.test.AuthenticationExtension;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.HandshakeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@DisplayName("TokenAuthenticationProvider")
@ExtendWith(MockitoExtension.class)
@ExtendWith(AuthenticationExtension.class)
class TokenAuthenticationProviderTest {

  private static final String TOKEN = "token";
  private static final User USER = new User();

  @Mock
  private UserSessionService mockService;

  @Mock
  private HttpServletRequest mockHttpServletRequest;

  @Mock
  private HandshakeRequest mockHandshakeRequest;

  @InjectMocks
  private TokenAuthenticationProvider provider;

  @Nested
  @DisplayName("when receives an HttpServletRequest")
  class WithHttpServletRequest {

    @Test
    @DisplayName("doesn't authenticate if no token is present")
    void withNoToken() {
      when(mockHttpServletRequest.getCookies()).thenReturn(new Cookie[0]);

      assertIsNotAuthenticated();
      provider.auth(mockHttpServletRequest);
      assertIsNotAuthenticated();
    }

    @Test
    @DisplayName("doesn't authenticate if the request is bearing an invalid token")
    void withInvalidToken() {
      when(mockService.validateSessionToken(TOKEN)).thenThrow(new UnauthorizedException());

      Cookie cookie = new Cookie(UserSessionService.COOKIE_NAME, TOKEN);
      when(mockHttpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});

      assertIsNotAuthenticated();
      provider.auth(mockHttpServletRequest);
      assertIsNotAuthenticated();
    }

    @Test
    @DisplayName("authenticate successfully if the request is bearing a valid token")
    void withValidToken() {
      when(mockService.validateSessionToken(TOKEN)).thenReturn(USER);

      Cookie cookie = new Cookie(UserSessionService.COOKIE_NAME, TOKEN);
      when(mockHttpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});

      assertIsNotAuthenticated();
      provider.auth(mockHttpServletRequest);
      assertIsAuthenticated();
    }
  }

  @Nested
  @DisplayName("when receives an HandshakeRequest")
  class WithHandshakeRequest {

    @Test
    @DisplayName("doesn't authenticate if no token is present")
    void withNoToken() {
      Map<String, List<String>> headers = Collections.emptyMap();
      when(mockHandshakeRequest.getHeaders()).thenReturn(headers);

      assertIsNotAuthenticated();
      provider.auth(mockHandshakeRequest);
      assertIsNotAuthenticated();
    }

    @Test
    @DisplayName("doesn't authenticate if the request is bearing an invalid token")
    void withInvalidToken() {
      when(mockService.validateSessionToken(TOKEN)).thenThrow(new UnauthorizedException());

      Map<String, List<String>> headers = Map.of(COOKIE, List.of(UserSessionService.COOKIE_NAME + "=" + TOKEN));
      when(mockHandshakeRequest.getHeaders()).thenReturn(headers);

      assertIsNotAuthenticated();
      provider.auth(mockHandshakeRequest);
      assertIsNotAuthenticated();
    }

    @Test
    @DisplayName("authenticate successfully if the request is bearing a valid token")
    void withValidToken() {
      when(mockService.validateSessionToken(TOKEN)).thenReturn(USER);

      Map<String, List<String>> headers = Map.of(COOKIE, List.of(UserSessionService.COOKIE_NAME + "=" + TOKEN));
      when(mockHandshakeRequest.getHeaders()).thenReturn(headers);

      assertIsNotAuthenticated();
      provider.auth(mockHandshakeRequest);
      assertIsAuthenticated();
    }
  }

  private void assertIsAuthenticated() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
    assertEquals(USER, auth.getPrincipal());
    assertTrue(auth.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_AUTHENTICATED)));
  }

  private void assertIsNotAuthenticated() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth);
  }

}