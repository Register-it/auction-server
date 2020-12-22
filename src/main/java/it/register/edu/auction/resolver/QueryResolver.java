package it.register.edu.auction.resolver;

import graphql.kickstart.tools.GraphQLQueryResolver;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.repository.ItemRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryResolver implements GraphQLQueryResolver {

  @Autowired
  private ItemRepository itemRepository;

  public List<Item> getItems() {
    return itemRepository.findAll();
  }
}
