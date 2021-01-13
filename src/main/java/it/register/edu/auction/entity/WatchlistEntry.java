package it.register.edu.auction.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "WATCHLIST")
@IdClass(WatchlistId.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistEntry {

  @Id
  @Column(name = "USER_ID")
  private int userId;

  @Id
  @Column(name = "ITEM_ID")
  private int itemId;

}
