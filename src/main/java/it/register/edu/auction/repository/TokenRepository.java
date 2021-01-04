package it.register.edu.auction.repository;

import it.register.edu.auction.entity.Token;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {

  void deleteByExpiresAtBefore(LocalDateTime dateTime);

  Optional<Token> findByIdAndExpiresAtAfter(String id, LocalDateTime dateTime);

}
