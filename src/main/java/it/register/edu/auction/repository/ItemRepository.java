package it.register.edu.auction.repository;

import it.register.edu.auction.entity.Item;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

  Optional<Item> findByIdAndAuctionExpirationAfter(int id, LocalDateTime dateTime);

  @Query("SELECT i FROM Item i WHERE i.id IN (SELECT w.itemId FROM WatchlistEntry w WHERE w.userId = :userId)")
  Page<Item> findWatchedByUser(@Param("userId") int userId, Pageable pageable);

  @Query("SELECT i FROM Item i WHERE i.id IN (SELECT DISTINCT b.itemId FROM Bid b WHERE b.userId = :userId)")
  Page<Item> findBiddedByUser(@Param("userId") int userId, Pageable pageable);

  @Query("SELECT i FROM Item i WHERE i.id IN (SELECT a.itemId FROM AwardedItem a WHERE a.userId = :userId)")
  Page<Item> findAwardedByUser(@Param("userId") int userId, Pageable pageable);

}
