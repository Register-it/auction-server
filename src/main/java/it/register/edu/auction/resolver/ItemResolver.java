package it.register.edu.auction.resolver;

import graphql.kickstart.tools.GraphQLResolver;
import it.register.edu.auction.entity.Auction;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.repository.AuctionRepository;
import it.register.edu.auction.repository.ImageRepository;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class ItemResolver implements GraphQLResolver<Item> {

  @Autowired
  private AuctionRepository auctionRepository;

  @Autowired
  private ImageRepository imageRepository;

  public List<URL> getImages(Item item) {
    return imageRepository.findByItemIdAndFormat(item.getId(), Format.FULLSIZE)
        .stream()
        .map(Image::getUrl)
        .collect(Collectors.toList());
  }

  public List<URL> getThumbnails(Item item, Integer limit) {
    int pageSize = limit != null ? limit : Integer.MAX_VALUE;
    return imageRepository.findByItemIdAndFormat(item.getId(), Format.THUMBNAIL, PageRequest.of(0, pageSize))
        .stream()
        .map(Image::getUrl)
        .collect(Collectors.toList());
  }

  public Auction getAuction(Item item) {
    return auctionRepository.findByItemId(item.getId());
  }

}
