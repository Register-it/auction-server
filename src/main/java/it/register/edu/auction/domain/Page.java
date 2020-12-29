package it.register.edu.auction.domain;

import java.util.List;
import lombok.Data;

@Data
public class Page<T> {

  private int current;
  private int totalPages;
  private boolean isFirst;
  private boolean isLast;
  private int totalElements;
  private List<T> elements;

  public static <U> Page<U> of(org.springframework.data.domain.Page<U> source) {
    Page<U> page = new Page<>();
    page.setCurrent(source.getNumber());
    page.setTotalPages(source.getTotalPages());
    page.setFirst(source.isFirst());
    page.setTotalElements((int) source.getTotalElements());
    page.setLast(source.isLast());
    page.setElements(source.getContent());

    return page;
  }
}
