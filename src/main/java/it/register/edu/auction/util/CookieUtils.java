package it.register.edu.auction.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

  private CookieUtils() {
  }

  public static void setCookie(HttpServletResponse response, String name, String value, LocalDateTime expiresAt) {
    setCookie(response, name, value, (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), expiresAt));
  }

  public static void deleteCookie(HttpServletResponse response, String name) {
    setCookie(response, name, "", 0);
  }

  public static Optional<String> getToken(HttpServletRequest request, String name) {

    if (request.getCookies() == null) {
      return Optional.empty();
    }

    return Arrays.stream(request.getCookies())
        .filter(cookie -> cookie.getName().equals(name))
        .map(Cookie::getValue)
        .findAny();
  }

  private static void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    cookie.setSecure(true);
    response.addCookie(cookie);
  }

}
