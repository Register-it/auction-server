package it.register.edu.auction.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "AWARDED")
public class AwardedItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "ITEM_ID", nullable = false)
  private int itemId;

  @Column(name = "USER_ID", nullable = false)
  private int userId;

  @Column(name = "PRICE", nullable = false)
  private BigDecimal price;

  @Column(name = "DATE_TIME", nullable = false)
  private LocalDateTime dateTime;

}
