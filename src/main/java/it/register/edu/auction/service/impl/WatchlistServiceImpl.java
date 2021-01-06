package it.register.edu.auction.service.impl;

import it.register.edu.auction.entity.WatchlistEntry;
import it.register.edu.auction.repository.WatchlistRepository;
import it.register.edu.auction.service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WatchlistServiceImpl implements WatchlistService {

  @Autowired
  private WatchlistRepository watchlistRepository;

  @Override
  public void addToWatchlist(int userId, int itemId) {
    watchlistRepository.save(WatchlistEntry.builder().userId(userId).itemId(itemId).build());
  }

  @Override
  public boolean isInWatchlist(int userId, int itemId) {
    return watchlistRepository.findByUserIdAndItemId(userId, itemId).isPresent();
  }

}
