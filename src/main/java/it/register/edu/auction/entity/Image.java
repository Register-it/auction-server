package it.register.edu.auction.entity;

import java.net.URL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Image {

  @Id
  @GeneratedValue
  private Integer id;

  @Enumerated(EnumType.STRING)
  private Format format;

  private URL url;

  @Column(name = "ITEM_ID")
  private Integer itemId;

  public enum Format {
    THUMBNAIL, FULLSIZE
  }

}
