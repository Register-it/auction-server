package it.register.edu.auction.test.integration.subscription;

import com.graphql.spring.boot.test.GraphQLResponse;
import it.register.edu.auction.test.integration.IntegrationTest;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("When the user is not logged in")
class SubscriptionNotLoggedTest extends IntegrationTest {

  @Test
  @DisplayName("the 'auctionEvent' subscription returns an authentication error")
  void subscribeNotAuthenticated() throws IOException {
    GraphQLResponse response = graphQLTestSubscription.start("graphql/subscription/auctionEvent.graphql")
        .awaitAndGetNextResponse(1000);
    assertUnauthorized(response);
  }

}
