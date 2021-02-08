package it.register.edu.auction.test.integration.mutation;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import com.graphql.spring.boot.test.GraphQLResponse;
import it.register.edu.auction.service.UserSessionService;
import it.register.edu.auction.test.integration.IntegrationTest;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

@DisplayName("The 'logout' mutation")
class LogoutMutationTest extends IntegrationTest {

  @Test
  @DisplayName("returns an error if the user is not logged in")
  void logoutNotLogged() throws IOException {
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/logout.graphql");
    assertUnauthorized(response);
  }

  @Test
  @DisplayName("unset the session cookie if successful")
  void logoutSuccessful() throws IOException {
    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/mutation/logout.graphql");

    assertTrue(response.isOk());
    assertNull(response.get("$.data.logout"));
    HttpHeaders headers = response.getRawResponse().getHeaders();
    boolean cookieIsUnset = headers.get(HttpHeaders.SET_COOKIE).stream()
        .map(value -> value.split(";")[0])
        .map(value -> value.split("="))
        .filter(parts -> parts[0].equals(UserSessionService.COOKIE_NAME))
        .allMatch(parts -> parts.length == 1);
    assertTrue(cookieIsUnset);

    verify(tokenRepository).deleteById(AUTH_TOKEN);
  }
}
