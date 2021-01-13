package it.register.edu.auction.service;

import it.register.edu.auction.entity.WatchlistId;

public interface WatchlistService {

  void addToWatchlist(int userId, int itemId);

  void removeFromWatchlist(WatchlistId id);

  boolean isInWatchlist(WatchlistId id);

}
