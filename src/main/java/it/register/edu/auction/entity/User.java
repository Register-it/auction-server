package it.register.edu.auction.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.net.URL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "AUCTION_USER")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "USERNAME", nullable = false)
  private String username;

  @JsonProperty(access = Access.WRITE_ONLY)
  @Column(name = "PASSWORD", nullable = false)
  private String password;

  @Column(name = "FIRST_NAME", nullable = false)
  private String firstName;

  @Column(name = "LAST_NAME", nullable = false)
  private String lastName;

  @Column(name = "IMAGE", nullable = false)
  private URL image;

}
