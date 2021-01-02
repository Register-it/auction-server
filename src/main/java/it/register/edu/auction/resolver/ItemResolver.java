package it.register.edu.auction.resolver;

import graphql.kickstart.tools.GraphQLResolver;
import it.register.edu.auction.domain.LimitedPageRequest;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.repository.BidRepository;
import it.register.edu.auction.repository.ImageRepository;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ItemResolver implements GraphQLResolver<Item> {

  @Autowired
  private ImageRepository imageRepository;

  @Autowired
  private BidRepository bidRepository;

  @Value("${auctions.pagination.max-images}")
  private int maxImages;

  public List<URL> getImages(Item item) {
    return imageRepository.findByItemIdAndFormat(item.getId(), Format.FULLSIZE)
        .stream()
        .map(Image::getUrl)
        .collect(Collectors.toList());
  }

  public List<URL> getThumbnails(Item item, Integer limit) {
    return imageRepository
        .findByItemIdAndFormat(item.getId(), Format.THUMBNAIL, LimitedPageRequest.of(0, limit, maxImages))
        .stream()
        .map(Image::getUrl)
        .collect(Collectors.toList());
  }

  public Integer getBidsNumber(Item item) {
    return bidRepository.countByItemId(item.getId());
  }

}
