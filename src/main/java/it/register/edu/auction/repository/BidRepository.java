package it.register.edu.auction.repository;

import it.register.edu.auction.domain.BidsNumber;
import it.register.edu.auction.entity.Bid;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {

  @Query(value = "SELECT COUNT(1) AS total, ITEM_ID AS itemId FROM BID WHERE ITEM_ID IN (:itemIds) GROUP BY ITEM_ID", nativeQuery = true)
  List<BidsNumber> countByItemIdInGroupByItemId(@Param("itemIds") Collection<Integer> itemIds);

  List<Bid> findByItemId(int itemId);

  Optional<Bid> findFirstByItemIdAndAmountGreaterThanEqual(int itemId, BigDecimal amount);

}
