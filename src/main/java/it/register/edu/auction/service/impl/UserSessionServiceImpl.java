package it.register.edu.auction.service.impl;

import it.register.edu.auction.entity.Token;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.exception.InvalidCredentialsException;
import it.register.edu.auction.exception.UnauthorizedException;
import it.register.edu.auction.repository.TokenRepository;
import it.register.edu.auction.repository.UserRepository;
import it.register.edu.auction.service.UserSessionService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserSessionServiceImpl implements UserSessionService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Value("${auctions.session.durationInMinutes}")
  private int sessionDurationInMinutes;

  @Override
  public Token issueSessionToken(String username, String password) {
    User user = userRepository.findByUsername(username).orElseThrow(InvalidCredentialsException::new);
    validatePassword(password, user.getPassword());
    return tokenRepository.save(Token.builder().id(generateToken()).userId(user.getId()).expiresAt(getTokenExpiration()).build());
  }

  @Override
  public User validateSessionToken(String token) {
    return tokenRepository.findByIdAndExpiresAtAfter(token, LocalDateTime.now())
        .flatMap(userToken -> userRepository.findById(userToken.getUserId()))
        .orElseThrow(UnauthorizedException::new);
  }

  @Override
  @Transactional
  public void deleteSessionToken(String token) {
    tokenRepository.deleteById(token);
  }

  @Override
  @Transactional
  public void deleteExpiredSessionTokens() {
    tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
  }

  private void validatePassword(String password, String hash) {
    if (!passwordEncoder.matches(password, hash)) {
      throw new InvalidCredentialsException();
    }
  }

  private String generateToken() {
    return UUID.randomUUID().toString();
  }

  private LocalDateTime getTokenExpiration() {
    return LocalDateTime.now().plus(Duration.ofMinutes(sessionDurationInMinutes));
  }

}
