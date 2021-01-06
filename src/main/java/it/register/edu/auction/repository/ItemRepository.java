package it.register.edu.auction.repository;

import it.register.edu.auction.entity.Item;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

  Optional<Item> findByIdAndAuctionExpirationAfter(int id, LocalDateTime dateTime);

}
