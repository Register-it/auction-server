package it.register.edu.auction.test.integration.mutation;

import static it.register.edu.auction.exception.GraphQLDataFetchingException.ERROR_CODE_INVALID_CREDENTIALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.graphql.spring.boot.test.GraphQLResponse;
import it.register.edu.auction.entity.Token;
import it.register.edu.auction.service.UserSessionService;
import it.register.edu.auction.test.integration.IntegrationTest;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.http.HttpHeaders;

@DisplayName("The 'login' mutation")
class LoginMutationTest extends IntegrationTest {

  @Captor
  private ArgumentCaptor<Token> tokenCaptor;

  @Test
  @DisplayName("returns an error if the username doesn't correspond to any user")
  void loginUserNotFound() throws IOException {
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/login.graphql");
    assertGraphQLError(response, ERROR_CODE_INVALID_CREDENTIALS);
  }

  @Test
  @DisplayName("returns an error if the password mismatch")
  void loginPasswordMismatch() throws IOException {
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(getTestUser()));
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/loginWrongPassword.graphql");
    assertGraphQLError(response, ERROR_CODE_INVALID_CREDENTIALS);
  }

  @Test
  @DisplayName("sets a session cookie if credentials are valid")
  void loginSuccess() throws IOException {
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(getTestUser()));
    when(tokenRepository.save(any(Token.class))).thenAnswer(i -> i.getArgument(0));

    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/login.graphql");
    assertTrue(response.isOk());
    assertNull(response.get("$.data.login"));
    HttpHeaders headers = response.getRawResponse().getHeaders();
    String sessionCookieValue = headers.get(HttpHeaders.SET_COOKIE).stream()
        .map(value -> value.split(";")[0])
        .map(value -> value.split("="))
        .filter(parts -> parts[0].equals(UserSessionService.COOKIE_NAME))
        .map(parts -> parts[1])
        .findAny()
        .orElseThrow(RuntimeException::new);

    verify(tokenRepository).save(tokenCaptor.capture());
    assertEquals(sessionCookieValue, tokenCaptor.getValue().getId());
    assertEquals(USER_ID, tokenCaptor.getValue().getUserId());
  }

}
