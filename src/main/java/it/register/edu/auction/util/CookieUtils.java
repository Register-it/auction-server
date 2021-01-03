package it.register.edu.auction.util;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

  private CookieUtils() {
  }

  public static void setCookie(HttpServletResponse response, String name, String value, TemporalAmount duration) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setMaxAge((int) duration.get(ChronoUnit.SECONDS));
    response.addCookie(cookie);
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

}
