package it.register.edu.auction.service;

public interface WatchlistService {

  void addToWatchlist(int userId, int itemId);

  void removeFromWatchlist(int userId, int itemId);

  boolean isInWatchlist(int userId, int itemId);

}
