package it.register.edu.auction.domain;

import it.register.edu.auction.exception.IllegalSizeException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class LimitedPageRequest extends PageRequest {

  protected LimitedPageRequest(int page, int size, Sort sort) {
    super(page, size, sort);
  }

  public static PageRequest of(int page, Integer size, int maxSize) {
    return of(page, size, maxSize, Sort.unsorted());
  }

  public static PageRequest of(int page, Integer size, int maxSize, Sort sort) {
    int pageSize = size != null ? size : maxSize;
    if (pageSize > maxSize) {
      throw new IllegalSizeException(pageSize, maxSize);
    }
    return new LimitedPageRequest(page, pageSize, sort);
  }

}
