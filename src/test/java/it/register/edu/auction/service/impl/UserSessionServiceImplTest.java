package it.register.edu.auction.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.register.edu.auction.entity.Token;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.exception.InvalidCredentialsException;
import it.register.edu.auction.exception.UnauthorizedException;
import it.register.edu.auction.repository.TokenRepository;
import it.register.edu.auction.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserSessionServiceImpl")
class UserSessionServiceImplTest {

  private static final int SESSION_DURATION = 10;
  private static final String PASSWORD = "password";
  private static final String USERNAME = "username";
  private static final String TOKEN_ID = "token";
  private static final String HASH = "hash";
  private static final int USER_ID = 123;
  private static final Token TOKEN = Token.builder().id(TOKEN_ID).userId(USER_ID).build();
  private static final User USER = User.builder().id(USER_ID).password(HASH).build();

  @Mock
  private UserRepository mockUserRepository;

  @Mock
  private TokenRepository mockTokenRepository;

  @Mock
  private PasswordEncoder mockPasswordEncoder;

  @InjectMocks
  private UserSessionServiceImpl service;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(service, "sessionDurationInMinutes", SESSION_DURATION);
  }

  @Test
  @DisplayName("successfully authenticates a user and issue a session token")
  void issueSessionTokenSuccessful() {
    when(mockUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(USER));
    when(mockPasswordEncoder.matches(PASSWORD, HASH)).thenReturn(true);
    when(mockTokenRepository.save(any(Token.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

    Token result = service.issueSessionToken(USERNAME, PASSWORD);

    assertNotNull(result.getId());
    assertEquals(USER_ID, result.getUserId());
    assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));
    assertFalse(result.getExpiresAt().isAfter(LocalDateTime.now().plus(Duration.ofMinutes(SESSION_DURATION))));
  }

  @Test
  @DisplayName("throws an exception if it can't find the user during authentication")
  void issueSessionTokenUserNotFound() {
    when(mockUserRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());
    assertThrows(InvalidCredentialsException.class, () -> service.issueSessionToken(USERNAME, PASSWORD));
    verify(mockTokenRepository, times(0)).save(any(Token.class));
  }

  @Test
  @DisplayName("throws an exception on password mismatch during authentication")
  void issueSessionTokenPasswordMismatch() {
    when(mockUserRepository.findByUsername(USERNAME)).thenReturn(Optional.of(USER));
    when(mockPasswordEncoder.matches(PASSWORD, HASH)).thenReturn(false);
    assertThrows(InvalidCredentialsException.class, () -> service.issueSessionToken(USERNAME, PASSWORD));
    verify(mockTokenRepository, times(0)).save(any(Token.class));
  }

  @Test
  @DisplayName("returns a user on successful validation of a session token")
  void validateSessionTokenSuccessful() {
    when(mockTokenRepository.findByIdAndExpiresAtAfter(eq(TOKEN_ID), any(LocalDateTime.class))).thenReturn(Optional.of(TOKEN));
    when(mockUserRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
    User result = service.validateSessionToken(TOKEN_ID);
    assertEquals(USER, result);
  }

  @Test
  @DisplayName("throws an exception on unsuccessful validation of a session token")
  void validateSessionTokenUnsuccessful() {
    when(mockTokenRepository.findByIdAndExpiresAtAfter(eq(TOKEN_ID), any(LocalDateTime.class))).thenReturn(Optional.empty());
    assertThrows(UnauthorizedException.class, () -> service.validateSessionToken(TOKEN_ID));
  }

  @Test
  @DisplayName("deletes the given token")
  void deleteSessionToken() {
    service.deleteSessionToken(TOKEN_ID);
    verify(mockTokenRepository).deleteById(TOKEN_ID);
  }

  @Test
  @DisplayName("deletes all expired session tokens")
  void deleteExpiredSessionTokens() {
    service.deleteExpiredSessionTokens();
    verify(mockTokenRepository).deleteByExpiresAtBefore(any(LocalDateTime.class));
  }

  @Test
  @DisplayName("creates a new user")
  void createUser() {
    User user = User.builder().password(PASSWORD).build();
    when(mockPasswordEncoder.encode(PASSWORD)).thenReturn(HASH);

    service.createUser(user);

    verify(mockUserRepository).save(user);
    assertEquals(HASH, user.getPassword());
  }
}