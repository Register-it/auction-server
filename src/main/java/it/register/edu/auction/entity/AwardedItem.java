package it.register.edu.auction.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "AWARDED")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AwardedItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "ITEM_ID", nullable = false)
  private int itemId;

  @Column(name = "USER_ID")
  private Integer userId;

  @Column(name = "BID_ID")
  private Integer bidId;

}
