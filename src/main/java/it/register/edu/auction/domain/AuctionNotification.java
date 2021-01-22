package it.register.edu.auction.domain;

import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Item;
import java.util.Optional;
import lombok.Data;

@Data
public class AuctionNotification {

  private Bid bid;
  private Item item;
  private Type type;

  public Optional<Integer> getItemId() {
    return Optional.ofNullable(item)
        .map(Item::getId)
        .or(() -> Optional.ofNullable(bid).map(Bid::getItemId));
  }

  public enum Type {
    NEW_BID,
    BID_EXCEEDED,
    AUCTION_EXPIRED,
    ITEM_AWARDED
  }
}
