package it.register.edu.auction.repository;

import it.register.edu.auction.entity.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistEntry, Integer> {

}
