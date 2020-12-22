package it.register.edu.auction.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Item {

  @Id
  @GeneratedValue
  private Integer id;

  private String title;

  private String description;

}
