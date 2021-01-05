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
@Table(name = "WATCHLIST")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "USER_ID")
  private int userId;

  @Column(name = "ITEM_ID")
  private int itemId;

}
