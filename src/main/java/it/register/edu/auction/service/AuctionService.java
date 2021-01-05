package it.register.edu.auction.service;

import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.Item;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface AuctionService {

  Page<Item> getItems(Pageable pageable);

  Optional<Item> getItem(int id);

  List<Bid> getBids(int itemId);

  int getBidsNumber(int itemId);

  List<URL> getImageUrls(int itemId, Format format);

  List<URL> getImageUrls(int itemId, Format format, Pageable pageable);

}
