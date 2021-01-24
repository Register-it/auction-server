package it.register.edu.auction.service;

import it.register.edu.auction.entity.Token;
import it.register.edu.auction.entity.User;

public interface UserSessionService {

  String COOKIE_NAME = "auction-session";
  String ROLE_AUTHENTICATED = "ROLE_AUTHENTICATED";

  Token issueSessionToken(String username, String password);

  User validateSessionToken(String token);

  void deleteSessionToken(String token);

  void deleteExpiredSessionTokens();

  User createUser(User user);

}
