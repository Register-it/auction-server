package it.register.edu.auction.test.integration;

import static it.register.edu.auction.exception.GraphQLDataFetchingException.ERROR_CODE_UNAUTHORIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.COOKIE;

import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestSubscription;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.Token;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.repository.AwardedItemRepository;
import it.register.edu.auction.repository.BidRepository;
import it.register.edu.auction.repository.ImageRepository;
import it.register.edu.auction.repository.ItemRepository;
import it.register.edu.auction.repository.TokenRepository;
import it.register.edu.auction.repository.UserRepository;
import it.register.edu.auction.repository.WatchlistRepository;
import it.register.edu.auction.service.UserSessionService;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("integration")
public abstract class IntegrationTest {

  protected static final int ITEM_ID = 123;
  protected static final String ITEM_TITLE = "Item Title";
  protected static final String ITEM_DESCRIPTION = "Item long description";
  protected static final String ITEM_INITIAL_PRICE_STR = "123.45";
  protected static final BigDecimal ITEM_INITIAL_PRICE = new BigDecimal(ITEM_INITIAL_PRICE_STR);
  protected static final String ITEM_CURRENT_PRICE_STR = "123.45";
  protected static final BigDecimal ITEM_CURRENT_PRICE = new BigDecimal(ITEM_CURRENT_PRICE_STR);
  protected static final String AUCTION_EXPIRATION_STR = "2030-06-13T12:30:00";
  protected static final LocalDateTime AUCTION_EXPIRATION = LocalDateTime.parse(AUCTION_EXPIRATION_STR);
  protected static final int BIDS_NUMBER = 13;
  protected static final String IMAGE_URL_PREFIX = "https://image.url/";
  protected static final int THUMBNAIL_ID1 = 21;
  protected static final int THUMBNAIL_ID2 = 22;
  protected static final int THUMBNAIL_ID3 = 23;
  protected static final int IMAGE_ID1 = 31;
  protected static final int IMAGE_ID2 = 32;
  protected static final int IMAGE_ID3 = 33;
  protected static final int USER_ID = 1313;
  protected static final String AUTH_TOKEN = "authToken";
  protected static final String USER_LAST_NAME = "Last Name";
  protected static final String USER_FIRST_NAME = "First Name";
  protected static final String USERNAME = "username";
  protected static final String USER_PROFILE_IMAGE = "https://image.url/profile";
  protected static final String PASSWORD_HASH = "$2a$10$JnA6ORPHQiFUBrlar26aF.8To/E9towkszxhXc.p9LM4/o/kTrI8m";

  @Autowired
  protected GraphQLTestTemplate graphQLTestTemplate;

  @Autowired
  protected GraphQLTestSubscription graphQLTestSubscription;

  @MockBean
  protected ItemRepository itemRepository;

  @MockBean
  protected BidRepository bidRepository;

  @MockBean
  protected AwardedItemRepository awardedItemRepository;

  @MockBean
  protected ImageRepository imageRepository;

  @MockBean
  protected TokenRepository tokenRepository;

  @MockBean
  protected UserRepository userRepository;

  @MockBean
  protected WatchlistRepository watchlistRepository;

  protected Item getTestItem(int itemId) {
    return Item.builder()
        .id(itemId)
        .title(ITEM_TITLE)
        .description(ITEM_DESCRIPTION)
        .initialPrice(ITEM_INITIAL_PRICE)
        .currentPrice(ITEM_CURRENT_PRICE)
        .auctionExpiration(AUCTION_EXPIRATION)
        .build();
  }

  protected Item getTestItem() {
    return getTestItem(ITEM_ID);
  }

  protected Bid getTestBid(int id) {
    return Bid.builder()
        .id(id)
        .userId(USER_ID)
        .itemId(ITEM_ID)
        .amount(new BigDecimal(100 + id))
        .build();
  }

  protected GraphQLTestTemplate authenticated(GraphQLTestTemplate template) throws MalformedURLException {
    Token token = Token.builder().userId(USER_ID).build();
    when(tokenRepository.findByIdAndExpiresAtAfter(eq(AUTH_TOKEN), any(LocalDateTime.class))).thenReturn(Optional.of(token));
    User user = getTestUser();
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
    return template.withAdditionalHeader(COOKIE, UserSessionService.COOKIE_NAME + "=" + AUTH_TOKEN);
  }

  protected User getTestUser() throws MalformedURLException {
    return User.builder()
        .id(USER_ID)
        .username(USERNAME)
        .password(PASSWORD_HASH)
        .firstName(USER_FIRST_NAME)
        .lastName(USER_LAST_NAME)
        .image(new URL(USER_PROFILE_IMAGE))
        .build();
  }

  protected void assertUnauthorized(GraphQLResponse response) {
    assertGraphQLError(response, ERROR_CODE_UNAUTHORIZED);
  }

  protected void assertGraphQLError(GraphQLResponse response, String error) {
    assertTrue(response.isOk());
    CustomGraphQLError[] errors = response.get("$.errors", CustomGraphQLError[].class);
    assertEquals(1, errors.length);
    assertEquals(error, errors[0].getErrorCode());
  }

  protected List<Item> getTestItemList(int size) {
    return getTestItemList(size, 0);
  }

  protected List<Item> getTestItemList(int size, int idStartFrom) {
    return IntStream.range(idStartFrom, idStartFrom + size).mapToObj(this::getTestItem).collect(Collectors.toList());
  }
}
