package it.register.edu.auction.resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import it.register.edu.auction.domain.LimitedPageRequest;
import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.repository.BidRepository;
import it.register.edu.auction.repository.ItemRepository;
import it.register.edu.auction.repository.UserRepository;
import it.register.edu.auction.service.TokenService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class QueryResolver implements GraphQLQueryResolver {

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private UserRepository userRepository;

  @Value("${auctions.pagination.max-items}")
  private int maxPageSize;

  public Page<Item> getItems(int page, int size) {
    return Page.of(itemRepository.findAll(LimitedPageRequest.of(page, size, maxPageSize)));
  }

  public Optional<Item> getItem(int id) {
    return itemRepository.findById(id);
  }

  public List<Bid> getBids(int itemId) {
    return bidRepository.findByItemId(itemId);
  }

  @Secured(TokenService.ROLE_AUTHENTICATED)
  public User me() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
