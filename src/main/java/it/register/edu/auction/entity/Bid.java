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
public class Bid {

  @Id
  @GeneratedValue
  private int id;

  private BigDecimal amount;

  @Column(name = "DATE_TIME")
  private LocalDateTime dateTime;

  @Column(name = "ITEM_ID")
  private int itemId;

  @Column(name = "USER_ID")
  private int userId;
}
