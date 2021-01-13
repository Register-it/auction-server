package it.register.edu.auction.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchlistId implements Serializable {

  private static final long serialVersionUID = -9017926125774917088L;

  private int userId;
  private int itemId;

}
