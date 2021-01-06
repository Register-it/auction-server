package it.register.edu.auction.repository;

import it.register.edu.auction.entity.Bid;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

  Integer countByItemId(Integer itemId);

  List<Bid> findByItemId(int itemId);

}
