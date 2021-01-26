package it.register.edu.auction.auth;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import javax.websocket.server.HandshakeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscriptionConnectionListener implements ApolloSubscriptionConnectionListener {

  @Autowired
  private TokenAuthenticationProvider tokenAuthenticationProvider;

  @Override
  public void onConnect(SubscriptionSession session, OperationMessage message) {
    HandshakeRequest request = (HandshakeRequest) session.getUserProperties().get(HandshakeRequest.class.getName());
    tokenAuthenticationProvider.auth(request);
  }

}
