package it.register.edu.auction.rest.controller;

import static it.register.edu.auction.resolver.root.QueryResolver.ITEM_DEFAULT_SORT;
import static it.register.edu.auction.service.UserSessionService.ROLE_AUTHENTICATED;

import io.swagger.annotations.ApiImplicitParam;
import it.register.edu.auction.domain.LimitedPageRequest;
import it.register.edu.auction.entity.Token;
import it.register.edu.auction.rest.domain.Credentials;
import it.register.edu.auction.rest.domain.User;
import it.register.edu.auction.service.AuctionService;
import it.register.edu.auction.service.UserSessionService;
import it.register.edu.auction.service.WatchlistService;
import it.register.edu.auction.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/user")
public class UserController {

  @Autowired
  private AuctionService auctionService;

  @Autowired
  private WatchlistService watchlistService;

  @Autowired
  private UserSessionService userSessionService;

  @Value("${auctions.pagination.max-items}")
  private int maxItems;

  @PostMapping("/session")
  public Token login(@RequestBody Credentials credentials) {
    return userSessionService.issueSessionToken(credentials.getUsername(), credentials.getPassword());
  }

  @GetMapping("/me")
  @ApiImplicitParam(name = "Cookie", value = "Session Cookie", required = true, paramType = "header", dataTypeClass = String.class, example = "auction-session=123123123")
  @Secured(ROLE_AUTHENTICATED)
  public User getLoggedUser() {
    User user = User.fromLoggedUser(AuthUtils.getLoggedUser());

    user.setWatched(watchlistService.getWatchedItems(user.getId(), LimitedPageRequest.of(0, null, maxItems, ITEM_DEFAULT_SORT)).getElements());
    user.setBid(auctionService.getBidItems(user.getId(), LimitedPageRequest.of(0, null, maxItems, ITEM_DEFAULT_SORT)).getElements());
    user.setAwarded(auctionService.getAwardedItems(user.getId(), LimitedPageRequest.of(0, null, maxItems, ITEM_DEFAULT_SORT)).getElements());

    return user;
  }

}
