package it.register.edu.auction.resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import it.register.edu.auction.domain.LimitedPageRequest;
import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.repository.ItemRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class QueryResolver implements GraphQLQueryResolver {

  @Autowired
  private ItemRepository itemRepository;

  @Value("${auctions.pagination.max-items}")
  private int maxPageSize;

  public Page<Item> getItems(int page, int size) {
    return Page.of(itemRepository.findAll(LimitedPageRequest.of(page, size, maxPageSize)));
  }

  public Optional<Item> getItem(int id) {
    return itemRepository.findById(id);
  }

}
