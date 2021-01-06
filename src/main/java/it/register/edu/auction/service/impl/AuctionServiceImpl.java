package it.register.edu.auction.service.impl;

import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.repository.BidRepository;
import it.register.edu.auction.repository.ImageRepository;
import it.register.edu.auction.repository.ItemRepository;
import it.register.edu.auction.repository.UserRepository;
import it.register.edu.auction.service.AuctionService;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuctionServiceImpl implements AuctionService {

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private BidRepository bidRepository;

  @Autowired
  private ImageRepository imageRepository;

  @Autowired
  private UserRepository userRepository;

  @Override
  public Page<Item> getItems(Pageable pageable) {
    return Page.of(itemRepository.findAll(pageable));
  }

  @Override
  public Optional<Item> getItem(int id) {
    return itemRepository.findById(id);
  }

  @Override
  public List<Bid> getBids(int itemId) {
    return bidRepository.findByItemId(itemId);
  }

  @Override
  public int getBidsNumber(int itemId) {
    return bidRepository.countByItemId(itemId);
  }

  @Override
  public List<URL> getImageUrls(int itemId, Format format) {
    return toUrlList(imageRepository.findByItemIdAndFormat(itemId, format).stream());
  }

  @Override
  public List<URL> getImageUrls(int itemId, Format format, Pageable pageable) {
    return toUrlList(imageRepository.findByItemIdAndFormat(itemId, format, pageable).stream());
  }

  @Override
  public Optional<Bid> bid(int itemId, BigDecimal amount) {
    return Optional.empty();
  }

  @Override
  public Optional<User> getUser(int id) {
    return userRepository.findById(id);
  }

  private List<URL> toUrlList(Stream<Image> images) {
    return images
        .map(Image::getUrl)
        .collect(Collectors.toList());
  }
}
