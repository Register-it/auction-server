package it.register.edu.auction.service.impl;

import it.register.edu.auction.entity.WatchlistEntry;
import it.register.edu.auction.entity.WatchlistId;
import it.register.edu.auction.repository.WatchlistRepository;
import it.register.edu.auction.service.WatchlistService;
import javax.transaction.Transactional;
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
  @Transactional
  public void removeFromWatchlist(WatchlistId id) {
    watchlistRepository.deleteById(id);
  }

  @Override
  public boolean isInWatchlist(WatchlistId id) {
    return watchlistRepository.findById(id).isPresent();
  }

}
