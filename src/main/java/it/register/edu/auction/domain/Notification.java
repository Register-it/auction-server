package it.register.edu.auction.domain;

import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import lombok.Data;

@Data
public class Notification {

  private Bid bid;
  private Item item;
  private Type type;

  public enum Type {
    NEW_BID,
    BID_EXCEEDED,
    AUCTION_EXPIRED,
    ITEM_AWARDED
  }
}
