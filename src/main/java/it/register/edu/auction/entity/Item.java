package it.register.edu.auction.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Item {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "TITLE", nullable = false)
  private String title;

  @Column(name = "DESCRIPTION", nullable = false)
  private String description;

  @Column(name = "AUCTION_EXPIRATION", nullable = false)
  private LocalDateTime auctionExpiration;

  @Column(name = "CURRENT_PRICE", nullable = false)
  private BigDecimal currentPrice;

  @Column(name = "INITIAL_PRICE", nullable = false)
  private BigDecimal initialPrice;

}
