package it.register.edu.auction.test.integration;

import org.mockito.ArgumentMatcher;
import org.springframework.data.domain.Pageable;

public class PageableMatcher implements ArgumentMatcher<Pageable> {

  private final int page;
  private final int size;

  public PageableMatcher(int page, int size) {
    this.page = page;
    this.size = size;
  }

  @Override
  public boolean matches(Pageable pageable) {
    return (pageable.getPageNumber() == page) && (pageable.getPageSize() == size);
  }
}
