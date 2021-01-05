package it.register.edu.auction.util;

import it.register.edu.auction.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtils {

  private AuthUtils() {
  }

  public static User getLoggedUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
