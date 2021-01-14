package it.register.edu.auction.service;

import it.register.edu.auction.entity.WatchlistId;
import java.util.Collection;
import java.util.List;

public interface WatchlistService {

  void addToWatchlist(int userId, int itemId);

  void removeFromWatchlist(WatchlistId id);

  List<Boolean> areInWatchlist(int userId, Collection<Integer> itemIds);
}
