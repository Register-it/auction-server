package it.register.edu.auction.repository;

import it.register.edu.auction.entity.AwardedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AwardedItemRepository extends JpaRepository<AwardedItem, Integer> {

}
