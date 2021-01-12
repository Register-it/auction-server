package it.register.edu.auction.repository;

import it.register.edu.auction.entity.Image;
import it.register.edu.auction.entity.Image.Format;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

  List<Image> findByItemIdInAndFormat(Collection<Integer> itemIds, Format format);

  @Query(value = "SELECT * FROM ("
      + "  SELECT ROW_NUMBER() OVER (PARTITION BY ITEM_ID ORDER BY ID) AS n, i.* FROM IMAGE i"
      + "  WHERE i.ITEM_ID IN (:itemIds) AND i.FORMAT = :#{#format.name()}) t"
      + " WHERE t.n <= :limit", nativeQuery = true)
  List<Image> findByItemIdInAndFormatAndLimit(@Param("itemIds") Collection<Integer> itemIds, @Param("format") Format format,
      @Param("limit") int limit);

}
