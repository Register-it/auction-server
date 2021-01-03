package it.register.edu.auction.service;

import it.register.edu.auction.entity.User;

public interface TokenService {

  String COOKIE_NAME = "auction-session";
  String ROLE_AUTHENTICATED = "ROLE_AUTHENTICATED";

  String issueToken(String username, String password);

  User validateToken(String token);

}
