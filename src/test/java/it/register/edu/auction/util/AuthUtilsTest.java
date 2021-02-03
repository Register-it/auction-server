package it.register.edu.auction.util;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.register.edu.auction.entity.User;
import it.register.edu.auction.test.AuthenticationExtension;
import it.register.edu.auction.test.WithAuthenticatedUser;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AuthenticationExtension.class)
@DisplayName("AuthUtils")
class AuthUtilsTest {

  private static final int USER_ID = 123;
  private static final String TOKEN = "token";

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("gets authenticated user from security context")
  void getLoggedUser() {
    User result = AuthUtils.getLoggedUser();
    assertEquals(USER_ID, result.getId());
  }

  @Test
  @DisplayName("returns an empty object if there's no authenticated user in the security context")
  void getUserWithRoleNoUser() {
    Optional<User> result = AuthUtils.getUserWithRole(ROLE_AUTHENTICATED);
    assertTrue(result.isEmpty());
  }

  @Test
  @WithAuthenticatedUser
  @DisplayName("returns an empty object if the authenticated user has not the requested role")
  void getUserWithRoleNotFound() {
    Optional<User> result = AuthUtils.getUserWithRole("anotherRole");
    assertTrue(result.isEmpty());
  }

  @Test
  @WithAuthenticatedUser(id = USER_ID)
  @DisplayName("returns the authenticated user if it's got the requested role")
  void getUserWithRoleFound() {
    Optional<User> result = AuthUtils.getUserWithRole(ROLE_AUTHENTICATED);
    assertTrue(result.isPresent());
    assertEquals(USER_ID, result.get().getId());
  }

  @Test
  @WithAuthenticatedUser(token = TOKEN)
  @DisplayName("retrieves the session token from the authenticated user in the security context")
  void getSessionToken() {
    String result = AuthUtils.getSessionToken();
    assertEquals(TOKEN, result);
  }
}