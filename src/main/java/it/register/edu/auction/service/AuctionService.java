package it.register.edu.auction.service;

import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.User;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface AuctionService {

  Page<Item> getItems(Pageable pageable);

  Optional<Item> getItem(int id);

  List<Bid> getBids(int itemId);

  int getBidsNumber(int itemId);

  Map<Integer, Integer> getBidsNumber(Collection<Integer> itemIds);

  List<Image> getImages(int itemId, Format format);

  List<Image> getImages(int itemId, Format format, Pageable pageable);

  Map<Integer, List<Image>> getImages(Collection<Integer> itemIds, Format format);

  Map<Integer, List<Image>> getImages(Collection<Integer> itemIds, Format format, Pageable pageable);

  Map<Integer, User> getUsers(Collection<Integer> userIds);

  Bid bid(int userId, int itemId, BigDecimal amount);

  Page<Item> getBidItems(int userId, Pageable pageable);

  List<Item> getBidItems(int userId);

  Page<Item> getAwardedItems(int userId, Pageable pageable);

  void concludeExpiredAuctions();

}
