package it.register.edu.auction.entity;

import java.net.URL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(name = "FORMAT", nullable = false)
  private Format format;

  @Column(name = "URL", nullable = false)
  private URL url;

  @Column(name = "ITEM_ID", nullable = false)
  private int itemId;

  public enum Format {
    THUMBNAIL, FULLSIZE
  }

}
