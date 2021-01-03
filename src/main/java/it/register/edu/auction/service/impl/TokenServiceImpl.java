package it.register.edu.auction.service.impl;

import it.register.edu.auction.entity.Token;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.exception.UnauthorizedException;
import it.register.edu.auction.repository.TokenRepository;
import it.register.edu.auction.repository.UserRepository;
import it.register.edu.auction.service.TokenService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Override
  public String issueToken(String username, String password) {
    User user = userRepository.findByUsername(username).orElseThrow(UnauthorizedException::new);
    validatePassword(password, user.getPassword());
    String token = generateToken();
    // TODO delete token after expiration
    tokenRepository.save(Token.builder().id(token).userId(user.getId()).build());
    return token;
  }

  @Override
  public User validateToken(String token) {
    Token userToken = tokenRepository.findById(token).orElseThrow(UnauthorizedException::new);
    return userRepository.findById(userToken.getUserId()).orElseThrow(UnauthorizedException::new);
  }

  private void validatePassword(String password, String hash) {
    if (!passwordEncoder.matches(password, hash)) {
      throw new UnauthorizedException();
    }
  }

  private String generateToken() {
    return UUID.randomUUID().toString();
  }

}
