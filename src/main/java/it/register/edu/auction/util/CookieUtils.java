package it.register.edu.auction.util;

import static org.springframework.http.HttpHeaders.COOKIE;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.HandshakeRequest;

public class CookieUtils {

  private static final String COOKIE_SEPARATOR = ";";
  private static final String NAME_VALUE_SEPARATOR = "=";
  private static boolean secure = true;

  private CookieUtils() {
  }

  public static void setSecure(boolean secure) {
    CookieUtils.secure = secure;
  }

  public static void setCookie(HttpServletResponse response, String name, String value, LocalDateTime expiresAt) {
    setCookie(response, name, value, (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), expiresAt));
  }

  public static void deleteCookie(HttpServletResponse response, String name) {
    setCookie(response, name, "", 0);
  }

  public static Optional<String> getCookieValue(HttpServletRequest request, String name) {
    if (request.getCookies() == null) {
      return Optional.empty();
    }

    return getCookieValue(Arrays.asList(request.getCookies()), name);
  }

  public static Optional<String> getCookieValue(HandshakeRequest request, String name) {
    List<Cookie> cookies = Optional.ofNullable(request.getHeaders().get(COOKIE)).orElse(Collections.emptyList())
        .stream()
        .flatMap(cookie -> Arrays.stream(cookie.split(COOKIE_SEPARATOR)))
        .map(cookie -> cookie.split(NAME_VALUE_SEPARATOR))
        .map(cookieParts -> new Cookie(cookieParts[0].trim(), cookieParts[1].trim()))
        .collect(Collectors.toList());

    return getCookieValue(cookies, name);
  }

  private static Optional<String> getCookieValue(List<Cookie> cookies, String name) {
    return cookies.stream()
        .filter(cookie -> cookie.getName().equals(name))
        .map(Cookie::getValue)
        .findAny();
  }

  private static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    cookie.setSecure(secure);
    response.addCookie(cookie);
  }

}
