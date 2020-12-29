package it.register.edu.auction.resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class QueryResolver implements GraphQLQueryResolver {

  @Autowired
  private ItemRepository itemRepository;

  public Page<Item> getItems(int page, int size) {
    return Page.of(itemRepository.findAll(PageRequest.of(page, size)));
  }
}
