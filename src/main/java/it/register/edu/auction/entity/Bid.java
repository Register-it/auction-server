package it.register.edu.auction.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid implements Serializable {

  private static final long serialVersionUID = -775123963766903330L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private BigDecimal amount;

  @Column(name = "DATE_TIME")
  private LocalDateTime dateTime;

  @Column(name = "ITEM_ID")
  private int itemId;

  @Column(name = "USER_ID")
  private int userId;
}
