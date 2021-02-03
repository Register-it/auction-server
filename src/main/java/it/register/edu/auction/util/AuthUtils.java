package it.register.edu.auction.util;

import it.register.edu.auction.entity.User;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

  private AuthUtils() {
  }

  public static User getLoggedUser() {
    return (User) getAuthentication().getPrincipal();
  }

  public static Optional<User> getUserWithRole(String role) {
    return userHasRole(role) ? Optional.of(getLoggedUser()) : Optional.empty();
  }

  public static String getSessionToken() {
    return (String) getAuthentication().getCredentials();
  }

  private static boolean userHasRole(String role) {
    return Optional.ofNullable(getAuthentication())
        .map(auth -> auth.getAuthorities().contains(new SimpleGrantedAuthority(role)))
        .orElse(false);
  }

  private static Authentication getAuthentication() {
    return SecurityContextHolder.getContext().getAuthentication();
  }

}
