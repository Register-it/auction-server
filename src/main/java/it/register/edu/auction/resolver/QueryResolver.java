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

    private static final int MAX_REQUEST_SIZE = 150;
    @Autowired
    private ItemRepository itemRepository;

    public Page<Item> getItems(int page, int size) {
        return Page.of(itemRepository.findAll(PageRequest.of(page, checkSize(size))));
    }

    public Item getItem(int id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("not found"));
    }


    private int checkSize(int size) {
        if (size > MAX_REQUEST_SIZE) {
            size = MAX_REQUEST_SIZE;
        }
        return size;
    }
}
