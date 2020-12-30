package it.register.edu.auction.resolver;

import graphql.kickstart.tools.GraphQLResolver;
import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import it.register.edu.auction.entity.Item;
import it.register.edu.auction.repository.BidRepository;
import it.register.edu.auction.repository.ImageRepository;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class ItemResolver implements GraphQLResolver<Item> {

  private static final int MAX_IMAGES = 50;

  @Autowired
  private ImageRepository imageRepository;

  @Autowired
  private BidRepository bidRepository;


  public List<URL> getImages(Item item) {
    return imageRepository.findByItemIdAndFormat(item.getId(), Format.FULLSIZE)
        .stream()
        .map(Image::getUrl)
        .collect(Collectors.toList());
  }

  public List<URL> getThumbnails(Item item, Integer limit) {
    int pageSize = getPageSize(limit);
    return imageRepository
        .findByItemIdAndFormat(item.getId(), Format.THUMBNAIL, PageRequest.of(0, pageSize))
        .stream()
        .map(Image::getUrl)
        .collect(Collectors.toList());
  }

  public Integer getBidsNumber(Item item) {
    return bidRepository.countByItemId(item.getId());
  }

  private int getPageSize(Integer limit) {
    int pageSize = limit != null ? limit : MAX_IMAGES;
    if (pageSize > MAX_IMAGES) {
      pageSize = MAX_IMAGES;
    }
    return pageSize;
  }
}
