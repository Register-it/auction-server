package it.register.edu.auction.resolver;

import static it.register.edu.auction.service.UserSessionService.COOKIE_NAME;
import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;
import static it.register.edu.auction.util.AuthUtils.getLoggedUser;
import static it.register.edu.auction.util.AuthUtils.getSessionToken;
import static it.register.edu.auction.util.CookieUtils.deleteCookie;
import static it.register.edu.auction.util.CookieUtils.setCookie;

import graphql.ExceptionWhileDataFetching;
import graphql.execution.DataFetcherResult;
import graphql.execution.DataFetcherResult.Builder;
import graphql.execution.ExecutionPath;
import graphql.kickstart.tools.GraphQLMutationResolver;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Token;
import it.register.edu.auction.entity.WatchlistId;
import it.register.edu.auction.exception.GraphQLDataFetchingException;
import it.register.edu.auction.exception.HigherBidExistsException;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.UserSessionService;
import it.register.edu.auction.service.WatchlistService;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

@Component
public class MutationResolver implements GraphQLMutationResolver {

  @Autowired
  private HttpServletResponse response;

  @Autowired
  private UserSessionService userSessionService;

  @Autowired
  private AuctionService auctionService;

  @Autowired
  private WatchlistService watchlistService;

  public void login(String username, String password) {
    Token token = userSessionService.issueSessionToken(username, password);
    setCookie(response, COOKIE_NAME, token.getId(), token.getExpiresAt());
  }

  @Secured(ROLE_AUTHENTICATED)
  public void logout() {
    userSessionService.deleteSessionToken(getSessionToken());
    deleteCookie(response, COOKIE_NAME);
  }

  @Secured(ROLE_AUTHENTICATED)
  public void watch(int itemId) {
    watchlistService.addToWatchlist(getLoggedUser().getId(), itemId);
  }

  @Secured(ROLE_AUTHENTICATED)
  public void unwatch(int itemId) {
    watchlistService.removeFromWatchlist(new WatchlistId(getLoggedUser().getId(), itemId));
  }

  @Secured(ROLE_AUTHENTICATED)
  public DataFetcherResult<Bid> bid(int itemId, BigDecimal amount, DataFetchingEnvironment env) {
    try {
      return result(auctionService.bid(getLoggedUser().getId(), itemId, amount));
    } catch (HigherBidExistsException e) {
      return result(e.getBid(), e, env);
    }
  }

  private DataFetcherResult<Bid> result(Bid data) {
    return result(data, null, null);
  }

  private DataFetcherResult<Bid> result(Bid data, GraphQLDataFetchingException error, DataFetchingEnvironment env) {
    Builder<Bid> resultBuilder = DataFetcherResult.<Bid>newResult().data(data);
    if (error != null) {
      resultBuilder.error(wrapError(error, env));
    }
    return resultBuilder.build();
  }

  private ExceptionWhileDataFetching wrapError(GraphQLDataFetchingException error, DataFetchingEnvironment env) {
    ExecutionPath path = env.getExecutionStepInfo().getPath();
    SourceLocation sourceLocation = env.getMergedField().getSingleField().getSourceLocation();
    return new ExceptionWhileDataFetching(path, error, sourceLocation);
  }

}
