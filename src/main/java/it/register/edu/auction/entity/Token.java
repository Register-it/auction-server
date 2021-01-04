package it.register.edu.auction.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

  @Id
  private String id;

  @Column(name = "USER_ID")
  private int userId;

  @Column(name = "EXPIRES_AT")
  private LocalDateTime expiresAt;

}
