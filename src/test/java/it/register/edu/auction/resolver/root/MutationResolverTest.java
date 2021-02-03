package it.register.edu.auction.resolver.root;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import graphql.ErrorType;
import graphql.execution.DataFetcherResult;
import graphql.execution.ExecutionPath;
import graphql.execution.ExecutionStepInfo;
import graphql.execution.MergedField;
import graphql.language.Field;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Token;
import it.register.edu.auction.exception.HigherBidExistsException;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.UserSessionService;
import it.register.edu.auction.service.WatchlistService;
import it.register.edu.auction.test.AuthenticationExtension;
import it.register.edu.auction.test.WithAuthenticatedUser;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Sinks.Many;

@ExtendWith(MockitoExtension.class)
@ExtendWith(AuthenticationExtension.class)
@DisplayName("MutationResolver")
class MutationResolverTest {

  private static final String USERNAME = "username";
  private static final String PASSWORD = "password";
  private static final String TOKEN = "token";
  private static final int COOKIE_DURATION = 60;
  private static final int USER_ID = 123;
  private static final int ITEM_ID = 987;
  private static final BigDecimal AMOUNT = new BigDecimal("123.45");

  @Mock
  private HttpServletResponse mockResponse;

  @Mock
  private UserSessionService mockUserSessionService;

  @Mock
  private AuctionService mockAuctionService;

  @Mock
  private WatchlistService mockWatchlistService;

  @Mock
  private Many<Bid> mockBidSink;

  @Mock
  private DataFetchingEnvironment mockEnvironment;

  @InjectMocks
  private MutationResolver resolver;

  @Captor
  private ArgumentCaptor<Cookie> cookieCaptor;

  @Test
  @DisplayName("performs a login by issuing a session token and then injecting it as a response cookie")
  void login() {
    LocalDateTime expiration = LocalDateTime.now().plus(Duration.ofSeconds(COOKIE_DURATION));
    when(mockUserSessionService.issueSessionToken(USERNAME, PASSWORD)).thenReturn(Token.builder().id(TOKEN).expiresAt(expiration).build());

    resolver.login(USERNAME, PASSWORD);

    verify(mockResponse).addCookie(cookieCaptor.capture());
    assertEquals(TOKEN, cookieCaptor.getValue().getValue());
    assertEquals(UserSessionService.COOKIE_NAME, cookieCaptor.getValue().getName());
    assertTrue(cookieCaptor.getValue().getMaxAge() <= COOKIE_DURATION);
  }

  @Test
  @DisplayName("performs a logout by removing the session cookie of the current logged in user")
  @WithAuthenticatedUser(token = TOKEN)
  void logout() {
    resolver.logout();

    verify(mockResponse).addCookie(cookieCaptor.capture());
    assertEquals("", cookieCaptor.getValue().getValue());
    assertEquals(UserSessionService.COOKIE_NAME, cookieCaptor.getValue().getName());
    assertEquals(0, cookieCaptor.getValue().getMaxAge());
  }

  @Test
  @DisplayName("adds an item the watchlist of the current logged in user")
  @WithAuthenticatedUser(id = USER_ID)
  void watch() {
    resolver.watch(ITEM_ID);
    verify(mockWatchlistService).addToWatchlist(USER_ID, ITEM_ID);
  }

  @Test
  @DisplayName("removes an item from the watchlist of the current logged in user")
  @WithAuthenticatedUser(id = USER_ID)
  void unwatch() {
    resolver.unwatch(ITEM_ID);
    verify(mockWatchlistService).removeFromWatchlist(USER_ID, ITEM_ID);
  }

  @Test
  @DisplayName("successfully places a bid and emits an event")
  @WithAuthenticatedUser(id = USER_ID)
  void bidSuccessful() {
    Bid bid = new Bid();
    when(mockAuctionService.bid(USER_ID, ITEM_ID, AMOUNT)).thenReturn(bid);

    DataFetcherResult<Bid> result = resolver.bid(ITEM_ID, AMOUNT, mockEnvironment);
    assertEquals(bid, result.getData());

    verify(mockBidSink).tryEmitNext(bid);
  }

  @Test
  @DisplayName("successfully places a bid and emits an event")
  @WithAuthenticatedUser(id = USER_ID)
  void bidErrorHigherBidExists() {
    ExecutionPath mockPath = mock(ExecutionPath.class);
    SourceLocation mockSourceLocation = mock(SourceLocation.class);
    prepareMockEnvironment(mockPath, mockSourceLocation);

    Bid higherBid = new Bid();
    when(mockAuctionService.bid(USER_ID, ITEM_ID, AMOUNT)).thenThrow(new HigherBidExistsException(higherBid));

    DataFetcherResult<Bid> result = resolver.bid(ITEM_ID, AMOUNT, mockEnvironment);

    assertEquals(higherBid, result.getData());
    assertEquals(1, result.getErrors().size());
    assertEquals(ErrorType.DataFetchingException, result.getErrors().get(0).getErrorType());
    assertEquals("HIGHER_BID_EXISTS", result.getErrors().get(0).getExtensions().get("ERROR_CODE"));
    assertEquals(mockPath, result.getErrors().get(0).getPath().get(0));
    assertEquals(mockSourceLocation, result.getErrors().get(0).getLocations().get(0));

    verify(mockBidSink, times(0)).tryEmitNext(higherBid);
  }

  private void prepareMockEnvironment(ExecutionPath mockPath, SourceLocation mockSourceLocation) {
    ExecutionStepInfo mockExecutionStepInfo = mock(ExecutionStepInfo.class);
    MergedField mockMergedField = mock(MergedField.class);
    Field mockField = mock(Field.class);

    when(mockEnvironment.getExecutionStepInfo()).thenReturn(mockExecutionStepInfo);
    when(mockExecutionStepInfo.getPath()).thenReturn(mockPath);
    when(mockPath.toList()).thenReturn(Collections.singletonList(mockPath));
    when(mockEnvironment.getMergedField()).thenReturn(mockMergedField);
    when(mockMergedField.getSingleField()).thenReturn(mockField);
    when(mockField.getSourceLocation()).thenReturn(mockSourceLocation);
  }
}