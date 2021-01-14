package it.register.edu.auction.repository;

import it.register.edu.auction.entity.WatchlistEntry;
import it.register.edu.auction.entity.WatchlistId;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistEntry, WatchlistId> {

  List<WatchlistEntry> findByUserIdAndItemIdIn(int userId, Collection<Integer> itemIds);

}
