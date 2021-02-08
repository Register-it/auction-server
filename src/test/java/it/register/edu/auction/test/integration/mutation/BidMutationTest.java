package it.register.edu.auction.test.integration.mutation;

import static it.register.edu.auction.exception.GraphQLDataFetchingException.ERROR_CODE_AUCTION_EXPIRED;
import static it.register.edu.auction.exception.GraphQLDataFetchingException.ERROR_CODE_HIGHER_BID_EXISTS;
import static it.register.edu.auction.exception.GraphQLDataFetchingException.ERROR_CODE_INVALID_BID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.graphql.spring.boot.test.GraphQLResponse;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.test.integration.IntegrationTest;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@DisplayName("The 'bid' mutation")
class BidMutationTest extends IntegrationTest {

  @Captor
  private ArgumentCaptor<Item> itemCaptor;

  @Test
  @DisplayName("returns an error if the user is not logged in")
  void bidNotLogged() throws IOException {
    GraphQLResponse response = graphQLTestTemplate.postForResource("graphql/mutation/bid.graphql");
    assertUnauthorized(response);
  }

  @Test
  @DisplayName("successfully makes a bid on the given item and updates the item price")
  void bidSuccessful() throws IOException {
    BigDecimal amount = new BigDecimal("234.56");
    Item item = getTestItem();
    when(itemRepository.findByIdAndAuctionExpirationAfter(eq(ITEM_ID), any(LocalDateTime.class))).thenReturn(Optional.of(item));
    when(bidRepository.findFirstByItemIdAndAmountGreaterThanEqual(eq(ITEM_ID), refEq(amount))).thenReturn(Optional.empty());
    when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArgument(0));
    when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(item));

    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/mutation/bid.graphql");

    assertTrue(response.isOk());
    assertEquals(0, amount.compareTo(response.get("$.data.bid.amount", BigDecimal.class)));

    verify(itemRepository).save(itemCaptor.capture());
    assertEquals(amount, itemCaptor.getValue().getCurrentPrice());
  }

  @Test
  @DisplayName("returns an error if the auction is already expired")
  void bidAuctionExpired() throws IOException {
    when(itemRepository.findByIdAndAuctionExpirationAfter(eq(ITEM_ID), any(LocalDateTime.class))).thenReturn(Optional.empty());
    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/mutation/bid.graphql");
    assertGraphQLError(response, ERROR_CODE_AUCTION_EXPIRED);
  }

  @Test
  @DisplayName("returns an error if the bid amount is less than the item initial price")
  void bidInvalid() throws IOException {
    Item item = getTestItem();
    item.setInitialPrice(new BigDecimal("235"));
    when(itemRepository.findByIdAndAuctionExpirationAfter(eq(ITEM_ID), any(LocalDateTime.class))).thenReturn(Optional.of(item));

    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/mutation/bid.graphql");
    assertGraphQLError(response, ERROR_CODE_INVALID_BID);
  }

  @Test
  @DisplayName("returns an error if a greater bid exists for the same item")
  void bidRefused() throws IOException {
    BigDecimal amount = new BigDecimal("234.56");
    Item item = getTestItem();
    BigDecimal currentPrice = new BigDecimal("235");
    Bid higher = Bid.builder().amount(currentPrice).build();
    when(itemRepository.findByIdAndAuctionExpirationAfter(eq(ITEM_ID), any(LocalDateTime.class))).thenReturn(Optional.of(item));
    when(bidRepository.findFirstByItemIdAndAmountGreaterThanEqual(eq(ITEM_ID), refEq(amount))).thenReturn(Optional.of(higher));

    GraphQLResponse response = authenticated(graphQLTestTemplate).postForResource("graphql/mutation/bid.graphql");
    assertEquals(0, currentPrice.compareTo(response.get("$.data.bid.amount", BigDecimal.class)));
    assertGraphQLError(response, ERROR_CODE_HIGHER_BID_EXISTS);
  }

}
