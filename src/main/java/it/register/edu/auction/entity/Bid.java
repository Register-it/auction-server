package it.register.edu.auction.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Bid {

  @Id
  @GeneratedValue
  private Integer id;

  private String username;

  private BigDecimal amount;

  @Column(name = "DATE_TIME")
  private LocalDateTime dateTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "AUCTION_ID")
  private Auction auction;
}
