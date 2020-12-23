package it.register.edu.auction.entity;

import java.net.URL;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ITEM_ID")
  private Item item;

  public enum Format {
    THUMBNAIL, FULLSIZE
  }

}
