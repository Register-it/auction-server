package it.register.edu.auction.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Auction {

  @Id
  @GeneratedValue
  private Integer id;

  @Column(name = "EXPIRES_ON")
  private LocalDateTime expiresOn;

  @Column(name = "CURRENT_PRICE")
  private BigDecimal currentPrice;

  @Column(name = "ITEM_ID")
  private Integer itemId;

}
