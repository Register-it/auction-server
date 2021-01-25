package it.register.edu.auction.subscription;

import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static org.springframework.http.HttpHeaders.COOKIE;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.service.UserSessionService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.websocket.server.HandshakeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscriptionConnectionListener implements ApolloSubscriptionConnectionListener {

  @Autowired
  private UserSessionService userSessionService;

  @Override
  public void onConnect(SubscriptionSession session, OperationMessage message) {
    HandshakeRequest request = (HandshakeRequest) session.getUserProperties().get(HandshakeRequest.class.getName());
    List<String> cookieHeaders = request.getHeaders().get(COOKIE);
    Map<String, String> cookies = cookieHeaders.stream()
        .flatMap(cookie -> Arrays.stream(cookie.split(";")))
        .map(cookie -> cookie.split("="))
        .collect(Collectors.toMap(parts -> parts[0].trim(), parts -> parts[1].trim()));

    if (cookies.containsKey(UserSessionService.COOKIE_NAME)) {
      String token = cookies.get(UserSessionService.COOKIE_NAME);
      try {
        User user = userSessionService.validateSessionToken(token);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, token,
            Collections.singleton(new SimpleGrantedAuthority(ROLE_AUTHENTICATED)));
        SecurityContextHolder.getContext().setAuthentication(auth);
      } catch (Exception e) {
        log.error("Cannot authenticate with token {}", token);
      }
    }
  }

}
