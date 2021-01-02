package it.register.edu.auction.repository;

import it.register.edu.auction.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

  Integer countByItemId(Integer itemId);

  List<Bid> findByItemId(int itemId);
}
