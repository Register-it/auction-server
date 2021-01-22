package it.register.edu.auction.subscription;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import javax.websocket.server.HandshakeRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionConnectionListener implements ApolloSubscriptionConnectionListener {

  @Override
  public void onConnect(SubscriptionSession session, OperationMessage message) {
    HandshakeRequest request = (HandshakeRequest) session.getUserProperties().get(HandshakeRequest.class.getName());
    SecurityContextHolder.getContext().setAuthentication((UsernamePasswordAuthenticationToken) request.getUserPrincipal());
  }

}
