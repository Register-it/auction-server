package it.register.edu.auction.service.impl;

import it.register.edu.auction.domain.AuctionNotification;
import it.register.edu.auction.domain.AuctionNotification.Type;
import it.register.edu.auction.domain.BidsNumber;
import it.register.edu.auction.domain.Page;
import it.register.edu.auction.entity.AwardedItem;
import it.register.edu.auction.entity.Bid;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.entity.User;
import it.register.edu.auction.exception.ExpiredAuctionException;
import it.register.edu.auction.exception.HigherBidExistsException;
import it.register.edu.auction.exception.InvalidBidException;
import it.register.edu.auction.repository.AwardedItemRepository;
import it.register.edu.auction.repository.BidRepository;
import it.register.edu.auction.repository.ImageRepository;
import it.register.edu.auction.repository.ItemRepository;
import it.register.edu.auction.repository.UserRepository;
import it.register.edu.auction.service.AuctionService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks.Many;

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

  @Autowired
  private AwardedItemRepository awardedItemRepository;

  @Autowired
  private Many<AuctionNotification> notificationSink;

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
  public Map<Integer, Integer> getBidsNumber(Collection<Integer> itemIds) {
    return bidRepository.countByItemIdInGroupByItemId(itemIds)
        .stream()
        .collect(Collectors.toMap(BidsNumber::getItemId, BidsNumber::getTotal));
  }

  @Override
  public Map<Integer, List<Image>> getImages(Collection<Integer> itemIds, Format format) {
    return imageRepository.findByItemIdInAndFormat(itemIds, format).stream()
        .collect(Collectors.groupingBy(Image::getItemId));
  }

  @Override
  public Map<Integer, List<Image>> getImages(Collection<Integer> itemIds, Format format, Pageable pageable) {
    return imageRepository.findByItemIdInAndFormatAndLimit(itemIds, format, pageable.getPageSize()).stream()
        .collect(Collectors.groupingBy(Image::getItemId));
  }

  @Override
  public Map<Integer, User> getUsers(Collection<Integer> userIds) {
    return userRepository.findAllById(userIds)
        .stream()
        .collect(Collectors.toMap(User::getId, u -> u));
  }

  @Override
  @Transactional
  public Bid bid(int userId, int itemId, BigDecimal amount) {

    Item item = itemRepository.findByIdAndAuctionExpirationAfter(itemId, LocalDateTime.now())
        .orElseThrow(ExpiredAuctionException::new);

    if (item.getInitialPrice().compareTo(amount) >= 0) {
      throw new InvalidBidException("Bid amount is less than item initial price");
    }

    return placeBid(userId, itemId, amount);
  }

  @Override
  public Page<Item> getBidItems(int userId, Pageable pageable) {
    return Page.of(itemRepository.findBidByUser(userId, pageable));
  }

  @Override
  public Page<Item> getAwardedItems(int userId, Pageable pageable) {
    return Page.of(itemRepository.findAwardedByUser(userId, pageable));
  }

  @Override
  public boolean hasBeenBid(int userId, int itemId) {
    return bidRepository.existsByItemIdAndUserId(itemId, userId);
  }

  @Override
  public void concludeExpiredAuctions() {
    itemRepository.findExpiredNotAwarded().forEach(this::concludeAuction);
  }

  private void concludeAuction(Item item) {
    AuctionNotification notification = AuctionNotification.builder().item(item).type(Type.AUCTION_EXPIRED).build();
    AwardedItem awardedItem = AwardedItem.builder().itemId(item.getId()).build();

    bidRepository.findFirstByItemIdOrderByAmountDesc(item.getId())
        .ifPresent(bid -> {
          notification.setBid(bid);
          awardedItem.setBidId(bid.getId());
          awardedItem.setUserId(bid.getUserId());
        });

    awardedItemRepository.save(awardedItem);
    notificationSink.tryEmitNext(notification);
  }

  private synchronized Bid placeBid(int userId, int itemId, BigDecimal amount) {
    bidRepository.findFirstByItemIdAndAmountGreaterThanEqual(itemId, amount)
        .ifPresent(bid -> {
          throw new HigherBidExistsException(bid);
        });

    return saveBid(userId, itemId, amount);
  }

  private Bid saveBid(int userId, int itemId, BigDecimal amount) {
    Bid bid = bidRepository.save(Bid.builder().userId(userId).itemId(itemId).amount(amount).dateTime(LocalDateTime.now()).build());

    itemRepository.findById(itemId)
        .map(item -> {
          item.setCurrentPrice(amount);
          return item;
        })
        .ifPresent(item -> itemRepository.save(item));

    return bid;
  }

}
