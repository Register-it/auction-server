package it.register.edu.auction.service.impl;

import it.register.edu.auction.entity.WatchlistEntry;
import it.register.edu.auction.entity.WatchlistId;
import it.register.edu.auction.repository.WatchlistRepository;
import it.register.edu.auction.service.WatchlistService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
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
  public List<Boolean> areInWatchlist(int userId, Collection<Integer> itemIds) {
    List<Integer> watched = watchlistRepository.findByUserIdAndItemIdIn(userId, itemIds)
        .stream()
        .map(WatchlistEntry::getItemId)
        .collect(Collectors.toList());

    return itemIds.stream()
        .map(watched::contains)
        .collect(Collectors.toList());
  }

}
