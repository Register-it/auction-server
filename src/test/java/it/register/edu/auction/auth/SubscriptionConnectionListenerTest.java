package it.register.edu.auction.auth;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import java.util.Map;
import javax.websocket.server.HandshakeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("SubscriptionConnectionListener")
@ExtendWith(MockitoExtension.class)
class SubscriptionConnectionListenerTest {

  @Mock
  private TokenAuthenticationProvider mockAuthProvider;

  @Mock
  private SubscriptionSession mockSession;

  @Mock
  private HandshakeRequest mockHandshakeRequest;

  @InjectMocks
  private SubscriptionConnectionListener listener;

  @Test
  @DisplayName("tries to authenticate with the handshake request of a subscription")
  void onConnect() {
    Map<String, Object> userProps = Map.of(HandshakeRequest.class.getName(), mockHandshakeRequest);
    when(mockSession.getUserProperties()).thenReturn(userProps);

    listener.onConnect(mockSession, null);

    verify(mockAuthProvider).auth(mockHandshakeRequest);
  }
}