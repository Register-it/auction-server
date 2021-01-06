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

  private String title;

  private String description;

  @Column(name = "AUCTION_EXPIRATION")
  private LocalDateTime auctionExpiration;

  @Column(name = "CURRENT_PRICE")
  private BigDecimal currentPrice;

  @Column(name = "INITIAL_PRICE")
  private BigDecimal initialPrice;

}
