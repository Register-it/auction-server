package it.register.edu.auction.repository;

import it.register.edu.auction.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

  List<Image> findByItemId(Integer itemId);

}
