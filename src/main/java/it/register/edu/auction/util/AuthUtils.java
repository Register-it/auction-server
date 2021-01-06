package it.register.edu.auction.util;

import it.register.edu.auction.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

  private AuthUtils() {
  }

  public static User getLoggedUser() {
    try {
      return (User) getAuthentication().getPrincipal();
    } catch (Exception e) {
      return null;
    }
  }

  public static String getSessionToken() {
    return (String) getAuthentication().getCredentials();
  }

  private static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

}
