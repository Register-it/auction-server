package it.register.edu.auction.repository;

import it.register.edu.auction.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Integer> {

  Auction findByItemId(Integer itemId);

}
