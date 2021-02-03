package it.register.edu.auction.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.COOKIE;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.HandshakeRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CookieUtils")
class CookieUtilsTest {

  private static final String NAME = "name";
  private static final String VALUE = "value";
  private static final int DURATION = 60;

  @Mock
  private HttpServletResponse mockResponse;

  @Mock
  private HttpServletRequest mockRequest;

  @Mock
  private HandshakeRequest mockHandshakeRequest;

  @Captor
  private ArgumentCaptor<Cookie> cookieCaptor;

  @BeforeEach
  void setUp() {
    CookieUtils.setSecure(true);
  }

  @Test
  @DisplayName("sets a secure cookie in the given servlet response")
  void setCookieSecure() {
    CookieUtils.setCookie(mockResponse, NAME, VALUE, LocalDateTime.now().plus(Duration.ofSeconds(DURATION)));

    verify(mockResponse).addCookie(cookieCaptor.capture());

    assertEquals(NAME, cookieCaptor.getValue().getName());
    assertEquals(VALUE, cookieCaptor.getValue().getValue());
    assertTrue(cookieCaptor.getValue().getMaxAge() > 0);
    assertTrue(cookieCaptor.getValue().getMaxAge() <= DURATION);
    assertTrue(cookieCaptor.getValue().getSecure());
  }

  @Test
  @DisplayName("sets a not-secure cookie in the given servlet response")
  void setCookieNotSecure() {
    CookieUtils.setSecure(false);

    CookieUtils.setCookie(mockResponse, NAME, VALUE, LocalDateTime.now().plus(Duration.ofSeconds(DURATION)));

    verify(mockResponse).addCookie(cookieCaptor.capture());

    assertEquals(NAME, cookieCaptor.getValue().getName());
    assertEquals(VALUE, cookieCaptor.getValue().getValue());
    assertTrue(cookieCaptor.getValue().getMaxAge() > 0);
    assertTrue(cookieCaptor.getValue().getMaxAge() <= DURATION);
    assertFalse(cookieCaptor.getValue().getSecure());
  }

  @Test
  @DisplayName("sets an expired cookie in the given servlet response")
  void deleteCookie() {
    CookieUtils.deleteCookie(mockResponse, NAME);

    verify(mockResponse).addCookie(cookieCaptor.capture());

    assertEquals(NAME, cookieCaptor.getValue().getName());
    assertEquals("", cookieCaptor.getValue().getValue());
    assertEquals(0, cookieCaptor.getValue().getMaxAge());
  }

  @Test
  @DisplayName("returns an empty object when finding a cookie in a HttpServletRequest that doesn't contain any cookie")
  void getCookieValueFromHttpServletRequestNoCookies() {
    Optional<String> result = CookieUtils.getCookieValue(mockRequest, NAME);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("returns an empty object when finding a cookie in a HttpServletRequest that doesn't contain it")
  void getCookieValueFromHttpServletRequestNotFound() {
    Cookie cookie = new Cookie("anotherName", VALUE);
    when(mockRequest.getCookies()).thenReturn(new Cookie[]{cookie});
    Optional<String> result = CookieUtils.getCookieValue(mockRequest, NAME);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("returns cookie value when finding a cookie in a HttpServletRequest that contains it")
  void getCookieValueFromHttpServletRequestFound() {
    Cookie cookie = new Cookie(NAME, VALUE);
    when(mockRequest.getCookies()).thenReturn(new Cookie[]{cookie});
    Optional<String> result = CookieUtils.getCookieValue(mockRequest, NAME);
    assertTrue(result.isPresent());
    assertEquals(VALUE, result.get());
  }

  @Test
  @DisplayName("returns an empty object when finding a cookie in a HandshakeRequest that doesn't contain any cookie")
  void getCookieValueFromHandshakeRequestNoCookies() {
    when(mockHandshakeRequest.getHeaders()).thenReturn(Collections.emptyMap());
    Optional<String> result = CookieUtils.getCookieValue(mockHandshakeRequest, NAME);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("returns an empty object when finding a cookie in a HandshakeRequest that doesn't contain it")
  void getCookieValueFromHandshakeRequestNotFound() {
    String cookie = "anotherName=" + VALUE;
    when(mockHandshakeRequest.getHeaders()).thenReturn(Collections.singletonMap(COOKIE, Collections.singletonList(cookie)));
    Optional<String> result = CookieUtils.getCookieValue(mockHandshakeRequest, NAME);
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("returns cookie value when finding a cookie in a HandshakeRequest that contains it")
  void getCookieValueFromHandshakeRequestFound() {
    String cookie = NAME + "=" + VALUE;
    when(mockHandshakeRequest.getHeaders()).thenReturn(Collections.singletonMap(COOKIE, Collections.singletonList(cookie)));
    Optional<String> result = CookieUtils.getCookieValue(mockHandshakeRequest, NAME);
    assertTrue(result.isPresent());
    assertEquals(VALUE, result.get());
  }

  @AfterEach
  void tearDown() {
    CookieUtils.setSecure(true);
  }
}