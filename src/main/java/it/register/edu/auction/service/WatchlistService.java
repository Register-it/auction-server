package it.register.edu.auction.service;

import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.WatchlistId;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface WatchlistService {

  void addToWatchlist(int userId, int itemId);

  void removeFromWatchlist(WatchlistId id);

  Page<Item> getWatchedItems(int userId, Pageable pageable);

  List<Boolean> areInWatchlist(int userId, Collection<Integer> itemIds);
}
